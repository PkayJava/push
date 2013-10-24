package com.itrustcambodia.push.page.model;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.page.KnownPage;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.validation.constraints.NotNull;
import com.itrustcambodia.pluggable.validation.constraints.Unique;
import com.itrustcambodia.pluggable.validation.controller.Navigation;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import com.itrustcambodia.pluggable.widget.Button;
import com.itrustcambodia.pluggable.widget.TextField;
import com.itrustcambodia.push.MenuUtils;
import com.itrustcambodia.push.entity.Model;

@Mount("/emod")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_EDIT_MODEL", description = "Access Edit Model Page") })
public class EditModelPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private Long modelId;

    @NotNull
    @TextField(label = "Name", placeholder = "Name", order = 1)
    @Unique(entity = Model.class, where = Model.NAME + " = :name and " + Model.ID + " != :modelId")
    private String name;

    public EditModelPage(Model model) {
        this.modelId = model.getId();
        this.name = model.getName();
        initializeInterceptor();
    }

    private void initializeInterceptor() {
    }

    @Button(label = "Okay", order = 1, validate = true)
    public Navigation okay() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        jdbcTemplate.update("update " + TableUtilities.getTableName(Model.class) + " set " + Model.NAME + " = ? where " + Model.ID + " = ?", this.name, this.modelId);
        return new Navigation(ModelManagementPage.class);
    }

    @Button(label = "Delete", order = 2, validate = false)
    public Navigation delete() {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(Model.class) + " where " + Model.ID + " = ?", this.modelId);
        return new Navigation(ModelManagementPage.class);
    }

    @Button(label = "Cancel", order = 3, validate = false)
    public Navigation cancel() {
        return new Navigation(ModelManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "Edit Model";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getReferences();
    }

}
