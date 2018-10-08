package com.magento;

import com.braintreegateway.Environment;
import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.core.HoverflyMode;
import java.net.Proxy;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

public class HoverflyProxyTest extends ProxyBraintreeTest {

  private static Hoverfly hoverfly;

  private TestName testName = new TestName();

  @Rule
  public TestName getTestName() {
    return testName;
  }


  @BeforeClass
  public static void setupHoverFly() throws Exception {
    hoverfly = new Hoverfly(HoverflyConfig.localConfigs(), HoverflyMode.CAPTURE);
    hoverfly.start();
  }


  @Override
  public Proxy getProxy() {
    return null;
  }


  @Override
  public Environment getEnvironment() {
    return new Environment("https://api.sandbox.braintreegateway.com:443",
        "https://auth.sandbox.venmo.com", new String[]{"cert.pem"}, "sandbox",
        "https://payments.sandbox.braintree-api.com/graphql");
  }

  @After
  public void stopRecording() {
    hoverfly.exportSimulation(
        Paths.get("./src/test/resources/hoverfly/mappings/" + testName.getMethodName()));

  }


  @AfterClass
  public static void shutdownHoverfly() {
    // hoverfly.exportSimulation(Paths.get("./src/test/resources/hoverfly/mappings"));
    hoverfly.close();
  }

}
