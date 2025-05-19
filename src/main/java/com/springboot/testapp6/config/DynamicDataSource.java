package com.springboot.testapp6.config;

import com.springboot.testapp6.config.filter.DataSourceFilter;
import lombok.Getter;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    @Getter
    private static String nowKey = "default";

    public static void setDataSourceKey(String key) {
        nowKey = key;
        DataSourceFilter.setDB(nowKey);
        contextHolder.set(key);
    }

    public static void clear() {
        contextHolder.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return contextHolder.get();
    }
}
