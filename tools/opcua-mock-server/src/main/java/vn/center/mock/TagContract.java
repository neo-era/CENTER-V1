package vn.center.mock;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

/**
 * Định nghĩa danh mục tag OPC-UA của tủ chiếu sáng theo hợp đồng công khai
 * (docs/architecture/opc-ua-tag-contract.md). Mock server tạo node cho toàn bộ tag này.
 *
 * <p>Kiểu: Word = UInt16, Float = IEEE-754 32-bit, Boolean, String (UTF-8).
 */
final class TagContract {

  private TagContract() {}

  /** Một tag: tên, kiểu dữ liệu OPC-UA, có cho ghi không, giá trị khởi tạo. */
  record TagDef(String name, NodeId dataType, boolean writable, Object initialValue) {}

  // --- Tên các tag được simulation tham chiếu trực tiếp ---
  static final String SETTIME_WORD = "1_07_SETTIME";
  static final String SETTIME_LAMP = "1_20_SETTIME_LAMP";
  static final String MAN_CTR = "1_33_MAN_CTR";
  static final String CONTACTOR_C1 = "1_30_CONTACTOR_C1";
  static final String LUX_VALUE = "1_293_LUX_VALUE";
  static final String LUX_LEVEL1 = "1_291_LUX_LEVEL1";
  static final String PHOTOCEL_SIGNAL = "1_44_PHOTOCEL_SIGNAL";
  static final String FREQUENCY = "2_28_F";
  static final String KWH = "2_29_KWH";
  static final String SETTIME_SYS = "SETTIME";

  static final List<TagDef> ALL = build();

  private static TagDef word(String n, boolean rw, int init) {
    return new TagDef(n, Identifiers.UInt16, rw, ushort(init));
  }

  private static TagDef flt(String n) {
    return new TagDef(n, Identifiers.Float, false, 0.0f);
  }

  private static TagDef fltRw(String n) {
    return new TagDef(n, Identifiers.Float, true, 0.0f);
  }

  private static TagDef bool(String n, boolean rw, boolean init) {
    return new TagDef(n, Identifiers.Boolean, rw, init);
  }

  private static TagDef str(String n, String init) {
    return new TagDef(n, Identifiers.String, false, init);
  }

  private static List<TagDef> build() {
    List<TagDef> t = new ArrayList<>();

    // ===== Nhóm 1: thời gian, lịch, trạng thái =====
    // Cài đặt thời gian (setpoint)
    t.add(word("1_00_SEC_SP", true, 0));
    t.add(word("1_01_MIN_SP", true, 0));
    t.add(word("1_02_HOUR_SP", true, 0));
    t.add(word("1_03_DOW_SP", true, 0));
    t.add(word("1_04_DAY_SP", true, 1));
    t.add(word("1_05_MONT_SP", true, 1));
    t.add(word("1_06_YEAR_SP", true, 2026));
    t.add(word(SETTIME_WORD, true, 0));
    // Lịch tắt/mở/tiết giảm
    t.add(word("1_08_HOUR_C1_ON", true, 18));
    t.add(word("1_09_MIN_C1_ON", true, 0));
    t.add(word("1_10_HOUR_C1_OFF", true, 5));
    t.add(word("1_11_MIN_C1_OFF", true, 30));
    t.add(word("1_12_HOUR_C2_ON", true, 18));
    t.add(word("1_13_MIN_C2_ON", true, 0));
    t.add(word("1_14_HOUR_C2_OFF", true, 5));
    t.add(word("1_15_MIN_C2_OFF", true, 30));
    t.add(word("1_16_HOUR_DIM_ON", true, 22));
    t.add(word("1_17_MIN_DIM_ON", true, 0));
    t.add(word("1_18_HOUR_DIM_OFF", true, 4));
    t.add(word("1_19_MIN_DIM_OFF", true, 0));
    t.add(word(SETTIME_LAMP, true, 0));
    t.add(word("1_21_SETUP", true, 0));
    // Thời gian thực hiện tại (PV)
    t.add(word("1_22_SEC_PV", false, 0));
    t.add(word("1_23_MIN_PV", false, 0));
    t.add(word("1_24_HOUR_PV", false, 0));
    t.add(word("1_25_DOW_PV", false, 0));
    t.add(word("1_26_DAY_PV", false, 1));
    t.add(word("1_27_MONT_PV", false, 1));
    t.add(word("1_28_YEAR_PV", false, 2026));
    t.add(word("1_29_ERR_CODE", false, 0));
    // LUX
    t.add(word(LUX_LEVEL1, true, 50));
    t.add(word("1_292_LUX_LEVEL2", true, 100));
    t.add(word(LUX_VALUE, false, 0));
    // Contactor & điều khiển
    t.add(bool(CONTACTOR_C1, false, false));
    t.add(bool("1_31_CONTACTOR_C2", false, false));
    t.add(bool("1_32_CONTACTOR_C3", false, false));
    t.add(bool(MAN_CTR, true, false));
    t.add(bool("1_34_ERR_VOLT", false, false));
    t.add(bool("1_35_SW_HAND_STATUS", false, false));
    t.add(bool("1_36_PHOTOCELL_1", false, false));
    t.add(bool("1_37_PHOTOCELL_2", false, false));
    t.add(bool("1_38_ERR_TIME", true, false));
    t.add(bool("1_39_MODE1", true, true));
    t.add(bool("1_40_MODE2", true, false));
    t.add(bool("1_41_EN_C1", true, true));
    t.add(bool("1_42_EN_C2", true, true));
    t.add(bool("1_43_ERR_CONNECT_ZEN", false, false));
    t.add(bool(PHOTOCEL_SIGNAL, false, false));
    t.add(bool("1_45_EN_DO", true, true));
    t.add(bool("1_46_ST_DO", false, false));
    t.add(bool("1_47_ERR_PW_OFF_PANEL", false, false));
    t.add(bool("1_48_ERR_PW_OFF_CTT", false, false));
    // Truyền thông / SIM / GPS
    t.add(word("1_49_INFO_SIM", false, 1)); // 1: Viettel
    t.add(word("1_50_INFO_ST_SIM", false, 4)); // 4: 4G
    t.add(word("1_51_INFO_WAVE", false, 25)); // 0..31
    t.add(word("1_52_PERCENT_BATTERY", false, 100));
    t.add(str("1_53_GPS", "10.762622,106.660172"));
    t.add(str("1_54_SERI_SIM", "89840000000000000001"));

    // ===== Nhóm 2: đo lường điện =====
    // Điện áp
    for (String n :
        new String[] {
          "2_00_V1N", "2_01_V2N", "2_02_V3N", "2_03_VLN",
          "2_04_V12", "2_05_V23", "2_06_V31", "2_07_VLL"
        }) {
      t.add(flt(n));
    }
    // Dòng điện
    for (String n : new String[] {"2_08_I1", "2_09_I2", "2_10_I3", "2_11_I"}) {
      t.add(flt(n));
    }
    // Công suất P/S/Q
    for (String n :
        new String[] {
          "2_12_P1", "2_13_P2", "2_14_P3",
          "2_15_S1", "2_16_S2", "2_17_S3",
          "2_18_Q1", "2_19_Q2", "2_20_Q3",
          "2_21_TOTAL_P", "2_22_TOTAL_S", "2_23_TOTAL_Q"
        }) {
      t.add(flt(n));
    }
    // Cosphi, tần số, điện năng
    for (String n :
        new String[] {"2_24_PF1", "2_25_PF2", "2_26_PF3", "2_27_PF", FREQUENCY, KWH, "2_30_KVAH", "2_31_KVARH"}) {
      t.add(flt(n));
    }
    // Ngưỡng cảnh báo (R/W)
    for (String n :
        new String[] {
          "2_32_HI_V", "2_33_LO_V", "2_34_HYS_V",
          "2_35_HI_I1", "2_36_LO_I1", "2_37_HYS_I1",
          "2_38_HI_I2", "2_39_LO_I2", "2_40_HYS_I2",
          "2_41_HI_I3", "2_42_LO_I3", "2_43_HYS_I3",
          "2_44_NO_LOAD_I1", "2_45_NO_LOAD_I2", "2_46_NO_LOAD_I3"
        }) {
      t.add(fltRw(n));
    }
    t.add(word("2_47_ERR_CODE_PM", false, 0));
    t.add(word("2_471_HI_I_LEAK", true, 30));
    t.add(word("2_472_I_LEAK", false, 0));
    // Cho phép kiểm tra lỗi (R/W boolean)
    for (String n :
        new String[] {
          "2_48_EN_HI_V", "2_49_EN_LO_V",
          "2_50_EN_HI_I1", "2_51_EN_LO_I1",
          "2_52_EN_HI_I2", "2_53_EN_LO_I2",
          "2_54_EN_HI_I3", "2_55_EN_LO_I3"
        }) {
      t.add(bool(n, true, false));
    }
    // Cờ lỗi đo lường (RO)
    for (String n :
        new String[] {
          "2_56_ERR_HI_V1", "2_57_ERR_LO_V1", "2_58_ERR_HI_V2", "2_59_ERR_LO_V2",
          "2_60_ERR_HI_V3", "2_61_ERR_LO_V3", "2_62_ERR_HI_I1", "2_63_ERR_LO_I1",
          "2_64_ERR_HI_I2", "2_65_ERR_LO_I2", "2_66_ERR_HI_I3", "2_67_ERR_LO_I3",
          "2_68_ERR_CONNECT_PM"
        }) {
      t.add(bool(n, false, false));
    }
    t.add(bool("2_69_SETUP_PM", true, false));
    for (String n :
        new String[] {"2_70_ERR_NO_LOAD_I1", "2_71_ERR_NO_LOAD_I2", "2_72_ERR_NO_LOAD_I3", "2_73_ERR_I_LEAK"}) {
      t.add(bool(n, false, false));
    }

    // ===== Tag hệ thống =====
    t.add(bool("NOTCONNECT", false, false));
    t.add(bool(SETTIME_SYS, true, false));

    return List.copyOf(t);
  }
}
