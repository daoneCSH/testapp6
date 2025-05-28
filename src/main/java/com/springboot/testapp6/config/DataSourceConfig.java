package com.springboot.testapp6.config;

import com.springboot.testapp6.domain.DatabaseProperties;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.springboot.testapp6.repository",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class DataSourceConfig {
    private final MultiDatabaseSettings multiDatabaseSettings;

    @Autowired
    private InitializerDatabase initializer;

    @Value("${feature.interactive-db-select:false}")
    private boolean interactiveMode;

    @Value("${datasource.active-key:mariadb}")
    private String activeKey;

    @Getter
    private static Map<String, DataSource> dataSourceMap = new HashMap<>();

    @Getter
    private static List<String> dataSourceKeyList = new ArrayList<>();

    @Primary
    @Bean
    public DataSource dynamicDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        List<String> availableKeys = new ArrayList<>();

        for (Map.Entry<String, DatabaseProperties> entry : multiDatabaseSettings.getDBs().entrySet()) {
            String key = entry.getKey();
            Boolean isConnet = false;
            try {
                DataSource ds = entry.getValue().getDataSource();
                try (Connection conn = ds.getConnection()) {
                    isConnet = true;
                    log.info("✅ {} 연결 성공!", key);
                    try {
                        initializer.tableExists(conn, key, "tb_user");
                    } catch (Exception e) {
                        log.error("❌ {} 테이블 확인 실패! e:{}", key, e.getMessage());
                    }

                    try {
                        initializer.functionExists(conn, key);
                    } catch (Exception e) {
                        log.error("❌ {} 함수 확인 실패! e:{}", key, e.getMessage());
                    }
                } catch (Exception e) {
                    log.warn("❌ {} 연결 실패: {}", key, e.getMessage());
                }

                if (isConnet) {
                    ds.getConnection().close();
                    targetDataSources.put(key, ds);
                    dataSourceMap.put(key, ds);
                    availableKeys.add(key);

                    dataSourceKeyList.add(key);
                }
            } catch (Exception e) {
                log.warn("❌ {} 연결 실패: {}", key, e.getMessage());
            }
        }

        if (availableKeys.isEmpty()) {
            log.error("❌ 사용할 수 있는 DB가 없습니다. 앱을 종료합니다.");
            // 사용할 수 있는 DB가 없기 때문에 종료
            System.exit(1);
        }

        String selectedKey;
        if (availableKeys.contains(activeKey)) {
            selectedKey = activeKey;
            log.info("✅ 선택된 DB:{}", selectedKey);
        } else {
            selectedKey = availableKeys.get(0);
            log.info("❌ 사용불가 DB:{}", activeKey);
            log.info("✅ 대체 DB:{}", selectedKey);
        }

        DynamicDataSource.setDataSourceKey(selectedKey);

        DynamicDataSource routingDataSource = new DynamicDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(targetDataSources.get(selectedKey));
        return routingDataSource;
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dynamicDataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dynamicDataSource);
        emf.setPackagesToScan("com.springboot.testapp6.domain"); // 엔티티 경로
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        String key = DynamicDataSource.getNowKey();

        Map<String, Object> jpaProps = new HashMap<>();

        boolean useJPA = multiDatabaseSettings.getDBs().get(key).getUseJSP();
        if (useJPA) {
            jpaProps.put("hibernate.hbm2ddl.auto", "update");  // 테이블 자동 생성
            jpaProps.put("hibernate.format_sql", "true");
        } else {
            jpaProps.put("hibernate.hbm2ddl.auto", "none");  // 테이블 자동 생성
            jpaProps.put("hibernate.format_sql", "false");
            jpaProps.put("hibernate.id.new_generator_mappings", "false");
        }
        try {
            String platform = multiDatabaseSettings.getDBs().get(key).getPlatform();
            if (!platform.isEmpty()) {
                jpaProps.put("hibernate.dialect", platform);
            }
        } catch (Exception e) {
            log.error("❌ {} dialect 추가 실패:{}",key, e.getMessage());
        }

        emf.setJpaPropertyMap(jpaProps);

        return emf;
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
