package com.magento.scenario;

import com.braintreegateway.BraintreeGateway;
import java.beans.Introspector;
import java.math.BigDecimal;
import java.util.UUID;

public interface Scenario {

  void scenario(BraintreeGateway gateway);

  default String getName() {
    return Introspector.decapitalize(getClass().getSimpleName());
  }

  default String getOrderId() {
    return UUID.randomUUID().toString();
  }

  default String getNonce() {
    return "fake-valid-nonce";
  }

  default BigDecimal getAmount() {
    return new BigDecimal("1000.00");
  }

}
