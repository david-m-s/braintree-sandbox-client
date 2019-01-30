package com.magento.scenario.play;

import com.braintreegateway.Environment;
import java.nio.file.Path;
import java.nio.file.Paths;
import io.specto.hoverfly.junit.core.HoverflyMode;

public class BraintreeScenarioRecorder extends AbstractBraintreeScenario {

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

  @Override
  protected HoverflyMode getHoverflyMode() {
    return HoverflyMode.CAPTURE;
  }

  @Override
  protected Path getHoverflyPath() {
    return Paths.get("target/hoverfly/capture/");
  }

}
