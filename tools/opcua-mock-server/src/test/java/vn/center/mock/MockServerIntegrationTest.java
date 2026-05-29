package vn.center.mock;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Kiểm tra: server khởi động, client đọc telemetry, và lệnh ghi R/W được phản ánh vào PV. */
class MockServerIntegrationTest {

  private MockOpcUaServer mock;

  @BeforeEach
  void startServer() throws Exception {
    mock = new MockOpcUaServer();
    mock.startup().get(15, SECONDS);
  }

  @AfterEach
  void stopServer() throws Exception {
    if (mock != null) {
      mock.shutdown().get(15, SECONDS);
    }
  }

  @Test
  void readsTelemetryAndReflectsWrites() throws Exception {
    String endpointUrl =
        "opc.tcp://"
            + MockOpcUaServer.HOSTNAME
            + ":"
            + MockOpcUaServer.TCP_BIND_PORT
            + MockOpcUaServer.ENDPOINT_PATH;

    OpcUaClient client =
        OpcUaClient.create(
            endpointUrl,
            endpoints ->
                endpoints.stream()
                    .filter(e -> SecurityPolicy.None.getUri().equals(e.getSecurityPolicyUri()))
                    .findFirst(),
            cfg ->
                cfg.setApplicationName(LocalizedText.english("center-v1-test-client"))
                    .setApplicationUri("urn:center-v1:test-client")
                    .build());
    client.connect().get(15, SECONDS);
    try {
      UShort nsIndex = client.getNamespaceTable().getIndex(CabinetNamespace.NAMESPACE_URI);
      assertNotNull(nsIndex, "Namespace của tủ phải được đăng ký");
      int ns = nsIndex.intValue();

      // 1) Đọc tần số lưới — phải có giá trị hợp lệ (~50Hz)
      DataValue freq = read(client, ns, "2_28_F");
      assertTrue(freq.getStatusCode().isGood(), "Đọc 2_28_F phải GOOD");
      float f = (Float) freq.getValue().getValue();
      assertTrue(f > 45 && f < 55, "Tần số phải quanh 50Hz, nhận: " + f);

      // 2) Đọc giá trị LUX — node tồn tại và GOOD
      DataValue lux = read(client, ns, "1_293_LUX_VALUE");
      assertTrue(lux.getStatusCode().isGood(), "Đọc LUX phải GOOD");
      assertNotNull(lux.getValue().getValue());

      // 3) Ghi 1_33_MAN_CTR=true -> sau 1 chu kỳ mô phỏng, 1_30_CONTACTOR_C1 phải = true
      NodeId manCtr = new NodeId(ns, CabinetNamespace.CABINET_CODE + "/1_33_MAN_CTR");
      StatusCode wc =
          client.writeValue(manCtr, new DataValue(new Variant(true), null, null)).get(10, SECONDS);
      assertTrue(wc.isGood(), "Ghi MAN_CTR phải GOOD");

      Thread.sleep(1600); // chờ một chu kỳ mô phỏng (1s)

      DataValue c1 = read(client, ns, "1_30_CONTACTOR_C1");
      assertEquals(
          Boolean.TRUE,
          c1.getValue().getValue(),
          "Contactor C1 phải bật sau khi điều khiển tay MAN_CTR=true");
    } finally {
      client.disconnect().get(10, SECONDS);
    }
  }

  private static DataValue read(OpcUaClient client, int ns, String tag) throws Exception {
    NodeId nodeId = new NodeId(ns, CabinetNamespace.CABINET_CODE + "/" + tag);
    return client.readValue(0, TimestampsToReturn.Both, nodeId).get(10, SECONDS);
  }
}
