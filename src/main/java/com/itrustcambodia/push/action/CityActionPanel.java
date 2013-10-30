package com.itrustcambodia.push.action;

import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.IModel;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.panel.KnownPanel;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.validation.controller.Navigation;
import com.itrustcambodia.pluggable.widget.Link;
import com.itrustcambodia.push.entity.City;
import com.itrustcambodia.push.page.city.CityManagementPage;
import com.itrustcambodia.push.page.city.EditCityPage;

public class CityActionPanel extends KnownPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -7706863718974576542L;

    private Map<String, Object> model;

    public CityActionPanel(String id, IModel<Map<String, Object>> model) {
        super(id);
        this.model = model.getObject();
        getLinkComponent("delete").add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));
    }

    @Link(label = "Delete", order = 1)
    public Navigation delete() {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(City.class) + " where " + City.ID + " = ?", model.get(TableUtilities.getTableName(City.class) + "." + City.ID));
        return new Navigation(CityManagementPage.class);
    }

    @Link(label = "Edit", order = 2)
    public Navigation edit() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        Long cityId = ((Number) model.get(TableUtilities.getTableName(City.class) + "." + City.ID)).longValue();
        City city = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(City.class) + " where " + City.ID + " = ?", new EntityRowMapper<City>(City.class), cityId);
        return new Navigation(new EditCityPage(city));
    }

}