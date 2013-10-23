package com.itrustcambodia.v5;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.v5.entity.Feedback;

public class FeedbackUtils {

    private FeedbackUtils() {
    }

    public static final void feedback(JdbcTemplate jdbcTemplate, long applicationId) {
        long count = jdbcTemplate.queryForObject("select count(*) from " + TableUtilities.getTableName(Feedback.class) + " where " + Feedback.APPLICATION_ID + " = ?", Long.class, applicationId);
        if (count <= 0) {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName(TableUtilities.getTableName(Feedback.class));
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(Feedback.APPLICATION_ID, applicationId);
            insert.execute(fields);
        }
    }
}
