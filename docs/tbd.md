# TBD — Страница идей и наработок

Данная страница призвана собрать в одном месте все имеющиеся идеи и задачи в беклоге, для более удобного просмотра и отслеживания.

## Основная структура

- [ ] Ядро приложения - сервер на `Java`, реализующий:
    - [ ] Авторизация пользователей
    - [ ] Приём входящих запросов и реализация ответов на них
    - [ ] Логирование событий в системе
- [ ] Базы данных:
    - [ ] База данных пользователей системы (регистрационные данные, роли и т.д.)
    - [ ] База данных сообщений
    - [ ] _База логов_
- [ ] GUI - в зависимости от типа пользователей

## Дополнительные задачи

- Изучить тикет системы
- Попробовать реализовать для авторизации SSO
- Прикрутить Google Translate API (обоснование в том, что для мультиязычных компаний поддержка компании не всегда может говорить свободно на языке клиента)
- Придумать как разграничивать разные запросы у одного пользователя
- Прикрутить возможность оценки сотрудника поддержки
- Реализовать автоматическое распределение поступающих в систему заявок между активными сотрудниками (придётся продумать алгоритм распределения, изучить разные типы очередей, проработать приоритетность заявок)
- Логирование заявок в специальную таблицу базы данных (для упрощения вывода статистики)
- Реализовать плагины для подключения других источников связи (см: [Прочее](#Прочее))
- Добавить возможность создания заявки-задачи (для фиксации обращения за пределами системы)

## Прочее

В текущий момент поддержка производится по разным каналам связи
- Мессенджеры
- Почта
- Хелпдеск (zen)

## Очень классные и сложные идеи

- Научится в CI/CD
- Настроить CI/CD для этого проекта
- Собрать проект в Docker image, дабы упростить установку
- Или выработать любую другую схему размещения конечного продукта на серверах заказчика