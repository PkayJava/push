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
import com.itrustcambodia.push.entity.Model;
import com.itrustcambodia.push.page.model.EditModelPage;
import com.itrustcambodia.push.page.model.ModelManagementPage;

public class ModelActionPanel extends KnownPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -6383358136649610289L;

    private Map<String, Object> model;

    public ModelActionPanel(String id, IModel<Map<String, Object>> model) {
        super(id);
        this.model = model.getObject();
        getLinkComponent("delete").add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));
    }

    @Link(label = "Delete", order = 1)
    public Navigation delete() {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(Model.class) + " where " + Model.ID + " = ?", model.get(Model.class) + "." + Model.ID);
        return new Navigation(ModelManagementPage.class);
    }

    @Link(label = "Edit", order = 2)
    public Navigation edit() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        Long modelId = ((Number) model.get(TableUtilities.getTableName(Model.class) + "." + Model.ID)).longValue();
        Model model = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Model.class) + " where " + Model.ID + " = ?", new EntityRowMapper<Model>(Model.class), modelId);
        return new Navigation(new EditModelPage(model));
    }

}