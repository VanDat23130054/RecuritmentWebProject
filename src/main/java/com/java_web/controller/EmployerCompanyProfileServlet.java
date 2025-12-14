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

import com.java_web.dao.CommonDAO;
import com.java_web.dao.CompanyDAO;
import com.java_web.dao.RecruiterDAO;
import com.java_web.model.auth.User;
import com.java_web.model.employer.Recruiter;

@WebServlet("/employer/company-profile")
public class EmployerCompanyProfileServlet extends HttpServlet {

    private CompanyDAO companyDAO;
    private RecruiterDAO recruiterDAO;
    private CommonDAO commonDAO;

    @Override
    public void init() throws ServletException {
        companyDAO = new CompanyDAO();
        recruiterDAO = new RecruiterDAO();
        commonDAO = new CommonDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?returnUrl="
                    + request.getRequestURI());
            return;
        }

        User user = (User) session.getAttribute("user");

        if (!"Recruiter".equals(user.getRole()) && !"EmployerAdmin".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        try {
            // Get recruiter profile
            Recruiter recruiter = recruiterDAO.getRecruiterByUserId(user.getUserId());

            if (recruiter == null || recruiter.getCompanyId() == null) {
                response.sendRedirect(request.getContextPath() + "/employer/setup-profile");
                return;
            }

            // Get company details
            Map<String, Object> company = companyDAO.getCompanyDetail(recruiter.getCompanyId());

            // Get cities for dropdown
            List<Map<String, Object>> cities = commonDAO.getAllCitiesWithList();

            request.setAttribute("company", company);
            request.setAttribute("cities", cities);
            request.setAttribute("user", user);

            request.getRequestDispatcher("/WEB-INF/views/employer/company-profile.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error loading company profile", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            Recruiter recruiter = recruiterDAO.getRecruiterByUserId(user.getUserId());

            if (recruiter == null || recruiter.getCompanyId() == null) {
                response.sendRedirect(request.getContextPath() + "/employer/setup-profile");
                return;
            }

            // Get form parameters
            String name = request.getParameter("name");
            String website = request.getParameter("website");
            String description = request.getParameter("description");
            String industry = request.getParameter("industry");
            String sizeRange = request.getParameter("sizeRange");
            String foundedYearStr = request.getParameter("foundedYear");
            String cityIdStr = request.getParameter("cityId");
            String logoUrl = request.getParameter("logoUrl");

            Integer foundedYear = null;
            if (foundedYearStr != null && !foundedYearStr.trim().isEmpty()) {
                foundedYear = Integer.parseInt(foundedYearStr);
            }

            Integer cityId = null;
            if (cityIdStr != null && !cityIdStr.trim().isEmpty()) {
                cityId = Integer.parseInt(cityIdStr);
            }

            // Update company profile
            boolean success = companyDAO.updateCompanyProfile(
                    recruiter.getCompanyId(),
                    name,
                    website,
                    description,
                    industry,
                    sizeRange,
                    foundedYear,
                    cityId,
                    logoUrl
            );

            if (success) {
                request.setAttribute("success", "Company profile updated successfully!");
            } else {
                request.setAttribute("error", "Failed to update company profile");
            }

            doGet(request, response);

        } catch (SQLException | NumberFormatException e) {
            request.setAttribute("error", "Error updating company profile: " + e.getMessage());
            try {
                doGet(request, response);
            } catch (Exception ex) {
                throw new ServletException("Error loading company profile", ex);
            }
        }
    }
}
