package com.example.demo.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.jpa.repository.JpaRepository;

@NoRepositoryBean
public interface BaseRepository<T, Id> extends JpaRepository<T,Id>{
  
}
