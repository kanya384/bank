package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешная валидация транзакции перевод между своими счетами"
    request {
        method 'POST'
        url '/transfer-self'
        headers {
            contentType(applicationJson())
        }

        body(
                accountId: 1,
                receiverAccountId: 2,
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