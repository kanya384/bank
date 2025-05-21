package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Получить список курсов валют"
    request {
        method 'GET'
        url '/exchange-rates'
    }


    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([
                [
                        currency: "RUB",
                        rate    : value(anyDouble()),
                ],
                [
                        currency: "USD",
                        rate    : value(anyDouble()),
                ],
                [
                        currency: "CNY",
                        rate    : value(anyDouble()),
                ],
        ])
    }
}