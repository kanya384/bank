package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Сохранить курсы валют"
    request {
        method 'POST'
        url '/exchange-rates'
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