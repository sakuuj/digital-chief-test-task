# Тестовое задание Java Trainee Digital Chief

### Что выполнено
Все, кроме дополнительного задания.
Тестовые данные: [app/src/main/resources/liquibase/changelog_0_1/changeset/insert_values.sql](app/src/main/resources/liquibase/changelog_0_1/changeset/insert_values.sql) 

### Rest Api 
По адресу http://localhost:8080/swagger-ui.html

### Инструкция по запуску
1. `./gradlew :app:bootJar`
2. `docker compose up`

### Задание
```
Обновления
v2 - п5: поиск должен осуществляться в эластике


Тестовое задание v2
Направление Поиск - Elasticsearch/Spring

- Неточности в требованиях трактовать на своё усмотрение, фиксировать отдельным списком уточнений.
- Технологии не указанные в задании добавлять на свой выбор (Maven\Gradle, Hibernate, Servlet/React и тд)

Создать гит репозиторий, всю работу фиксировать в нем.

Развернуть локальное окружение через docker compose, включающий образ базы данных (PostgreSQL), поискового движка (Elasticsearch).
В БД создать схему, состоящую из двух сущностей продукт и ску, со связью один ко многим (один продукт имеет несколько дочерних ску)
добавить в модели вариативные поля по типу (число, дата, текст и т.д.)
наполнить бд данными (например онлайн генераторы данных) продукт~20, ску~50
Создать Spring Boot приложение, подключить БД и эластик
эластик подключается клиентом самого эластика, стартер от спринга не использовать
Реализовать службу[1] загрузки данных из базы в эластик
должен создаваться один индекс, в котором будет информация по обоим моделям
фильтровать продукты по признаку, например active или startDate
добавить возможность запуска службы через вызов апи
предусмотреть многократный запуск службы
Реализовать апи и сервис поиска в эластике
результат - список продуктов
входной параметр - поисковое слово, должно участвовать в поиске по атрибутам товара\ску (например по имени и описанию)

[1] - под службой понимается абстрактный юнит бизнес логики -  задача\функционал, выраженный через java код.


Критерий приемки
Проект можно склонировать с удаленного гит репозитория, запустить по инструкции, получить результат поиска через апи.





Дополнительно (необязательно)
Реализовать фильтр (без входного параметра со стороны апи), который включается через флаг в конфигурации. Фильтр должен включать в себя условие по двум атрибутам ску, например цвет и доступность, при этом возвращаться из сервиса должны продукты, с конкретными ску, удовлетворяющие фильтру.

Пример
Продукт1 со ску1 и ску2
ску1
цвет: красный
доступность: есть

ску2
цвет: зеленый
доступность: нет

по фильтру - доступный красный, сервис возвращает продукт1 только со ску1
```