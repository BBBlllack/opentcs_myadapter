// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernelcontrolcenter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import org.opentcs.configuration.ConfigurationBindingProvider;
import org.opentcs.configuration.gestalt.GestaltConfigurationBindingProvider;
import org.opentcs.customizations.ConfigurableInjectionModule;
import org.opentcs.customizations.controlcenter.ControlCenterInjectionModule;
import org.opentcs.util.Environment;
import org.opentcs.util.logging.UncaughtExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The kernel control center process's default entry point.
 */
public class RunKernelControlCenter {

  /**
   * This class' logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(RunKernelControlCenter.class);

  /**
   * Prevents external instantiation.
   */
  private RunKernelControlCenter() {
  }

  /**
   * The kernel control center client's main entry point.
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionLogger(false));

    Environment.logSystemInfo();
    try {
      Injector injector = Guice.createInjector(customConfigurationModule());
      injector.getInstance(KernelControlCenterApplication.class).initialize();
    }
    catch (Exception e) {
        LOG.error(e.getMessage(), e);
    }

  }

  /**
   * Builds and returns a Guice module containing the custom configuration for the kernel control
   * center application, including additions and overrides by the user.
   *
   * @return The custom configuration module.
   */
  private static Module customConfigurationModule() {
    ConfigurationBindingProvider bindingProvider = configurationBindingProvider();
    ConfigurableInjectionModule kernelControlCenterInjectionModule
        = new DefaultKernelControlCenterInjectionModule();
    kernelControlCenterInjectionModule.setConfigBindingProvider(bindingProvider);
    return Modules.override(kernelControlCenterInjectionModule)
        .with(findRegisteredModules(bindingProvider));
  }

  /**
   * Finds and returns all Guice modules registered via ServiceLoader.
   *
   * @return The registered/found modules.
   */
  private static List<ControlCenterInjectionModule> findRegisteredModules(
      ConfigurationBindingProvider bindingProvider
  ) {
    List<ControlCenterInjectionModule> registeredModules = new ArrayList<>();
    for (ControlCenterInjectionModule module : ServiceLoader.load(
        ControlCenterInjectionModule.class
    )) {
      LOG.info(
          "Integrating injection module {} (source: {})",
          module.getClass().getName(),
          module.getClass().getProtectionDomain().getCodeSource()
      );
      module.setConfigBindingProvider(bindingProvider);
      registeredModules.add(module);
    }
    return registeredModules;
  }

  private static ConfigurationBindingProvider configurationBindingProvider() {
    String chosenProvider = System.getProperty("opentcs.configuration.provider", "gestalt");
    switch (chosenProvider) {
      case "gestalt":
      default:
        LOG.info("Using gestalt as the configuration provider.");
        return gestaltConfigurationBindingProvider();
    }
  }

  private static ConfigurationBindingProvider gestaltConfigurationBindingProvider() {
    return new GestaltConfigurationBindingProvider(
        Paths.get(
            System.getProperty("opentcs.base", "."),
            "config",
            "opentcs-kernelcontrolcenter-defaults-baseline.properties"
        )
            .toAbsolutePath(),
        Paths.get(
            System.getProperty("opentcs.base", "."),
            "config",
            "opentcs-kernelcontrolcenter-defaults-custom.properties"
        )
            .toAbsolutePath(),
        Paths.get(
            System.getProperty("opentcs.home", "."),
            "config",
            "opentcs-kernelcontrolcenter.properties"
        )
            .toAbsolutePath()
    );
  }

}
