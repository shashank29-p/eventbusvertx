package com.torryharris.employee.crud.model;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class ResponseCodec implements MessageCodec<Response, Response> {
  @Override
  public void encodeToWire(Buffer buffer, Response response) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.put("statuscode", response.getStatusCode());
    jsonObject.put("responsebody", response.getResponseBody());
    jsonObject.put("header", response.getHeaders());

    String jsonTostring = jsonObject.encode();
    buffer.appendString(jsonTostring);

  }

  @Override
  public Response decodeFromWire(int pos, Buffer buffer) {
    int _pos = pos;
    int length = buffer.getInt(_pos);
    String jsonStr = buffer.getString(_pos+=4, _pos+=length);
    JsonObject contentJson = new JsonObject(jsonStr);
    int statusCode = contentJson.getInteger("statuscode");
    String responsebody = contentJson.getString("responsebody");
    JsonObject headers = contentJson.getJsonObject("headers");

    return new Response(statusCode, responsebody, headers);
  }

  @Override
  public Response transform(Response response) {

    return response;
  }

  @Override
  public String name() {

    return this.getClass().getSimpleName();
  }

  @Override
  public byte systemCodecID() {

    return -1;
  }
}
