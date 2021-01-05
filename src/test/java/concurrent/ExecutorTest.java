package concurrent;

import org.junit.Test;

import java.util.concurrent.Executor;


public class ExecutorTest {
  @Test
  public void test() {
    Executor executor = t -> {
      System.out.println(t);
    };
    executor.execute(() -> {
      System.out.println("hello");
    });
  }
}
