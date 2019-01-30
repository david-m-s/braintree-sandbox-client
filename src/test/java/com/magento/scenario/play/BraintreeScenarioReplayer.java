package com.magento.scenario.play;

import com.braintreegateway.Environment;
import org.junit.jupiter.api.AfterEach;
import java.nio.file.Path;
import java.nio.file.Paths;
import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyMode;

public class BraintreeScenarioReplayer extends AbstractBraintreeScenario {

  @Override
  protected Environment getEnvironment() {
    return new Environment(Environment.PRODUCTION.baseURL, Environment.PRODUCTION.authURL,
        new String[] {"cert.pem"}, "hoverfly-replayer", Environment.PRODUCTION.graphQLURL);
  }

  @Override
  protected BraintreeCredentials getBraintreeCredentials() {
    return new BraintreeCredentials("c95xy5xmkzv83sxv", "public key", "private key");
  }

  @AfterEach
  public void assertHoverfly(Hoverfly hoverfly) {
    hoverfly.assertThatNoDiffIsReported(true);
  }

  @Override
  protected HoverflyMode getHoverflyMode() {
    return HoverflyMode.SIMULATE;
  }

  @Override
  protected Path getHoverflyPath() {
    return Paths.get("src/test/resources/hoverfly/reply/");
  }
}
