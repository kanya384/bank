package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Смена пароля пользователя"
    request {
        method 'PUT'
        url '/user/change-password/1'
        headers {
            contentType(applicationJson())
        }

        body(
                password: "password"
        )
    }


    response {
        status 200

        headers {
            contentType(applicationJson())
        }

        body(
                id: 1,
                login: "user",
                surname: "surname",
                name: "user",
                password: value(anyNonBlankString()),
                email: "test01@mail.ru",
                birth: "20.06.1990"
        )
    }
}