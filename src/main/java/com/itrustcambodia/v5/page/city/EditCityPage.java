package com.itrustcambodia.v5.page.city;

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
import com.itrustcambodia.v5.entity.City;

@Mount("/encity")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_EDIT_CITY", description = "Access Edit City Page") })
public class EditCityPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private Long cityId;

    @NotNull
    @TextField(label = "Name", placeholder = "Name", order = 1)
    @Unique(entity = City.class, where = City.NAME + " = :name and " + City.ID + " != :cityId")
    private String name;

    public EditCityPage(City city) {
        this.cityId = city.getId();
        this.name = city.getName();
        initializeInterceptor();
    }

    private void initializeInterceptor() {
    }

    @Button(label = "Okay", order = 1, validate = true)
    public Navigation okay() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        jdbcTemplate.update("update " + TableUtilities.getTableName(City.class) + " set " + City.NAME + " = ? where " + City.ID + " = ?", this.name, this.cityId);
        return new Navigation(CityManagementPage.class);
    }

    @Button(label = "Delete", order = 2, validate = false)
    public Navigation delete() {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(City.class) + " where " + City.ID + " = ?", this.cityId);
        return new Navigation(CityManagementPage.class);
    }

    @Button(label = "Cancel", order = 3, validate = false)
    public Navigation cancel() {
        return new Navigation(CityManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "Edit City";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getReferences();
    }

}
