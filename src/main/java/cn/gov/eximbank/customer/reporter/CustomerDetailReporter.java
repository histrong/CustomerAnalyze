package cn.gov.eximbank.customer.reporter;

import cn.gov.eximbank.customer.model.*;
import cn.gov.eximbank.customer.util.CustomerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomerDetailReporter {

    private static Logger logger = LoggerFactory.getLogger(CustomerDetailReporter.class);

    private CustomerRepository customerRepository;

    private GroupCustomerRepository groupCustomerRepository;

    private ValidCustomerStateRepository validCustomerStateRepository;

    public CustomerDetailReporter(CustomerRepository customerRepository,
                                  GroupCustomerRepository groupCustomerRepository,
                                  ValidCustomerStateRepository validCustomerStateRepository) {
        this.customerRepository = customerRepository;
        this.groupCustomerRepository = groupCustomerRepository;
        this.validCustomerStateRepository = validCustomerStateRepository;
    }

    public void reportValidCustomer(String period) {
        List<ValidCustomerState> validCustomerStates = validCustomerStateRepository.findByPeriod(period);
        File file = new File("data/result.txt");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write("客户编号".getBytes());
            outputStream.write('\n');
            for (ValidCustomerState validCustomerState : validCustomerStates) {
                outputStream.write(validCustomerState.getCustomerId().getBytes());
                outputStream.write('\n');
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void reportCustomerDetails() {
        for (String period : ReporterUtil.getPeriods()) {
            System.out.println(period);
            reportCustomerDetailsWithPeriod(period);
            System.out.println();
        }
    }

    public void reportCustomerDetailsWithPeriod(String period) {
        CustomerDetail totalCustomerDetail = new CustomerDetail();
        Map<String, CustomerDetail> branchCustomerDetails = new HashMap<String, CustomerDetail>();
        List<ValidCustomerState> validCustomerStates = validCustomerStateRepository.findByPeriod(period);
        for (ValidCustomerState validCustomerState : validCustomerStates) {
            handleValidCustomerState(validCustomerState, totalCustomerDetail, branchCustomerDetails);
        }

        System.out.println("经营单位;同业客户;主权客户;境内客户;境外客户;未知;小微;中型;大型;集团成员;未知;国有;集体;民营;外资");
        for (String branch : branchCustomerDetails.keySet()) {
            reportCustomerDetail(branch, branchCustomerDetails.get(branch));
        }
        reportCustomerDetail("总计", totalCustomerDetail);
    }

    private void reportCustomerDetail(String branch, CustomerDetail customerDetail) {
        System.out.println(branch + ";"
                + customerDetail.getBankCustomer() + ";"
                + customerDetail.getGovernmentCustomer() + ";"
                + customerDetail.getInsideCustomer() + ";"
                + customerDetail.getOutsideCustomer() + ";"
                + customerDetail.getScaleCustomer()[0] + ";"
                + customerDetail.getScaleCustomer()[1] + ";"
                + customerDetail.getScaleCustomer()[2] + ";"
                + customerDetail.getScaleCustomer()[3] + ";"
                + customerDetail.getInGroupCustomer() + ";"
                + customerDetail.getOwnershipCustomer()[0] + ";"
                + customerDetail.getOwnershipCustomer()[1] + ";"
                + customerDetail.getOwnershipCustomer()[2] + ";"
                + customerDetail.getOwnershipCustomer()[3] + ";"
                + customerDetail.getOwnershipCustomer()[4] + ";");
    }

    private void handleValidCustomerState(ValidCustomerState validCustomerState,
                                          CustomerDetail totalCustomerDetail,
                                          Map<String, CustomerDetail> branchCustomerDetails) {
        if (!branchCustomerDetails.containsKey(validCustomerState.getBranch())) {
            branchCustomerDetails.put(validCustomerState.getBranch(), new CustomerDetail());
        }

        // 判断是同业客户
        if (CustomerUtil.isBankCustomer(validCustomerState.getCustomerName())) {
            totalCustomerDetail.increaseBankCustomer();
            branchCustomerDetails.get(validCustomerState.getBranch()).increaseBankCustomer();
        }

        // 判断是主权客户
        if (validCustomerState.getBranch().contains("主权")) {
            totalCustomerDetail.increaseGovernmentCustomer();
            branchCustomerDetails.get(validCustomerState.getBranch()).increaseGovernmentCustomer();
        }

        // 判断境内外客户
        if (validCustomerState.getProvince() == null || validCustomerState.getProvince().equals("")) {
            totalCustomerDetail.increaseOutsideCustomer();
            branchCustomerDetails.get(validCustomerState.getBranch()).increaseOutsideCustomer();
        }
        else {
            totalCustomerDetail.increaseInsideCustomer();
            branchCustomerDetails.get(validCustomerState.getBranch()).increaseInsideCustomer();
        }

        // 判断企业规模
        int scaleIndex = ReporterUtil.getScaleIndex(validCustomerState.getScale());
        totalCustomerDetail.increaseScaleCustomer(scaleIndex);
        branchCustomerDetails.get(validCustomerState.getBranch()).increaseScaleCustomer(scaleIndex);

        // 判断是集团成员
        Optional<Customer> customerInDB = customerRepository.findById(validCustomerState.getCustomerId());
        if (customerInDB.isPresent() && CustomerReporter.isInGroup(customerInDB.get())) {
            totalCustomerDetail.increaseInGroupCustomer();
            branchCustomerDetails.get(validCustomerState.getBranch()).increaseInGroupCustomer();
        }

        // 判断所有制
        String ownership = ReporterUtil.ownershipMapping(validCustomerState.getOwnership());
        int ownershipIndex = ReporterUtil.getOwnershipIndex(ownership);
        totalCustomerDetail.increaseOwnershipCustomer(ownershipIndex);
        branchCustomerDetails.get(validCustomerState.getBranch()).increaseOwnershipCustomer(ownershipIndex);
    }
}
