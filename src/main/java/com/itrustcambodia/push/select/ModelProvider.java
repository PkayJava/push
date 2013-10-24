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
import com.itrustcambodia.push.entity.Model;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

public class ModelProvider extends TextChoiceProvider<Model> {

    /**
     * 
     */
    private static final long serialVersionUID = -200059333918010136L;

    public ModelProvider() {
    }

    @Override
    protected String getDisplayText(Model choice) {
        return choice.getName();
    }

    @Override
    protected Object getId(Model choice) {
        return choice.getId();
    }

    @Override
    public void query(String term, int page, Response<Model> response) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();
        List<Model> choices = jdbcTemplate.query("select * from " + TableUtilities.getTableName(Model.class) + " where " + Model.NAME + " like ?", new EntityRowMapper<Model>(Model.class), term + "%");
        response.addAll(choices);
    }

    @Override
    public Collection<Model> toChoices(Collection<String> ids) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Model.ID, ids);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<Model> choices = namedParameterJdbcTemplate.query("select * from " + TableUtilities.getTableName(Model.class) + " where " + Model.ID + " in (:" + Model.ID + ")", params, new EntityRowMapper<Model>(Model.class));

        return choices;
    }
}