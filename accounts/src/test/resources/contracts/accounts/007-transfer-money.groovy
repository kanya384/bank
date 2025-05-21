package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Снять деньги со счета"
    request {
        method 'PUT'
        url '/accounts/transfer-money'
        headers {
            contentType(applicationJson())
        }

        body(
                "fromAccountId": 5,
                "fromMoneyAmount": 10,
                "toAccountId": 6,
                "toMoneyAmount": 10
        )
    }


    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                completed: true
        )
    }
}