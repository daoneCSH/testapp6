package com.springboot.testapp6.config;

import com.springboot.testapp6.domain.DatabaseProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "datasources")
public class MultiDatabaseSettings {
    private Map<String, DatabaseProperties> DBs;
}
