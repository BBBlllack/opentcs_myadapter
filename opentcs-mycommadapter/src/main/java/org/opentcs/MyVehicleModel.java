package org.opentcs;

import jakarta.annotation.Nonnull;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleProcessModel;

public class MyVehicleModel extends VehicleProcessModel {
  /**
   * Creates a new instance.
   *
   * @param attachedVehicle The vehicle attached to the new instance.
   */
  public MyVehicleModel(
      @Nonnull
      Vehicle attachedVehicle
  ) {
    super(attachedVehicle);
  }

}
