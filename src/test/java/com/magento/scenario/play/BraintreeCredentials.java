package com.magento.scenario.play;

public class BraintreeCredentials {
  private String merchantId;
  private String publicKey;
  private String privateKey;

  public BraintreeCredentials(String merchantId, String publicKey, String privateKey) {
    this.merchantId = merchantId;
    this.publicKey = publicKey;
    this.privateKey = privateKey;
  }

  public String getMerchantId() {
    return merchantId;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public String getPrivateKey() {
    return privateKey;
  }


}
