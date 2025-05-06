package contracts.users


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Удалить счет по ID"
    request {
        method 'DELETE'
        url '/accounts/1'
    }


    response {
        status 200
    }
}