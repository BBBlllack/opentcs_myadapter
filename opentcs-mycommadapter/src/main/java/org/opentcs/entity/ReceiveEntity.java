package org.opentcs.entity;

import lombok.Builder;
import lombok.ToString;

/**
 * 接收小车发来的响应消息对象
 */
@Builder
@ToString
public class ReceiveEntity {
  // 车辆当前执行的操作种类
  private VehicleOperation operation;
  // 车辆名称
  private String vehicleName;
  // 时间戳
  private Long timestamp;
  // 车辆状态, IDLE, CHARGING...
  private String state;
  // 电量
  private Double battery;
  // 当前位置
  private String position;
  // 下达的指令
  private Object instruction;
  // 指令完成反馈, failed, done
  // 如果是握手信息反馈, 此处放握手消息而不是指令
  private String instructionFeedBack;
}
