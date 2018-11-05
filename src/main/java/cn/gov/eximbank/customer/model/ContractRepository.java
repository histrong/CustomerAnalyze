package cn.gov.eximbank.customer.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContractRepository extends CrudRepository<Contract, String> {

    List<Contract> findByCustomerId(String customerId);
}
