package com.magento.scenario.play;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.magento.scenario.CancelFails;
import com.magento.scenario.CreateTransaction;
import com.magento.scenario.CreateTransactionCancel;
import com.magento.scenario.CreateTransactionRefund;
import com.magento.scenario.CreateTransactionSubmitForSettlement;
import com.magento.scenario.RefundFails;
import com.magento.scenario.Scenario;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.bridge.SLF4JBridgeHandler;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import ch.qos.logback.classic.jul.JULHelper;
import io.specto.hoverfly.junit.rule.HoverflyRule;

@RunWith(Parameterized.class)
public abstract class AbstractBraintreeScenario {

  @Rule
  public HoverflyRule hoverfly;

  protected Scenario scenario;

  protected BraintreeGateway gateway;

  @Parameters(name = "{0}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] {{new CreateTransaction()}, {new CancelFails()},
        {new CreateTransactionCancel()}, {new CreateTransactionRefund()},
        {new CreateTransactionSubmitForSettlement()}, {new RefundFails()}});
  }

  @BeforeClass
  public static void instalJULBridgeHandler() {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  @Before
  public void setUpGlobal() {
    BraintreeCredentials bc = getBraintreeCredentials();
    gateway = new BraintreeGateway(getEnvironment(), bc.getMerchantId(), bc.getPublicKey(),
        bc.getPrivateKey());

    Logger braintreeLogger = JULHelper.asJULLogger("Braintree");
    braintreeLogger.setLevel(Level.ALL);
    gateway.getConfiguration().setLogger(braintreeLogger);
  }

  protected abstract Environment getEnvironment();

  protected abstract BraintreeCredentials getBraintreeCredentials();

  @Test
  public void scenario() {
    scenario.scenario(gateway);
  }


}
