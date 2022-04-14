package com.torryharris.employee.crud.model;

import java.util.Objects;

public class Employee {

  private long id;
  private String name;
  private String designation;
  private double salary;
  private String username;
private String password;


  public long getId() {
    return id;
  }

  public Employee setId(long uid) {
    this.id = uid;
    return this;
  }

  public String getName() {
    return name;
  }

  public Employee setName(String name) {
    this.name = name;
    return this;
  }

  public String getDesignation() {
    return designation;
  }

  public Employee setDesignation(String designation) {
    this.designation = designation;
    return this;
  }

  public double getSalary() {
    return salary;
  }

  public Employee setSalary(double salary) {
    this.salary = salary;
    return this;
  }
  public String getUsername() {return username;}

  public Employee setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getPassword() { return password;}

  public Employee setPassword(String password) {
    this.password = password;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Employee employee = (Employee) o;
    return Objects.equals(id, employee.id) && name.equals(employee.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  @Override
  public String toString() {
    return "Employee{" +
      "uid='" + id + '\'' +
      ", name='" + name + '\'' +
      ", designation='" + designation + '\'' +
      ", salary=" + salary + '\'' +
      ", username=" +username +'\''+
      ",password=" +password+

      '}';
  }
}
