package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ищет пользователя по логину"
    request {
        method 'POST'
        url '/user/find-by-login'
        headers {
            contentType(applicationJson())
        }

        body(
                login: "user"
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