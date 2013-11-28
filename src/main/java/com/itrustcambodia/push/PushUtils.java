package com.itrustcambodia.push;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.push.entity.Application;
import com.itrustcambodia.push.entity.Device;
import com.itrustcambodia.push.entity.Queue;
import com.itrustcambodia.push.entity.QueueDevice;

public final class PushUtils {
    private PushUtils() {
    }

    public static final void schedule(JdbcTemplate jdbcTemplate, Long userId, List<Long> applications, String token, String message, Date when) {
        String application = TableUtilities.getTableName(Application.class);
        String device = TableUtilities.getTableName(Device.class);
        StringBuffer select = new StringBuffer();
        select.append("select " + device + ".* from " + device + " inner join " + application + " on " + device + "." + Device.APPLICATION_ID + " = " + application + "." + com.itrustcambodia.push.entity.Application.ID);

        Map<String, Object> params = new HashMap<String, Object>();
        List<String> wheres = new ArrayList<String>();

        if (applications != null && !applications.isEmpty()) {
            wheres.add(device + "." + Device.APPLICATION_ID + " in (:" + device + "." + Device.APPLICATION_ID + ")");
            params.put(device + "." + Device.APPLICATION_ID, applications);
        }

        wheres.add(device + "." + Device.TOKEN + " = :" + device + "." + Device.TOKEN);
        params.put(device + "." + Device.TOKEN, token);

        wheres.add(device + "." + Device.FLAG + " = '" + Device.Flag.ACTIVE + "'");
        wheres.add(application + "." + com.itrustcambodia.push.entity.Application.USER_ID + " = :" + application + "." + com.itrustcambodia.push.entity.Application.USER_ID);
        params.put(application + "." + com.itrustcambodia.push.entity.Application.USER_ID, userId);

        if (!wheres.isEmpty()) {
            select.append(" where " + org.apache.commons.lang3.StringUtils.join(wheres, " and "));
        }

        DeviceRunner deviceRunner = new DeviceRunner(select, message, userId, jdbcTemplate, params, when);
        Thread thread = new Thread(deviceRunner);
        thread.start();

    }

    public static final void schedule(JdbcTemplate jdbcTemplate, Long userId, List<Long> countries, List<Long> cities, List<Long> applications, List<Long> platforms, List<Long> manufactures, List<Long> models, List<Long> versions, String message, Date when) {
        String application = TableUtilities.getTableName(Application.class);
        String device = TableUtilities.getTableName(Device.class);

        StringBuffer select = new StringBuffer();
        select.append("select " + device + ".* from " + device + " inner join " + application + " on " + device + "." + Device.APPLICATION_ID + " = " + application + "." + com.itrustcambodia.push.entity.Application.ID);

        Map<String, Object> params = new HashMap<String, Object>();
        List<String> wheres = new ArrayList<String>();
        if (manufactures != null && !manufactures.isEmpty()) {
            wheres.add(device + "." + Device.MANUFACTURE_ID + " in (:" + device + "." + Device.MANUFACTURE_ID + ")");
            params.put(device + "." + Device.MANUFACTURE_ID, manufactures);
        }
        if (countries != null && !countries.isEmpty()) {
            wheres.add(device + "." + Device.COUNTRY_ID + " in (:" + device + "." + Device.COUNTRY_ID + ")");
            params.put(device + "." + Device.COUNTRY_ID, countries);
        }
        if (cities != null && !cities.isEmpty()) {
            wheres.add(device + "." + Device.CITY_ID + " in (:" + device + "." + Device.CITY_ID + ")");
            params.put(device + "." + Device.CITY_ID, cities);
        }
        if (applications != null && !applications.isEmpty()) {
            wheres.add(device + "." + Device.APPLICATION_ID + " in (:" + device + "." + Device.APPLICATION_ID + ")");
            params.put(device + "." + Device.APPLICATION_ID, applications);
        }
        if (platforms != null && !platforms.isEmpty()) {
            wheres.add(device + "." + Device.PLATFORM_ID + " in (:" + device + "." + Device.PLATFORM_ID + ")");
            params.put(device + "." + Device.PLATFORM_ID, platforms);
        }
        if (models != null && !models.isEmpty()) {
            wheres.add(device + "." + Device.MODEL_ID + " in (:" + device + "." + Device.MODEL_ID + ")");
            params.put(device + "." + Device.MODEL_ID, models);
        }
        if (versions != null && !versions.isEmpty()) {
            wheres.add(device + "." + Device.VERSION_ID + " in (:" + device + "." + Device.VERSION_ID + ")");
            params.put(device + "." + Device.VERSION_ID, versions);
        }

        wheres.add(device + "." + Device.FLAG + " = '" + Device.Flag.ACTIVE + "'");
        wheres.add(application + "." + com.itrustcambodia.push.entity.Application.USER_ID + " = :" + application + "." + com.itrustcambodia.push.entity.Application.USER_ID);
        params.put(application + "." + com.itrustcambodia.push.entity.Application.USER_ID, userId);

        if (!wheres.isEmpty()) {
            select.append(" where " + org.apache.commons.lang3.StringUtils.join(wheres, " and "));
        }

        DeviceRunner deviceRunner = new DeviceRunner(select, message, userId, jdbcTemplate, params, when);
        Thread thread = new Thread(deviceRunner);
        thread.start();

    }

    private static class DeviceRunner implements Runnable {

        private StringBuffer select;

        private String message;

        private Long userId;

        private JdbcTemplate jdbcTemplate;

        private Map<String, Object> params;

        private Date when;

        public DeviceRunner(StringBuffer select, String message, Long userId, JdbcTemplate jdbcTemplate, Map<String, Object> params, Date when) {
            super();
            this.select = select;
            this.message = message;
            this.userId = userId;
            this.jdbcTemplate = jdbcTemplate;
            this.params = params;
            this.when = when;
        }

        @Override
        public void run() {
            long queueId = 0;
            long limit = 100;
            NamedParameterJdbcTemplate query = new NamedParameterJdbcTemplate(jdbcTemplate);
            Long count = query.queryForObject("select count(*) from (" + select.toString() + ") pp", params, Long.class);
            long current = 0;
            while (current <= count) {
                List<Device> devices = query.query(select.toString() + " limit " + current + "," + limit, params, new EntityRowMapper<Device>(Device.class));
                if (devices != null && !devices.isEmpty()) {
                    SimpleJdbcInsert queueInsert = new SimpleJdbcInsert(jdbcTemplate);
                    queueInsert.withTableName(TableUtilities.getTableName(Queue.class));
                    queueInsert.usingGeneratedKeyColumns(Queue.ID);

                    Map<String, Object> fields = new HashMap<String, Object>();

                    fields.put(Queue.MESSAGE, message);
                    fields.put(Queue.USER_ID, userId);
                    fields.put(Queue.QUEUE_DATE, when == null ? new Date() : when);
                    queueId = queueInsert.executeAndReturnKey(fields).longValue();

                    SimpleJdbcInsert deviceQueue = new SimpleJdbcInsert(jdbcTemplate);
                    deviceQueue.setTableName(TableUtilities.getTableName(QueueDevice.class));
                    for (Device device : devices) {
                        FeedbackUtils.feedback(jdbcTemplate, device.getApplicationId());
                        Map<String, Object> f = new HashMap<String, Object>();
                        f.put(QueueDevice.DEVICE_ID, device.getId());
                        f.put(QueueDevice.QUEUE_ID, queueId);
                        deviceQueue.execute(f);
                    }
                }
                current = current + count;
            }
            // return queueId;
        }

    }

}