package com.itrustcambodia.push.select;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Application;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.push.entity.Version;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

public class VersionProvider extends TextChoiceProvider<Version> {

    /**
     * 
     */
    private static final long serialVersionUID = -200059333918010136L;

    public VersionProvider() {
    }

    @Override
    protected String getDisplayText(Version choice) {
        return choice.getName();
    }

    @Override
    protected Object getId(Version choice) {
        return choice.getId();
    }

    @Override
    public void query(String term, int page, Response<Version> response) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();
        List<Version> choices = jdbcTemplate.query("select * from " + TableUtilities.getTableName(Version.class) + " where " + Version.NAME + " like ?", new EntityRowMapper<Version>(Version.class), term + "%");
        response.addAll(choices);
    }

    @Override
    public Collection<Version> toChoices(Collection<String> ids) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Version.ID, ids);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<Version> choices = namedParameterJdbcTemplate.query("select * from " + TableUtilities.getTableName(Version.class) + " where " + Version.ID + " in (:" + Version.ID + ")", params, new EntityRowMapper<Version>(Version.class));

        return choices;
    }
}