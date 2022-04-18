package com.torryharris.employee.crud.verticles;

import com.torryharris.employee.crud.dao.Dao;
import com.torryharris.employee.crud.dao.impl.EmployeeJdbcDao;
import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.model.Response;
import com.torryharris.employee.crud.util.Utils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Employeeinvoke extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(Employeeinvoke.class);
  private final Dao<Employee> employeeDao;

  public Employeeinvoke(Vertx vertx) {
    employeeDao = new EmployeeJdbcDao(vertx);
  }

  @Override
  public void start(Promise<Void> promise) throws Exception {

    vertx.eventBus().consumer("getid", message -> {
      String id = ((String) message.body());
      employeeDao.get(id)
        .future()
        .onSuccess(employees -> {
          message.reply(employees);
        });
    });


    vertx.eventBus().consumer("getall", message -> {
      employeeDao.getAll()
        .future()
        .onSuccess(employees -> {
          message.reply(Json.encode(employees));
        });
    });


    vertx.eventBus().consumer("deleteid", message -> {
      String id = ((String) message.body());
      employeeDao.delete(id);
      message.reply(Json.encode(id));
      promise.tryComplete();
    });

    vertx.eventBus().consumer("save", message -> {
      String msg = (String) message.body();
      Employee emp = (Json.decodeValue(msg, Employee.class));
      employeeDao.save(emp);
      message.reply(msg);
    });


    vertx.eventBus().consumer("update", message -> {
      String msg = (String) message.body();
      Employee empl = (Json.decodeValue(msg, Employee.class));
      employeeDao.update(empl);
      message.reply(msg);

    });
    vertx.eventBus().consumer("login", message -> {
      String msg = (String) message.body();
      String[] arr = msg.split(":");
      for (String name : arr) {
        System.out.println(name);
      }
      String username = arr[0];
      String password = arr[1];
      LOGGER.info(username);
      LOGGER.info(password);
      employeeDao.login(username, password);
      message.reply(msg);
    });

    vertx.eventBus().consumer("response", message -> {
      String id = (String) message.body();
      Response response = new Response();
      employeeDao.get(id)
        .future()
        .onSuccess(emp -> {
          if (emp.isPresent()) {
            Employee employee = emp.get();
            response.setStatusCode(200).setResponseBody(Json.encode(employee));

          } else {
            response.setStatusCode(400).setResponseBody(Utils.getErrorResponse("Employee Id not found").encode());
          }
          message.reply(response);
        })
        .onFailure(throwable -> {
            LOGGER.catching(throwable);
          }
        );
    });

  }
}
