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
import com.itrustcambodia.push.entity.City;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

public class CityProvider extends TextChoiceProvider<City> {

    /**
     * 
     */
    private static final long serialVersionUID = 1193280767531341031L;

    public CityProvider() {
    }

    @Override
    protected String getDisplayText(City choice) {
        return choice.getName();
    }

    @Override
    protected Object getId(City choice) {
        return choice.getId();
    }

    @Override
    public void query(String term, int page, Response<City> response) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();
        List<City> choices = jdbcTemplate.query("select * from " + TableUtilities.getTableName(City.class) + " where " + City.NAME + " like ?", new EntityRowMapper<City>(City.class), term + "%");
        response.addAll(choices);
    }

    @Override
    public Collection<City> toChoices(Collection<String> ids) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(City.ID, ids);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<City> choices = namedParameterJdbcTemplate.query("select * from " + TableUtilities.getTableName(City.class) + " where " + City.ID + " in (:" + City.ID + ")", params, new EntityRowMapper<City>(City.class));

        return choices;
    }
}