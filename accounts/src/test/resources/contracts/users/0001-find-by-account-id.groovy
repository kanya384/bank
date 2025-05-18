package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ищет пользователя по id его аккаунта"
    request {
        method 'GET'
        url '/user/find-by-account-id/1'
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
                email: "test01@mail.ru"
        )
    }
}