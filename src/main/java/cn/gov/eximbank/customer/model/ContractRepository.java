package cn.gov.eximbank.customer.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContractRepository extends CrudRepository<Contract, String> {

    Contract findContractById(String id);

    List<Contract> findAllByCustomerId(String customer_id);
}
