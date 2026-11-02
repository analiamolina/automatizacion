
@PIM2
Feature: Búsqueda de solicitudes de vacaciones
Scenario Outline: Validar búsqueda de solicitudes con filtros combinados

Given que existen solicitudes de vacaciones
And el usuario se encuentra en "Leave List"

When ingresa la fecha inicial "<fechaInicio>"
And ingresa la fecha final "<fechaFin>"
And selecciona el estado "<estado>"
And ingresa el nombre del empleado "<empleado>"
And selecciona al empleado desde el autocompletado
And selecciona la unidad "<unidad>"
And configura empleados anteriores en "<anteriores>"
And presiona el botón "Search"

Then se debe completar la búsqueda sin errores
And se debe visualizar la tabla de resultados
And las solicitudes deben corresponder al empleado "<empleado>"
And las solicitudes deben cumplir el estado "<estado>"
And las solicitudes deben estar dentro del período consultado
And las solicitudes deben corresponder a la unidad "<unidad>"
And los registros deben respetar el filtro de empleados anteriores
And no deben existir registros duplicados
And todas las páginas deben respetar los filtros aplicados

When el usuario presiona el botón "Reset"
Then los filtros deben recuperar sus valores predeterminados
And no deben permanecer los criterios ingresados anteriormente

Examples:
| fechaInicio | fechaFin   | estado           | empleado           | unidad         | anteriores |
| 2026-01-01  | 2026-12-31 | Pending Approval | QA Employee 01     | Engineering    | No         |
| 2026-01-01  | 2026-12-31 | Scheduled        | QA Employee 02     | Engineering    | No         |
| 2026-01-01  | 2026-12-31 | Taken            | QA Employee 03     | Administration | Yes        |
| 2026-01-01  | 2026-12-31 | Rejected         | QA Employee 04     | Sales          | No         |
| 2026-01-01  | 2026-12-31 | Pending Approval | Rebecca Harmony    | Quality Assurance| No         |
| 2026-01-01  | 2026-12-31 | Scheduled        | Rajat Gupta        | Administration | No         |