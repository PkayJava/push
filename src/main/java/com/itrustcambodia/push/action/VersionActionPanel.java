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
import com.itrustcambodia.push.entity.Version;
import com.itrustcambodia.push.page.version.EditVersionPage;
import com.itrustcambodia.push.page.version.VersionManagementPage;

public class VersionActionPanel extends KnownPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -5027154582778721613L;

    private Map<String, Object> model;

    public VersionActionPanel(String id, IModel<Map<String, Object>> model) {
        super(id);
        this.model = model.getObject();
        getLinkComponent("delete").add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));
    }

    @Link(label = "Delete", order = 1)
    public Navigation delete() {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(Version.class) + " where " + Version.ID + " = ?", model.get(TableUtilities.getTableName(Version.class) + "." + Version.ID));
        return new Navigation(VersionManagementPage.class);
    }

    @Link(label = "Edit", order = 2)
    public Navigation edit() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        Long versionId = ((Number) model.get(TableUtilities.getTableName(Version.class) + "." + Version.ID)).longValue();
        Version version = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Version.class) + " where " + Version.ID + " = ?", new EntityRowMapper<Version>(Version.class), versionId);
        return new Navigation(new EditVersionPage(version));
    }

}