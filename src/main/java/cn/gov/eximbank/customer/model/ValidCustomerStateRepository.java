package cn.gov.eximbank.customer.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ValidCustomerStateRepository extends CrudRepository<ValidCustomerState, String> {

    ValidCustomerState findByPeriodAndCustomerId(String period, String customerId);

    List<ValidCustomerState> findByPeriod(String period);
}
