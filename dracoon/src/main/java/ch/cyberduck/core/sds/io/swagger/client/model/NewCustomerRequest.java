/*
 * DRACOON
 * Version 4.4.0 - built at: 2017-12-04 04:14:43, API server: https://demo.dracoon.com/api/v4
 *
 * OpenAPI spec version: 4.4.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package ch.cyberduck.core.sds.io.swagger.client.model;

import java.util.Objects;
import ch.cyberduck.core.sds.io.swagger.client.model.CustomerAttributes;
import ch.cyberduck.core.sds.io.swagger.client.model.FirstAdminUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * NewCustomerRequest
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-05-03T10:55:56.129+02:00")
public class NewCustomerRequest {
  @JsonProperty("companyName")
  private String companyName = null;

  /**
   * Customer contract type.
   */
  public enum CustomerContractTypeEnum {
    PAY("pay"),
    
    FREE("free"),
    
    DEMO("demo");

    private String value;

    CustomerContractTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static CustomerContractTypeEnum fromValue(String text) {
      for (CustomerContractTypeEnum b : CustomerContractTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("customerContractType")
  private CustomerContractTypeEnum customerContractType = null;

  @JsonProperty("activationCode")
  private String activationCode = null;

  @JsonProperty("trialDays")
  private Integer trialDays = null;

  @JsonProperty("providerCustomerId")
  private String providerCustomerId = null;

  @JsonProperty("quotaMax")
  private Long quotaMax = null;

  @JsonProperty("userMax")
  private Integer userMax = null;

  @JsonProperty("lockStatus")
  private Boolean lockStatus = null;

  @JsonProperty("firstAdminUser")
  private FirstAdminUser firstAdminUser = null;

  @JsonProperty("customerAttributes")
  private CustomerAttributes customerAttributes = null;

  public NewCustomerRequest companyName(String companyName) {
    this.companyName = companyName;
    return this;
  }

   /**
   * Company name
   * @return companyName
  **/
  @ApiModelProperty(value = "Company name")
  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public NewCustomerRequest customerContractType(CustomerContractTypeEnum customerContractType) {
    this.customerContractType = customerContractType;
    return this;
  }

   /**
   * Customer contract type.
   * @return customerContractType
  **/
  @ApiModelProperty(required = true, value = "Customer contract type.")
  public CustomerContractTypeEnum getCustomerContractType() {
    return customerContractType;
  }

  public void setCustomerContractType(CustomerContractTypeEnum customerContractType) {
    this.customerContractType = customerContractType;
  }

  public NewCustomerRequest activationCode(String activationCode) {
    this.activationCode = activationCode;
    return this;
  }

   /**
   * Customer activation code string, valid only for types free and demo, for pay customers it is empty
   * @return activationCode
  **/
  @ApiModelProperty(value = "Customer activation code string, valid only for types free and demo, for pay customers it is empty")
  public String getActivationCode() {
    return activationCode;
  }

  public void setActivationCode(String activationCode) {
    this.activationCode = activationCode;
  }

  public NewCustomerRequest trialDays(Integer trialDays) {
    this.trialDays = trialDays;
    return this;
  }

   /**
   * Number of trial days (relevant only for demo contact type, for all other types will be reset to 0)
   * @return trialDays
  **/
  @ApiModelProperty(value = "Number of trial days (relevant only for demo contact type, for all other types will be reset to 0)")
  public Integer getTrialDays() {
    return trialDays;
  }

  public void setTrialDays(Integer trialDays) {
    this.trialDays = trialDays;
  }

  public NewCustomerRequest providerCustomerId(String providerCustomerId) {
    this.providerCustomerId = providerCustomerId;
    return this;
  }

   /**
   * Provider customer ID value, relevant only for type &#39;pay&#39;
   * @return providerCustomerId
  **/
  @ApiModelProperty(value = "Provider customer ID value, relevant only for type 'pay'")
  public String getProviderCustomerId() {
    return providerCustomerId;
  }

  public void setProviderCustomerId(String providerCustomerId) {
    this.providerCustomerId = providerCustomerId;
  }

  public NewCustomerRequest quotaMax(Long quotaMax) {
    this.quotaMax = quotaMax;
    return this;
  }

   /**
   * Maximal disc space which can be allocated by customer in bytes
   * @return quotaMax
  **/
  @ApiModelProperty(required = true, value = "Maximal disc space which can be allocated by customer in bytes")
  public Long getQuotaMax() {
    return quotaMax;
  }

  public void setQuotaMax(Long quotaMax) {
    this.quotaMax = quotaMax;
  }

  public NewCustomerRequest userMax(Integer userMax) {
    this.userMax = userMax;
    return this;
  }

   /**
   * Maximal number of users
   * @return userMax
  **/
  @ApiModelProperty(required = true, value = "Maximal number of users")
  public Integer getUserMax() {
    return userMax;
  }

  public void setUserMax(Integer userMax) {
    this.userMax = userMax;
  }

  public NewCustomerRequest lockStatus(Boolean lockStatus) {
    this.lockStatus = lockStatus;
    return this;
  }

   /**
   * Lock status: false - Unlocked, true - Locked. All users of this customer will be blocked and can not login anymore
   * @return lockStatus
  **/
  @ApiModelProperty(value = "Lock status: false - Unlocked, true - Locked. All users of this customer will be blocked and can not login anymore")
  public Boolean getLockStatus() {
    return lockStatus;
  }

  public void setLockStatus(Boolean lockStatus) {
    this.lockStatus = lockStatus;
  }

  public NewCustomerRequest firstAdminUser(FirstAdminUser firstAdminUser) {
    this.firstAdminUser = firstAdminUser;
    return this;
  }

   /**
   * Dataspace admin user data
   * @return firstAdminUser
  **/
  @ApiModelProperty(required = true, value = "Dataspace admin user data")
  public FirstAdminUser getFirstAdminUser() {
    return firstAdminUser;
  }

  public void setFirstAdminUser(FirstAdminUser firstAdminUser) {
    this.firstAdminUser = firstAdminUser;
  }

  public NewCustomerRequest customerAttributes(CustomerAttributes customerAttributes) {
    this.customerAttributes = customerAttributes;
    return this;
  }

   /**
   * Customer Attributes
   * @return customerAttributes
  **/
  @ApiModelProperty(value = "Customer Attributes")
  public CustomerAttributes getCustomerAttributes() {
    return customerAttributes;
  }

  public void setCustomerAttributes(CustomerAttributes customerAttributes) {
    this.customerAttributes = customerAttributes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NewCustomerRequest newCustomerRequest = (NewCustomerRequest) o;
    return Objects.equals(this.companyName, newCustomerRequest.companyName) &&
        Objects.equals(this.customerContractType, newCustomerRequest.customerContractType) &&
        Objects.equals(this.activationCode, newCustomerRequest.activationCode) &&
        Objects.equals(this.trialDays, newCustomerRequest.trialDays) &&
        Objects.equals(this.providerCustomerId, newCustomerRequest.providerCustomerId) &&
        Objects.equals(this.quotaMax, newCustomerRequest.quotaMax) &&
        Objects.equals(this.userMax, newCustomerRequest.userMax) &&
        Objects.equals(this.lockStatus, newCustomerRequest.lockStatus) &&
        Objects.equals(this.firstAdminUser, newCustomerRequest.firstAdminUser) &&
        Objects.equals(this.customerAttributes, newCustomerRequest.customerAttributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(companyName, customerContractType, activationCode, trialDays, providerCustomerId, quotaMax, userMax, lockStatus, firstAdminUser, customerAttributes);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NewCustomerRequest {\n");
    
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    customerContractType: ").append(toIndentedString(customerContractType)).append("\n");
    sb.append("    activationCode: ").append(toIndentedString(activationCode)).append("\n");
    sb.append("    trialDays: ").append(toIndentedString(trialDays)).append("\n");
    sb.append("    providerCustomerId: ").append(toIndentedString(providerCustomerId)).append("\n");
    sb.append("    quotaMax: ").append(toIndentedString(quotaMax)).append("\n");
    sb.append("    userMax: ").append(toIndentedString(userMax)).append("\n");
    sb.append("    lockStatus: ").append(toIndentedString(lockStatus)).append("\n");
    sb.append("    firstAdminUser: ").append(toIndentedString(firstAdminUser)).append("\n");
    sb.append("    customerAttributes: ").append(toIndentedString(customerAttributes)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
}
