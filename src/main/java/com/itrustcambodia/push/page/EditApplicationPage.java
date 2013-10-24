package com.itrustcambodia.push.page;

import java.util.List;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.springframework.jdbc.core.JdbcTemplate;

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
import com.itrustcambodia.push.MenuUtils;
import com.itrustcambodia.push.entity.Application;
import com.itrustcambodia.push.entity.User;

@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_EDIT_APPLICATION", description = "Access Edit Application Page") })
@Mount("/eapp")
public class EditApplicationPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private Long userId;

    private Long applicationId;

    @NotNull
    @TextField(label = "Name", placeholder = "Name", order = 1)
    @Unique(entity = Application.class, where = Application.NAME + " = :name and " + Application.USER_ID + " = :userId and " + Application.ID + " != :applicationId")
    private String name;

    @TextArea(label = "Description", order = 2)
    private String description;

    @TextField(label = "Android Package Id", placeholder = "Android Package Id", order = 3)
    @Unique(entity = Application.class, where = Application.PACKAGE_ID + " = :androidPackageId and " + Application.ID + " != :applicationId")
    private String androidPackageId;

    @TextField(label = "Sender Id", placeholder = "Sender Id", order = 3.1)
    private String sendId;

    @TextField(label = "Android API Key", placeholder = "Android API Key", order = 4)
    private String androidAPIKey;

    @FileUploadField(label = "iOS Push Certificate", order = 6)
    private FileUpload iOSPushCertificate;

    @TextField(label = "iOS Push Certificate Secret", placeholder = "Secret Key", order = 7)
    private String iOSPushCertificatePassword;

    public EditApplicationPage(Application application) {
        this.name = application.getName();
        this.description = application.getDescription();
        this.androidAPIKey = application.getAndroidAPIKey();
        this.androidPackageId = application.getPackageId();
        this.sendId = application.getSenderId();
        this.userId = application.getUserId();
        this.applicationId = application.getId();
        this.iOSPushCertificatePassword = application.getiOSPushCertificatePassword();
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
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        if (iOSPushCertificate == null || iOSPushCertificate.getBytes() == null || iOSPushCertificate.getBytes().length == 0) {
            jdbcTemplate.update("update " + TableUtilities.getTableName(Application.class) + " set " + Application.NAME + " = ?, " + Application.DESCRIPTION + " = ?, " + Application.PACKAGE_ID + " = ?, " + Application.ANDROID_API_KEY + " = ?, " + Application.SENDER_ID + " = ?, " + Application.IOS_PUSH_CERTIFICATE_PASSWORD + " = ? where " + Application.ID + " = ?", this.name, this.description, this.androidPackageId, this.androidAPIKey, this.sendId, this.iOSPushCertificatePassword, this.applicationId);
        } else {
            String iosCertificate = application.getGson().toJson(this.iOSPushCertificate.getBytes());
            jdbcTemplate.update("update " + TableUtilities.getTableName(Application.class) + " set " + Application.NAME + " = ?, " + Application.DESCRIPTION + " = ?, " + Application.PACKAGE_ID + " = ?, " + Application.ANDROID_API_KEY + " = ?, " + Application.SENDER_ID + " = ?, " + Application.IOS_PUSH_CERTIFICATE_PASSWORD + " = ?, " + Application.IOS_PUSH_CERTIFICATE + " = ? where " + Application.ID + " = ?", this.name, this.description, this.androidPackageId, this.androidAPIKey, this.sendId, this.iOSPushCertificatePassword, iosCertificate, this.applicationId);
        }

        return new Navigation(ApplicationManagementPage.class);
    }

    @Button(label = "Cancel", order = 3, validate = false)
    public Navigation cancel() {
        return new Navigation(ApplicationManagementPage.class);
    }

    @Button(label = "Delete", order = 2, validate = false)
    public Navigation delete() {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", this.applicationId);
        return new Navigation(ApplicationManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "Edit Application";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getApps();
    }

}
