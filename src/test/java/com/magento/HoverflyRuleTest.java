package com.magento;

import com.braintreegateway.Environment;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import java.net.Proxy;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;

public class HoverflyRuleTest extends ProxyBraintreeTest {

  @ClassRule
  public static HoverflyRule hoverfly = HoverflyRule.inCaptureMode();

  @Rule
  public TestName testName = new TestName();

  @Override
  public Proxy getProxy() {
    return null;
  }

  // @After
  // public void saveRecord() throws InterruptedException {
  // Thread.sleep(1000);
  // hoverfly.capture(testName.getMethodName()+".json");
  // }

  @Override
  public Environment getEnvironment() {
    return new Environment("https://api.sandbox.braintreegateway.com:443",
        "https://auth.sandbox.venmo.com", new String[]{"cert.pem"}, "sandbox",
        "https://payments.sandbox.braintree-api.com/graphql");
  }


}
