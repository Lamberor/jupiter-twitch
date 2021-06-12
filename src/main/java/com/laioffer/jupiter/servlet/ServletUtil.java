package com.laioffer.jupiter.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.jupiter.entity.Item;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ServletUtil {
    public static void writeItemMap(HttpServletResponse response, Map<String, List<Item>> itemMap) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(new ObjectMapper().writeValueAsString(itemMap));
    }
}
