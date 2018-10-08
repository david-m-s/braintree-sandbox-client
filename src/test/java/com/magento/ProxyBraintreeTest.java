package com.magento;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationErrorCode;
import com.braintreegateway.util.Http;
import com.braintreegateway.util.NodeWrapper;
import com.magento.simple.WiremockTest;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.Proxy;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class ProxyBraintreeTest {

  // The same environment as Environment#SANDBOX, but with the certificates
  // replaced to accept those from hoverfly for ssl proxying. This is necessary
  // as the braintree java api is using its own sslConnectionFactory with the
  // certificates set.
  // public static final Environment env = new
  // Environment("https://api.sandbox.braintreegateway.com:443",
  // "https://auth.sandbox.venmo.com", new String[] { "cert.pem" }, "sandbox",
  // "https://payments.sandbox.braintree-api.com/graphql");

  // private static final Environment env = Environment.SANDBOX;
  private BraintreeGateway gateway;
  public final String baseUrl = getEnvironment().baseURL;

  @Before
  public void setUpGlobal() {
    gateway = new BraintreeGateway(getEnvironment(), "c95xy5xmkzv83sxv", "gd2cncr42hg7jq8t",
        "230a1fad2dea71bb0f5a4644c3cc6b81");
    Logger logger = Logger.getLogger("Braintree");
    logger.setLevel(Level.ALL);
    gateway.getConfiguration().setLogger(logger);
    setProxy();

  }

  @BeforeClass
  public static void initLogging() {
    final InputStream inputStream = WiremockTest.class.getResourceAsStream("/logging.properties");
    try {
      LogManager.getLogManager().readConfiguration(inputStream);
    } catch (final IOException e) {
      Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
      Logger.getAnonymousLogger().severe(e.getMessage());
    }
  }

  // @Before
  public void setProxy() {
    gateway.getConfiguration().setProxy(getProxy());
  }

  public abstract Proxy getProxy();

  public abstract Environment getEnvironment();

  // https://articles.braintreepayments.com/control-panel/transactions/duplicate-checking
  @Test
  public void createTransaction() {
    String orderId = UUID.randomUUID().toString();
    TransactionRequest request = new TransactionRequest().amount(new BigDecimal("1000.01"))
        .paymentMethodNonce("fake-valid-nonce").orderId(orderId);
    Result<Transaction> result = gateway.transaction().sale(request);
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.AUTHORIZED, result.getTarget().getStatus());
  }

  @Test
  public void createTransactionSubmitForSettlement() {
    String orderId = UUID.randomUUID().toString();
    TransactionRequest request = new TransactionRequest().amount(new BigDecimal("1000.02"))
        .paymentMethodNonce("fake-valid-nonce").orderId(orderId);
    Result<Transaction> result = gateway.transaction().sale(request);
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.AUTHORIZED, result.getTarget().getStatus());
    result = gateway.transaction().submitForSettlement(result.getTarget().getId());
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.SUBMITTED_FOR_SETTLEMENT, result.getTarget().getStatus());
  }

  @Test
  public void createTransactionCancel() {
    String orderId = UUID.randomUUID().toString();
    TransactionRequest request = new TransactionRequest().amount(new BigDecimal("1000.03"))
        .paymentMethodNonce("fake-valid-nonce").orderId(orderId);
    Result<Transaction> result = gateway.transaction().sale(request);
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.AUTHORIZED, result.getTarget().getStatus());
    result = gateway.transaction().voidTransaction(result.getTarget().getId());
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.VOIDED, result.getTarget().getStatus());
  }

  @Test
  public void cancelFails() {
    String orderId = UUID.randomUUID().toString();
    TransactionRequest request = new TransactionRequest().amount(new BigDecimal("1000.04"))
        .paymentMethodNonce("fake-valid-nonce").orderId(orderId);
    Result<Transaction> result = gateway.transaction().sale(request);
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.AUTHORIZED, result.getTarget().getStatus());
    result = gateway.transaction().submitForSettlement(result.getTarget().getId());
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.SUBMITTED_FOR_SETTLEMENT, result.getTarget().getStatus());

    result = settle(result.getTarget().getId());
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.SETTLED, result.getTarget().getStatus());

    result = gateway.transaction().voidTransaction(result.getTarget().getId());
    Assert.assertFalse(result.isSuccess());
    Assert.assertNull(result.getTarget());
    Assert.assertEquals(1, result.getErrors().deepSize());
    Assert.assertEquals(ValidationErrorCode.TRANSACTION_CANNOT_BE_VOIDED,
        result.getErrors().getAllDeepValidationErrors().get(0).getCode());

  }

  @Test
  public void createTransactionRefund() {
    String orderId = UUID.randomUUID().toString();
    TransactionRequest request = new TransactionRequest().amount(new BigDecimal("1000.05"))
        .paymentMethodNonce("fake-valid-nonce").orderId(orderId);
    Result<Transaction> result = gateway.transaction().sale(request);
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.AUTHORIZED, result.getTarget().getStatus());
    result = gateway.transaction().submitForSettlement(result.getTarget().getId());
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.SUBMITTED_FOR_SETTLEMENT, result.getTarget().getStatus());

    result = settle(result.getTarget().getId());
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.SETTLED, result.getTarget().getStatus());

    result = gateway.transaction().refund(result.getTarget().getId());
    Assert.assertTrue(result.isSuccess());
    Assert.assertEquals(Status.SUBMITTED_FOR_SETTLEMENT, result.getTarget().getStatus());
  }

  @Test
  public void refundFails() {
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

  private Result<Transaction> settle(String id) {
    Http http = new Http(gateway.getConfiguration());
    NodeWrapper response =
        http.put(gateway.getConfiguration().getMerchantPath() + "/transactions/" + id + "/settle");
    return new Result<Transaction>(response, Transaction.class);
  }

}
