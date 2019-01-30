package com.magento.scenario;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationErrorCode;
import org.junit.jupiter.api.Assertions;
import java.math.BigDecimal;

public class CancelFails implements Scenario {

  @Override
  public void scenario(BraintreeGateway gateway) {
    TransactionRequest request = new TransactionRequest().amount(getAmount())
        .paymentMethodNonce(getNonce()).orderId(getOrderId());
    Result<Transaction> result = gateway.transaction().sale(request);
    Assertions.assertTrue(result.isSuccess());
    Assertions.assertEquals(Status.AUTHORIZED, result.getTarget().getStatus());
    result = gateway.transaction().submitForSettlement(result.getTarget().getId());
    Assertions.assertTrue(result.isSuccess());
    Assertions.assertEquals(Status.SUBMITTED_FOR_SETTLEMENT, result.getTarget().getStatus());

    result = new GatewayHelper(gateway).settle(result.getTarget().getId());
    Assertions.assertTrue(result.isSuccess());
    Assertions.assertEquals(Status.SETTLED, result.getTarget().getStatus());

    result = gateway.transaction().voidTransaction(result.getTarget().getId());
    Assertions.assertFalse(result.isSuccess());
    Assertions.assertNull(result.getTarget());
    Assertions.assertEquals(1, result.getErrors().deepSize());
    Assertions.assertEquals(ValidationErrorCode.TRANSACTION_CANNOT_BE_VOIDED,
        result.getErrors().getAllDeepValidationErrors().get(0).getCode());
  }

  @Override
  public BigDecimal getAmount() {
    return new BigDecimal("1000.04");
  }

  @Override
  public String toString() {
    return getName();
  }


}
