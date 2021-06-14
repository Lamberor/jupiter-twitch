package com.laioffer.jupiter.servlet;

import com.laioffer.jupiter.entity.Item;
import com.laioffer.jupiter.recommendation.ItemRecommender;
import com.laioffer.jupiter.recommendation.RecommendationException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "RecommendationServlet", value = "/recommendation")
public class RecommendationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        ItemRecommender itemRecommender = new ItemRecommender();
        Map<String, List<Item>> itemMap;

        // If the user has successfully logged in,
        // recommend by the favorite records, otherwise recommend by the top games
        try {
            if (session == null) {
                itemMap = itemRecommender.recommendItemsByDefault();
            } else {
                String userId = (String) request.getSession().getAttribute("user_id");
                itemMap = itemRecommender.recommendItemByUser(userId);
            }
        } catch (RecommendationException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        ServletUtil.writeData(response, itemMap);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
