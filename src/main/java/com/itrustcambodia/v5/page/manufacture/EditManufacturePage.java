package com.itrustcambodia.v5.page.manufacture;

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
import com.itrustcambodia.v5.MenuUtils;
import com.itrustcambodia.v5.entity.Manufacture;

@Mount("/eman")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_EDIT_MANUFACTURE", description = "Access Edit Manufacture Page") })
public class EditManufacturePage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private Long manufactureId;

    @NotNull
    @TextField(label = "Name", placeholder = "Name", order = 1)
    @Unique(entity = Manufacture.class, where = Manufacture.NAME + " = :name and " + Manufacture.ID + " != :manufactureId")
    private String name;

    public EditManufacturePage(Manufacture manufacture) {
        this.manufactureId = manufacture.getId();
        this.name = manufacture.getName();
        initializeInterceptor();
    }

    private void initializeInterceptor() {
    }

    @Button(label = "Okay", order = 1, validate = true)
    public Navigation okay() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        jdbcTemplate.update("update " + TableUtilities.getTableName(Manufacture.class) + " set " + Manufacture.NAME + " = ? where " + Manufacture.ID + " = ?", this.name, this.manufactureId);
        return new Navigation(ManufactureManagementPage.class);
    }

    @Button(label = "Delete", order = 2, validate = false)
    public Navigation delete() {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(Manufacture.class) + " where " + Manufacture.ID + " = ?", this.manufactureId);
        return new Navigation(ManufactureManagementPage.class);
    }

    @Button(label = "Cancel", order = 3, validate = false)
    public Navigation cancel() {
        return new Navigation(ManufactureManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "Edit Manufacture";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getReferences();
    }

}
