package com.syberry.mood.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration bean for setting up application.
 */
@Configuration
public class AppConfig {

  /**
   * Defines object mapper bean.
   *
   * @return object mapper
   */
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
