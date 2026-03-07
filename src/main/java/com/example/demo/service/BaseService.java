package com.example.demo.service;

import java.util.List;

public interface BaseService<T, Id> {
  T create(T entity);
  T getById(Id id);
  T update(Id id, T entity);
  void delete(Id id);
  List<T> getAll();
}
