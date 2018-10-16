package com.magento.scenario;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationErrorCode;
import org.junit.Assert;
import java.math.BigDecimal;
import java.util.UUID;

public class RefundFails implements Scenario {

  @Override
  public void scenario(BraintreeGateway gateway) {
    String orderId = UUID.randomUUID().toString();
    TransactionRequest request = new TransactionRequest().amount(new BigDecimal("1000.06"))
        .paymentMethodNonce("fake-valid-nonce").orderId(orderId);
    Result<Transaction> result = gateway.transaction().sale(request);
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.AUTHORIZED, result.getTarget().getStatus());
    result = gateway.transaction().refund(result.getTarget().getId());
    Assert.assertFalse(result.isSuccess());
    Assert.assertNull(result.getTarget());
    Assert.assertEquals(1, result.getErrors().deepSize());
    Assert.assertEquals(ValidationErrorCode.TRANSACTION_CANNOT_REFUND_UNLESS_SETTLED,
        result.getErrors().getAllDeepValidationErrors().get(0).getCode());
  }

}
