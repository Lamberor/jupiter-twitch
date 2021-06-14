package com.laioffer.jupiter.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.jupiter.db.MySQLConnection;
import com.laioffer.jupiter.db.MySQLException;
import com.laioffer.jupiter.entity.LoginRequestBody;
import com.laioffer.jupiter.entity.User;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", value = "/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Read user data from the request body
        User user = ServletUtil.readRequestBody(
                User.class, request
        );
        if (user == null) {
            System.err.println("User information incorrect.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean isUserAdded = false;
        MySQLConnection connection = null;

        try (MySQLConnection conn = new MySQLConnection()) {
            // Add the new user to the database
            user.setPassword(ServletUtil.encryptPassword(user.getUserId(), user.getPassword()));
            isUserAdded = conn.addUser(user);
        } catch (MySQLException e) {
            throw new ServletException(e);
        }

        if (!isUserAdded) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }
}
