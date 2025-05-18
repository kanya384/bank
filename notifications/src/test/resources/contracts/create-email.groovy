package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Создать email уведомление"
    request {
        method 'POST'
        url '/notification/email'
        headers {
            contentType(applicationJson())
        }

        body(
                "recipient": anyEmail(),
                "subject": anyNonEmptyString(),
                "message": anyNonEmptyString()
        )
    }


    response {
        status 201

        headers {
            contentType(applicationJson())
        }

        body(
                id: value(anyNumber()),
                recipient: anyEmail(),
                subject: anyNonEmptyString(),
                message: anyNonEmptyString(),
                sent: false,
                createdAt: value(isoDateTime()),
                modifiedAt: value(isoDateTime())
        )
    }
}