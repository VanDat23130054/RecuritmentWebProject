package com.java_web.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.java_web.models.User;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean ok = true;

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        boolean gender = "true".equals(request.getParameter("female"));

        int yearOfBirth = 0;
        try {
            String yob = request.getParameter("yearOfBirth");
            if (yob == null || yob.isEmpty()) {
                ok = false;
            } else {
                yearOfBirth = Integer.parseInt(yob);
            }
        } catch (NumberFormatException nFE) {
            ok = false;
        }
        String industry = request.getParameter("industry");
        String jobTitle = request.getParameter("jobTitle");
        String city = request.getParameter("city");
        String company = request.getParameter("company");
        String telephone = request.getParameter("telephone");
        String[] favorites = request.getParameterValues("favorites");

        String desiredPlatform = request.getParameter("desiredPlatform");

        if ((firstName == null) || (firstName.isEmpty())) {
            ok = false;
        }
        if ((lastName == null) || (lastName.isEmpty())) {
            ok = false;
        }
        if ((email == null) || (email.isEmpty())) {
            ok = false;
        }
        if ((password == null) || (password.isEmpty())) {
            ok = false;
        }

        if (ok) {
            User user = new User(firstName, lastName, email, password, gender,
                    yearOfBirth, industry, jobTitle, company, city, telephone,
                    favorites, desiredPlatform);
            // set in request (for immediate forward) and session (in case JSP expects session-scoped bean)
            request.setAttribute("user", user);
            request.getSession().setAttribute("user", user);
            request.getServletContext().getRequestDispatcher("/WEB-INF/views/user/welcome.jsp").forward(request, response);
        } else {
            request.getServletContext().getRequestDispatcher("/WEB-INF/views/user/regagain.jsp").forward(request, response);
        }
    }

    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
