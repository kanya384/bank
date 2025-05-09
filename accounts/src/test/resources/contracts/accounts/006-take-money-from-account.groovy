package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Снять деньги со счета"
    request {
        method 'PUT'
        url '/accounts/3/take-money'
        headers {
            contentType(applicationJson())
        }

        body(
                "amount": 0.50
        )
    }


    response {
        status 200

        body(
                id: value(anyNumber()),
                userId: 2,
                currency: "USD",
                amount: 10,
                createdAt: value(isoDate()),
                modifiedAt: value(isoDate())
        )
    }
}