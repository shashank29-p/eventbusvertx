package com.torryharris.employee.crud.verticles;

import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.model.Response;
import com.torryharris.employee.crud.model.ResponseCodec;
import com.torryharris.employee.crud.util.ConfigKeys;
import com.torryharris.employee.crud.util.PropertyFileUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;

public class ApiServer extends AbstractVerticle {
  private static final Logger logger = LogManager.getLogger(ApiServer.class);
  private static Router router;
  private Employeeinvoke employeeinvoke;


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    employeeinvoke = new Employeeinvoke(vertx);
    router = Router.router(vertx);

    // Attach a BodyHandler to parse request body and set upload to false
    router.route().handler(BodyHandler.create(false));


    //register a codec for messages
    EventBus eventBus = getVertx().eventBus();
    eventBus.registerDefaultCodec(Response.class, new ResponseCodec());

    //get by id using response codec
    router.get("/response/:id").handler(routingContext -> {
      String id = routingContext.request().getParam("id");
      vertx.eventBus().request("response", id, reply -> {
        if (reply.succeeded()) {
          Response response = (Response) reply.result().body();
          routingContext.response().putHeader("content-type", "application/json").setStatusCode(200).end(response.getResponseBody());
        }
      });
    });


    router.get("/employees")
      .handler(routingContext -> {
//        Employee employee = new Employee();
        vertx.eventBus().request("getall", null, reply -> {
          System.out.println(reply.result().body());
          routingContext.response().putHeader("content-type", "application/json").end(reply.result().body().toString());
        });
      });


    router.get("/employees/:id").produces("*/json")
      .handler(routingContext -> {
        String id = routingContext.request().getParam("id");
        vertx.eventBus().request("getid", id, reply -> {
          if (reply.succeeded()) {
            HttpServerResponse serverResponse = (HttpServerResponse) reply.result().body();
            Response responses = (Response) reply.result().body();
            System.out.println(responses);
            serverResponse.end(responses.toString());
          } else {
            routingContext.response().setStatusCode(401).setStatusMessage("failed");
          }
        });
      });

    router.delete("/employees/:id").produces("*/json")
      .handler(routingContext -> {
        String id = routingContext.request().getParam("id");
        vertx.eventBus().request("deleteid", id, reply -> {
          if (reply.succeeded()) {
            JsonObject json = new JsonObject().put("message", "Employee record deleted");
            routingContext.response().putHeader("content-type", "application/json").end(json.toString());
            System.out.println(reply.result().body());
          } else {
            routingContext.response().setStatusCode(401).setStatusMessage("failed");
          }
        });
      });

    router.post("/employees").handler(BodyHandler.create())
      .handler(routingContext -> {
        Employee employee = Json.decodeValue(routingContext.getBody(), Employee.class);
        vertx.eventBus().request("save", Json.encode(employee), reply -> {
          if (reply.succeeded()) {
            routingContext.response().putHeader("content-type", "application/json").end(reply.result().body().toString());
            System.out.println(reply.result().body().toString());
          } else {
            routingContext.response().setStatusCode(401).setStatusMessage("failed");
          }
        });
      });

    router.put("/employees").consumes("*/json").handler(BodyHandler.create()).produces("*/json")
      .handler(routingContext -> {
        Employee employee = Json.decodeValue(routingContext.getBody(), Employee.class);
        vertx.eventBus().request("update", Json.encode(employee), reply -> {
          if (reply.succeeded()) {
            routingContext.response().putHeader("content-type", "application/json").end(reply.result().body().toString());
            System.out.println(reply.result().body().toString());
          } else {
            routingContext.response().setStatusCode(401).setStatusMessage("failed");
          }
        });

      });


    router.get("/login").consumes("*/json")
      .handler(routingContext -> {
        String user = routingContext.request().getHeader(HttpHeaders.AUTHORIZATION);
        user = user.substring(6);
        String s = new String(Base64.getDecoder().decode(user));
        vertx.eventBus().request("login", Json.encode(s), reply -> {
          if (reply.succeeded()) {
            JsonObject json = new JsonObject().put("message", "Employee login successfull");
            routingContext.response().putHeader("content-type", "application/json").end(json.toString());
//            System.out.println(reply.result().body().toString());
          }
        });
      });


    HttpServerOptions options = new HttpServerOptions().setTcpKeepAlive(true);
    vertx.createHttpServer(options)
      .exceptionHandler(logger::catching)
      .requestHandler(router)
      .listen(Integer.parseInt(PropertyFileUtils.getProperty(ConfigKeys.HTTP_SERVER_PORT)))
      .onSuccess(httpServer -> {
        logger.info("Server started on port {}", httpServer.actualPort());
        startPromise.tryComplete();
      })
      .onFailure(startPromise::tryFail);
  }

  private void sendResponse(RoutingContext routingContext, Response response) {
    response.getHeaders().stream()
      .forEach(entry -> routingContext.response().putHeader(entry.getKey(), entry.getValue().toString()));
    routingContext.response().setStatusCode(response.getStatusCode())
      .end(response.getResponseBody());
  }
}
