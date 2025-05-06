package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Получить аккаунты пользователя"
    request {
        method 'GET'
        url '/accounts/2'
    }


    response {
        status 200

        headers {
            contentType(applicationJson())
        }

        body([
                [
                        id        : value(anyNumber()),
                        userId    : 2,
                        currency  : "RUB",
                        amount    : 0.0,
                        createdAt : value(isoDate()),
                        modifiedAt: value(isoDate())
                ],
                [
                        id        : value(anyNumber()),
                        userId    : 2,
                        currency  : "CNY",
                        amount    : 0.0,
                        createdAt : value(isoDate()),
                        modifiedAt: value(isoDate())
                ],
                [
                        id        : value(anyNumber()),
                        userId    : 2,
                        currency  : "USD",
                        amount    : 0.0,
                        createdAt : value(isoDate()),
                        modifiedAt: value(isoDate())
                ]
        ])
    }
}