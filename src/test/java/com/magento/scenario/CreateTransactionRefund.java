package com.magento.scenario;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.TransactionRequest;
import org.junit.Assert;
import java.math.BigDecimal;

public class CreateTransactionRefund implements Scenario {

  @Override
  public void scenario(BraintreeGateway gateway) {
    TransactionRequest request = new TransactionRequest().amount(getAmount())
        .paymentMethodNonce(getNonce()).orderId(getOrderId());
    Result<Transaction> result = gateway.transaction().sale(request);
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.AUTHORIZED, result.getTarget().getStatus());
    result = gateway.transaction().submitForSettlement(result.getTarget().getId());
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.SUBMITTED_FOR_SETTLEMENT, result.getTarget().getStatus());

    result = new GatewayHelper(gateway).settle(result.getTarget().getId());
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.SETTLED, result.getTarget().getStatus());

    result = gateway.transaction().refund(result.getTarget().getId());
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.SUBMITTED_FOR_SETTLEMENT, result.getTarget().getStatus());
  }

  @Override
  public BigDecimal getAmount() {
    return new BigDecimal("1000.05");
  }

}
