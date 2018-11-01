package cn.gov.eximbank.customer.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, String> {

    List<Customer> findAllByBranch(String branch);
}
