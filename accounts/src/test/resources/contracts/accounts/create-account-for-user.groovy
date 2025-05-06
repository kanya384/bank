package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Создать счет для пользователя"
    request {
        method 'POST'
        url '/accounts'
        headers {
            contentType(applicationJson())
        }

        body(
                "userId": 1,
                "currency": "USD"
        )
    }


    response {
        status 201

        headers {
            contentType(applicationJson())
        }

        body(
                id: value(anyNumber()),
                userId: 1,
                currency: "USD",
                amount: 0.0,
                createdAt: value(isoDate()),
                modifiedAt: value(isoDate())
        )
    }
}