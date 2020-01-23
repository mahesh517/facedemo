
package com.app.detection.model.SdkLicense;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sdk {

    @SerializedName("sgst")
    @Expose
    private Boolean sgst;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("type_of_sdk")
    @Expose
    private String typeOfSdk;
    @SerializedName("no_of_license_purchased")
    @Expose
    private Integer noOfLicensePurchased;
    @SerializedName("no_of_license_left")
    @Expose
    private Integer noOfLicenseLeft;
    @SerializedName("bundle_identifier")
    @Expose
    private String bundleIdentifier;
    @SerializedName("package_name")
    @Expose
    private String packageName;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("app_name")
    @Expose
    private String appName;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("active_from")
    @Expose
    private String activeFrom;
    @SerializedName("added_type")
    @Expose
    private String addedType;
    @SerializedName("total_amount")
    @Expose
    private Integer totalAmount;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("discount")
    @Expose
    private Integer discount;
    @SerializedName("discount_code")
    @Expose
    private String discountCode;
    @SerializedName("invoice_no")
    @Expose
    private String invoiceNo;
    @SerializedName("gstin")
    @Expose
    private String gstin;
    @SerializedName("gst_amount")
    @Expose
    private Integer gstAmount;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public Boolean getSgst() {
        return sgst;
    }

    public void setSgst(Boolean sgst) {
        this.sgst = sgst;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTypeOfSdk() {
        return typeOfSdk;
    }

    public void setTypeOfSdk(String typeOfSdk) {
        this.typeOfSdk = typeOfSdk;
    }

    public Integer getNoOfLicensePurchased() {
        return noOfLicensePurchased;
    }

    public void setNoOfLicensePurchased(Integer noOfLicensePurchased) {
        this.noOfLicensePurchased = noOfLicensePurchased;
    }

    public Integer getNoOfLicenseLeft() {
        return noOfLicenseLeft;
    }

    public void setNoOfLicenseLeft(Integer noOfLicenseLeft) {
        this.noOfLicenseLeft = noOfLicenseLeft;
    }

    public String getBundleIdentifier() {
        return bundleIdentifier;
    }

    public void setBundleIdentifier(String bundleIdentifier) {
        this.bundleIdentifier = bundleIdentifier;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(String activeFrom) {
        this.activeFrom = activeFrom;
    }

    public String getAddedType() {
        return addedType;
    }

    public void setAddedType(String addedType) {
        this.addedType = addedType;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public Integer getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(Integer gstAmount) {
        this.gstAmount = gstAmount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

}
