package com.example.dss.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "dssEntityManagerFactory", transactionManagerRef = "dssTransactionManager", basePackages = "com.example.dss.datasource")
public class DataSourceEntityManagerFactory {

  protected String[] packagesToScan() {
    return new String[]{"com.example.dss.datasource"};
  }

  protected Map<String, String> hibernateProperties() {
    final Map<String, String> prop = new HashMap<>();
    prop.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    prop.put("hibernate.ddl-auto", "none");
    return prop;
  }

  @Primary
  @Bean(name = "dssEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean dssEntityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("dssDataSource") HikariDataSource dssDataSource) {
    return builder.dataSource(dssDataSource).packages(packagesToScan()).persistenceUnit("dssDataSource-pu").properties(hibernateProperties()).build();
  }

  @Primary
  @Bean(name = "dssTransactionManager")
  public PlatformTransactionManager dssTransactionManager(@Qualifier("dssEntityManagerFactory") EntityManagerFactory dssEntityManagerFactory) {
    return new JpaTransactionManager(dssEntityManagerFactory);
  }
}
