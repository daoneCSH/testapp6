package com.springboot.testapp6.domain;

import lombok.Data;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

@Data
public class DatabaseProperties {
    String url;
    String user;
    String pw;
    String driver;
    String platform;
    Boolean useJSP;


    public DataSource getDataSource()
    {
        DataSource ds = DataSourceBuilder.create()
                .url(url)
                .username(user)
                .password(pw)
                .driverClassName(driver)
                .build();
        return ds;
    }
}
