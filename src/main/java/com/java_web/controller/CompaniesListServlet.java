package com.java_web.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.java_web.dao.CompanyDAO;
import com.java_web.model.dto.CompanyListDTO;

@WebServlet("/companies")
public class CompaniesListServlet extends HttpServlet {

    private CompanyDAO companyDAO;

    @Override
    public void init() throws ServletException {
        companyDAO = new CompanyDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        try {
            // Get pagination parameters
            String pageStr = request.getParameter("page");
            int currentPage = (pageStr != null && !pageStr.isEmpty()) ? Integer.parseInt(pageStr) : 1;
            int pageSize = 12;

            // Get top employers (for now get a large number to show all)
            List<CompanyListDTO> companies = companyDAO.getTopEmployers(500);

            // Simple pagination in memory
            int totalCompanies = companies.size();
            int totalPages = (totalCompanies + pageSize - 1) / pageSize;

            if (currentPage < 1) {
                currentPage = 1;
            }
            if (currentPage > totalPages && totalPages > 0) {
                currentPage = totalPages;
            }

            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalCompanies);

            List<CompanyListDTO> pagedCompanies = companies.subList(startIndex, endIndex);

            // Set attributes for JSP
            request.setAttribute("companies", pagedCompanies);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalCompanies", totalCompanies);

            request.getRequestDispatcher("/WEB-INF/views/company/companies-list.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error loading companies", e);
        }
    }
}
