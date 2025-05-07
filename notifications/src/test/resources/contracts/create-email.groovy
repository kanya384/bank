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
                "recipient": "test01@mail.ru",
                "subject": "Тестовый заголовок",
                "message": "Текст сообщения"
        )
    }


    response {
        status 201

        headers {
            contentType(applicationJson())
        }

        body(
                id: value(anyNumber()),
                recipient: "test01@mail.ru",
                subject: "Тестовый заголовок",
                message: "Текст сообщения",
                sent: false,
                createdAt: value(isoDateTime()),
                modifiedAt: value(isoDateTime())
        )
    }
}