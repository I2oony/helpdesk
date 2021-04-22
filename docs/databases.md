# Описание структур используемых объектов

Структура тикета:

```json
{
    "id": ticket_id,
    "title": title,
    "requester": clientId,
    "operator": [user_1, user_2, ...],
    "state": state,
    "messages": [
        {
            "id": 0,
            "from": clientId,
            "text": "Some message text.",
            "ts": unix_timestamp
        }
    ],
    "totalMessages": messagesCount
}
```

state - enum:

- created - только что созданный тикет
- waiting - ожидает ответа оператора
- freeze - ожидает ответа клиента
- closed - закрытый тикет

Структура таблички пользователей системы

| id | username | firstName | lastName | email        | role | pass |
| -- | -------- | --------- | -------- | -------------| ---- | ---- |
| ObjectId | latin chars and digits | имя | фамилия | почтовый ящик | роль пользователя, enum: client, operator, admin | пароль |