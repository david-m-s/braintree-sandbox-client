package com.magento.scenario;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import io.specto.hoverfly.junit.rule.HoverflyRule;

@RunWith(Parameterized.class)
public class BraintreeScenarioRecorder {

  @Rule
  public HoverflyRule hoverfly;

  private Scenario scenario;

  protected BraintreeGateway gateway;

  public BraintreeScenarioRecorder(Scenario scenario) {
    this.scenario = scenario;
    hoverfly = HoverflyRule.inCaptureMode(Introspector.decapitalize(scenario.getName()) + ".json");
  }

  @Parameters(name = "{0}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] {{new CreateTransaction()}, {new CancelFails()},
        {new CreateTransactionCancel()}, {new CreateTransactionRefund()},
        {new CreateTransactionSubmitForSettlement()}, {new RefundFails()}});
  }

  @BeforeClass
  public static void initLogging() {
    final InputStream inputStream =
        BraintreeScenarioRecorder.class.getResourceAsStream("/logging.properties");
    try {
      LogManager.getLogManager().readConfiguration(inputStream);
    } catch (final IOException e) {
      Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
      Logger.getAnonymousLogger().severe(e.getMessage());
    }
  }

  @Before
  public void setUpGlobal() {
    gateway = new BraintreeGateway(getEnvironment(), "c95xy5xmkzv83sxv", "gd2cncr42hg7jq8t",
        "230a1fad2dea71bb0f5a4644c3cc6b81");

    Logger logger = Logger.getLogger("Braintree");
    logger.setLevel(Level.ALL);
    gateway.getConfiguration().setLogger(logger);
  }

  private Environment getEnvironment() {
    return new Environment(Environment.SANDBOX.baseURL, Environment.SANDBOX.authURL,
        new String[] {"cert.pem"}, "hoverfly-recorder", Environment.SANDBOX.graphQLURL);
  }

  @Test
  public void scenario() {
    scenario.scenario(gateway);
  }
}
