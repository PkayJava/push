package com.itrustcambodia.v5.page.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

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
import com.itrustcambodia.pluggable.widget.Select2Choice;
import com.itrustcambodia.pluggable.widget.TextField;
import com.itrustcambodia.v5.MenuUtils;
import com.itrustcambodia.v5.entity.Application;
import com.itrustcambodia.v5.entity.City;
import com.itrustcambodia.v5.entity.Country;
import com.itrustcambodia.v5.entity.Device;
import com.itrustcambodia.v5.entity.Manufacture;
import com.itrustcambodia.v5.entity.Model;
import com.itrustcambodia.v5.entity.Platform;
import com.itrustcambodia.v5.entity.User;
import com.itrustcambodia.v5.entity.Version;
import com.itrustcambodia.v5.select.ApplicationProvider;
import com.itrustcambodia.v5.select.CityProvider;
import com.itrustcambodia.v5.select.CountryProvider;
import com.itrustcambodia.v5.select.ManufactureProvider;
import com.itrustcambodia.v5.select.ModelProvider;
import com.itrustcambodia.v5.select.PlatformProvider;
import com.itrustcambodia.v5.select.VersionProvider;

@Mount("/ndevice")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_NEW_DEVICE", description = "Access New Device Page") })
public class NewDevicePage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = 5880591781948400300L;

    private Long userId;

    @NotNull
    @TextField(label = "Token", placeholder = "Token", order = 1)
    private String token;

    @NotNull
    @Select2Choice(label = "Platform", provider = PlatformProvider.class, order = 2)
    private Platform platform;

    @NotNull
    @Select2Choice(label = "Version", provider = VersionProvider.class, order = 3)
    private Version version;

    @NotNull
    @Select2Choice(label = "Manufacture", provider = ManufactureProvider.class, order = 4)
    private Manufacture manufacture;

    @NotNull
    @Select2Choice(label = "Model", provider = ModelProvider.class, order = 5)
    private Model model;

    @NotNull
    @Select2Choice(label = "Application", provider = ApplicationProvider.class, order = 1.1)
    private Application application;

    @NotNull
    @Select2Choice(label = "Country", provider = CountryProvider.class, order = 6)
    private Country country;

    @NotNull
    @Select2Choice(label = "City", provider = CityProvider.class, order = 7)
    private City city;

    public NewDevicePage() {
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        WebSession session = (WebSession) getSession();
        User user = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(User.class) + " where " + User.LOGIN + " = ?", new EntityRowMapper<User>(User.class), session.getUsername());
        this.userId = user.getId();
    }

    @Button(label = "Okay", validate = true)
    public Navigation okay() {

        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.withTableName(TableUtilities.getTableName(Device.class));

        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(Device.APPLICATION_ID, this.application.getId());
        fields.put(Device.CITY_ID, city.getId());
        fields.put(Device.COUNTRY_ID, country.getId());
        fields.put(Device.FLAG, Device.Flag.ACTIVE);
        fields.put(Device.IP, request.getRemoteAddr());
        fields.put(Device.MANUFACTURE_ID, manufacture.getId());
        fields.put(Device.MODEL_ID, model.getId());
        fields.put(Device.PLATFORM_ID, platform.getId());
        fields.put(Device.TOKEN, token);
        fields.put(Device.VERSION_ID, version.getId());
        insert.execute(fields);

        return new Navigation(NewDevicePage.class);
    }

    @Override
    public String getPageTitle() {
        return "New Device";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getApps();
    }

}
