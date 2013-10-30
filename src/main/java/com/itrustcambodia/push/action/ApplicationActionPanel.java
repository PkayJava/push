package com.itrustcambodia.push.action;

import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.panel.KnownPanel;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.validation.controller.Navigation;
import com.itrustcambodia.push.entity.Application;
import com.itrustcambodia.push.entity.Device;
import com.itrustcambodia.push.page.ApplicationManagementPage;
import com.itrustcambodia.push.page.EditApplicationPage;

public class ApplicationActionPanel extends KnownPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -4825102078652369872L;

    private Map<String, Object> model;

    public ApplicationActionPanel(String id, IModel<Map<String, Object>> model) {
        super(id);
        this.model = model.getObject();
        Link<?> delete = getLinkComponent("delete");
        delete.add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));
    }

    @com.itrustcambodia.pluggable.widget.Link(label = "Delete", order = 1)
    public Navigation delete() {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", model.get(TableUtilities.getTableName(Application.class) + "." + Application.ID));
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(Device.class) + " where " + Device.APPLICATION_ID + " = ?", model.get(TableUtilities.getTableName(Application.class) + "." + Application.ID));
        return new Navigation(ApplicationManagementPage.class);
    }

    @com.itrustcambodia.pluggable.widget.Link(label = "Edit", order = 2)
    public Navigation edit() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        Long applicationId = ((Number) model.get(TableUtilities.getTableName(Application.class) + "." + Application.ID)).longValue();
        Application app = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", new EntityRowMapper<Application>(Application.class), applicationId);
        return new Navigation(new EditApplicationPage(app));
    }

}