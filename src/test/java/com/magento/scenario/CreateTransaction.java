package com.magento.scenario;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.TransactionRequest;
import org.junit.Assert;
import java.math.BigDecimal;

public class CreateTransaction implements Scenario {

  @Override
  public void scenario(BraintreeGateway gateway) {
    TransactionRequest request = new TransactionRequest().amount(getAmount())
        .paymentMethodNonce(getNonce()).orderId(getOrderId());
    Result<Transaction> result = gateway.transaction().sale(request);
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.AUTHORIZED, result.getTarget().getStatus());
  }

  @Override
  public BigDecimal getAmount() {
    return new BigDecimal("1000.01");
  }
}
