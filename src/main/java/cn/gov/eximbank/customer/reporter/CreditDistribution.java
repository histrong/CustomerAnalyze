package cn.gov.eximbank.customer.reporter;

public class CreditDistribution {

    private int[] creditCounts;

    public CreditDistribution() {
        creditCounts = new int[ReporterUtil.credits.length];
    }

    public void addCreditCount(String credit) {
        ++creditCounts[ReporterUtil.adaptToCreditIndex(credit)];
    }

    public int getCreditCount(String credit) {
        return creditCounts[ReporterUtil.adaptToCreditIndex(credit)];
    }


}
