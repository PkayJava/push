package com.itrustcambodia.push.entity;

import java.io.Serializable;
import java.util.Date;

import com.itrustcambodia.pluggable.database.annotation.Column;
import com.itrustcambodia.pluggable.database.annotation.Entity;
import com.itrustcambodia.pluggable.database.annotation.GeneratedValue;
import com.itrustcambodia.pluggable.database.annotation.GenerationType;
import com.itrustcambodia.pluggable.database.annotation.Id;
import com.itrustcambodia.pluggable.database.annotation.Table;

@Entity
@Table(name = "tbl_history")
public class History implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5342756469170663058L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, columnDefinition = "INT")
    private Long id;
    public static final String ID = "history_id";

    @Column(name = TOKEN, columnDefinition = "VARCHAR(255)")
    private String token;
    public static final String TOKEN = "token";

    @Column(name = MESSAGE, columnDefinition = "TEXT")
    private String message;
    public static final String MESSAGE = "message";

    @Column(name = VERSION, columnDefinition = "VARCHAR(255)")
    private String version;
    public static final String VERSION = "version";

    @Column(name = PLATFORM, columnDefinition = "VARCHAR(255)")
    private String platform;
    public static final String PLATFORM = "platform";

    @Column(name = IP, columnDefinition = "VARCHAR(255)")
    private String ip;
    public static final String IP = "ip";

    @Column(name = QUEUE_DATE, columnDefinition = "TIMESTAMP")
    private Date queueDate;
    public static final String QUEUE_DATE = "queue_date";

    @Column(name = SENT_DATE, columnDefinition = "TIMESTAMP")
    private Date sentDate;
    public static final String SENT_DATE = "sent_date";

    @Column(name = STATUS, columnDefinition = "VARCHAR(255)")
    private String status;
    public static final String STATUS = "status";

    @Column(name = MANUFACTURE, columnDefinition = "VARCHAR(255)")
    private String manufacture;
    public static final String MANUFACTURE = "manufacture";

    @Column(name = APPLICATION, columnDefinition = "VARCHAR(255)")
    private String application;
    public static final String APPLICATION = "application";

    @Column(name = MODEL, columnDefinition = "VARCHAR(255)")
    private String model;
    public static final String MODEL = "model";

    @Column(name = USER_ID, columnDefinition = "INT")
    private Long userId;
    public static final String USER_ID = User.ID;

    @Column(name = CITY, columnDefinition = "VARCHAR(255)")
    private Long city;
    public static final String CITY = "city";

    @Column(name = COUNTRY, columnDefinition = "VARCHAR(255)")
    private Long country;
    public static final String COUNTRY = "country";

    public static abstract class Status {
        public static final String SUCCESS = "SUCCESS";
        public static final String ERROR = "ERROR";
        public static final String CANCEL = "CANCEL";
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getQueueDate() {
        return queueDate;
    }

    public void setQueueDate(Date queueDate) {
        this.queueDate = queueDate;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCity() {
        return city;
    }

    public void setCity(Long city) {
        this.city = city;
    }

    public Long getCountry() {
        return country;
    }

    public void setCountry(Long country) {
        this.country = country;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

}
