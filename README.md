# java-kanban
Task tracker для создания, хранения и управления задачами.
Приложение различает отдельные задачи, а также эпики и их подзадачи.

Реализованы функции:
* Создание, обновление и удаление задач отдельных типов
* Получение задачи определённого типа по id
* Получение списков задач отдельных типов
* Получение списка задач эпика по id
* Удаление всех задач определённого типа
* Просмотр истории в виде 10 последних просмотренных задач

Хранение задач реализовано при помощи класса HashMap.
Управление задачами и просмотр истории осуществляется через интерфейс TaskManager.
Unit-тесты реализованы при помощи фреймворка JUnit 5.

