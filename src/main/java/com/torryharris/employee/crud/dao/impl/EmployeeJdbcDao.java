package com.torryharris.employee.crud.dao.impl;

import com.torryharris.employee.crud.dao.Dao;
import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.service.JdbcDbService;
import com.torryharris.employee.crud.util.ConfigKeys;
import com.torryharris.employee.crud.util.PropertyFileUtils;
import com.torryharris.employee.crud.util.QueryNames;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeJdbcDao implements Dao<Employee> {
  private static final Logger LOGGER = LogManager.getLogger(EmployeeJdbcDao.class);
  private JDBCPool jdbcPool;

  public EmployeeJdbcDao(Vertx vertx) {
    jdbcPool = JdbcDbService.getInstance(vertx, getJdbcConnectionOptions(), getPoolOptions()).getJdbcPool();
  }


  @Override
  public Promise<Optional<Employee>> get(String id) {
    Promise<Optional<Employee>> optionalPromise = Promise.promise();
    jdbcPool.preparedQuery(PropertyFileUtils.getQuery(QueryNames.GET_BY_ID))
      .execute(Tuple.of(id))
      .onSuccess(rowSet -> {
        Optional<Employee> employeeOptional;
        if (rowSet.size() == 1) {
          Employee employee = new Employee();
          for (Row row : rowSet) {
            employee.setId(Long.parseLong(id))
              .setName(row.getString("name"))
              .setUsername(row.getString("username"))
              .setDesignation(row.getString("designation"))
              .setSalary(row.getLong("salary"));
            break;
          }
          employeeOptional = Optional.of(employee);
        } else {
          employeeOptional = Optional.empty();
        }
        optionalPromise.tryComplete(employeeOptional);
      })
      .onFailure(optionalPromise::tryFail);
    return optionalPromise;
  }

  @Override
  public Promise<List<Employee>> getAll() {
    Promise<List<Employee>> promise = Promise.promise();
    List<Employee> employees = new ArrayList<>();
    jdbcPool.preparedQuery(PropertyFileUtils.getQuery(QueryNames.GET_ALL_EMPLOYEES))
      .execute()
      .onSuccess(rows -> {
        for (Row row : rows) {
          Employee employee = new Employee();
          employee.setId(row.getLong("id"))
            .setName(row.getString("name"))
            .setDesignation(row.getString("designation"))
            .setSalary(row.getLong("salary"))
            .setUsername(row.getString("username"));

          employees.add(employee);
        }
        promise.tryComplete(employees);
      });
    return promise;
  }

  @Override
  public void save(Employee employee) {
    Promise<List<Employee>> promise1 = Promise.promise();
    List<Employee> employeess = new ArrayList<>();
    Employee emp = new Employee();
    jdbcPool.preparedQuery(PropertyFileUtils.getQuery(QueryNames.INSERT_EMPLOYEE))
      .execute(Tuple.of(employee.getId(), employee.getName(), employee.getDesignation(), employee.getSalary(), employee.getUsername(), employee.getPassword()))
      .onSuccess(rows -> {
        for (Row row : rows) {
          emp.setId(row.getLong("id"))
            .setName(row.getString("name"))
            .setDesignation(row.getString("designation"))
            .setSalary(row.getLong("salary"))
            .setUsername(row.getString("username"))
            .setPassword(row.getString("password"));

          employeess.add(emp);
        }
        promise1.tryComplete(employeess);
      });

  }

  @Override
  public void update(Employee employee) {
    Promise<List<Employee>> promise1 = Promise.promise();
    List<Employee> employeess = new ArrayList<>();
    Employee emp = new Employee();
    jdbcPool.preparedQuery(PropertyFileUtils.getQuery(QueryNames.UPDATE_EMPLOYEE))
      .execute(Tuple.of(employee.getName(), employee.getDesignation(), employee.getSalary(), employee.getId()))
      .onSuccess(rows -> {
        for (Row row : rows) {
          emp.setId(row.getLong("id"))
            .setName(row.getString("name"))
            .setDesignation(row.getString("designation"))
            .setSalary(row.getLong("salary"));
          employeess.add(emp);

        }
        promise1.tryComplete(employeess);
      });

  }

  @Override
  public void delete(String id) {
    Promise<List<Employee>> promise2 = Promise.promise();
    List<Employee> employeess = new ArrayList<>();
    Employee emp = new Employee();
    jdbcPool.preparedQuery(PropertyFileUtils.getQuery(QueryNames.DELETE_EMPLOYEE))
      .execute(Tuple.of(id))
      .onSuccess(rows -> {
        for (Row row : rows) {
          emp.setId(row.getLong("id"))
            .setName(row.getString("name"))
            .setDesignation(row.getString("designation"))
            .setSalary(row.getLong("salary"))
            .setUsername(row.getString("username"));

          employeess.remove(emp);
        }
      });
  }

  @Override
  public Promise<List<Employee>> login(String username, String password) {
    Promise<List<Employee>> promise1 = Promise.promise();
    List<Employee> employeess = new ArrayList<>();
    Employee emp = new Employee();
    jdbcPool.preparedQuery(PropertyFileUtils.getQuery(QueryNames.LOGIN_EMPLOYEE))
      .execute(Tuple.of(username, password, emp.getId(), emp.getName(), emp.getDesignation(), emp.getUsername()))
      .onSuccess(rows -> {
        for (Row row : rows) {

          emp.setId(row.getLong("id"))
            .setName(row.getString("name"))
            .setDesignation(row.getString("designation"))
            .setUsername(row.getString("username"));
          employeess.add(emp);
        }
        promise1.tryComplete(employeess);
      });

    return promise1;
  }

  private JDBCConnectOptions getJdbcConnectionOptions() {
    return new JDBCConnectOptions()
      .setJdbcUrl(PropertyFileUtils.getProperty(ConfigKeys.JDBC_URL))
      .setUser(PropertyFileUtils.getProperty(ConfigKeys.JDBC_USERNAME))
      .setPassword(PropertyFileUtils.getProperty(ConfigKeys.JDBC_PASSWORD))
      .setAutoGeneratedKeys(true);
  }

  private PoolOptions getPoolOptions() {
    return new PoolOptions()
      .setMaxSize(Integer.parseInt(PropertyFileUtils.getProperty(ConfigKeys.POOL_SIZE)));
  }
}
