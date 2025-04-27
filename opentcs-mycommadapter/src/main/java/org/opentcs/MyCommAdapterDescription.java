package org.opentcs;

import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;

public class MyCommAdapterDescription extends VehicleCommAdapterDescription {

  public final static String MY_ADAPTER_NAME = "MyCommAdapterDescription.description";

  public MyCommAdapterDescription() {

  }

  @Override
  public String getDescription() {
    return MY_ADAPTER_NAME;
  }

  @Override
  public boolean isSimVehicleCommAdapter() {
    return true;
  }
}
