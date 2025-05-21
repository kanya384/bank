import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Получить аккаунт по id"
    request {
        method 'GET'
        url '/accounts/6'
    }


    response {
        status 200

        headers {
            contentType(applicationJson())
        }

        body(
                id: value(anyNumber()),
                userId: 4,
                currency: "RUB",
                amount: value(anyNumber()),
                createdAt: value(isoDate()),
                modifiedAt: value(isoDate())
        )
    }
}