package com.torryharris.employee.crud.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ResourceBundle;

public class PropertyFileUtils {
  private static final Logger LOGGER = LogManager.getLogger(PropertyFileUtils.class);
  private static final ResourceBundle queriesBundle = ResourceBundle.getBundle("queries");
  private static final ResourceBundle propertiesBundle = ResourceBundle.getBundle("application");

  /**
   * Method to retrieve the query for the given key from
   * the queries.properties file
   *
   * @param queryConst name of the query
   * @return query for the given name
   */
  public static String getQuery(String queryConst) {
    LOGGER.trace("getQuery Invoked, queryName {}", queryConst);
    return queriesBundle.getString(queryConst);
  }

  /**
   * Method to retrieve the property for the given key from
   * the application.properties file
   *
   * @param msgKey-key for which message has to be retrieved
   * @return message for given key
   */
  public static String getProperty(String msgKey) {
    LOGGER.trace("getMessage Invoked, msgKey:{}", msgKey);
    return propertiesBundle.getString(msgKey);
  }
}
