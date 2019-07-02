package com.springboot.mall.endpoint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "dbstatus", enableByDefault = true)
public class DataBaseConnectionEndpoint {

    private static final String DRIVER = "com.mysql.jdbc.Driver";

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    /**
     * One endpoint only can have one @ReadOperation - represents the HTTP GET Method
     *
     * @return
     */
    @ReadOperation
    public Map<String, Object> checkDbStatus() {
        Map<String, Object> msgMap = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            Class.forName(DRIVER);
            msgMap.put("success", true);
            msgMap.put("message", "Database Connection Successful!");
        } catch (ClassNotFoundException | SQLException e) {
            msgMap.put("success", false);
            msgMap.put("message", "Database Connection Failure!");
        }
        return msgMap;
    }
}
