package com.magento.junit5;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import java.nio.file.Path;
import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyMode;
import io.specto.hoverfly.junit.core.SimulationSource;

public class HoverflyPerMethodExtension
    implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  private Hoverfly hoverfly;
  private HoverflyMode mode;
  private Path basePath;

  public HoverflyPerMethodExtension(HoverflyMode mode, Path basePath) {
    this.mode = mode;
    this.basePath = basePath;
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    hoverfly = new Hoverfly(mode);
    hoverfly.start();

    if (mode.allowSimulationImport()) {
      hoverfly.simulate(SimulationSource.file(resolveTestPath(context)));
    }
  }

  private Path resolveTestPath(ExtensionContext context) {
    return basePath.resolve(context.getDisplayName() + ".json");
  }

  @Override
  public void afterEach(ExtensionContext context) {
    if (isRunning()) {
      try {
        this.hoverfly.exportSimulation(resolveTestPath(context));
      } finally {
        this.hoverfly.close();
        this.hoverfly = null;
      }
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return Hoverfly.class.isAssignableFrom(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return this.hoverfly;
  }

  private boolean isRunning() {
    return this.hoverfly != null;
  }
}
