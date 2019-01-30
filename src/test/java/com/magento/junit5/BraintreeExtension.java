package com.magento.junit5;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.magento.scenario.play.BraintreeCredentials;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.bridge.SLF4JBridgeHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import ch.qos.logback.classic.jul.JULHelper;

public class BraintreeExtension implements BeforeEachCallback, ParameterResolver {
  private BraintreeGateway gateway;

  private BraintreeCredentials bc;

  private Environment environment;

  public BraintreeExtension(BraintreeCredentials braintreeCredentials, Environment environment) {
    this.bc = braintreeCredentials;
    this.environment = environment;
  }

  static {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    gateway = new BraintreeGateway(environment, bc.getMerchantId(), bc.getPublicKey(),
        bc.getPrivateKey());

    Logger braintreeLogger = JULHelper.asJULLogger("Braintree");
    braintreeLogger.setLevel(Level.ALL);
    gateway.getConfiguration().setLogger(braintreeLogger);
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return BraintreeGateway.class.isAssignableFrom(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return gateway;
  }

}
