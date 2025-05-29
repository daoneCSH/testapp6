package com.springboot.testapp6.config;

import com.springboot.testapp6.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;

@Slf4j
@Component
public class InitializerDatabase {

    public void tableExists(Connection conn, String databaseName, String tableName) {
        try {
            // table_exists.sql 파일 로드
            String sql = loadSqlFromFile("/db/" + databaseName + "/table_exists.sql");

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, tableName);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getInt(1) > 0) {
                            log.info("✅ {} 테이블이 존재합니다.", databaseName);
                        } else {
                            log.info("❌ {} 테이블이 존재하지 않습니다.", databaseName);
                            createTable(conn, databaseName);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("❌ SQL 파일 로드 중 오류: {}", e.getMessage());
        } catch (SQLException e) {
            log.error("❌ {} 테이블 존재 여부 확인 중 오류: {}", databaseName, e.getMessage());
        }
    }


    private void createTable(Connection conn, String databaseName) {
        try {
            String tableSql = loadSqlFromFile("/db/" + databaseName + "/create_table.sql");
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(tableSql);
                log.info("✅ {} 테이블이 생성되었습니다.", databaseName);
            }
        } catch (IOException e) {
            log.error("❌ SQL 파일 로드 중 오류: {}", e.getMessage());
        } catch (SQLException e) {
            log.error("❌ {} 테이블 생성 중 오류: {}", databaseName, e.getMessage());
        }
    }

    public void ensureTableExistsOrRecreate() throws SQLException {
        String databaseName = DynamicDataSource.getNowKey();
        log.warn("🗑️ {} 테이블 삭제 시작", databaseName);

        DataSource ds = DataSourceConfig.getDataSourceMap().get(databaseName);
        try (Connection conn = ds.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                // 테이블 삭제 (DROP TABLE)
                String dropSql = "DROP TABLE IF EXISTS " + User.GetTableName;
                stmt.executeUpdate(dropSql);
                log.warn("🗑️ {} 테이블 삭제 완료", databaseName);

                createTable(conn, DynamicDataSource.getNowKey());

            } catch (SQLException dropEx) {
                log.error("🚨 테이블 삭제 실패: {}", dropEx.getMessage());
            }
        } catch (Exception e) {
            log.error("❌ {} 테이블 삭제 중 오류: {}", databaseName, e.getMessage());
        }

    }




    public void functionExists(Connection conn, String databaseName) {
        try {
            // function_exists.sql 파일 로드
            String sql = loadSqlFromFile("/db/" + databaseName + "/function_exists.sql");

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "db_encrypt_full");

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getInt(1) > 0) {
                            log.info("✅ {} 함수가 존재합니다.", databaseName);
                        } else {
                            log.info("❌ {} 함수가 존재하지 않습니다.", databaseName);
                            createFunction(conn, databaseName);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("❌ SQL 파일 로드 중 오류: {}", e.getMessage());
        } catch (SQLException e) {
            log.error("❌ {} 함수 존재 여부 확인 중 오류: {}", databaseName, e.getMessage());
        }
    }

    private void createFunction(Connection conn, String databaseName) {
        try {
            // 함수 생성 SQL 파일 경로는 /mysql/functions/함수명.sql 형식으로 가정
            String functionSql = loadSqlFromFile("/db/" + databaseName + "/functions.sql");
            String[] statements = functionSql.split("(?<=;)\\s*(?=CREATE|$)");
            try (Statement stmt = conn.createStatement()) {
                for (String statement : statements) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        log.info("📄 함수 실행: \n{}", trimmed);
                        stmt.execute(trimmed);
                    }
                }
            }

        } catch (IOException e) {
            log.error("❌ SQL 파일 로드 중 오류: {}", e.getMessage());
        } catch (SQLException e) {
            log.error("❌ {} 함수 생성 중 오류: {}", databaseName, e.getMessage());
        }
    }


    private String loadSqlFromFile(String path) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new FileNotFoundException("SQL 파일이 존재하지 않음: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}