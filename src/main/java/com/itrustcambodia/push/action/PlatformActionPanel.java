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
import com.itrustcambodia.push.entity.Platform;
import com.itrustcambodia.push.page.platform.EditPlatformPage;
import com.itrustcambodia.push.page.platform.PlatformManagementPage;

public class PlatformActionPanel extends KnownPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -587913682304507115L;

    private Map<String, Object> model;

    public PlatformActionPanel(String id, IModel<Map<String, Object>> model) {
        super(id);
        this.model = model.getObject();
        getLinkComponent("delete").add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));
    }

    @Link(label = "Delete", order = 1)
    public Navigation delete() {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(Platform.class) + " where " + Platform.ID + " = ?", model.get(TableUtilities.getTableName(Platform.class) + "." + Platform.ID));
        return new Navigation(PlatformManagementPage.class);
    }

    @Link(label = "Edit", order = 2)
    public Navigation edit() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        Long platformId = ((Number) model.get(TableUtilities.getTableName(Platform.class) + "." + Platform.ID)).longValue();
        Platform platform = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Platform.class) + " where " + Platform.ID + " = ?", new EntityRowMapper<Platform>(Platform.class), platformId);
        return new Navigation(new EditPlatformPage(platform));
    }

}