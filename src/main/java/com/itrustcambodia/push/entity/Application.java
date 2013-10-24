package com.itrustcambodia.push.entity;

import java.io.Serializable;

import com.itrustcambodia.pluggable.database.annotation.Column;
import com.itrustcambodia.pluggable.database.annotation.Entity;
import com.itrustcambodia.pluggable.database.annotation.GeneratedValue;
import com.itrustcambodia.pluggable.database.annotation.GenerationType;
import com.itrustcambodia.pluggable.database.annotation.Id;
import com.itrustcambodia.pluggable.database.annotation.Table;
import com.itrustcambodia.pluggable.database.annotation.Unique;

@Entity
@Table(name = "tbl_application")
public class Application implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 73511044537029437L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, columnDefinition = "INT")
    private Long id;
    public static final String ID = "application_id";

    @Column(name = NAME, columnDefinition = "VARCHAR(255)")
    private String name;
    public static final String NAME = "name";

    @Column(name = SENDER_ID, columnDefinition = "VARCHAR(255)")
    private String senderId;
    public static final String SENDER_ID = "sender_id";

    @Column(name = DESCRIPTION, columnDefinition = "VARCHAR(255)")
    private String description;
    public static final String DESCRIPTION = "description";

    @Unique
    @Column(name = PACKAGE_ID, columnDefinition = "VARCHAR(255)")
    private String packageId;
    public static final String PACKAGE_ID = "package_id";

    @Unique
    @Column(name = BUNDLE_ID, columnDefinition = "VARCHAR(255)")
    private String bundleId;
    public static final String BUNDLE_ID = "bundle_id";

    @Column(name = ANDROID_API_KEY, columnDefinition = "TEXT")
    private String androidAPIKey;
    public static final String ANDROID_API_KEY = "android_api_key";

    @Column(name = IOS_PUSH_CERTIFICATE, columnDefinition = "TEXT")
    private String iOSPushCertificate;
    public static final String IOS_PUSH_CERTIFICATE = "ios_push_certificate";

    @Column(name = IOS_PUSH_CERTIFICATE_PASSWORD, columnDefinition = "VARCHAR(255)")
    private String iOSPushCertificatePassword;
    public static final String IOS_PUSH_CERTIFICATE_PASSWORD = "ios_push_certificate_password";

    @Column(name = USER_ID, columnDefinition = "INT")
    private Long userId;
    public static final String USER_ID = User.ID;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getAndroidAPIKey() {
        return androidAPIKey;
    }

    public void setAndroidAPIKey(String androidAPIKey) {
        this.androidAPIKey = androidAPIKey;
    }

    public String getiOSPushCertificate() {
        return iOSPushCertificate;
    }

    public void setiOSPushCertificate(String iOSPushCertificate) {
        this.iOSPushCertificate = iOSPushCertificate;
    }

    public String getiOSPushCertificatePassword() {
        return iOSPushCertificatePassword;
    }

    public void setiOSPushCertificatePassword(String iOSPushCertificatePassword) {
        this.iOSPushCertificatePassword = iOSPushCertificatePassword;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

}
