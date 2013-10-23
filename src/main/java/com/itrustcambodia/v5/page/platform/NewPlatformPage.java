package com.itrustcambodia.v5.page.platform;

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
import com.itrustcambodia.v5.MenuUtils;
import com.itrustcambodia.v5.entity.Platform;

@Mount("/nplat")
@AuthorizeInstantiation(roles = {@Role(name ="ROLE_PAGE_NEW_PLATFORM", description = "Access New Platform Page")})
public class NewPlatformPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    @NotNull
    @TextField(label = "Name", placeholder = "Name", order = 1)
    @Unique(entity = Platform.class, where = Platform.NAME + " = :name")
    private String name;

    public NewPlatformPage() {
        initializeInterceptor();
    }

    private void initializeInterceptor() {
    }

    @Button(label = "Okay", order = 1, validate = true)
    public Navigation okay() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        SimpleJdbcInsert insert = new SimpleJdbcInsert(application.getJdbcTemplate());
        insert.withTableName(TableUtilities.getTableName(com.itrustcambodia.v5.entity.Platform.class));
        Map<String, Object> fields = new HashMap<String, Object>();

        fields.put(com.itrustcambodia.v5.entity.Platform.NAME, this.name);

        insert.execute(fields);

        return new Navigation(PlatformManagementPage.class);
    }

    @Button(label = "Cancel", order = 2, validate = false)
    public Navigation cancel() {
        return new Navigation(PlatformManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "New Platform";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getReferences();
    }

}
