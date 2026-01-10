package com.java_web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.java_web.dao.CandidateDAO;
import com.java_web.dao.ChatDAO;
import com.java_web.dao.RecruiterDAO;
import com.java_web.model.auth.User;
import com.java_web.model.candidate.Candidate;
import com.java_web.model.employer.Recruiter;
import com.java_web.model.system.Conversation;
import com.java_web.model.system.Message;

@WebServlet("/chat/*")
public class ChatServlet extends HttpServlet {

    private ChatDAO chatDAO;
    private CandidateDAO candidateDAO;
    private RecruiterDAO recruiterDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        chatDAO = new ChatDAO();
        candidateDAO = new CandidateDAO();
        recruiterDAO = new RecruiterDAO();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();

        try {
            // Route handling
            if (pathInfo == null || pathInfo.equals("/")) {
                // Show conversations list
                showConversationsList(request, response, user);
            } else if (pathInfo.startsWith("/conversation/")) {
                // Show specific conversation
                String convIdStr = pathInfo.substring("/conversation/".length());
                Integer conversationId = Integer.parseInt(convIdStr);
                showConversation(request, response, user, conversationId);
            } else if (pathInfo.equals("/messages")) {
                // AJAX: Get messages for a conversation
                getMessagesAjax(request, response, user);
            } else if (pathInfo.equals("/unread-count")) {
                // AJAX: Get unread message count
                getUnreadCountAjax(request, response, user);
            } else if (pathInfo.equals("/conversations")) {
                // AJAX: Get conversations list
                getConversationsAjax(request, response, user);
            } else if (pathInfo.equals("/search")) {
                // AJAX: Search for recruitersor candidates
                searchRecipientsAjax(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated");
            return;
        }

        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.equals("/send")) {
                // Send a new message
                sendMessage(request, response, user);
            } else if (pathInfo != null && pathInfo.equals("/mark-read")) {
                // Mark messages as read
                markAsRead(request, response, user);
            } else if (pathInfo != null && pathInfo.equals("/start")) {
                // Start a new conversation
                startConversation(request, response, user);
            } else {
                sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            }
        } catch (SQLException e) {
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }

    // ==================== Page Handlers ====================
    private void showConversationsList(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException, ServletException, IOException {

        System.out.println("[CHAT SERVLET] showConversationsList - userId: " + user.getUserId() + ", role: " + user.getRole());

        try {
            List<Conversation> conversations = chatDAO.getUserConversations(user.getUserId(), user.getRole());
            System.out.println("[CHAT SERVLET] Found " + (conversations != null ? conversations.size() : 0) + " conversations");

            request.setAttribute("conversations", conversations);
            request.setAttribute("currentUserId", user.getUserId());
            request.setAttribute("currentUserRole", user.getRole());

            request.getRequestDispatcher("/WEB-INF/views/chat/conversations.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("[CHAT SERVLET] ERROR in showConversationsList: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Error loading conversations", e);
        }
    }

    private void showConversation(HttpServletRequest request, HttpServletResponse response,
            User user, Integer conversationId) throws SQLException, ServletException, IOException {

        // Check access
        if (!chatDAO.hasAccessToConversation(conversationId, user.getUserId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        Conversation conversation = chatDAO.getConversationById(conversationId);
        List<Message> messages = chatDAO.getConversationMessages(conversationId, 1, 100);

        // Mark messages as read
        chatDAO.markMessagesAsRead(conversationId, user.getUserId());

        request.setAttribute("conversation", conversation);
        request.setAttribute("messages", messages);
        request.setAttribute("currentUserId", user.getUserId());
        request.setAttribute("currentUserRole", user.getRole());

        request.getRequestDispatcher("/WEB-INF/views/chat/chat.jsp").forward(request, response);
    }

    // ==================== AJAX Handlers ====================
    private void getMessagesAjax(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException, IOException {

        String convIdStr = request.getParameter("conversationId");
        if (convIdStr == null || convIdStr.isEmpty()) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Conversation ID required");
            return;
        }

        Integer conversationId = Integer.parseInt(convIdStr);

        // Check access
        if (!chatDAO.hasAccessToConversation(conversationId, user.getUserId())) {
            sendJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        int page = getIntParam(request, "page", 1);
        int pageSize = getIntParam(request, "pageSize", 50);

        System.out.println("[CHAT SERVLET] getMessagesAjax - convId: " + conversationId + ", userId: " + user.getUserId() + ", page: " + page);

        try {
            List<Message> messages = chatDAO.getConversationMessages(conversationId, page, pageSize);
            System.out.println("[CHAT SERVLET] Retrieved " + (messages != null ? messages.size() : 0) + " messages");
            sendJsonResponse(response, messages);
        } catch (Exception e) {
            System.err.println("[CHAT SERVLET] ERROR in getMessagesAjax: " + e.getMessage());
            e.printStackTrace();
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching messages: " + e.getMessage());
        }
    }

    private void getConversationsAjax(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException, IOException {

        List<Conversation> conversations = chatDAO.getUserConversations(user.getUserId(), user.getRole());
        sendJsonResponse(response, conversations);
    }

    private void getUnreadCountAjax(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException, IOException {

        int unreadCount = chatDAO.getUnreadMessageCount(user.getUserId(), user.getRole());
        sendJsonResponse(response, java.util.Map.of("unreadCount", unreadCount));
    }

    private void sendMessage(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException, IOException {

        String convIdStr = request.getParameter("conversationId");
        String body = request.getParameter("body");

        if (convIdStr == null || body == null || body.trim().isEmpty()) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Conversation ID and message body required");
            return;
        }

        Integer conversationId = Integer.parseInt(convIdStr);

        // Check access
        if (!chatDAO.hasAccessToConversation(conversationId, user.getUserId())) {
            sendJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        System.out.println("[CHAT SERVLET] sendMessage - convId: " + conversationId + ", userId: " + user.getUserId() + ", body length: " + body.length());

        try {
            Message message = chatDAO.sendMessage(conversationId, user.getUserId(), body.trim());

            if (message != null) {
                System.out.println("[CHAT SERVLET] Message sent successfully: " + message.getMessageId());
                sendJsonResponse(response, message);
            } else {
                System.out.println("[CHAT SERVLET] sendMessage returned null");
                sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to send message");
            }
        } catch (Exception e) {
            System.err.println("[CHAT SERVLET] ERROR in sendMessage: " + e.getMessage());
            e.printStackTrace();
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error sending message: " + e.getMessage());
        }
    }

    private void markAsRead(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException, IOException {

        String convIdStr = request.getParameter("conversationId");
        if (convIdStr == null) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Conversation ID required");
            return;
        }

        Integer conversationId = Integer.parseInt(convIdStr);

        // Check access
        if (!chatDAO.hasAccessToConversation(conversationId, user.getUserId())) {
            sendJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        int count = chatDAO.markMessagesAsRead(conversationId, user.getUserId());
        sendJsonResponse(response, java.util.Map.of("markedRead", count));
    }

    private void startConversation(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException, IOException {

        String candidateIdStr = request.getParameter("candidateId");
        String recruiterIdStr = request.getParameter("recruiterId");
        String jobIdStr = request.getParameter("jobId");

        System.out.println("[CHAT DEBUG] startConversation called");
        System.out.println("[CHAT DEBUG] candidateIdStr: '" + candidateIdStr + "'");
        System.out.println("[CHAT DEBUG] recruiterIdStr: '" + recruiterIdStr + "'");
        System.out.println("[CHAT DEBUG] jobIdStr: '" + jobIdStr + "'");

        if (candidateIdStr == null || candidateIdStr.isEmpty() || recruiterIdStr == null || recruiterIdStr.isEmpty()) {
            System.out.println("[CHAT DEBUG] Missing required params - sending 400 error");
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Candidate ID and Recruiter ID required");
            return;
        }

        Integer candidateId = Integer.parseInt(candidateIdStr);
        Integer recruiterId = Integer.parseInt(recruiterIdStr);
        Integer jobId = (jobIdStr != null && !jobIdStr.isEmpty()) ? Integer.parseInt(jobIdStr) : null;

        Conversation conversation = chatDAO.getOrCreateConversation(candidateId, recruiterId, jobId);

        if (conversation != null) {
            sendJsonResponse(response, java.util.Map.of(
                    "success", true,
                    "conversationId", conversation.getConversationId(),
                    "redirectUrl", request.getContextPath() + "/chat/conversation/" + conversation.getConversationId()
            ));
        } else {
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create conversation");
        }
    }

    // ==================== Utility Methods ====================
    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(objectMapper.writeValueAsString(data));
        out.flush();
    }

    private void sendJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(objectMapper.writeValueAsString(java.util.Map.of("error", message)));
        out.flush();
    }

    private int getIntParam(HttpServletRequest request, String name, int defaultValue) {
        String value = request.getParameter(name);
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private void searchRecipientsAjax(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException, SQLException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String query = request.getParameter("q");
        // Query can be empty - we'll show all application-based contacts
        query = query != null ? query.trim() : "";

        List<Object> results = null;

        try {
            // Based on user role, get application-based contacts
            String userRole = user.getRole();
            HttpSession session = request.getSession(false);

            System.out.println("[ChatServlet] searchRecipientsAjax - userRole: " + userRole + ", query: " + query);

            if (userRole != null && userRole.equalsIgnoreCase("candidate")) {
                // Candidate: Get recruiters from jobs they applied to
                Integer candidateId = (Integer) session.getAttribute("candidateId");

                // Fallback: fetch candidateId if not in session
                if (candidateId == null) {
                    System.out.println("[ChatServlet] candidateId not in session, fetching from DB...");
                    Candidate candidate = candidateDAO.getCandidateByUserId(user.getUserId());
                    if (candidate != null) {
                        candidateId = candidate.getCandidateId();
                        session.setAttribute("candidateId", candidateId);
                        System.out.println("[ChatServlet] Fetched candidateId: " + candidateId);
                    }
                }

                System.out.println("[ChatServlet] Candidate search - candidateId: " + candidateId);
                if (candidateId != null) {
                    results = chatDAO.getAppliedJobRecruiters(candidateId, query.isEmpty() ? null : query);
                } else {
                    System.out.println("[ChatServlet] WARNING: candidateId is still null");
                }
            } else if (userRole != null && (userRole.equalsIgnoreCase("recruiter") || userRole.equalsIgnoreCase("EmployerAdmin"))) {
                // Recruiter: Get candidates who applied to their jobs
                Integer recruiterId = (Integer) session.getAttribute("recruiterId");

                // Fallback: fetch recruiterId if not in session
                if (recruiterId == null) {
                    System.out.println("[ChatServlet] recruiterId not in session, fetching from DB...");
                    Recruiter recruiter = recruiterDAO.getRecruiterByUserId(user.getUserId());
                    if (recruiter != null) {
                        recruiterId = recruiter.getRecruiterId();
                        session.setAttribute("recruiterId", recruiterId);
                        System.out.println("[ChatServlet] Fetched recruiterId: " + recruiterId);
                    }
                }

                System.out.println("[ChatServlet] Recruiter search - recruiterId: " + recruiterId);
                if (recruiterId != null) {
                    results = chatDAO.getJobApplicantCandidates(recruiterId, query.isEmpty() ? null : query);
                } else {
                    System.out.println("[ChatServlet] WARNING: recruiterId is still null");
                }
            } else {
                System.out.println("[ChatServlet] WARNING: Unknown role or null role");
            }

            if (results != null) {
                System.out.println("[ChatServlet] Found " + results.size() + " results");
                String jsonResponse = objectMapper.writeValueAsString(results);
                response.getWriter().write(jsonResponse);
            } else {
                System.out.println("[ChatServlet] No results (null)");
                response.getWriter().write("[]");
            }
        } catch (SQLException e) {
            System.err.println("[ChatServlet] SQL Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage().replace("\"", "'") + "\"}");
        } catch (Exception e) {
            System.err.println("[ChatServlet] Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
