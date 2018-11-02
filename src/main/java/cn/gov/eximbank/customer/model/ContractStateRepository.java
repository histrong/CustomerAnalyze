package cn.gov.eximbank.customer.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContractStateRepository extends CrudRepository<ContractState, String> {

    ContractState findByPeriodAndContractId(String period, String contractId);

    List<ContractState> findByPeriod(String period);

    List<ContractState> findByPeriodAndQualityLevelGreaterThan(String period, int level);
}
