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
import com.itrustcambodia.push.entity.Manufacture;
import com.itrustcambodia.push.page.manufacture.EditManufacturePage;
import com.itrustcambodia.push.page.manufacture.ManufactureManagementPage;

public class ManufactureActionPanel extends KnownPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -7100191469082094146L;

    private Map<String, Object> model;

    public ManufactureActionPanel(String id, IModel<Map<String, Object>> model) {
        super(id);
        this.model = model.getObject();
        getLinkComponent("delete").add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));
    }

    @Link(label = "Delete", order = 1)
    public Navigation delete() {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(Manufacture.class) + " where " + Manufacture.ID + " = ?", model.get(TableUtilities.getTableName(Manufacture.class) + "." + Manufacture.ID));
        return new Navigation(ManufactureManagementPage.class);
    }

    @Link(label = "Edit", order = 2)
    public Navigation edit() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        Long manufactureId = ((Number) model.get(TableUtilities.getTableName(Manufacture.class) + "." + Manufacture.ID)).longValue();
        Manufacture manufacture = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Manufacture.class) + " where " + Manufacture.ID + " = ?", new EntityRowMapper<Manufacture>(Manufacture.class), manufactureId);
        return new Navigation(new EditManufacturePage(manufacture));
    }

}