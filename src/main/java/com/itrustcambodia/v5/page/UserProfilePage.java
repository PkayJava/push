package com.itrustcambodia.v5.page;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.core.WebSession;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.page.KnownPage;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.validation.constraints.NotNull;
import com.itrustcambodia.pluggable.validation.controller.Navigation;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import com.itrustcambodia.pluggable.widget.Button;
import com.itrustcambodia.pluggable.widget.LabelField;
import com.itrustcambodia.pluggable.widget.TextField;
import com.itrustcambodia.v5.MenuUtils;
import com.itrustcambodia.v5.entity.User;

@Mount("/profile")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_USER_PROFILE", description = "Access User Profile Page") })
public class UserProfilePage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = -6050255044135005992L;

    private Long userId;

    @LabelField(label = "Login", order = 1)
    private String login;

    @NotNull
    @TextField(label = "Password", placeholder = "Password", order = 2)
    private String password;

    public UserProfilePage() {
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        WebSession session = (WebSession) getSession();
        User user = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(User.class) + " where " + User.LOGIN + " = ?", new EntityRowMapper<User>(User.class), session.getUsername());
        this.userId = user.getId();
        this.login = user.getLogin();
        this.password = user.getPassword();
    }

    @Button(label = "Update", validate = true)
    public Navigation okay() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        jdbcTemplate.update("update " + TableUtilities.getTableName(User.class) + " set " + User.PASSWORD + " = ? where " + User.ID + " = ?", this.password, userId);
        return new Navigation(ApplicationManagementPage.class);
    }

    @Button(label = "Cancel", validate = false)
    public Navigation cancel() {
        return new Navigation(ApplicationManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "Profile";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getApps();
    }

}
