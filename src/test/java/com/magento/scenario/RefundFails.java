package com.magento.scenario;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationErrorCode;
import org.junit.jupiter.api.Assertions;
import java.math.BigDecimal;

public class RefundFails implements Scenario {

  @Override
  public void scenario(BraintreeGateway gateway) {
    TransactionRequest request = new TransactionRequest().amount(getAmount())
        .paymentMethodNonce(getNonce()).orderId(getOrderId());
    Result<Transaction> result = gateway.transaction().sale(request);
    Assertions.assertTrue(result.isSuccess());
    Assertions.assertEquals(Status.AUTHORIZED, result.getTarget().getStatus());
    result = gateway.transaction().refund(result.getTarget().getId());
    Assertions.assertFalse(result.isSuccess());
    Assertions.assertNull(result.getTarget());
    Assertions.assertEquals(1, result.getErrors().deepSize());
    Assertions.assertEquals(ValidationErrorCode.TRANSACTION_CANNOT_REFUND_UNLESS_SETTLED,
        result.getErrors().getAllDeepValidationErrors().get(0).getCode());
  }

  @Override
  public BigDecimal getAmount() {
    return new BigDecimal("1000.06");
  }

  @Override
  public String toString() {
    return getName();
  }
}
