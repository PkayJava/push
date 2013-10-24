package com.itrustcambodia.push.page.country;

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
import com.itrustcambodia.push.entity.Country;

@Mount("/econ")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_EDIT_COUNTRY", description = "Access Edit Country Page") })
public class EditCountryPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private Long countryId;

    @NotNull
    @TextField(label = "Name", placeholder = "Name", order = 1)
    @Unique(entity = Country.class, where = Country.NAME + " = :name and " + Country.ID + " != :countryId")
    private String name;

    public EditCountryPage(Country country) {
        this.countryId = country.getId();
        this.name = country.getName();
        initializeInterceptor();
    }

    private void initializeInterceptor() {
    }

    @Button(label = "Okay", order = 1, validate = true)
    public Navigation okay() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        jdbcTemplate.update("update " + TableUtilities.getTableName(Country.class) + " set " + Country.NAME + " = ? where " + Country.ID + " = ?", this.name, this.countryId);
        return new Navigation(CountryManagementPage.class);
    }

    @Button(label = "Delete", order = 2, validate = false)
    public Navigation delete() {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(Country.class) + " where " + Country.ID + " = ?", this.countryId);
        return new Navigation(CountryManagementPage.class);
    }

    @Button(label = "Cancel", order = 3, validate = false)
    public Navigation cancel() {
        return new Navigation(CountryManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "Edit Country";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getReferences();
    }

}
