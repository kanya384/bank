package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ограничение по возрасту"
    request {
        method 'POST'
        url '/user/register'
        headers {
            contentType(applicationJson())
        }

        body(
                "name": "username",
                "surname": "surname",
                "email": "test99@mail.ru",
                login: "login",
                password: "password",
                "birth": "01.02.3999"
        )
    }


    response {
        status 400

        headers {
            contentType(applicationJson())
        }

        body(
                "code": "BAD_REQUEST",
                "message": "Ошибка(-и) валидации: birth - Пользователь должен быть совершеннолетним"
        )
    }
}