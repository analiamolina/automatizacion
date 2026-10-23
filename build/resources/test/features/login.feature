@login @pruebasRegresion
Feature: funcionalidad de login

  #Autor: fcarcamo
  #Fecha creación: 12-12-2025
  @testCase_1 @rutaCritica
  Scenario: Validar login
    Given abrir el navegador en la url "https://practicetestautomation.com/practice-test-login/"
    And ingresar el usuario "student"
    And ingresar la pass "Password123"
    When presiono el boton Submit
    Then se valida el mensaje Logged In Successfully

  @negative @testCase_3
  Scenario: Validar login con datos negativos
    Given abrir el navegador en la url "https://practicetestautomation.com/practice-test-login/"
    And ingresar el usuario "wronguser"
    And ingresar la pass "wrongpass"
    When presiono el boton Submit
    Then se valida el mensaje Your username is invalid!

