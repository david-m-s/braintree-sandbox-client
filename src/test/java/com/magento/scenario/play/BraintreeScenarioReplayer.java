package com.magento.scenario.play;

import com.braintreegateway.Environment;
import com.magento.scenario.Scenario;
import org.junit.After;
import java.nio.file.Paths;
import io.specto.hoverfly.junit.core.SimulationSource;
import io.specto.hoverfly.junit.rule.HoverflyRule;

public class BraintreeScenarioReplayer extends AbstractBraintreeScenario {


  public BraintreeScenarioReplayer(Scenario scenario) {
    this.scenario = scenario;
    hoverfly = HoverflyRule.inSimulationMode(SimulationSource
        .file(Paths.get("src/test/resources/hoverfly/reply/" + scenario.getName() + ".json")));
  }

  @Override
  protected Environment getEnvironment() {
    return new Environment(Environment.PRODUCTION.baseURL, Environment.PRODUCTION.authURL,
        new String[] {"cert.pem"}, "hoverfly-replayer", Environment.PRODUCTION.graphQLURL);
  }

  @Override
  protected BraintreeCredentials getBraintreeCredentials() {
    return new BraintreeCredentials("c95xy5xmkzv83sxv", "public key", "private key");
  }

  @After
  public void assertHoverfly() {
    hoverfly.assertThatNoDiffIsReported();
  }
}
