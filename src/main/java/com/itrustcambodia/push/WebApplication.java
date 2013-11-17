package com.itrustcambodia.push;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.wicket.RuntimeConfigurationType;
import org.cloudfoundry.runtime.service.relational.CloudDataSourceFactory;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.entity.AbstractUser;
import com.itrustcambodia.pluggable.migration.AbstractApplicationMigrator;
import com.itrustcambodia.pluggable.page.ApplicationSettingPage;
import com.itrustcambodia.pluggable.page.WebPage;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.push.entity.User;
import com.itrustcambodia.push.page.ApplicationManagementPage;
import com.itrustcambodia.push.page.DashboardPage;
import com.itrustcambodia.push.page.HomePage;
import com.itrustcambodia.push.page.SettingPage;
import com.itrustcambodia.push.page.UserProfilePage;
import com.itrustcambodia.push.support.ApplicationMigration;
import com.jolbox.bonecp.BoneCPDataSource;

public class WebApplication extends AbstractWebApplication {

    private static final long serialVersionUID = -9091053763635561669L;

    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        return RuntimeConfigurationType.DEPLOYMENT;
    }

    @Override
    public String getBrandLabel() {
        return "Push";
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public String getPluginLabel() {
        return "Plugins";
    }

    @Override
    public List<Menu> getApplicationMenus(Roles roles) {
        List<Menu> menus = new ArrayList<Menu>();

        Menu apps = Menu.linkMenu("Apps", ApplicationManagementPage.class);
        menus.add(apps);

        Menu profile = Menu.linkMenu("Profile", UserProfilePage.class);
        menus.add(profile);

        Menu references = Menu.parentMenu("References", MenuUtils.getReferences());
        menus.add(references);
        return menus;
    }

    @Override
    public Class<? extends AbstractApplicationMigrator> getMigrator() {
        return ApplicationMigration.class;
    }

    @Override
    public Class<? extends ApplicationSettingPage> getSettingPage() {
        return SettingPage.class;
    }

    @Override
    public String[] getPackages() {
        return new String[] { "com.itrustcambodia.push" };
    }

    @Override
    public Class<? extends WebPage> getDashboardPage() {
        return DashboardPage.class;
    }

    @Override
    public Class<? extends AbstractUser> getUserEntity() {
        return User.class;
    }

//    @Override
//    protected DataSource initDataSource() {
//        CloudDataSourceFactory cloudDataSourceFactory = new CloudDataSourceFactory();
//        cloudDataSourceFactory.setServiceName("v5_mysql");
//        try {
//            cloudDataSourceFactory.afterPropertiesSet();
//            return cloudDataSourceFactory.getObject();
//        } catch (Exception e) {
//        }
//        return null;
//    }

     @Override
     protected DataSource initDataSource() {
     BoneCPDataSource dataSource = new BoneCPDataSource();
     dataSource.setDriverClass("com.mysql.jdbc.Driver");
     dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1/v5?createDatabaseIfNotExist=true");
     dataSource.setUsername("root");
     dataSource.setPassword("");
     dataSource.setIdleConnectionTestPeriod(60, TimeUnit.SECONDS);
     dataSource.setIdleMaxAgeInSeconds(240);
     dataSource.setMaxConnectionsPerPartition(30);
     dataSource.setMinConnectionsPerPartition(10);
     dataSource.setPartitionCount(3);
     dataSource.setAcquireIncrement(5);
     dataSource.setStatementsCacheSize(100);
     dataSource.setCloseConnectionWatch(true);
     dataSource.setReleaseHelperThreads(3);
     return dataSource;
     }

    @Override
    public String getRealm() {
        return "Security";
    }

    @Override
    public String getSecretKey() {
        return "d9b790d3a010441aa001d73141134c8d";
    }
}
