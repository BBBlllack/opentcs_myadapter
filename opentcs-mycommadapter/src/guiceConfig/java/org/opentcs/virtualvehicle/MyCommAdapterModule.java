package org.opentcs.virtualvehicle;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.opentcs.MyCommAdapterComponentsFactory;
import org.opentcs.MyCommAdapterFactory;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCommAdapterModule extends KernelInjectionModule {
    private static final Logger log = LoggerFactory.getLogger(MyCommAdapterModule.class);

    public MyCommAdapterModule() {

    }

  @Override
  protected void configure() {
    /**
     bind(VirtualVehicleConfiguration.class)
     .toInstance(configuration);
     install(new FactoryModuleBuilder().build(LoopbackAdapterComponentsFactory.class));
     vehicleCommAdaptersBinder().addBinding().to(LoopbackCommunicationAdapterFactory.class);
     */
    install(new FactoryModuleBuilder().build(MyCommAdapterComponentsFactory.class));
    log.info("MyCommAdapterModule has been injected");
    vehicleCommAdaptersBinder().addBinding().to(MyCommAdapterFactory.class);
  }
}
