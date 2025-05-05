package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Регистрирует пользователя"
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
                "birth": "01.02.1990"
        )
    }


    response {
        status 201

        headers {
            contentType(applicationJson())
        }

        body([
                id      : value(alphaNumeric()),
                name    : "username",
                surname : "surname",
                email   : "test99@mail.ru",
                login   : "login",
                password: value(anyNonBlankString()),
                birth   : "01.02.1990",
        ])
    }
}