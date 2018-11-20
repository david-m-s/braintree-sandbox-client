package com.magento.scenario.play;

import com.braintreegateway.Environment;
import com.magento.scenario.Scenario;
import static java.beans.Introspector.decapitalize;
import io.specto.hoverfly.junit.rule.HoverflyRule;

public class BraintreeScenarioRecorder extends AbstractBraintreeScenario {

  public BraintreeScenarioRecorder(Scenario scenario) {
    this.scenario = scenario;
    hoverfly = HoverflyRule.inCaptureMode(decapitalize(scenario.getName()) + ".json");
  }

  @Override
  protected Environment getEnvironment() {
    return new Environment(Environment.SANDBOX.baseURL, Environment.SANDBOX.authURL,
        new String[] {"cert.pem"}, "hoverfly-recorder", Environment.SANDBOX.graphQLURL);
  }

  @Override
  protected BraintreeCredentials getBraintreeCredentials() {
    return new BraintreeCredentials("c95xy5xmkzv83sxv", "gd2cncr42hg7jq8t",
        "230a1fad2dea71bb0f5a4644c3cc6b81");
  }

}
