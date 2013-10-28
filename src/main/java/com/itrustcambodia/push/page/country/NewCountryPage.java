package com.itrustcambodia.push.page.country;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

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

@Mount("/ncon")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_NEW_COUNTRY", description = "Access New Country Page") })
public class NewCountryPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    @NotNull
    @TextField(label = "Name", placeholder = "Name", order = 1)
    @Unique(entity = Country.class, where = Country.NAME + " = :name")
    private String name;

    public NewCountryPage() {
        initializeInterceptor();
    }

    private void initializeInterceptor() {
    }

    @Button(label = "Okay", order = 1, validate = true)
    public Navigation okay() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        SimpleJdbcInsert insert = new SimpleJdbcInsert(application.getJdbcTemplate());
        insert.withTableName(TableUtilities.getTableName(Country.class));
        Map<String, Object> fields = new HashMap<String, Object>();

        fields.put(Country.NAME, this.name);

        insert.execute(fields);

        return new Navigation(CountryManagementPage.class);
    }

    @Button(label = "Cancel", order = 2, validate = false)
    public Navigation cancel() {
        return new Navigation(CountryManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "New Country";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getReferences();
    }

}
