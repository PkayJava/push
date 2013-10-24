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
import com.itrustcambodia.push.entity.Country;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

public class CountryProvider extends TextChoiceProvider<Country> {

    /**
     * 
     */
    private static final long serialVersionUID = 1193280767531341031L;

    public CountryProvider() {
    }

    @Override
    protected String getDisplayText(Country choice) {
        return choice.getName();
    }

    @Override
    protected Object getId(Country choice) {
        return choice.getId();
    }

    @Override
    public void query(String term, int page, Response<Country> response) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();
        List<Country> choices = jdbcTemplate.query("select * from " + TableUtilities.getTableName(Country.class) + " where " + Country.NAME + " like ?", new EntityRowMapper<Country>(Country.class), term + "%");
        response.addAll(choices);
    }

    @Override
    public Collection<Country> toChoices(Collection<String> ids) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Country.ID, ids);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<Country> choices = namedParameterJdbcTemplate.query("select * from " + TableUtilities.getTableName(Country.class) + " where " + Country.ID + " in (:" + Country.ID + ")", params, new EntityRowMapper<Country>(Country.class));

        return choices;
    }
}