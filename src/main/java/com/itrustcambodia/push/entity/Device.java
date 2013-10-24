package com.itrustcambodia.push.entity;

import java.io.Serializable;

import com.itrustcambodia.pluggable.database.annotation.Column;
import com.itrustcambodia.pluggable.database.annotation.Entity;
import com.itrustcambodia.pluggable.database.annotation.GeneratedValue;
import com.itrustcambodia.pluggable.database.annotation.GenerationType;
import com.itrustcambodia.pluggable.database.annotation.Id;
import com.itrustcambodia.pluggable.database.annotation.Table;

@Entity
@Table(name = "tbl_device")
public class Device implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5251458334819130694L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, columnDefinition = "INT")
    private Long id;
    public static final String ID = "device_id";

    @Column(name = TOKEN, columnDefinition = "VARCHAR(255)")
    private String token;
    public static final String TOKEN = "token";

    @Column(name = PLATFORM_ID, columnDefinition = "INT")
    private Long platformId;
    public static final String PLATFORM_ID = "platform_id";

    @Column(name = VERSION_ID, columnDefinition = "INT")
    private Long versionId;
    public static final String VERSION_ID = "version_id";

    @Column(name = MANUFACTURE_ID, columnDefinition = "INT")
    private Long manufactureId;
    public static final String MANUFACTURE_ID = "manufacture_id";

    @Column(name = MODEL_ID, columnDefinition = "INT")
    private Long modelId;
    public static final String MODEL_ID = "model_id";

    @Column(name = IP, columnDefinition = "VARCHAR(255)")
    private String ip;
    public static final String IP = "ip";

    @Column(name = FLAG, columnDefinition = "VARCHAR(255)")
    private String flag;
    public static final String FLAG = "flag";

    @Column(name = APPLICATION_ID, columnDefinition = "INT")
    private Long applicationId;
    public static final String APPLICATION_ID = Application.ID;

    @Column(name = COUNTRY_ID, columnDefinition = "INT")
    private Long countryId;
    public static final String COUNTRY_ID = Country.ID;

    @Column(name = CITY_ID, columnDefinition = "INT")
    private Long cityId;
    public static final String CITY_ID = City.ID;

    public static abstract class Flag {
        public static final String ACTIVE = "ACTIVE";
        public static final String DELETE = "DELETE";
    }

    public static abstract class Platform {
        public static final String IOS = "iOS";
        public static final String ANDROID = "Android";
        public static final String BLACK_BERRY = "Black Berry";
        public static final String WINDOW_PHONE = "Window Phone";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getManufactureId() {
        return manufactureId;
    }

    public void setManufactureId(Long manufactureId) {
        this.manufactureId = manufactureId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

}
