package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Положить деньги на счет"
    request {
        method 'PUT'
        url '/accounts/2/put-money'
        headers {
            contentType(applicationJson())
        }

        body(
                "amount": 10.50
        )
    }


    response {
        status 200

        body(
                id: value(anyNumber()),
                userId: 2,
                currency: "RUB",
                amount: 110.5,
                createdAt: value(isoDate()),
                modifiedAt: value(isoDate())
        )
    }
}