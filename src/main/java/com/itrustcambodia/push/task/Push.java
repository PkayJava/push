package com.itrustcambodia.push.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.Payload;
import javapns.notification.PushNotificationPayload;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.quartz.Job;
import com.itrustcambodia.pluggable.quartz.Scheduled;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.push.entity.Application;
import com.itrustcambodia.push.entity.City;
import com.itrustcambodia.push.entity.Country;
import com.itrustcambodia.push.entity.Device;
import com.itrustcambodia.push.entity.History;
import com.itrustcambodia.push.entity.Manufacture;
import com.itrustcambodia.push.entity.Model;
import com.itrustcambodia.push.entity.Platform;
import com.itrustcambodia.push.entity.Queue;
import com.itrustcambodia.push.entity.QueueDevice;
import com.itrustcambodia.push.entity.Version;

@DisallowConcurrentExecution
@Scheduled(cron = "0/2 * * * * ?")
public class Push extends Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(Push.class);

    @Override
    public void process(AbstractWebApplication application, JobExecutionContext context) {

        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        Gson gson = application.getGson();

        Queue queue = null;
        try {
            queue = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Queue.class) + " where " + Queue.QUEUE_DATE + " <= now() " + " order by " + Queue.QUEUE_DATE + " asc limit 1", new EntityRowMapper<Queue>(Queue.class));
        } catch (EmptyResultDataAccessException e) {
        }

        if (queue != null) {

            Map<Long, String> models = new HashMap<Long, String>();
            for (Model model : jdbcTemplate.query("select * from " + TableUtilities.getTableName(Model.class), new EntityRowMapper<Model>(Model.class))) {
                models.put(model.getId(), model.getName());
            }
            Map<Long, String> manufactures = new HashMap<Long, String>();
            for (Manufacture manufacture : jdbcTemplate.query("select * from " + TableUtilities.getTableName(Manufacture.class), new EntityRowMapper<Manufacture>(Manufacture.class))) {
                manufactures.put(manufacture.getId(), manufacture.getName());
            }
            Map<Long, String> versions = new HashMap<Long, String>();
            for (Version version : jdbcTemplate.query("select * from " + TableUtilities.getTableName(Version.class), new EntityRowMapper<Version>(Version.class))) {
                versions.put(version.getId(), version.getName());
            }
            Map<Long, String> platforms = new HashMap<Long, String>();
            for (Platform platform : jdbcTemplate.query("select * from " + TableUtilities.getTableName(Platform.class), new EntityRowMapper<Platform>(Platform.class))) {
                platforms.put(platform.getId(), platform.getName());
            }
            Map<Long, String> cities = new HashMap<Long, String>();
            for (City city : jdbcTemplate.query("select * from " + TableUtilities.getTableName(City.class), new EntityRowMapper<City>(City.class))) {
                cities.put(city.getId(), city.getName());
            }
            Map<Long, String> countries = new HashMap<Long, String>();
            for (Country country : jdbcTemplate.query("select * from " + TableUtilities.getTableName(Country.class), new EntityRowMapper<Country>(Country.class))) {
                countries.put(country.getId(), country.getName());
            }

            LOGGER.info("broadcast message : queue id '{}' message '{}'", queue.getId(), queue.getMessage());
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName(TableUtilities.getTableName(History.class));
            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                }
                List<Device> devices = jdbcTemplate.query("select device.* from " + TableUtilities.getTableName(Device.class) + " device inner join " + TableUtilities.getTableName(QueueDevice.class) + " push_queue_device on device." + Device.ID + " = push_queue_device." + QueueDevice.DEVICE_ID + " where push_queue_device." + QueueDevice.QUEUE_ID + " = ? and device." + Device.FLAG + " = ? limit 1000", new EntityRowMapper<Device>(Device.class), queue.getId(), Device.Flag.ACTIVE);
                if (devices == null || devices.isEmpty()) {
                    jdbcTemplate.update("delete from " + TableUtilities.getTableName(Queue.class) + " where " + Queue.ID + " = ?", queue.getId());
                    break;
                }

                Map<Long, List<Device>> androids = new HashMap<Long, List<Device>>();
                Map<Long, List<Device>> ioss = new HashMap<Long, List<Device>>();
                List<Device> cancels = new ArrayList<Device>();

                for (Device device : devices) {
                    if (platforms.get(device.getPlatformId()).equals(Device.Platform.ANDROID)) {
                        if (androids.get(device.getApplicationId()) == null) {
                            androids.put(device.getApplicationId(), new ArrayList<Device>());
                        }
                        androids.get(device.getApplicationId()).add(device);
                    } else if (platforms.get(device.getPlatformId()).equals(Device.Platform.IOS)) {
                        if (ioss.get(device.getApplicationId()) == null) {
                            ioss.put(device.getApplicationId(), new ArrayList<Device>());
                        }
                        ioss.get(device.getApplicationId()).add(device);
                    } else {
                        cancels.add(device);
                    }
                }

                if (!cancels.isEmpty()) {
                    for (Device device : cancels) {
                        Map<String, Object> fields = new HashMap<String, Object>();
                        fields.put(History.IP, device.getIp());
                        fields.put(History.MANUFACTURE, manufactures.get(device.getManufactureId()));
                        fields.put(History.MESSAGE, queue.getMessage());
                        fields.put(History.MODEL, models.get(device.getModelId()));
                        fields.put(History.PLATFORM, platforms.get(device.getId()));
                        fields.put(History.QUEUE_DATE, queue.getQueueDate());
                        fields.put(History.SENT_DATE, new Date());
                        fields.put(History.TOKEN, device.getToken());
                        fields.put(History.CITY, cities.get(device.getCityId()));
                        fields.put(History.COUNTRY, countries.get(device.getCountryId()));
                        fields.put(History.VERSION, versions.get(device.getVersionId()));
                        fields.put(History.STATUS, History.Status.CANCEL);
                        fields.put(History.USER_ID, queue.getUserId());
                        fields.put(History.APPLICATION, jdbcTemplate.queryForObject("select " + Application.NAME + " from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", String.class, device.getApplicationId()));
                        insert.execute(fields);
                        jdbcTemplate.update("delete from " + TableUtilities.getTableName(QueueDevice.class) + " where " + QueueDevice.DEVICE_ID + " = ? and " + QueueDevice.QUEUE_ID + " = ?", device.getId(), queue.getId());
                    }
                }

                if (!ioss.isEmpty()) {
                    for (Entry<Long, List<Device>> entry : ioss.entrySet()) {
                        Long applicationId = entry.getKey();
                        String iOSPushCertificate = jdbcTemplate.queryForObject("select " + Application.IOS_PUSH_CERTIFICATE + " from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", String.class, applicationId);
                        String iOSPushCertificatePassword = jdbcTemplate.queryForObject("select " + Application.IOS_PUSH_CERTIFICATE_PASSWORD + " from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", String.class, applicationId);
                        List<String> tokens = new ArrayList<String>(entry.getValue().size());
                        for (Device device : entry.getValue()) {
                            tokens.add(device.getToken());
                        }
                        if (!tokens.isEmpty()) {
                            try {
                                Payload payload = PushNotificationPayload.fromJSON(queue.getMessage());
                                javapns.Push.payload(payload, gson.fromJson(iOSPushCertificate, byte[].class), iOSPushCertificatePassword, true, tokens);
                                for (Device device : entry.getValue()) {
                                    Map<String, Object> fields = new HashMap<String, Object>();
                                    fields.put(History.IP, device.getIp());
                                    fields.put(History.MANUFACTURE, manufactures.get(device.getManufactureId()));
                                    fields.put(History.MESSAGE, queue.getMessage());
                                    fields.put(History.MODEL, models.get(device.getModelId()));
                                    fields.put(History.PLATFORM, platforms.get(device.getPlatformId()));
                                    fields.put(History.QUEUE_DATE, queue.getQueueDate());
                                    fields.put(History.SENT_DATE, new Date());
                                    fields.put(History.CITY, cities.get(device.getCityId()));
                                    fields.put(History.COUNTRY, countries.get(device.getCountryId()));
                                    fields.put(History.TOKEN, device.getToken());
                                    fields.put(History.VERSION, versions.get(device.getVersionId()));
                                    fields.put(History.USER_ID, queue.getUserId());
                                    fields.put(History.APPLICATION, jdbcTemplate.queryForObject("select " + Application.NAME + " from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", String.class, device.getApplicationId()));
                                    fields.put(History.STATUS, History.Status.SUCCESS);
                                    LOGGER.info("success {}", device.getToken());
                                    insert.execute(fields);
                                    jdbcTemplate.update("delete from " + TableUtilities.getTableName(QueueDevice.class) + " where " + QueueDevice.DEVICE_ID + " = ? and " + QueueDevice.QUEUE_ID + " = ?", device.getId(), queue.getId());
                                }
                            } catch (JsonSyntaxException e) {
                                for (Device device : entry.getValue()) {
                                    Map<String, Object> fields = new HashMap<String, Object>();
                                    fields.put(History.IP, device.getIp());
                                    fields.put(History.MANUFACTURE, manufactures.get(device.getManufactureId()));
                                    fields.put(History.MESSAGE, queue.getMessage());
                                    fields.put(History.MODEL, models.get(device.getModelId()));
                                    fields.put(History.PLATFORM, platforms.get(device.getPlatformId()));
                                    fields.put(History.QUEUE_DATE, queue.getQueueDate());
                                    fields.put(History.SENT_DATE, new Date());
                                    fields.put(History.CITY, cities.get(device.getCityId()));
                                    fields.put(History.COUNTRY, countries.get(device.getCountryId()));
                                    fields.put(History.TOKEN, device.getToken());
                                    fields.put(History.VERSION, versions.get(device.getVersionId()));
                                    fields.put(History.USER_ID, queue.getUserId());
                                    fields.put(History.APPLICATION, jdbcTemplate.queryForObject("select " + Application.NAME + " from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", String.class, device.getApplicationId()));
                                    fields.put(History.STATUS, History.Status.ERROR);
                                    LOGGER.info("failed {}", device.getToken());
                                    insert.execute(fields);
                                    jdbcTemplate.update("delete from " + TableUtilities.getTableName(QueueDevice.class) + " where " + QueueDevice.DEVICE_ID + " = ? and " + QueueDevice.QUEUE_ID + " = ?", device.getId(), queue.getId());
                                }
                                LOGGER.info("message format problem {}", e.getMessage());
                            } catch (CommunicationException e) {
                                LOGGER.info("communication link problem {}", e.getMessage());
                            } catch (KeystoreException e) {
                                LOGGER.info("certificate invalid problem {}", e.getMessage());
                                for (Device device : entry.getValue()) {
                                    Map<String, Object> fields = new HashMap<String, Object>();
                                    fields.put(History.IP, device.getIp());
                                    fields.put(History.MANUFACTURE, manufactures.get(device.getManufactureId()));
                                    fields.put(History.MESSAGE, queue.getMessage());
                                    fields.put(History.MODEL, models.get(device.getModelId()));
                                    fields.put(History.PLATFORM, platforms.get(device.getPlatformId()));
                                    fields.put(History.QUEUE_DATE, queue.getQueueDate());
                                    fields.put(History.SENT_DATE, new Date());
                                    fields.put(History.CITY, cities.get(device.getCityId()));
                                    fields.put(History.COUNTRY, countries.get(device.getCountryId()));
                                    fields.put(History.TOKEN, device.getToken());
                                    fields.put(History.VERSION, versions.get(device.getVersionId()));
                                    fields.put(History.USER_ID, queue.getUserId());
                                    fields.put(History.APPLICATION, jdbcTemplate.queryForObject("select " + Application.NAME + " from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", String.class, device.getApplicationId()));
                                    fields.put(History.STATUS, History.Status.ERROR);
                                    LOGGER.info("failed {}", device.getToken());
                                    insert.execute(fields);
                                    jdbcTemplate.update("delete from " + TableUtilities.getTableName(QueueDevice.class) + " where " + QueueDevice.DEVICE_ID + " = ? and " + QueueDevice.QUEUE_ID + " = ?", device.getId(), queue.getId());
                                }
                            }
                        }
                    }
                }

                if (!androids.isEmpty()) {
                    Message message = gson.fromJson(queue.getMessage(), Message.class);
                    for (Entry<Long, List<Device>> entry : androids.entrySet()) {
                        Long applicationId = entry.getKey();
                        String key = jdbcTemplate.queryForObject("select " + Application.ANDROID_API_KEY + " from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", String.class, applicationId);
                        List<String> tokens = new ArrayList<String>(entry.getValue().size());
                        for (Device device : entry.getValue()) {
                            tokens.add(device.getToken());
                        }
                        Sender sender = new Sender(key);

                        MulticastResult results = null;
                        try {
                            results = sender.send(message, tokens, 3);
                        } catch (IOException e) {
                            LOGGER.info("push error {}", e.getMessage());
                        }

                        if (results != null && results.getResults() != null && !results.getResults().isEmpty()) {
                            for (int i = 0; i < results.getResults().size(); i++) {
                                Result result = results.getResults().get(i);
                                Device device = entry.getValue().get(i);
                                Map<String, Object> fields = new HashMap<String, Object>();
                                fields.put(History.IP, device.getIp());
                                fields.put(History.MANUFACTURE, manufactures.get(device.getManufactureId()));
                                fields.put(History.MESSAGE, queue.getMessage());
                                fields.put(History.MODEL, models.get(device.getModelId()));
                                fields.put(History.PLATFORM, platforms.get(device.getPlatformId()));
                                fields.put(History.QUEUE_DATE, queue.getQueueDate());
                                fields.put(History.SENT_DATE, new Date());
                                fields.put(History.CITY, cities.get(device.getCityId()));
                                fields.put(History.COUNTRY, countries.get(device.getCountryId()));
                                fields.put(History.TOKEN, device.getToken());
                                fields.put(History.VERSION, versions.get(device.getVersionId()));
                                fields.put(History.USER_ID, queue.getUserId());
                                fields.put(History.APPLICATION, jdbcTemplate.queryForObject("select " + Application.NAME + " from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", String.class, device.getApplicationId()));
                                if (result.getMessageId() == null) {
                                    fields.put(History.STATUS, History.Status.ERROR);
                                    jdbcTemplate.update("update " + TableUtilities.getTableName(Device.class) + " set " + Device.FLAG + " = ? where " + Device.ID + " = ?", Device.Flag.DELETE, device.getId());
                                    LOGGER.info("failed {}", device.getToken());
                                } else {
                                    fields.put(History.STATUS, History.Status.SUCCESS);
                                    LOGGER.info("success {}", device.getToken());
                                }
                                insert.execute(fields);
                                jdbcTemplate.update("delete from " + TableUtilities.getTableName(QueueDevice.class) + " where " + QueueDevice.DEVICE_ID + " = ? and " + QueueDevice.QUEUE_ID + " = ?", device.getId(), queue.getId());
                            }
                        }
                    }
                }
            }
        }
    }
}
