package com.itrustcambodia.v5.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.google.gson.Gson;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.core.WebSession;
import com.itrustcambodia.pluggable.page.KnownPage;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.validation.constraints.NotNull;
import com.itrustcambodia.pluggable.validation.constraints.Unique;
import com.itrustcambodia.pluggable.validation.controller.Navigation;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import com.itrustcambodia.pluggable.widget.Button;
import com.itrustcambodia.pluggable.widget.FileUploadField;
import com.itrustcambodia.pluggable.widget.TextArea;
import com.itrustcambodia.pluggable.widget.TextField;
import com.itrustcambodia.v5.MenuUtils;
import com.itrustcambodia.v5.entity.Application;
import com.itrustcambodia.v5.entity.User;

@Mount("/napp")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_NEW_APPLICATION", description = "Access New Application Page") })
public class NewApplicationPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private Long userId;

    @NotNull
    @TextField(label = "Name", placeholder = "Name", order = 1)
    @Unique(entity = Application.class, where = Application.NAME + " = :name and " + Application.USER_ID + " = :userId")
    private String name;

    @TextArea(label = "Description", order = 2)
    private String description;

    @TextField(label = "Android Package Id", placeholder = "Android Package Id", order = 3)
    @Unique(entity = Application.class, where = Application.PACKAGE_ID + " = :androidPackageId")
    private String androidPackageId;

    @TextField(label = "Sender Id", placeholder = "Sender Id", order = 4)
    private String sendId;

    @TextField(label = "Android API Key", placeholder = "Android API Key", order = 5)
    private String androidAPIKey;

    @FileUploadField(label = "iOS Push Certificate", order = 6)
    private FileUpload iOSPushCertificate;

    @TextField(label = "iOS Push Certificate Secret", placeholder = "Secret Key", order = 7)
    private String iOSPushCertificatePassword;

    public NewApplicationPage() {
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        WebSession session = (WebSession) getSession();
        this.userId = jdbcTemplate.queryForObject("select " + User.ID + " from " + TableUtilities.getTableName(User.class) + " where " + User.LOGIN + " = ?", Long.class, session.getUsername());
    }

    @Button(label = "Okay", order = 1, validate = true)
    public Navigation okay() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        Gson gson = application.getGson();
        SimpleJdbcInsert insert = new SimpleJdbcInsert(application.getJdbcTemplate());
        insert.withTableName(TableUtilities.getTableName(Application.class));
        Map<String, Object> fields = new HashMap<String, Object>();

        fields.put(Application.NAME, this.name);

        if (this.description != null && !"".equals(this.description)) {
            fields.put(Application.DESCRIPTION, this.description);
        }

        fields.put(Application.PACKAGE_ID, this.androidPackageId);
        fields.put(Application.USER_ID, this.userId);
        fields.put(Application.ANDROID_API_KEY, this.androidAPIKey);
        fields.put(Application.SENDER_ID, this.sendId);
        if (iOSPushCertificate != null && iOSPushCertificate.getBytes() != null && iOSPushCertificate.getBytes().length > 0) {
            fields.put(Application.IOS_PUSH_CERTIFICATE, gson.toJson(iOSPushCertificate.getBytes()));
        }
        fields.put(Application.IOS_PUSH_CERTIFICATE_PASSWORD, this.iOSPushCertificatePassword);

        insert.execute(fields);

        return new Navigation(ApplicationManagementPage.class);
    }

    @Button(label = "Cancel", order = 2, validate = false)
    public Navigation cancel() {
        return new Navigation(ApplicationManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "New Application";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getApps();
    }
}
