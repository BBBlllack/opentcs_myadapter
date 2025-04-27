package org.opentcs;


import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleProcessModel;

public interface MyCommAdapterComponentsFactory {

  MyCommAdapter createMyCommAdapter(VehicleProcessModel vehicleProcessModel,
      int commandsCapacity, Vehicle vehicle);
}
