package vn.center.mock;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespaceWithLifecycle;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Không gian địa chỉ OPC-UA cho 1 tủ mẫu, hiện thực hợp đồng tag
 * (docs/architecture/opc-ua-tag-contract.md) và mô phỏng telemetry biến thiên theo thời gian.
 */
final class CabinetNamespace extends ManagedNamespaceWithLifecycle {

  static final String NAMESPACE_URI = "urn:center-v1:lighting-cabinet";
  static final String CABINET_CODE = "CB-DEMO-001";

  private static final Logger log = LoggerFactory.getLogger(CabinetNamespace.class);
  private static final UByte ACCESS_RO = ubyte(AccessLevel.CurrentRead.getValue());
  private static final UByte ACCESS_RW =
      ubyte(AccessLevel.CurrentRead.getValue() | AccessLevel.CurrentWrite.getValue());

  private final SubscriptionModel subscriptionModel;
  private final Map<String, UaVariableNode> nodes = new HashMap<>();
  private final ScheduledExecutorService simulator =
      Executors.newSingleThreadScheduledExecutor(r -> {
        Thread th = new Thread(r, "cabinet-simulator");
        th.setDaemon(true);
        return th;
      });

  private double kwhAccumulator = 0.0;
  private long tick = 0;

  CabinetNamespace(OpcUaServer server) {
    super(server, NAMESPACE_URI);
    this.subscriptionModel = new SubscriptionModel(server, this);
    getLifecycleManager().addLifecycle(subscriptionModel);
    getLifecycleManager().addStartupTask(this::createNodes);
    getLifecycleManager().addStartupTask(this::startSimulation);
    getLifecycleManager().addShutdownTask(this::stopSimulation);
  }

  // Chuyển tiếp các sự kiện monitored-item cho SubscriptionModel (để client subscribe nhận dữ liệu).
  @Override
  public void onDataItemsCreated(List<DataItem> dataItems) {
    subscriptionModel.onDataItemsCreated(dataItems);
  }

  @Override
  public void onDataItemsModified(List<DataItem> dataItems) {
    subscriptionModel.onDataItemsModified(dataItems);
  }

  @Override
  public void onDataItemsDeleted(List<DataItem> dataItems) {
    subscriptionModel.onDataItemsDeleted(dataItems);
  }

  @Override
  public void onMonitoringModeChanged(List<MonitoredItem> monitoredItems) {
    subscriptionModel.onMonitoringModeChanged(monitoredItems);
  }

  private void createNodes() {
    UaFolderNode root =
        new UaFolderNode(
            getNodeContext(),
            newNodeId(CABINET_CODE),
            newQualifiedName(CABINET_CODE),
            LocalizedText.english("Tu chieu sang " + CABINET_CODE));
    getNodeManager().addNode(root);
    root.addReference(
        new Reference(
            root.getNodeId(), Identifiers.Organizes, Identifiers.ObjectsFolder.expanded(), false));

    for (TagContract.TagDef tag : TagContract.ALL) {
      UByte access = tag.writable() ? ACCESS_RW : ACCESS_RO;
      UaVariableNode node =
          new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
              .setNodeId(newNodeId(CABINET_CODE + "/" + tag.name()))
              .setAccessLevel(access)
              .setUserAccessLevel(access)
              .setBrowseName(newQualifiedName(tag.name()))
              .setDisplayName(LocalizedText.english(tag.name()))
              .setDataType(tag.dataType())
              .setTypeDefinition(Identifiers.BaseDataVariableType)
              .build();
      node.setValue(new DataValue(new Variant(tag.initialValue())));
      getNodeManager().addNode(node);
      root.addOrganizes(node);
      nodes.put(tag.name(), node);
    }
    log.info("Đã tạo {} tag cho tủ {}", nodes.size(), CABINET_CODE);
  }

  private void startSimulation() {
    simulator.scheduleAtFixedRate(this::tickSafe, 1, 1, TimeUnit.SECONDS);
    log.info("Bắt đầu mô phỏng telemetry (chu kỳ 1s)");
  }

  private void stopSimulation() {
    simulator.shutdownNow();
  }

  private void tickSafe() {
    try {
      simulateTick();
    } catch (RuntimeException e) {
      log.warn("Lỗi trong vòng mô phỏng: {}", e.toString());
    }
  }

  /** Một bước mô phỏng: cập nhật thời gian PV, đèn theo lux, đo điện, và phản ánh các lệnh ghi. */
  private void simulateTick() {
    tick++;

    // 1) Thời gian thực hiện tại (PV) theo đồng hồ hệ thống
    LocalDateTime now = LocalDateTime.now();
    setWord("1_22_SEC_PV", now.getSecond());
    setWord("1_23_MIN_PV", now.getMinute());
    setWord("1_24_HOUR_PV", now.getHour());
    setWord("1_25_DOW_PV", now.getDayOfWeek().getValue() % 7); // 0=CN
    setWord("1_26_DAY_PV", now.getDayOfMonth());
    setWord("1_27_MONT_PV", now.getMonthValue());
    setWord("1_28_YEAR_PV", now.getYear());

    // 2) LUX theo chu kỳ ngày/đêm + photocell
    int hour = now.getHour();
    boolean daytime = hour >= 6 && hour < 18;
    int lux =
        daytime
            ? (int) (300 + 400 * Math.sin(Math.PI * (hour - 6) / 12.0) + rnd(0, 50))
            : (int) rnd(0, 8);
    setWord(TagContract.LUX_VALUE, lux);
    int level1 = getWord(TagContract.LUX_LEVEL1);
    boolean dark = lux < level1;
    setBool(TagContract.PHOTOCEL_SIGNAL, dark);
    setBool("1_36_PHOTOCELL_1", dark);
    setBool("1_37_PHOTOCELL_2", !dark);

    // 3) Lệnh ghi -> phản ánh PV/ngõ ra (xem hợp đồng tag mục 5)
    boolean manualOn = getBool(TagContract.MAN_CTR);
    boolean lampsOn = manualOn || dark; // đèn bật khi điều khiển tay hoặc trời tối
    setBool(TagContract.CONTACTOR_C1, lampsOn);
    setBool("1_31_CONTACTOR_C2", lampsOn);
    setBool("1_32_CONTACTOR_C3", lampsOn && hour >= 22); // tiết giảm khuya

    if (getWord(TagContract.SETTIME_WORD) == 1) {
      // Nạp setpoint thời gian vào PV rồi tự reset
      setWord("1_22_SEC_PV", getWord("1_00_SEC_SP"));
      setWord("1_23_MIN_PV", getWord("1_01_MIN_SP"));
      setWord("1_24_HOUR_PV", getWord("1_02_HOUR_SP"));
      setWord(TagContract.SETTIME_WORD, 0);
      log.info("Đã nạp setpoint thời gian (1_07_SETTIME)");
    }
    if (getWord(TagContract.SETTIME_LAMP) == 1) {
      setWord(TagContract.SETTIME_LAMP, 0);
      log.info("Đã nạp lịch tắt/mở/tiết giảm (1_20_SETTIME_LAMP)");
    }
    if (getBool(TagContract.SETTIME_SYS)) {
      setBool(TagContract.SETTIME_SYS, false);
      log.info("Đã đồng bộ thời gian thực (SETTIME)");
    }

    // 4) Đo lường điện 3 pha
    double pf = 0.92 + rnd(-0.02, 0.02);
    double freq = 50.0 + rnd(-0.05, 0.05);
    double totalP = 0;
    double totalS = 0;
    double totalQ = 0;
    String[] vTags = {"2_00_V1N", "2_01_V2N", "2_02_V3N"};
    String[] iTags = {"2_08_I1", "2_09_I2", "2_10_I3"};
    String[] pTags = {"2_12_P1", "2_13_P2", "2_14_P3"};
    String[] sTags = {"2_15_S1", "2_16_S2", "2_17_S3"};
    String[] qTags = {"2_18_Q1", "2_19_Q2", "2_20_Q3"};
    String[] pfTags = {"2_24_PF1", "2_25_PF2", "2_26_PF3"};
    double vSum = 0;
    for (int ph = 0; ph < 3; ph++) {
      double v = 220 + 4 * Math.sin(tick / 30.0 + ph) + rnd(-1.5, 1.5);
      double i = lampsOn ? 9 + 2 * Math.sin(tick / 20.0 + ph) + rnd(-0.3, 0.3) : 0.15 + rnd(0, 0.05);
      double s = v * i / 1000.0; // kVA
      double p = s * pf; // kW
      double q = Math.sqrt(Math.max(0, s * s - p * p)); // kVAr
      setFloat(vTags[ph], v);
      setFloat(iTags[ph], i);
      setFloat(pTags[ph], p);
      setFloat(sTags[ph], s);
      setFloat(qTags[ph], q);
      setFloat(pfTags[ph], pf);
      totalP += p;
      totalS += s;
      totalQ += q;
      vSum += v;
    }
    double vAvg = vSum / 3.0;
    setFloat("2_03_VLN", vAvg);
    setFloat("2_04_V12", vAvg * Math.sqrt(3));
    setFloat("2_05_V23", vAvg * Math.sqrt(3));
    setFloat("2_06_V31", vAvg * Math.sqrt(3));
    setFloat("2_07_VLL", vAvg * Math.sqrt(3));
    setFloat("2_11_I", (getFloat("2_08_I1") + getFloat("2_09_I2") + getFloat("2_10_I3")) / 3.0);
    setFloat("2_21_TOTAL_P", totalP);
    setFloat("2_22_TOTAL_S", totalS);
    setFloat("2_23_TOTAL_Q", totalQ);
    setFloat("2_27_PF", pf);
    setFloat(TagContract.FREQUENCY, freq);

    // Điện năng tích lũy (kWh) — mỗi tick = 1 giây
    kwhAccumulator += totalP / 3600.0;
    setFloat(TagContract.KWH, kwhAccumulator);
    setFloat("2_30_KVAH", kwhAccumulator * 1.08);
    setFloat("2_31_KVARH", kwhAccumulator * 0.42);
  }

  // ---- Helpers đọc/ghi node ----

  private void set(String name, Object value) {
    UaVariableNode node = nodes.get(name);
    if (node != null) {
      node.setValue(new DataValue(new Variant(value), StatusCode.GOOD, DateTime.now()));
    }
  }

  private void setWord(String name, int v) {
    set(name, ushort(Math.max(0, Math.min(65535, v))));
  }

  private void setFloat(String name, double v) {
    set(name, (float) v);
  }

  private void setBool(String name, boolean v) {
    set(name, v);
  }

  private int getWord(String name) {
    Object o = raw(name);
    return (o instanceof UShort u) ? u.intValue() : 0;
  }

  private float getFloat(String name) {
    Object o = raw(name);
    return (o instanceof Float f) ? f : 0f;
  }

  private boolean getBool(String name) {
    return Boolean.TRUE.equals(raw(name));
  }

  private Object raw(String name) {
    UaVariableNode node = nodes.get(name);
    return node == null ? null : node.getValue().getValue().getValue();
  }

  private static double rnd(double min, double max) {
    return ThreadLocalRandom.current().nextDouble(min, max);
  }
}
