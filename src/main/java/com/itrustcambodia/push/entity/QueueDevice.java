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
@Table(name = "tbl_queue_device")
public class QueueDevice implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1189944225000859120L;

    private static final String A = "a";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, columnDefinition = "INT")
    private Long id;
    public static final String ID = "queue_device_id";

    @Unique(group = A)
    @Column(name = QUEUE_ID, columnDefinition = "INT", nullable = false)
    private Long queueId;
    public static final String QUEUE_ID = Queue.ID;

    @Unique(group = A)
    @Column(name = DEVICE_ID, columnDefinition = "INT", nullable = false)
    private Long deviceId;
    public static final String DEVICE_ID = Device.ID;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

}
