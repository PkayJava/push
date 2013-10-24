package com.itrustcambodia.push.entity;

import java.io.Serializable;

import com.itrustcambodia.pluggable.database.annotation.Column;
import com.itrustcambodia.pluggable.database.annotation.Entity;
import com.itrustcambodia.pluggable.database.annotation.GeneratedValue;
import com.itrustcambodia.pluggable.database.annotation.GenerationType;
import com.itrustcambodia.pluggable.database.annotation.Id;
import com.itrustcambodia.pluggable.database.annotation.Table;

@Entity
@Table(name = "tbl_platform")
public class Platform implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -9044132266649833376L;

    @Id
    @Column(name = ID, columnDefinition = "INT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public static final String ID = "platform_id";

    @Column(name = NAME, columnDefinition = "VARCHAR(255)")
    private String name;
    public static final String NAME = "name";

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

}
