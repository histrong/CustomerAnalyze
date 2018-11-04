package cn.gov.eximbank.customer.model;

import org.springframework.data.repository.CrudRepository;

public interface ValidCustomerStateRepository extends CrudRepository<ValidCustomerState, String> {

    ValidCustomerState findByPeriodAndCustomerId(String period, String customerId);
}
