package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Обновление информации о пользователе"
    request {
        method 'PUT'
        url '/user/1'
        headers {
            contentType(applicationJson())
        }

        body(
                surname: "new-surname",
                name: "new-user",
                email: "test02@mail.ru",
                birth: "20.06.1900"
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
                surname: "new-surname",
                name: "new-user",
                password: value(anyNonBlankString()),
                email: "test02@mail.ru",
                birth: "20.06.1900"
        )
    }
}