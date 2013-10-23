package com.itrustcambodia.v5.support;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javapns.notification.Payload;
import javapns.notification.PushNotificationPayload;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.google.android.gcm.server.Message;
import com.google.gson.Gson;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Version;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.database.Table;
import com.itrustcambodia.pluggable.entity.Group;
import com.itrustcambodia.pluggable.entity.Role;
import com.itrustcambodia.pluggable.migration.AbstractApplicationMigrator;
import com.itrustcambodia.pluggable.utilities.GroupUtilities;
import com.itrustcambodia.pluggable.utilities.SecurityUtilities;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.v5.PushUtils;
import com.itrustcambodia.v5.entity.Application;
import com.itrustcambodia.v5.entity.City;
import com.itrustcambodia.v5.entity.Country;
import com.itrustcambodia.v5.entity.Device;
import com.itrustcambodia.v5.entity.Feedback;
import com.itrustcambodia.v5.entity.History;
import com.itrustcambodia.v5.entity.Manufacture;
import com.itrustcambodia.v5.entity.Model;
import com.itrustcambodia.v5.entity.Platform;
import com.itrustcambodia.v5.entity.Queue;
import com.itrustcambodia.v5.entity.QueueDevice;
import com.itrustcambodia.v5.entity.User;

public class ApplicationMigration extends AbstractApplicationMigrator {

    @Version(value = 0.03, description = "Install Group")
    public void patchVersion_0_03() {
        JdbcTemplate jdbcTemplate = getApplication().getBean(JdbcTemplate.class);
        Group group = GroupUtilities.createGroup(jdbcTemplate, "Simple User", "Simple User Group", false);
        List<String> roles = Arrays.asList("ROLE_REST_REFERENCE_APPLICATION", "ROLE_REST_REFERENCE_VERSION", "ROLE_REST_REFERENCE_MODEL", "ROLE_REST_REFERENCE_CITY", "ROLE_REST_REFERENCE_COUNTRY", "ROLE_REST_REFERENCE_PLATFORM", "ROLE_REST_REFERENCE_MANUFACTURE", "ROLE_REST_DEVICE_IOS", "ROLE_REST_DEVICE_ANDROID", "ROLE_PAGE_USER_PROFILE", "ROLE_PAGE_NEW_DEVICE", "ROLE_PAGE_JSON_DOC", "ROLE_PAGE_PUSH_MESSAGE", "ROLE_PAGE_DASHBOARD", "ROLE_PAGE_APPLICATION_MANAGEMENT", "ROLE_PAGE_NEW_APPLICATION", "ROLE_PAGE_EDIT_APPLICATION", "ROLE_PAGE_CITY_MANAGEMENT", "ROLE_PAGE_COUNTRY_MANAGEMENT", "ROLE_PAGE_PLATFORM_MANAGEMENT", "ROLE_PAGE_MANUFACTURE_MANAGEMENT", "ROLE_PAGE_MODEL_MANAGEMENT", "ROLE_PAGE_VERSION_MANAGEMENT", "ROLE_PAGE_MODEL_MANAGEMENT");
        for (String role : roles) {
            Role object = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Role.class) + " where " + Role.NAME + " = ?", new EntityRowMapper<Role>(Role.class), role);
            SecurityUtilities.grantAccess(jdbcTemplate, group, object);
        }
    }

    @Version(value = 0.02, description = "Install Data Reference")
    public void patchVersion_0_02() {
        JdbcTemplate jdbcTemplate = getApplication().getBean(JdbcTemplate.class);
        List<String> models = Arrays.asList("iPhone 5", "06v20_v89_gq2009hd", "86VEBC", "A10", "A100", "A21", "A50", "A777", "ADR3010", "AigoPad M60", "ALCATEL ONE TOUCH 5020D", "ASP-5300Z", "ASUS Transformer Pad TF300T", "C1505", "C3100", "C5303", "C5503", "C6502", "C6602", "C6603", "C6802", "C7500", "Dell Streak", "Desire HD", "Desire S", "e1901_v77_gq2002tc", "e1901_v77_jbl1_9p017", "e1911_v77_hjy1", "G3", "Galaxy Nexus", "generic", "GN800", "GT I9000", "GT-I8150", "GT-I8160", "GT-I8160L", "GT-I8190", "GT-I8190N", "GT-I8262", "GT-I8552", "GT-I9000", "GT-I9003", "GT-I9070", "GT-I9082", "GT-I9082L", "GT-I9100", "GT-I9100G", "GT-I9100T", "GT-I9105", "GT-I9152", "GT-I9190", "GT-I9192", "GT-I9200", "GT-I9205", "GT-I9220", "GT-I9300", "GT-I9300T", "GT-I9305", "GT-I9500", "GT-I9502",
                "GT-I9505", "GT-N5100", "GT-N5110", "GT-N7000", "GT-N7000B", "GT-N7100", "GT-N7102", "GT-N7105", "GT-N8000", "GT-N8013", "GT-P1000", "GT-P1000T", "GT-P3100", "GT-P3100B", "GT-P5100", "GT-P5110", "GT-P5113", "GT-P6200", "GT-P6200L", "GT-P6201", "GT-P6800", "GT-P7500", "GT-Q999", "GT-S5300", "GT-S5300B", "GT-S5310B", "GT-S5360", "GT-S5360B", "GT-S5570", "GT-S5570B", "GT-S5660", "GT-S5830", "GT-S5830i", "GT-S5830L", "GT-S5830T", "GT-S6102", "GT-S6102B", "GT-S6310", "GT-S6500", "GT-S6500D", "GT-S6802", "GT-S7270L", "GT-S7500", "GT-S7500L", "GT-S7562", "H7100", "HD2", "Hiya Smart S7", "HTC Butterfly", "HTC ChaCha A810e", "HTC Desire HD A9191", "HTC Desire S", "HTC EVO 3D X515m", "HTC Explorer A310e", "HTC HD2", "HTC One", "HTC PH39100", "HTC Raider X710e",
                "HTC Sensation XL with Beats Audio X315e", "HTC Sensation Z710a", "HTC Wildfire", "HTC_P515E", "HUAWEI G510-0100", "HUAWEI MT1-U06", "HUAWEI P6-U06", "i-MOBILE i-STYLE 5", "i-mobile i-STYLE Q2", "i-mobile i-style Q3", "i-mobile IQ 5", "i-mobile IQ 5.1", "i-mobile IQ 5.3", "i-mobile IQ X", "i-mobile IQ XA", "i-note WiFi 1.1", "i-STYLE Q4", "i7S", "Ideos", "IDEOS X5", "IM-A760S", "IM-A770K", "IM-A800S", "IM-A810K", "IM-A810S", "IM-A810S/K", "IM-A820L", "IM-A830L", "IM-A830S", "IM-A840S", "IM-A850K", "IM-A850L", "IM-A850S", "IM-A850S/L/K", "IM-A860L", "IM-A860S", "IM-T100K", "Incredible S", "INFINITY_LOTUS", "iPhone5", "iPhone_4S", "ISW12HT", "J-Q8D", "JS401", "K-Touch Q4", "K-Touch_W650", "KM-S200", "LG-D700", "LG-E400", "LG-E400g", "LG-E510", "LG-E612", "LG-E615",
                "LG-E730", "LG-E975", "LG-F100L", "LG-F100S", "LG-F120K", "LG-F120L", "LG-F160K", "LG-F160L", "LG-F160L/LV", "LG-F160LV", "LG-F180L", "LG-F180S", "LG-F200K", "LG-F200L", "LG-F200S", "LG-F240K", "LG-F240L", "LG-F240S", "LG-KU5400", "LG-KU5900", "LG-LU6200", "LG-LU6800", "LG-MS695", "LG-Optimus", "LG-P700", "LG-P725", "LG-P920", "LG-P940", "LG-P970", "LG-P990", "LG-SU640", "LG-SU660", "LG-SU760", "LG-SU870", "LT15i", "LT18a", "LT18i", "LT22i", "LT26i", "LT26ii", "LT26w", "LT28h", "LT30p", "MD706", "MediaPad 7 Lite", "MF8503", "MID", "MT11i", "MT15i", "MT25i", "MT27i", "myTouch_4G_Slide", "N7000+", "Newman A17", "Nexus 4", "Nexus S", "P2000", "POCKET", "Qmobile-S11", "R8015", "R8111", "S6292", "SAMSUNG-SGH-I317", "SAMSUNG-SGH-I337", "SAMSUNG-SGH-I497", "SAMSUNG-SGH-I717",
                "SAMSUNG-SGH-I747", "SAMSUNG-SGH-I997R", "SC-01C", "SC-02B", "SCH-R530M", "SCH-R920", "SGH-I997", "SGH-M919", "SGH-T889", "SGH-T959V", "SGH-T989", "SGH-T999", "SGH-T999N", "SGT-A22", "SGT-i10", "SGT-X40", "SGT-X41", "SHV-E110S", "SHV-E120K", "SHV-E120L", "SHV-E120S", "SHV-E160K", "SHV-E160L", "SHV-E160S", "SHV-E210K", "SHV-E210L", "SHV-E210S", "SHV-E220S", "SHV-E230K", "SHV-E230L", "SHV-E250K", "SHV-E250L", "SHV-E250S", "SHV-E300K", "SHV-E300S", "SHV-E330S", "SHW-M100S", "SHW-M110S", "SHW-M130K", "SHW-M180S", "SHW-M190S", "SHW-M240S", "SHW-M250K", "SHW-M250L", "SHW-M250S", "SHW-M290S", "SHW-M340L", "SHW-M340S", "SHW-M440S", "SHW-M460D", "SM-C101", "SM-N900", "SM-N900S", "SM-N900T", "SM-T211", "SMART X10", "SO-03D", "ST15i", "ST18i", "ST21i", "ST21i2", "ST25i", "ST26a",
                "ST26i", "ST27i", "SU950", "T-Mobile G2", "TGA99", "TRUE BEYOND TAB 3G", "U8185", "U8510", "U8860", "V13-PRO", "v89_jbl1a698", "WellcoM-A99", "WIKO-CINK SLIM", "WT19i", "X10 TripNMiUI-1.8.26", "X10i", "X8", "X9015", "X9017", "Xperia Ray", "yuanpeng");
        SimpleJdbcInsert insertModel = new SimpleJdbcInsert(jdbcTemplate);
        insertModel.withTableName(TableUtilities.getTableName(Model.class));
        for (String model : models) {
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(Model.NAME, model);
            insertModel.execute(fields);
        }

        SimpleJdbcInsert insertPlatform = new SimpleJdbcInsert(jdbcTemplate);
        insertPlatform.withTableName(TableUtilities.getTableName(Platform.class));
        List<String> platforms = Arrays.asList(Device.Platform.ANDROID, Device.Platform.IOS);
        for (String platform : platforms) {
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(Model.NAME, platform);
            insertPlatform.execute(fields);
        }

        SimpleJdbcInsert insertVersion = new SimpleJdbcInsert(jdbcTemplate);
        insertVersion.withTableName(TableUtilities.getTableName(com.itrustcambodia.v5.entity.Version.class));
        List<String> versions = Arrays.asList("8", "9", "10", "13", "15", "16", "17", "18", "7.0.2");
        for (String version : versions) {
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(Model.NAME, version);
            insertVersion.execute(fields);
        }

        SimpleJdbcInsert insertManufacture = new SimpleJdbcInsert(jdbcTemplate);
        insertManufacture.withTableName(TableUtilities.getTableName(Manufacture.class));
        List<String> manufactures = Arrays.asList("Acer", "Apple Inc.", "alps", "android", "asus", "Dell Inc.", "FIH", "GIONEE", "GT-I9300", "HTC", "HUAWEI", "HXXD", "i-mobile", "Imobile", "JYT", "K-Touch", "KTTech", "LGE", "MIKI", "OPPO", "PANTECH", "Qmobile", "samsung", "SEMC", "Sh!tEricsson", "Skyworth", "SMN", "Sony", "Sony Ericsson", "sprd", "TCT", "TripNDroid Mobile Eng.", "unknown", "Wiko");
        for (String manufacture : manufactures) {
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(Model.NAME, manufacture);
            insertManufacture.execute(fields);
        }

        SimpleJdbcInsert insertCountry = new SimpleJdbcInsert(jdbcTemplate);
        insertCountry.withTableName(TableUtilities.getTableName(Country.class));
        List<String> countries = Arrays.asList("Cambodia");
        for (String country : countries) {
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(Model.NAME, country);
            insertCountry.execute(fields);
        }

        SimpleJdbcInsert insertCity = new SimpleJdbcInsert(jdbcTemplate);
        insertCity.withTableName(TableUtilities.getTableName(City.class));
        List<String> cities = Arrays.asList("Phnom Penh");
        for (String city : cities) {
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(Model.NAME, city);
            insertCity.execute(fields);
        }
    }

    @Version(value = 0.01, description = "Install Project")
    public void patchVersion_0_01() {
        Table table = null;
        AbstractWebApplication application = getApplication();

        table = application.getSchema().getTable(TableUtilities.getTableName(Application.class));
        if (!table.exists()) {
            application.getSchema().createTable(Application.class, Application.SENDER_ID, Application.ANDROID_API_KEY, Application.BUNDLE_ID, Application.DESCRIPTION, Application.ID, Application.IOS_PUSH_CERTIFICATE, Application.IOS_PUSH_CERTIFICATE_PASSWORD, Application.NAME, Application.PACKAGE_ID, Application.USER_ID);
        }

        table = application.getSchema().getTable(TableUtilities.getTableName(Feedback.class));
        if (!table.exists()) {
            application.getSchema().createTable(Feedback.class, Feedback.ID, Feedback.APPLICATION_ID);
        }

        table = application.getSchema().getTable(TableUtilities.getTableName(City.class));
        if (!table.exists()) {
            application.getSchema().createTable(City.class, City.ID, City.NAME);
        }

        table = application.getSchema().getTable(TableUtilities.getTableName(Country.class));
        if (!table.exists()) {
            application.getSchema().createTable(Country.class, Country.ID, Country.NAME);
        }

        table = application.getSchema().getTable(TableUtilities.getTableName(Device.class));
        if (!table.exists()) {
            application.getSchema().createTable(Device.class, Device.APPLICATION_ID, Device.CITY_ID, Device.COUNTRY_ID, Device.FLAG, Device.ID, Device.IP, Device.MANUFACTURE_ID, Device.MODEL_ID, Device.PLATFORM_ID, Device.TOKEN, Device.VERSION_ID);
        }

        table = application.getSchema().getTable(TableUtilities.getTableName(History.class));
        if (!table.exists()) {
            application.getSchema().createTable(History.class, History.CITY, History.COUNTRY, History.ID, History.IP, History.MANUFACTURE, History.MESSAGE, History.MODEL, History.PLATFORM, History.QUEUE_DATE, History.SENT_DATE, History.STATUS, History.TOKEN, History.USER_ID, History.VERSION);
        }

        table = application.getSchema().getTable(TableUtilities.getTableName(Manufacture.class));
        if (!table.exists()) {
            application.getSchema().createTable(Manufacture.class, Manufacture.ID, Manufacture.NAME);
        }

        table = application.getSchema().getTable(TableUtilities.getTableName(Model.class));
        if (!table.exists()) {
            application.getSchema().createTable(Model.class, Model.ID, Model.NAME);
        }

        table = application.getSchema().getTable(TableUtilities.getTableName(Platform.class));
        if (!table.exists()) {
            application.getSchema().createTable(Platform.class, Platform.ID, Platform.NAME);
        }

        table = application.getSchema().getTable(TableUtilities.getTableName(Queue.class));
        if (!table.exists()) {
            application.getSchema().createTable(Queue.class, Queue.ID, Queue.MESSAGE, Queue.QUEUE_DATE, Queue.USER_ID);
        }

        table = application.getSchema().getTable(TableUtilities.getTableName(QueueDevice.class));
        if (!table.exists()) {
            application.getSchema().createTable(QueueDevice.class, QueueDevice.DEVICE_ID, QueueDevice.ID, QueueDevice.QUEUE_ID);
        }

        table = application.getSchema().getTable(TableUtilities.getTableName(com.itrustcambodia.v5.entity.Version.class));
        if (!table.exists()) {
            application.getSchema().createTable(com.itrustcambodia.v5.entity.Version.class, com.itrustcambodia.v5.entity.Version.ID, com.itrustcambodia.v5.entity.Version.NAME);
        }
    }

}
