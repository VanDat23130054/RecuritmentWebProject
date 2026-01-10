package com.java_web.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.java_web.dao.CommonDAO;

@WebServlet("/about")
public class AboutServlet extends HttpServlet {

    private CommonDAO commonDAO;

    @Override
    public void init() throws ServletException {
        commonDAO = new CommonDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Fetch actual statistics from database
            int totalJobs = commonDAO.getTotalJobCount();
            int totalCompanies = commonDAO.getTotalCompanyCount();
            int totalCandidates = commonDAO.getTotalCandidateCount();
            int totalApplications = commonDAO.getTotalApplicationCount();

            request.setAttribute("totalJobs", totalJobs);
            request.setAttribute("totalCompanies", totalCompanies);
            request.setAttribute("totalCandidates", totalCandidates);
            request.setAttribute("totalApplications", totalApplications);

        } catch (SQLException e) {
            // Set default values if database error
            request.setAttribute("totalJobs", 0);
            request.setAttribute("totalCompanies", 0);
            request.setAttribute("totalCandidates", 0);
            request.setAttribute("totalApplications", 0);
        }

        request.getRequestDispatcher("/WEB-INF/views/about.jsp").forward(request, response);
    }
}
