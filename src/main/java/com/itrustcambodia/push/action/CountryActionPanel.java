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
import com.itrustcambodia.push.entity.Country;
import com.itrustcambodia.push.page.country.CountryManagementPage;
import com.itrustcambodia.push.page.country.EditCountryPage;

public class CountryActionPanel extends KnownPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -8411826203179387021L;

    private Map<String, Object> model;

    public CountryActionPanel(String id, IModel<Map<String, Object>> model) {
        super(id);
        this.model = model.getObject();
        getLinkComponent("delete").add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));
    }

    @Link(label = "Delete", order = 1)
    public Navigation delete() {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(Country.class) + " where " + Country.ID + " = ?", model.get(TableUtilities.getTableName(Country.class) + "." + Country.ID));
        return new Navigation(CountryManagementPage.class);
    }

    @Link(label = "Edit", order = 2)
    public Navigation edit() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        Long cityId = ((Number) model.get(TableUtilities.getTableName(Country.class) + "." + Country.ID)).longValue();
        Country country = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Country.class) + " where " + Country.ID + " = ?", new EntityRowMapper<Country>(Country.class), cityId);
        return new Navigation(new EditCountryPage(country));
    }

}