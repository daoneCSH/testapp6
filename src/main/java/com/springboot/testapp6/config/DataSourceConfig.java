package com.springboot.testapp6.config;

import com.springboot.testapp6.config.filter.DataSourceFilter;
import com.springboot.testapp6.domain.DatabaseProperties;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            try {
                DataSource ds = entry.getValue().getDataSource();
                ds.getConnection().close();
                targetDataSources.put(key, ds);
                availableKeys.add(key);
                log.info("✅ {} 연결 성공!", key);
                dataSourceKeyList.add(key);
            } catch (Exception e) {
                log.warn("❌ {} 연결 실패: {}", key, e.getMessage());
            }
        }


        if (availableKeys.isEmpty()) {
            throw new IllegalStateException("❌ 사용할 수 있는 DB가 없습니다.");
        }

        // ✅ 사용자에게 선택지 제공
        System.out.println("사용 가능한 DB 목록:");
        for (int i = 0; i < availableKeys.size(); i++) {
            System.out.printf("  [%d] %s\n", i + 1, availableKeys.get(i));
        }

        int selection = -1;
        Scanner scanner = new Scanner(System.in);
        while (selection < 1 || selection > availableKeys.size()) {
            System.out.print("사용할 DB 번호를 입력하세요: ");
            if (scanner.hasNextInt()) {
                selection = scanner.nextInt();
            } else {
                scanner.next(); // 잘못된 입력 무시
            }
        }

        String selectedKey = availableKeys.get(selection - 1);
        System.out.println("✅ 선택된 DB: " + selectedKey);

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
        jpaProps.put("hibernate.hbm2ddl.auto", "update");  // 테이블 자동 생성
        jpaProps.put("hibernate.format_sql", "true");
        try {
            jpaProps.put("hibernate.dialect", multiDatabaseSettings.getDBs().get(key).getPlatform());
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
