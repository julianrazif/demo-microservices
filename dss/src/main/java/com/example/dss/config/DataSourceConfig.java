package com.example.dss.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

  @Primary
  @Bean(name = "dssDataSourceProperties")
  @ConfigurationProperties(prefix = "app.datasource")
  public DataSourceProperties dssDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Primary
  @Bean(name = "dssDataSource")
  @ConfigurationProperties(prefix = "hikari.datasource")
  public HikariDataSource dssDataSource(@Qualifier("dssDataSourceProperties") DataSourceProperties dssDataSourceProperties) {
    return dssDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
  }
}
