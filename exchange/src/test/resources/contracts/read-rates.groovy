package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Получить курсы валют"
    request {
        method 'GET'
        url '/exchange-rates'
    }


    response {
        status 200
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