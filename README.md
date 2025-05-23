# Приложение «Банк»

Микросервисное приложение «Банк» написано с использованием Spring Boot и паттернов микросервисной архитектуры.

Список микросервисов:

- Фронт (**front**)   
  веб-приложение с клиентским HTML-интерфейсом
- Сервис аккаунтов (**accounts**)   
  хранит информацию о зарегистрированных аккаунтах и счетах в каждом из них
- Сервис обналичивания денег (**cash**)    
  осуществляет пополнение счёта или снятие денег со счёта
- Сервис перевода денег между счетами (**transfer**)   
  осуществляет перевод денег между счетами одного пользователя и между счетами разных пользователей
- Сервис конвертации валют (**exchange**)   
  хранит информацию о конвертации валюты при её покупке/продаже
- Сервис генерации курсов валют (**exchange-generator**)    
  каждую секунду по расписанию генерирует курсы валют
- Сервис блокировки подозрительных операций (**blocker**)    
  отслеживает подозрительные операции
- Сервис уведомлений (**notifications**)   
  отправляет уведомления на email

Сервисы взаимодействуют через Gateway API. Сервисы авторизуются для выполнения запросов в другие сервисы через OAuth 2.0
(**Keycloak**). Для распределеных конфигов и service discovery используется **Consul**.

## Использование

На устройстве должен быть установлен и запущен Docker. Для сборки и запуска приложения распакуйте архив `storage.zip`
в корне (в архиве персистентные хранилища для keycloak и consul, чтобы в ручную не пришлось их настраивать) и выполните
команду:

```sh
docker compose up -d
```

После того, как все контейнеры поднимутся, будет доступна страница регистрации по адресу:

```
http://localhost/register
```