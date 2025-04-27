package org.opentcs;


import org.opentcs.drivers.vehicle.VehicleProcessModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyUpdateVehicleModelUtils {
  private final static Logger LOGGER = LoggerFactory.getLogger(MyUpdateVehicleModelUtils.class);

  public static void updatePosition(VehicleProcessModel model, String position) {
    LOGGER.info("Updating position: " + position);
    if (position != null) {
      try {
        model.setPosition(position);
      }
      catch (Exception e) {
        LOGGER.error(e.getMessage());
      }
    }
  }
}
