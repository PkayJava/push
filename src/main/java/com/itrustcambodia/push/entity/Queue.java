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
@Table(name = "tbl_queue")
public class Queue implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8146967139989617767L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, columnDefinition = "INT")
    private Long id;
    public static final String ID = "queue_id";

    @Column(name = MESSAGE, columnDefinition = "TEXT")
    private String message;
    public static final String MESSAGE = "message";

    @Column(name = QUEUE_DATE, columnDefinition = "TIMESTAMP")
    private Date queueDate;
    public static final String QUEUE_DATE = "queue_date";

    @Column(name = USER_ID, columnDefinition = "INT")
    private Long userId;
    public static final String USER_ID = User.ID;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getQueueDate() {
        return queueDate;
    }

    public void setQueueDate(Date queueDate) {
        this.queueDate = queueDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
