package com.magento.scenario.play;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.magento.junit5.BraintreeExtension;
import com.magento.junit5.HoverflyPerMethodExtension;
import com.magento.scenario.CancelFails;
import com.magento.scenario.CreateTransaction;
import com.magento.scenario.CreateTransactionCancel;
import com.magento.scenario.CreateTransactionRefund;
import com.magento.scenario.CreateTransactionSubmitForSettlement;
import com.magento.scenario.RefundFails;
import com.magento.scenario.Scenario;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.nio.file.Path;
import java.util.Arrays;
import io.specto.hoverfly.junit.core.HoverflyMode;

public abstract class AbstractBraintreeScenario {

  @RegisterExtension
  HoverflyPerMethodExtension hoverflyExtension =
      new HoverflyPerMethodExtension(getHoverflyMode(), getHoverflyPath());

  @RegisterExtension
  BraintreeExtension braintreeExtension =
      new BraintreeExtension(getBraintreeCredentials(), getEnvironment());

  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] {{new CreateTransaction()}, {new CancelFails()},
        {new CreateTransactionCancel()}, {new CreateTransactionRefund()},
        {new CreateTransactionSubmitForSettlement()}, {new RefundFails()}});
  }

  @ParameterizedTest(name = "{arguments}")
  @MethodSource("data")
  public void scenario(Scenario scenario, BraintreeGateway gateway) {
    scenario.scenario(gateway);
  }

  protected abstract HoverflyMode getHoverflyMode();

  protected abstract Path getHoverflyPath();

  protected abstract BraintreeCredentials getBraintreeCredentials();

  protected abstract Environment getEnvironment();
}
