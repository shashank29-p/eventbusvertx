package com.torryharris.employee.crud.model;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

public class Response {
  private int statusCode;
  private String responseBody;
  private JsonObject headers;

  public Response() {
    headers = new JsonObject().put(HttpHeaders.CONTENT_TYPE.toString(), "application/json");
  }

  public Response(int statusCode, String responsebody, String headers) {
    this.statusCode=statusCode;
    this.responseBody=responsebody;

  }


  public int getStatusCode() {
    return statusCode;
  }

  public Response setStatusCode(int statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  public String getResponseBody() {
    return responseBody;
  }

  public Response setResponseBody(String responseBody) {
    this.responseBody = responseBody;
    return this;
  }

  public JsonObject getHeaders() {
    return headers;
  }

  public Response setHeaders(JsonObject headers) {
    this.headers = headers;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Response response = (Response) o;
    return statusCode == response.statusCode && Objects.equals(responseBody, response.responseBody)
      && Objects.equals(headers, response.headers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(statusCode, responseBody, headers);
  }
}
