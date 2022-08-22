package com.example.api.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.session.FlushMode;
import org.springframework.session.SaveMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

import java.io.IOException;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600, flushMode = FlushMode.IMMEDIATE, saveMode = SaveMode.ON_SET_ATTRIBUTE, redisNamespace = "API:SESSION", cleanupCron = "*/1 * * * * *")
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {

  @Bean
  public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
    return new RedissonConnectionFactory(redisson);
  }

  @Bean(destroyMethod = "shutdown")
  public RedissonClient redisson(@Value("classpath:/redisson.yaml") Resource configFile) throws IOException {
    Config config = Config.fromYAML(configFile.getInputStream());
    return Redisson.create(config);
  }
}
