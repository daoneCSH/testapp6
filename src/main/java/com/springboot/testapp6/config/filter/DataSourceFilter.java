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
    private static String LAST_DB;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String dbKey = request.getParameter("db");
//        log.info("dbKey:{} DynamicDataSourcekey:{} LAST_DB:{}", dbKey, DynamicDataSource.getNowKey(), LAST_DB);

        if (LAST_DB == null || LAST_DB.isEmpty() || LAST_DB.isBlank()) {
            LAST_DB = DynamicDataSource.getNowKey();
        }

        if (dbKey != null && !dbKey.isBlank()) {
            if (dbKey != DynamicDataSource.getNowKey()) {
                LAST_DB = dbKey;
            }
        }
        DynamicDataSource.setDataSourceKey(LAST_DB);

        try {
            filterChain.doFilter(request, response);
        } finally {
            DynamicDataSource.clear(); // ThreadLocal 정리
        }
    }
}
