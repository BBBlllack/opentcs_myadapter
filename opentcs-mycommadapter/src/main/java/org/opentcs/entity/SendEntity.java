package org.opentcs.entity;

import java.util.Map;
import lombok.Builder;
import lombok.ToString;

/**
 * 给小车发送的消息对象
 */
@Builder
@ToString
public class SendEntity {
  public VehicleOperation operation;
  public Object instruction; // 如果是握手信息, 此处放握手消息而不是指令
  public String message;
  public Map<String, Object> others;
  public Long timestamp;
}
