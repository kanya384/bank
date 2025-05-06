package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Заблокированная транзакция перевод себе"
    request {
        method 'POST'
        url '/transfer-self'
        headers {
            contentType(applicationJson())
        }

        body(
                accountId: 1,
                receiverAccountId: 2,
                amount: 1000,
                createdAt: "2025-08-09 21:30"
        )
    }


    response {
        status 200

        headers {
            contentType(applicationJson())
        }

        body(
                isBlocked: true
        )
    }
}