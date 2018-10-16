package com.magento.scenario;

import com.braintreegateway.BraintreeGateway;

public interface Scenario {

  void scenario(BraintreeGateway gateway);

  default String getName() {
    return getClass().getSimpleName();
  }

}
