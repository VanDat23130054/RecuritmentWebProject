package com.java_web.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.dao.CommonDAO;
import com.java_web.dao.CompanyDAO;
import com.java_web.dao.JobDAO;
import com.java_web.dao.SavedJobDAO;
import com.java_web.model.auth.User;
import com.java_web.model.dto.CompanyDetailDTO;
import com.java_web.model.dto.JobSearchDTO;

@WebServlet("/company/*")
public class CompanyDetailServlet extends HttpServlet {

    private CompanyDAO companyDAO;
    private JobDAO jobDAO;
    private CommonDAO commonDAO;
    private SavedJobDAO savedJobDAO;

    @Override
    public void init() throws ServletException {
        companyDAO = new CompanyDAO();
        jobDAO = new JobDAO();
        commonDAO = new CommonDAO();
        savedJobDAO = new SavedJobDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        try {
            // Extract company ID from path: /company/{id}
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendRedirect(request.getContextPath() + "/jobs");
                return;
            }

            String companyIdStr = pathInfo.substring(1); // Remove leading slash
            if (companyIdStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/jobs");
                return;
            }

            Integer companyId = Integer.parseInt(companyIdStr);

            // Get company details
            CompanyDetailDTO company = companyDAO.getCompanyDetail(companyId);
            if (company == null) {
                response.sendRedirect(request.getContextPath() + "/jobs");
                return;
            }

            // Get company jobs (page 1, 50 per page)
            List<JobSearchDTO> companyJobs = jobDAO.searchJobsByCompany(companyId, 1, 50);

            // Get current user for saved jobs check
            HttpSession session = request.getSession(false);
            User user = null;
            List<Integer> savedJobIds = List.of();

            if (session != null) {
                user = (User) session.getAttribute("user");
                if (user != null && "Candidate".equals(user.getRole())) {
                    savedJobIds = savedJobDAO.getSavedJobIds(user.getUserId());
                }
            }

            // Set attributes for JSP
            request.setAttribute("company", company);
            request.setAttribute("companyJobs", companyJobs);
            request.setAttribute("user", user);
            request.setAttribute("savedJobIds", savedJobIds);

            request.getRequestDispatcher("/WEB-INF/views/company/company-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/jobs");
        } catch (SQLException e) {
            throw new ServletException("Error loading company details", e);
        }
    }
}
