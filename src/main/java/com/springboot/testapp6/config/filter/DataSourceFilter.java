package com.springboot.testapp6.config.filter;

import com.springboot.testapp6.config.DynamicDataSource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class DataSourceFilter extends OncePerRequestFilter {
    private static final String DEFAULT_DB = "DB1";
    private static String LAST_DB = DEFAULT_DB;

    public static void setDB(String dbKey) {
        LAST_DB = dbKey;
    }
    public static String getDB() {
         return LAST_DB;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String dbKey = request.getParameter("db");
        log.info("dbKey={} LAST_DB={}", dbKey, LAST_DB);
        if (dbKey != null && !dbKey.isBlank()) {
            LAST_DB = dbKey;
        }
        DynamicDataSource.setDataSourceKey(LAST_DB);

        try {
            filterChain.doFilter(request, response);
        } finally {
            DynamicDataSource.clear(); // ThreadLocal 정리
        }
    }
}
