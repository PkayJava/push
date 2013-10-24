package com.itrustcambodia.push.page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.core.WebSession;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.page.KnownPage;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.validation.controller.Navigation;
import com.itrustcambodia.pluggable.validation.type.TextFieldType;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import com.itrustcambodia.pluggable.widget.Button;
import com.itrustcambodia.pluggable.widget.Select2MultiChoice;
import com.itrustcambodia.pluggable.widget.TextArea;
import com.itrustcambodia.pluggable.widget.TextField;
import com.itrustcambodia.push.MenuUtils;
import com.itrustcambodia.push.PushUtils;
import com.itrustcambodia.push.entity.Application;
import com.itrustcambodia.push.entity.City;
import com.itrustcambodia.push.entity.Country;
import com.itrustcambodia.push.entity.Manufacture;
import com.itrustcambodia.push.entity.Model;
import com.itrustcambodia.push.entity.Platform;
import com.itrustcambodia.push.entity.User;
import com.itrustcambodia.push.entity.Version;
import com.itrustcambodia.push.select.ApplicationProvider;
import com.itrustcambodia.push.select.CityProvider;
import com.itrustcambodia.push.select.CountryProvider;
import com.itrustcambodia.push.select.ManufactureProvider;
import com.itrustcambodia.push.select.ModelProvider;
import com.itrustcambodia.push.select.PlatformProvider;
import com.itrustcambodia.push.select.VersionProvider;

@Mount("/push")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_PUSH_MESSAGE", description = "Access Push Message Page") })
public class PushMessagePage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    @Select2MultiChoice(label = "Manufactures", minimumInputLength = 1, order = 1, provider = ManufactureProvider.class)
    private Manufacture[] manufactures;

    @Select2MultiChoice(label = "Platforms", minimumInputLength = 1, order = 2, provider = PlatformProvider.class)
    private Platform[] platforms;

    @Select2MultiChoice(label = "Models", minimumInputLength = 1, order = 3, provider = ModelProvider.class)
    private Model[] models;

    @Select2MultiChoice(label = "Versions", minimumInputLength = 1, order = 4, provider = VersionProvider.class)
    private Version[] versions;

    @Select2MultiChoice(label = "Countries", minimumInputLength = 1, order = 5, provider = CountryProvider.class)
    private Country[] countries;

    @Select2MultiChoice(label = "Cities", minimumInputLength = 1, order = 6, provider = CityProvider.class)
    private City[] cities;

    @Select2MultiChoice(label = "Apps", minimumInputLength = 1, order = 0.3, provider = ApplicationProvider.class)
    private Application[] applications;

    @TextField(label = "when", order = 0.2, pattern = "yyyy-MM-dd HH:mm:ss ZZ", type = TextFieldType.DATETIME)
    private Date when;

    @TextArea(label = "message", order = 0.1)
    private String message;

    public PushMessagePage() {
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        this.when = new Date();
    }

    @Button(label = "Okay", order = 1, validate = true)
    public Navigation okay() {

        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        WebSession session = (WebSession) getSession();
        User user = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(User.class) + " where " + User.LOGIN + " = ?", new EntityRowMapper<User>(User.class), session.getUsername());

        List<Long> listManufacture = null;
        if (manufactures != null && manufactures.length > 0) {
            listManufacture = new ArrayList<Long>();
            for (Manufacture manufacture : manufactures) {
                listManufacture.add(manufacture.getId());
            }
        }

        List<Long> listApplication = null;
        if (applications != null && applications.length > 0) {
            listApplication = new ArrayList<Long>();
            for (Application application : applications) {
                listApplication.add(application.getId());
            }
        }

        List<Long> listVersion = null;
        if (versions != null && versions.length > 0) {
            listVersion = new ArrayList<Long>();
            for (Version version : versions) {
                listVersion.add(version.getId());
            }
        }

        List<Long> listModel = null;
        if (models != null && models.length > 0) {
            listModel = new ArrayList<Long>();
            for (Model model : models) {
                listModel.add(model.getId());
            }
        }

        List<Long> listPlatform = null;
        if (platforms != null && platforms.length > 0) {
            listPlatform = new ArrayList<Long>();
            for (Platform platform : platforms) {
                listPlatform.add(platform.getId());
            }
        }

        List<Long> listCity = null;
        if (cities != null && cities.length > 0) {
            listCity = new ArrayList<Long>();
            for (City city : cities) {
                listCity.add(city.getId());
            }
        }

        List<Long> listCountry = null;
        if (countries != null && countries.length > 0) {
            listCountry = new ArrayList<Long>();
            for (Country country : countries) {
                listCountry.add(country.getId());
            }
        }

        AbstractWebApplication application = (AbstractWebApplication) getApplication();

        PushUtils.schedule(application.getJdbcTemplate(), user.getId(), listCountry, listCity, listApplication, listPlatform, listManufacture, listModel, listVersion, message, when);

        return new Navigation(ApplicationManagementPage.class);
    }

    @Button(label = "Cancel", order = 2, validate = false)
    public Navigation cancel() {
        return new Navigation(ApplicationManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "Push Message";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getApps();
    }

}
