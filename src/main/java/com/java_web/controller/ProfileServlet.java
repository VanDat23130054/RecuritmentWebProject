package com.java_web.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.dao.CandidateDAO;
import com.java_web.dao.JobDAO;
import com.java_web.dao.SavedJobDAO;
import com.java_web.dao.UserDAO;
import com.java_web.model.auth.User;
import com.java_web.model.candidate.Candidate;

/**
 * Servlet implementation class ProfileServlet
 */
@WebServlet("/profile/*")
public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserDAO userDAO;
	private JobDAO jobDAO;
	private CandidateDAO candidateDAO;
	private SavedJobDAO savedJobDAO;
	@Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        jobDAO = new JobDAO();
        candidateDAO = new CandidateDAO();
        savedJobDAO = new SavedJobDAO();
    }
   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 HttpSession session = request.getSession();
		 Integer userId = (Integer) session.getAttribute("userId");
		 
		 if (userId == null) {
			 response.sendRedirect(request.getContextPath() + "/login");
			 return;
		 }
		 
		 try {
			 // Fetch user profile details
			 User user = userDAO.findById(userId);
			 request.setAttribute("user", user);
			 
			 // If user is recruiter, load their posted jobs
			 if (user != null && "Recruiter".equals(user.getRole())) {
				 List<Map<String, Object>> jobs = jobDAO.getJobsByRecruiter(userId);
				 request.setAttribute("jobs", jobs);
			 }
			 
			 // If user is candidate, load candidate profile and saved jobs
			 if (user != null && "Candidate".equals(user.getRole())) {
				 Candidate candidate = candidateDAO.getCandidateByUserId(userId);
				 request.setAttribute("candidate", candidate);
				 // load saved jobs ids (to show quick list) - using SavedJobDAO
				 request.setAttribute("savedJobs", savedJobDAO.getSavedJobsByUser(userId));
			 }
			 
			 // Forward to profile JSP
			 request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
		 } catch (SQLException e) {
			 throw new ServletException(e);
		 }
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}