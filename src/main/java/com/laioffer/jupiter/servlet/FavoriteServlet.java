package com.laioffer.jupiter.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.jupiter.db.MySQLConnection;
import com.laioffer.jupiter.db.MySQLException;
import com.laioffer.jupiter.entity.FavoriteRequestBody;
import com.laioffer.jupiter.entity.Item;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "FavoriteServlet", value = "/favorite")
public class FavoriteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the session is still valid,
        // which means the user has been logged in successfully
        HttpSession session = request.getSession(false);
        if (session == null) {
           response.setStatus(HttpServletResponse.SC_FORBIDDEN);
           return;
        }
        String userId = (String) session.getAttribute("user_id");

        try (MySQLConnection conn = new MySQLConnection()) {
            Map<String, List<Item>> itemMap = conn.getFavoriteItems(userId);
            ServletUtil.writeData(response, itemMap);
        } catch (MySQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String userId = (String) session.getAttribute("user_id");

        // Get favorite item information from request body
        ObjectMapper mapper = new ObjectMapper();
        FavoriteRequestBody body = mapper.readValue(
                request.getReader(), FavoriteRequestBody.class);
        if (body == null) {
            System.err.println("Failed to convert JSON to FavoriteRequestBody.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Recall that MySQLConnection implements AutoCloseable
        try (MySQLConnection conn = new MySQLConnection()) {
            conn.setFavoriteItem(userId, body.getFavoriteItem());
        } catch (MySQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String userId = (String) session.getAttribute("user_id");

        ObjectMapper mapper = new ObjectMapper();
        FavoriteRequestBody body = mapper.readValue(request.getReader(), FavoriteRequestBody.class);
        if (body == null) {
            System.err.println("Failed to convert JSON to FavoriteRequestBody.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Recall that MySQLConnection implements AutoCloseable
        try (MySQLConnection conn = new MySQLConnection()) {
            // Remove the favorite item to the database
            conn.unsetFavoriteItem(userId, body.getFavoriteItem().getId());
        } catch (MySQLException e) {
            throw new ServletException(e);
        }
    }
}
