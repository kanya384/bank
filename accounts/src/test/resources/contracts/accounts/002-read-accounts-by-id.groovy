package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Получить аккаунт по id"
    request {
        method 'GET'
        url '/accounts/2'
    }


    response {
        status 200

        headers {
            contentType(applicationJson())
        }

        body(
                id: value(anyNumber()),
                userId: 2,
                currency: "RUB",
                amount: 100.0,
                createdAt: value(isoDate()),
                modifiedAt: value(isoDate())
        )
    }
}