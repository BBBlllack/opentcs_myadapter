package org.opentcs.entity;

import cn.hutool.core.util.StrUtil;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

public enum VehicleOperation {
  HANDSHAKE, // 握手信息, 协商加密方式, 通告车辆名称
  MOV,
  CHARGE,
  LOAD,
  UNLOAD,
  NOP;

  public static VehicleOperation fromString(String string) {
    if (string == null) {
      return NOP;
    }
    return switch (string) {
      case "move" -> MOV;
      case "charge" -> CHARGE;
      case "load" -> LOAD;
      case "unload" -> UNLOAD;
      case "nop" -> NOP;
      default -> NOP;
    };
  }
}
