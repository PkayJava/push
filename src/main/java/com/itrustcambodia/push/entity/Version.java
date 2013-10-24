package com.itrustcambodia.push.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.itrustcambodia.pluggable.database.annotation.Column;
import com.itrustcambodia.pluggable.database.annotation.Entity;
import com.itrustcambodia.pluggable.database.annotation.GeneratedValue;
import com.itrustcambodia.pluggable.database.annotation.GenerationType;
import com.itrustcambodia.pluggable.database.annotation.Id;
import com.itrustcambodia.pluggable.database.annotation.Table;

@Entity
@Table(name = "tbl_version")
public class Version implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -9044132266649833376L;

    public static final Map<String, String> VERSION = new HashMap<String, String>();

    static {
        VERSION.put("3", "Cupcake");
        VERSION.put("4", "Donut");
        VERSION.put("5", "Eclair");
        VERSION.put("6", "Eclair");
        VERSION.put("7", "Eclair");
        VERSION.put("8", "Froyo");
        VERSION.put("9", "Gingerbread");
        VERSION.put("10", "Gingerbread");
        VERSION.put("11", "Honeycomb");
        VERSION.put("12", "Honeycomb");
        VERSION.put("13", "Honeycomb");
        VERSION.put("14", "Cream Sandwich");
        VERSION.put("15", "Cream Sandwich");
        VERSION.put("16", "Jelly Bean 4.1.x");
        VERSION.put("17", "Jelly Bean 4.2.x");
        VERSION.put("18", "Jelly Bean 4.3.x");
        VERSION.put("19", "KitKat 4.4");
        VERSION.put("7.0.1", "iOS 7.0.1");
        VERSION.put("7.0.2", "iOS 7.0.2");
        VERSION.put("7.0.3", "iOS 7.0.3");
    }

    @Id
    @Column(name = ID, columnDefinition = "INT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public static final String ID = "version_id";

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

    public String getDescription() {
        if (this.name != null && !"".equals(this.name)) {
            String code = VERSION.get(this.name);
            if (code == null || "".equals(code)) {
                return this.name;
            } else {
                return code;
            }
        }
        return null;
    }

}
