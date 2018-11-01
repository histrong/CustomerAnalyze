package cn.gov.eximbank.customer.model;

import org.springframework.data.repository.CrudRepository;

public interface ContractStateRepository extends CrudRepository<ContractState, String> {

    ContractState findByPeriodAndContractId(String period, String contractId);
}
