package com.magento.scenario;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.TransactionRequest;
import org.junit.Assert;
import java.math.BigDecimal;
import java.util.UUID;

public class CreateTransaction implements Scenario {

  @Override
  public void scenario(BraintreeGateway gateway) {
    String orderId = UUID.randomUUID().toString();
    TransactionRequest request = new TransactionRequest().amount(new BigDecimal("1000.01"))
        .paymentMethodNonce("fake-valid-nonce").orderId(orderId);
    Result<Transaction> result = gateway.transaction().sale(request);
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.AUTHORIZED, result.getTarget().getStatus());
  }

}
