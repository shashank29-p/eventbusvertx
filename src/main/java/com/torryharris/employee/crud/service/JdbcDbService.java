package com.torryharris.employee.crud.service;

import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;

public class JdbcDbService {
  private volatile static JdbcDbService jdbcDbService;
  private final JDBCPool jdbcPool;

  private JdbcDbService(Vertx vertx, JDBCConnectOptions connectOptions, PoolOptions poolOptions) {
    jdbcPool = JDBCPool.pool(vertx, connectOptions, poolOptions);
  }

  public static JdbcDbService getInstance(Vertx vertx, JDBCConnectOptions connectOptions, PoolOptions poolOptions) {
    if (jdbcDbService == null) {
      synchronized (JdbcDbService.class) {
        if (jdbcDbService == null) {
          jdbcDbService = new JdbcDbService(vertx, connectOptions, poolOptions);
        }
      }
    }
    return jdbcDbService;
  }

  public JDBCPool getJdbcPool() {
    return jdbcPool;
  }
}
