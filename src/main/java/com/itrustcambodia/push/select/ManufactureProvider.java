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
import com.itrustcambodia.push.entity.Manufacture;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

public class ManufactureProvider extends TextChoiceProvider<Manufacture> {

    /**
     * 
     */
    private static final long serialVersionUID = -200059333918010136L;

    public ManufactureProvider() {
    }

    @Override
    protected String getDisplayText(Manufacture choice) {
        return choice.getName();
    }

    @Override
    protected Object getId(Manufacture choice) {
        return choice.getId();
    }

    @Override
    public void query(String term, int page, Response<Manufacture> response) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();
        List<Manufacture> choices = jdbcTemplate.query("select * from " + TableUtilities.getTableName(Manufacture.class) + " where " + Manufacture.NAME + " like ?", new EntityRowMapper<Manufacture>(Manufacture.class), term + "%");
        response.addAll(choices);
    }

    @Override
    public Collection<Manufacture> toChoices(Collection<String> ids) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Manufacture.ID, ids);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<Manufacture> choices = namedParameterJdbcTemplate.query("select * from " + TableUtilities.getTableName(Manufacture.class) + " where " + Manufacture.ID + " in (:" + Manufacture.ID + ")", params, new EntityRowMapper<Manufacture>(Manufacture.class));

        return choices;
    }
}