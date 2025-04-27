package org.opentcs;

import static java.util.Objects.requireNonNull;

import com.google.inject.Guice;
import com.google.inject.Injector;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import java.util.concurrent.ScheduledExecutorService;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;
import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;
import org.opentcs.drivers.vehicle.VehicleCommAdapterFactory;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCommAdapterFactory implements VehicleCommAdapterFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyCommAdapterFactory.class);

  private final MyCommAdapterComponentsFactory adapterFactory;

  private boolean initialized;

  @Inject
  public MyCommAdapterFactory(MyCommAdapterComponentsFactory adapterFactory) {
    this.adapterFactory = requireNonNull(adapterFactory, "MyCommAdapterComponentsFactory");
  }

  @Override
  public VehicleCommAdapterDescription getDescription() {
    return new MyCommAdapterDescription();
  }

  @Override
  public boolean providesAdapterFor(
      @Nonnull
      Vehicle vehicle
  ) {
    requireNonNull(vehicle, "vehicle");
    return true;
  }

  @Nullable
  @Override
  public VehicleCommAdapter getAdapterFor(
      @Nonnull
      Vehicle vehicle
  ) {
    requireNonNull(vehicle, "vehicle");
    VehicleProcessModel vehicleProcessModel = new VehicleProcessModel(vehicle);
    MyVehicleModel myVehicleModel = new MyVehicleModel(vehicle);
    int commandsCapacity = 1;
    return adapterFactory.createMyCommAdapter(vehicleProcessModel, commandsCapacity, vehicle);
  }

  @Override
  public void initialize() {
    if (initialized) {
      return;
    }
    initialized = true;
  }

  @Override
  public boolean isInitialized() {
    return this.initialized;
  }

  @Override
  public void terminate() {
    if (!isInitialized()) {
      return;
    }
    initialized = false;
  }
}
