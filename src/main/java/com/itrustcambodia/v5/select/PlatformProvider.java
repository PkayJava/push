package com.itrustcambodia.v5.select;

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
import com.itrustcambodia.v5.entity.Platform;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

public class PlatformProvider extends TextChoiceProvider<Platform> {

    /**
     * 
     */
    private static final long serialVersionUID = -200059333918010136L;

    public PlatformProvider() {
    }

    @Override
    protected String getDisplayText(Platform choice) {
        return choice.getName();
    }

    @Override
    protected Object getId(Platform choice) {
        return choice.getId();
    }

    @Override
    public void query(String term, int page, Response<Platform> response) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();
        List<Platform> choices = jdbcTemplate.query("select * from " + TableUtilities.getTableName(Platform.class) + " where " + Platform.NAME + " like ?", new EntityRowMapper<Platform>(Platform.class), term + "%");
        response.addAll(choices);
    }

    @Override
    public Collection<Platform> toChoices(Collection<String> ids) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Platform.ID, ids);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<Platform> choices = namedParameterJdbcTemplate.query("select * from " + TableUtilities.getTableName(Platform.class) + " where " + Platform.ID + " in (:" + Platform.ID + ")", params, new EntityRowMapper<Platform>(Platform.class));

        return choices;
    }
}