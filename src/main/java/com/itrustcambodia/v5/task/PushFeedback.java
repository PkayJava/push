package com.itrustcambodia.v5.task;

import java.util.List;

import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.quartz.Job;
import com.itrustcambodia.pluggable.quartz.Scheduled;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.v5.entity.Application;
import com.itrustcambodia.v5.entity.Feedback;

@DisallowConcurrentExecution
@Scheduled(cron = "0 0 * * * ?")
public class PushFeedback extends Job {

    @Override
    public void process(AbstractWebApplication application, JobExecutionContext context) {
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        Gson gson = application.getGson();
        List<Feedback> feedbacks = jdbcTemplate.query("select * from " + TableUtilities.getTableName(Feedback.class), new EntityRowMapper<Feedback>(Feedback.class));
        if (feedbacks != null && !feedbacks.isEmpty()) {
            for (Feedback feedback : feedbacks) {
                String iOSPushCertificate = jdbcTemplate.queryForObject("select " + Application.IOS_PUSH_CERTIFICATE + " from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", String.class, feedback.getApplicationId());
                String iOSPushCertificatePassword = jdbcTemplate.queryForObject("select " + Application.IOS_PUSH_CERTIFICATE_PASSWORD + " from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", String.class, feedback.getApplicationId());
                try {
                    List<Device> inactiveDevices = javapns.Push.feedback(gson.fromJson(iOSPushCertificate, byte[].class), iOSPushCertificatePassword, true);
                    if (inactiveDevices != null && !inactiveDevices.isEmpty()) {
                        for (Device device : inactiveDevices) {
                            jdbcTemplate.update("update " + TableUtilities.getTableName(com.itrustcambodia.v5.entity.Device.class) + " set " + com.itrustcambodia.v5.entity.Device.FLAG + " = ? where " + com.itrustcambodia.v5.entity.Device.TOKEN + " = ?", com.itrustcambodia.v5.entity.Device.Flag.DELETE, device.getToken());
                        }
                    }
                    jdbcTemplate.update("delete from " + TableUtilities.getTableName(Feedback.class) + " where " + Feedback.ID + " = ?", feedback.getId());
                } catch (JsonSyntaxException e) {
                    jdbcTemplate.update("delete from " + TableUtilities.getTableName(Feedback.class) + " where " + Feedback.ID + " = ?", feedback.getId());
                } catch (CommunicationException e) {
                } catch (KeystoreException e) {
                    jdbcTemplate.update("delete from " + TableUtilities.getTableName(Feedback.class) + " where " + Feedback.ID + " = ?", feedback.getId());
                }
            }
        }
    }
}
