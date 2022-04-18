package com.torryharris.employee.crud.dao;

import com.torryharris.employee.crud.model.Employee;
import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import sun.security.util.Password;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

  Promise<Optional<T>> get(String id);

  Promise<List<T>> getAll();

  void save(T t);

  void update(T t);

  void delete(String id);

  Promise<List<T>> login(String username,String password);

}
