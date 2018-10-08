package com.magento;

import com.braintreegateway.Environment;
import java.net.Proxy;

public class NoProxyTest extends ProxyBraintreeTest {

  @Override
  public Proxy getProxy() {
    return Proxy.NO_PROXY;
  }

  @Override
  public Environment getEnvironment() {
    return Environment.SANDBOX;
  }

}
