// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import org.opentcs.configuration.ConfigurationBindingProvider;
import org.opentcs.configuration.gestalt.GestaltConfigurationBindingProvider;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.opentcs.strategies.basic.dispatching.DefaultDispatcherModule;
import org.opentcs.strategies.basic.peripherals.dispatching.DefaultPeripheralJobDispatcherModule;
import org.opentcs.strategies.basic.routing.DefaultRouterModule;
import org.opentcs.strategies.basic.scheduling.DefaultSchedulerModule;
import org.opentcs.util.Environment;
import org.opentcs.util.logging.UncaughtExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The kernel process's default entry point.
 */
public class RunKernel {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(RunKernel.class);

  /**
   * Prevents external instantiation.
   */
  private RunKernel() {
  }

  /**
   * Initializes the system and starts the openTCS kernel including modules.
   *
   * @param args The command line arguments.
   * @throws Exception If there was a problem starting the kernel.
   */
  public static void main(String[] args)
      throws Exception {
    LOG.info("Starting kernel");
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionLogger(false));

    Environment.logSystemInfo();

    LOG.debug("Setting up openTCS kernel {}...", Environment.getBaselineVersion());

    try {
      Injector injector = Guice.createInjector(customConfigurationModule());
      injector.getInstance(KernelStarter.class).startKernel();
    }
    catch (IOException e) {
      LOG.error("Error starting kernel", e);
    }
  }

  /**
   * Builds and returns a Guice module containing the custom configuration for the kernel
   * application, including additions and overrides by the user.
   *
   * @return The custom configuration module.
   */
  private static Module customConfigurationModule() {
    List<KernelInjectionModule> defaultModules
        = Arrays.asList(
            new DefaultKernelInjectionModule(),
            new DefaultDispatcherModule(),
            new DefaultRouterModule(),
            new DefaultSchedulerModule(),
            new DefaultPeripheralJobDispatcherModule()
        );

    ConfigurationBindingProvider bindingProvider = configurationBindingProvider();
    for (KernelInjectionModule defaultModule : defaultModules) {
      defaultModule.setConfigBindingProvider(bindingProvider);
    }

    return Modules.override(defaultModules)
        .with(findRegisteredModules(bindingProvider));
  }

  /**
   * Finds and returns all Guice modules registered via ServiceLoader.
   *
   * @return The registered/found modules.
   */
  private static List<KernelInjectionModule> findRegisteredModules(
      ConfigurationBindingProvider bindingProvider
  ) {
    List<KernelInjectionModule> registeredModules = new ArrayList<>();
    for (KernelInjectionModule module : ServiceLoader.load(KernelInjectionModule.class)) {
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
            "opentcs-kernel-defaults-baseline.properties"
        )
            .toAbsolutePath(),
        Paths.get(
            System.getProperty("opentcs.base", "."),
            "config",
            "opentcs-kernel-defaults-custom.properties"
        )
            .toAbsolutePath(),
        Paths.get(
            System.getProperty("opentcs.home", "."),
            "config",
            "opentcs-kernel.properties"
        )
            .toAbsolutePath()
    );
  }

}
