package com.magento.scenario;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.TransactionRequest;
import org.junit.jupiter.api.Assertions;
import java.math.BigDecimal;

public class CreateTransaction implements Scenario {

  @Override
  public void scenario(BraintreeGateway gateway) {
    TransactionRequest request = new TransactionRequest().amount(getAmount())
        .paymentMethodNonce(getNonce()).orderId(getOrderId());
    Result<Transaction> result = gateway.transaction().sale(request);
    Assertions.assertTrue(result.isSuccess());
    Assertions.assertEquals(Status.AUTHORIZED, result.getTarget().getStatus());
  }

  @Override
  public BigDecimal getAmount() {
    return new BigDecimal("1000.01");
  }

  @Override
  public String toString() {
    return getName();
  }
}
