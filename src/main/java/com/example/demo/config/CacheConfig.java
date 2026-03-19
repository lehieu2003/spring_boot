package com.example.demo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

  public static final String USERS_BY_ID_CACHE = "usersById";
  public static final String USERS_BY_EMAIL_CACHE = "usersByEmail";
  public static final String USERS_ALL_CACHE = "usersAll";
  public static final String USERS_ORDERED_CACHE = "usersOrderedByName";
  public static final String USERS_BY_NAME_CACHE = "usersByNameSearch";

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager(
        USERS_BY_ID_CACHE,
        USERS_BY_EMAIL_CACHE,
        USERS_ALL_CACHE,
        USERS_ORDERED_CACHE,
        USERS_BY_NAME_CACHE
    );

    cacheManager.setCaffeine(Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(1_000));

    return cacheManager;
  }
}
