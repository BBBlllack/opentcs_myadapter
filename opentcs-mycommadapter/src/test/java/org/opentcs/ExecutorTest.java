package org.opentcs;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExecutorTest {

  private final static ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

  private Long startTime;

  @BeforeEach
  void start(){
    startTime = System.currentTimeMillis();
  }

  @AfterEach
  void end(){
    System.out.println("consume Time: " + (System.currentTimeMillis() - startTime) + "ms");
  }
  @Test
  void test01()
      throws InterruptedException, ExecutionException {
    List<Future<String>> futures = executor.invokeAll(List.of(
        () -> {
          Thread.sleep(1000);
          return "ok1";
        },
        () -> {
          Thread.sleep(2000);
          return "ok2";
        }
    ));

    for (Future<String> future : futures) {
      String r = future.get();
      System.out.println(r);
    }

    executor.shutdown();
  }

  @Test
  void test02(){
    ScheduledFuture<?> schedule = executor.schedule(
        () -> {
          try {
            Thread.sleep(1000);
            System.out.println("test02...");
          }
          catch (InterruptedException e) {

          }
        },
        1,
        TimeUnit.SECONDS
    );
  }



}
