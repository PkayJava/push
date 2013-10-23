package com.itrustcambodia.v5;

import java.util.ArrayList;
import java.util.List;

import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.v5.page.ApplicationManagementPage;
import com.itrustcambodia.v5.page.NewApplicationPage;
import com.itrustcambodia.v5.page.PushMessagePage;
import com.itrustcambodia.v5.page.UserProfilePage;
import com.itrustcambodia.v5.page.city.CityManagementPage;
import com.itrustcambodia.v5.page.country.CountryManagementPage;
import com.itrustcambodia.v5.page.device.NewDevicePage;
import com.itrustcambodia.v5.page.manufacture.ManufactureManagementPage;
import com.itrustcambodia.v5.page.model.ModelManagementPage;
import com.itrustcambodia.v5.page.platform.PlatformManagementPage;
import com.itrustcambodia.v5.page.version.VersionManagementPage;

public class MenuUtils {
    public static final List<Menu> getReferences() {
        List<Menu> references = new ArrayList<Menu>();
        references.add(Menu.linkMenu("Manufacture", ManufactureManagementPage.class));
        references.add(Menu.linkMenu("Model", ModelManagementPage.class));
        references.add(Menu.linkMenu("Version", VersionManagementPage.class));
        references.add(Menu.linkMenu("Platform", PlatformManagementPage.class));
        references.add(Menu.linkMenu("Country", CountryManagementPage.class));
        references.add(Menu.linkMenu("City", CityManagementPage.class));

        return references;
    }

    public static final List<Menu> getApps() {
        List<Menu> apps = new ArrayList<Menu>();
        apps.add(Menu.linkMenu("Apps", ApplicationManagementPage.class));
        apps.add(Menu.linkMenu("New App", NewApplicationPage.class));
        apps.add(Menu.linkMenu("New Device", NewDevicePage.class));
        apps.add(Menu.linkMenu("Push Message", PushMessagePage.class));
        apps.add(Menu.linkMenu("Profile", UserProfilePage.class));
        return apps;
    }

}
