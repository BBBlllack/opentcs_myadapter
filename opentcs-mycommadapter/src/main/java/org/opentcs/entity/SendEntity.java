package org.opentcs.entity;

import java.util.Map;
import org.opentcs.drivers.vehicle.MovementCommand;


/**
 * 给小车发送的消息对象
 */

public class SendEntity {
  private VehicleOperation operation;
  private Object instruction;
  private String message; // 如果是握手信息, 此处放握手消息
  private Map<String, Object> others;
  private Long timestamp;

  private SendEntity() {
  }

  private SendEntity(
      VehicleOperation operation, Object instruction, String message, Map<String, Object> others,
      Long timestamp
  ) {
    this.operation = operation;
    this.instruction = instruction;
    this.message = message;
    this.others = others;
    this.timestamp = timestamp;
  }

  public static SendEntityBuilder builder(){
    return new SendEntityBuilder();
  }

  public static class SendEntityBuilder {
    private VehicleOperation operation;
    private Object instruction; // 如果是握手信息, 此处放握手消息而不是指令
    private String message;
    private Map<String, Object> others;
    private Long timestamp;

    public SendEntityBuilder operation(final VehicleOperation operation){
      this.operation = operation;
      return this;
    }
    public SendEntityBuilder instruction(final Object instruction){
      this.instruction = instruction;
      return this;
    }
    public SendEntityBuilder message(final String message){
      this.message = message;
      return this;
    }
    public SendEntityBuilder others(final Map<String, Object> others){
      this.others = others;
      return this;
    }
    public SendEntityBuilder timestamp(final Long timestamp){
      this.timestamp = timestamp;
      return this;
    }
    public SendEntity build(){
      return new SendEntity(operation, instruction, message, others, timestamp);
    }
  }

  public VehicleOperation getOperation() {
    return operation;
  }

  public void setOperation(VehicleOperation operation) {
    this.operation = operation;
  }

  public Object getInstruction() {
    return instruction;
  }

  public void setInstruction(MovementCommand instruction) {
    this.instruction = instruction;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Map<String, Object> getOthers() {
    return others;
  }

  public void setOthers(Map<String, Object> others) {
    this.others = others;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return "SendEntity{" +
        "operation=" + operation +
        ", instruction=" + instruction +
        ", message='" + message + '\'' +
        ", others=" + others +
        ", timestamp=" + timestamp +
        '}';
  }
}
