package com.itrustcambodia.push.page.version;

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
import com.itrustcambodia.push.entity.Version;

@Mount("/ever")
@AuthorizeInstantiation(roles = {@Role(name = "ROLE_PAGE_EDIT_VERSION", description = "Access Edit Version Page")})
public class EditVersionPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private Long versionId;

    @NotNull
    @TextField(label = "Name", placeholder = "Name", order = 1)
    @Unique(entity = Version.class, where = Version.NAME + " = :name and " + Version.ID + " != :versionId")
    private String name;

    public EditVersionPage(Version version) {
        this.versionId = version.getId();
        this.name = version.getName();
        initializeInterceptor();
    }

    private void initializeInterceptor() {
    }

    @Button(label = "Okay", order = 1, validate = true)
    public Navigation okay() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        jdbcTemplate.update("update " + TableUtilities.getTableName(Version.class) + " set " + Version.NAME + " = ? where " + Version.ID + " = ?", this.name, this.versionId);
        return new Navigation(VersionManagementPage.class);
    }

    @Button(label = "Delete", order = 2, validate = false)
    public Navigation delete() {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(Version.class) + " where " + Version.ID + " = ?", this.versionId);
        return new Navigation(VersionManagementPage.class);
    }

    @Button(label = "Cancel", order = 3, validate = false)
    public Navigation cancel() {
        return new Navigation(VersionManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "Edit Version";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getReferences();
    }

}
