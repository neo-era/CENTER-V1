package vn.center.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Điểm khởi chạy mock OPC-UA server. Dừng bằng Ctrl+C. */
public final class MockServerApplication {

  private static final Logger log = LoggerFactory.getLogger(MockServerApplication.class);

  private MockServerApplication() {}

  public static void main(String[] args) throws Exception {
    MockOpcUaServer mock = new MockOpcUaServer();
    mock.startup().get();
    log.info(
        "Mock server đã sẵn sàng. Tủ mẫu: {}. Namespace: {}",
        CabinetNamespace.CABINET_CODE,
        CabinetNamespace.NAMESPACE_URI);

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  log.info("Đang dừng mock server...");
                  try {
                    mock.shutdown().get();
                  } catch (Exception e) {
                    Thread.currentThread().interrupt();
                  }
                }));

    Thread.currentThread().join(); // chạy tới khi bị dừng
  }
}
