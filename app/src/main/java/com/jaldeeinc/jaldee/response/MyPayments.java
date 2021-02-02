package com.jaldeeinc.jaldee.response;

public class MyPayments {

    int id;
    String requestJson;
    String customerName;
    String customerPhone;
    String customerEmail;
    String amount;
    int custId;
    String paymentMode;
    String ynwUuid;
    int accountId;
    String paymentRefId;
    String status;
    String paymentGateway;
    String acceptPaymentBy;
    String paymentOn;
    String refundableAmount;
    String accountName;
    String txnType;
    String paymentPurpose;
    String gatewayCommission;
    String jaldeeCommission;
    String settlementAmount;
    boolean testTransaction;
    String paymentModeName;
    String accountEncodedId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getYnwUuid() {
        return ynwUuid;
    }

    public void setYnwUuid(String ynwUuid) {
        this.ynwUuid = ynwUuid;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getPaymentRefId() {
        return paymentRefId;
    }

    public void setPaymentRefId(String paymentRefId) {
        this.paymentRefId = paymentRefId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public String getAcceptPaymentBy() {
        return acceptPaymentBy;
    }

    public void setAcceptPaymentBy(String acceptPaymentBy) {
        this.acceptPaymentBy = acceptPaymentBy;
    }

    public String getPaymentOn() {
        return paymentOn;
    }

    public void setPaymentOn(String paymentOn) {
        this.paymentOn = paymentOn;
    }

    public String getRefundableAmount() {
        return refundableAmount;
    }

    public void setRefundableAmount(String refundableAmount) {
        this.refundableAmount = refundableAmount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getPaymentPurpose() {
        return paymentPurpose;
    }

    public void setPaymentPurpose(String paymentPurpose) {
        this.paymentPurpose = paymentPurpose;
    }

    public String getGatewayCommission() {
        return gatewayCommission;
    }

    public void setGatewayCommission(String gatewayCommission) {
        this.gatewayCommission = gatewayCommission;
    }

    public String getJaldeeCommission() {
        return jaldeeCommission;
    }

    public void setJaldeeCommission(String jaldeeCommission) {
        this.jaldeeCommission = jaldeeCommission;
    }

    public String getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(String settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public boolean isTestTransaction() {
        return testTransaction;
    }

    public void setTestTransaction(boolean testTransaction) {
        this.testTransaction = testTransaction;
    }

    public String getPaymentModeName() {
        return paymentModeName;
    }

    public void setPaymentModeName(String paymentModeName) {
        this.paymentModeName = paymentModeName;
    }

    public String getAccountEncodedId() {
        return accountEncodedId;
    }

    public void setAccountEncodedId(String accountEncodedId) {
        this.accountEncodedId = accountEncodedId;
    }
}
