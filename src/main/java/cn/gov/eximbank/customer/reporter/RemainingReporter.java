package cn.gov.eximbank.customer.reporter;

import cn.gov.eximbank.customer.model.Customer;
import cn.gov.eximbank.customer.model.CustomerRepository;
import cn.gov.eximbank.customer.model.ValidCustomerState;
import cn.gov.eximbank.customer.model.ValidCustomerStateRepository;
import cn.gov.eximbank.customer.util.CustomerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RemainingReporter {

    private static Logger logger = LoggerFactory.getLogger(RemainingReporter.class);

    private CustomerRepository customerRepository;

    private ValidCustomerStateRepository validCustomerStateRepository;

    public RemainingReporter(CustomerRepository customerRepository, ValidCustomerStateRepository validCustomerStateRepository) {
        this.customerRepository = customerRepository;
        this.validCustomerStateRepository = validCustomerStateRepository;
    }

    public void reportRemainingDetails() {
        for (String period : ReporterUtil.getPeriods()) {
            System.out.println(period);
            reportRemainingDetailWithPeriod(period);
            System.out.println();
        }
    }

    public void reportRemainingDetailWithPeriod(String period) {
        RemainingDetail totalRemainingDetail = new RemainingDetail();
        Map<String, RemainingDetail> branchRemainingDetails = new HashMap<String, RemainingDetail>();
        List<ValidCustomerState> validCustomerStates = validCustomerStateRepository.findByPeriod(period);
        for (ValidCustomerState validCustomerState : validCustomerStates) {
            handleValidCustomerState(validCustomerState, totalRemainingDetail, branchRemainingDetails);
        }

        System.out.println("经营单位;同业客户;主权客户;境内客户;境外客户;未知;小微;中型;大型;集团成员;未知;国有;集体;民营;外资");
        for (String branch : branchRemainingDetails.keySet()) {
            reportCustomerDetail(branch, branchRemainingDetails.get(branch));
        }
        reportCustomerDetail("总计", totalRemainingDetail);
    }

    private void reportCustomerDetail(String branch, RemainingDetail remainingDetail) {
        System.out.println(branch + ";"
                + remainingDetail.getBankRemaining() + ";"
                + remainingDetail.getGovernmentRemaining() + ";"
                + remainingDetail.getInsideRemaining() + ";"
                + remainingDetail.getOutsideRemaining() + ";"
                + remainingDetail.getScaleRemaining()[0] + ";"
                + remainingDetail.getScaleRemaining()[1] + ";"
                + remainingDetail.getScaleRemaining()[2] + ";"
                + remainingDetail.getScaleRemaining()[3] + ";"
                + remainingDetail.getInGroupRemaining() + ";"
                + remainingDetail.getOwnershipRemaining()[0] + ";"
                + remainingDetail.getOwnershipRemaining()[1] + ";"
                + remainingDetail.getOwnershipRemaining()[2] + ";"
                + remainingDetail.getOwnershipRemaining()[3] + ";"
                + remainingDetail.getOwnershipRemaining()[4] + ";");
    }

    private void handleValidCustomerState(ValidCustomerState validCustomerState,
                                          RemainingDetail totalRemainingDetail,
                                          Map<String, RemainingDetail> branchRemainingDetails) {
        if (!branchRemainingDetails.containsKey(validCustomerState.getBranch())) {
            branchRemainingDetails.put(validCustomerState.getBranch(), new RemainingDetail());
        }

        // 判断是同业客户
        if (CustomerUtil.isBankCustomer(validCustomerState.getCustomerName())) {
            totalRemainingDetail.addBankRemaining(validCustomerState.getRemaining());
            branchRemainingDetails.get(validCustomerState.getBranch()).addBankRemaining(validCustomerState.getRemaining());
        }

        // 判断是主权客户
        if (validCustomerState.getBranch().contains("主权")) {
            totalRemainingDetail.addGovernmentRemaining(validCustomerState.getRemaining());
            branchRemainingDetails.get(validCustomerState.getBranch()).addGovernmentRemaining(validCustomerState.getRemaining());
        }

        // 判断境内外客户
        if (validCustomerState.getProvince() == null || validCustomerState.getProvince().equals("")) {
            totalRemainingDetail.addOutsideRemaining(validCustomerState.getRemaining());
            branchRemainingDetails.get(validCustomerState.getBranch()).addOutsideRemaining(validCustomerState.getRemaining());
        }
        else {
            totalRemainingDetail.addInsideRemaining(validCustomerState.getRemaining());
            branchRemainingDetails.get(validCustomerState.getBranch()).addInsideRemaining(validCustomerState.getRemaining());
        }

        // 判断企业规模
        int scaleIndex = ReporterUtil.getScaleIndex(validCustomerState.getScale());
        totalRemainingDetail.addScaleRemaining(scaleIndex, validCustomerState.getRemaining());
        branchRemainingDetails.get(validCustomerState.getBranch())
                .addScaleRemaining(scaleIndex, validCustomerState.getRemaining());

        // 判断是集团成员
        Optional<Customer> customerInDB = customerRepository.findById(validCustomerState.getCustomerId());
        if (customerInDB.isPresent() && CustomerReporter.isInGroup(customerInDB.get())) {
            totalRemainingDetail.addInGroupRemaining(validCustomerState.getRemaining());
            branchRemainingDetails.get(validCustomerState.getBranch()).addInGroupRemaining(validCustomerState.getRemaining());
        }

        // 判断所有制
        String ownership = ReporterUtil.ownershipMapping(validCustomerState.getOwnership());
        int ownershipIndex = ReporterUtil.getOwnershipIndex(ownership);
        totalRemainingDetail.addOwnershipRemaining(ownershipIndex, validCustomerState.getRemaining());
        branchRemainingDetails.get(validCustomerState.getBranch())
                .addOwnershipRemaining(ownershipIndex, validCustomerState.getRemaining());
    }
}
