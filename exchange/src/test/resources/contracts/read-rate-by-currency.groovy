package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Получить курс по валюте"
    request {
        method 'GET'
        url '/exchange-rates/RUB'
    }


    response {
        status 200
        body(
                currency: "RUB",
                rate: value(anyDouble()),
        )
    }
}