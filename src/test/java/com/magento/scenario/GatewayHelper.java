package com.magento.scenario;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.test.TestingGateway;
import com.braintreegateway.util.Http;

public class GatewayHelper {
  private BraintreeGateway gateway;

  public GatewayHelper(BraintreeGateway gateway) {
    this.gateway = gateway;
  }

  /**
   * Use admin API to settle a transaction.
   *
   * @param id The transactionId of the transaction to settle.
   * @return The {@link Result} of the operation.
   */
  public Result<Transaction> settle(String id) {

    Http http = new Http(gateway.getConfiguration());
    return new TestingGateway(http, gateway.getConfiguration()).settle(id);
  }
}
