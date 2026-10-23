@PIM
Feature: Búsqueda y filtros de la lista de empleados

  Background:
    Given abrir el navegador en la url "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login"
    And que el usuario inicia sesión en OrangeHRM con el usuario "Admin" y la contraseña "admin123"
    And que el usuario ingresa al módulo PIM

  @PIM @TC-001
  Scenario Outline: Buscar empleado por nombre

    When el usuario ingresa "<employeeName>" en el campo Nombre del empleado de PIM
    And hace clic en Buscar
    Then se deben mostrar los resultados de la búsqueda
    And cada resultado debe contener "<expectedName>"

    Examples:
      | employeeName | expectedName |
      | Amelia       | Amelia       |

  @PIM @TC-002
  Scenario Outline: Búsqueda de empleado por ID de empleado
    When el usuario introduce el ID de empleado "<employeeId>"
    And hace clic en Buscar
    Then el sistema debería devolver "<expectedRecords>" registros
    Examples:
      | employeeId | expectedRecords |
      | 0001       | 1               |
      | 999999     | 0               |

  @PIM @TC-004
  Scenario Outline: Buscar empleados por estado laboral
    When el usuario selecciona el estado laboral "<status>"
    And hace clic en Buscar
    Then cada resultado debe tener el estado laboral "<status>"
    Examples:
      | status                |
      | Freelance             |
      | Full-Time Contract    |
      | Full-Time Permanent   |
      | Full-Time Probation   |
      | Part-Time Contract    |


  @PIM @TC-005
  Scenario Outline: Buscar empleados utilizando todos los filtros PIM disponibles
    When el usuario ingresa "<employeeName>" en el campo Nombre del empleado de PIM
    And el usuario introduce el ID de empleado "<employeeId>"
    And el usuario selecciona el estado laboral "<status>"
    And el usuario selecciona la opción de inclusión "<includeOption>"
    And el usuario selecciona el puesto de trabajo "<jobTitle>"
    And el usuario selecciona la subunidad "<subUnit>"
    And el usuario introduce el nombre del supervisor "<supervisor>"
    And hace clic en Buscar
    Then todos los resultados deben coincidir con empleado "<employeeName>", ID "<employeeId>", estado "<status>", puesto "<jobTitle>" y subunidad "<subUnit>"
    Examples:
      | employeeName | employeeId | status                | includeOption              | jobTitle          | subUnit | supervisor          |
      | Amelia      | 0001      | Full-Time Permanent   | Current Employees Only     | Account Assistant        | Administration | John Smith     |
      | James       | 0002      | Full-Time Contract    | Current and Past Employees | Automation Engineer L000 | Engineering  | Peter Anderson |
      | Linda       | 0003      | Part-Time Contract    | Past Employees Only        | Automation Tester        | Development  | Linda Brown    |
