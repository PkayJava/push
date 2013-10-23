package com.itrustcambodia.v5.page;

import java.util.List;

import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.layout.AbstractLayout;
import com.itrustcambodia.pluggable.page.WebPage;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.v5.layout.PublicLayout;

public class HomePage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = 6867842308714577170L;

    @Override
    public AbstractLayout requestLayout(String id) {
        return new PublicLayout(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        AbstractLayout layout = requestLayout("layout");
        add(layout);
    }

    @Override
    public String getPageTitle() {
        return "Home";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        // TODO Auto-generated method stub
        return null;
    }
}
