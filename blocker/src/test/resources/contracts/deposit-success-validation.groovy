package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешная валидация транзакции депозит"
    request {
        method 'POST'
        url '/deposit'
        headers {
            contentType(applicationJson())
        }

        body(
                accountId: 1,
                amount: 100,
                createdAt: "2025-08-09 10:30"
        )
    }


    response {
        status 200

        headers {
            contentType(applicationJson())
        }

        body(
                isBlocked: false
        )
    }
}