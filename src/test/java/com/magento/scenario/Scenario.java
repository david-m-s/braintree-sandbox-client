package com.magento.scenario;

import com.braintreegateway.BraintreeGateway;
import java.beans.Introspector;

public interface Scenario {

  void scenario(BraintreeGateway gateway);

  default String getName() {
    return Introspector.decapitalize(getClass().getSimpleName());
  }

}
