package cn.gov.eximbank.customer.model;

import org.springframework.data.repository.CrudRepository;

public interface GroupCustomerRepository extends CrudRepository<GroupCustomer, String> {

    GroupCustomer findByName(String name);
}
