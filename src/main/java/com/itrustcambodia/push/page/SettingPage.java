package com.itrustcambodia.push.page;

import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.page.ApplicationSettingPage;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@Mount("/c")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_SETTING", description = "Access Setting Page") })
public class SettingPage extends ApplicationSettingPage {

    /**
     * 
     */
    private static final long serialVersionUID = -7800529883566015005L;

    @Override
    public String getPageTitle() {
        return "Application Setting";
    }

}
