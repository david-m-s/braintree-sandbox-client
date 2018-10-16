package com.magento.scenario;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;
import io.specto.hoverfly.junit.core.SimulationSource;
import io.specto.hoverfly.junit.rule.HoverflyRule;

@RunWith(Parameterized.class)
public class BraintreeScenarioReplayer {

  @Rule
  public HoverflyRule hoverfly;

  private Scenario scenario;

  protected BraintreeGateway gateway;

  public BraintreeScenarioReplayer(Scenario scenario) {
    this.scenario = scenario;
    hoverfly = HoverflyRule.inSimulationMode(SimulationSource
        .file(Paths.get("src/test/resources/hoverfly/reply/" + scenario.getName() + ".json")));
  }

  @Parameters(name = "{0}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] {{new CreateTransaction()}, {new CancelFails()},
        {new CreateTransactionCancel()}, {new CreateTransactionRefund()},
        {new CreateTransactionSubmitForSettlement()}, {new RefundFails()}});
  }

  @Before
  public void setUpGlobal() {
    gateway = new BraintreeGateway(getEnvironment(), "c95xy5xmkzv83sxv", "gd2cncr42hg7jq8t",
        "230a1fad2dea71bb0f5a4644c3cc6b81");

    Logger logger = Logger.getLogger("Braintree");
    gateway.getConfiguration().setLogger(logger);
  }

  private Environment getEnvironment() {
    return new Environment(Environment.PRODUCTION.baseURL, Environment.PRODUCTION.authURL,
        new String[] {"cert.pem"}, "hoverfly-replayer", Environment.PRODUCTION.graphQLURL);
  }

  @Test
  public void scenario() {
    scenario.scenario(gateway);
  }

  @After
  public void checkNoDiffs() {
    hoverfly.assertThatNoDiffIsReported();
  }

}
