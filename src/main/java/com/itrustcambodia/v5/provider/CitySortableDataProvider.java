package com.itrustcambodia.v5.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.v5.entity.Application;
import com.itrustcambodia.v5.entity.City;

public class CitySortableDataProvider extends SortableDataProvider<City, String> implements IFilterStateLocator<City> {

    /**
     * 
     */
    private static final long serialVersionUID = -8377805268917413429L;

    private City filter = new City();

    private Map<String, Object> whereValues = new HashMap<String, Object>();

    /**
     * constructor
     */
    public CitySortableDataProvider() {
        this.filter = new City();
        setSort("name", SortOrder.ASCENDING);
    }

    public CitySortableDataProvider(City filter) {
        this.filter = filter;
        setSort("name", SortOrder.ASCENDING);
    }

    private String select() {
        return "select * from " + TableUtilities.getTableName(City.class);
    }

    private String count() {
        return "select count(*) from " + TableUtilities.getTableName(City.class);
    }

    private String where() {
        List<String> where = new ArrayList<String>();
        whereValues.clear();

        if (filter.getName() != null && !"".equals(filter.getName())) {
            where.add(Application.NAME + " like :name");
            whereValues.put("name", filter.getName() + "%");
        }

        if (!where.isEmpty()) {
            return "where " + StringUtils.join(where, " and ");
        } else {
            return "";
        }

    }

    private String orderBy() {
        return "order by " + getSort().getProperty() + " " + (getSort().isAscending() ? "asc" : "desc");
    }

    private String limit(long first, long count) {
        return "limit " + first + "," + count;
    }

    @Override
    public Iterator<City> iterator(long first, long count) {
        AbstractWebApplication application = (AbstractWebApplication) org.apache.wicket.Application.get();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        String where = where();
        if ("".equals(where)) {
            return jdbcTemplate.query(select() + " " + orderBy() + " " + limit(first, count), new EntityRowMapper<City>(City.class)).listIterator();
        } else {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
            return template.query(select() + " " + where + " " + orderBy() + " " + limit(first, count), whereValues, new EntityRowMapper<City>(City.class)).listIterator();
        }
    }

    /**
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public long size() {
        AbstractWebApplication application = (AbstractWebApplication) org.apache.wicket.Application.get();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        String where = where();
        if ("".equals(where)) {
            return jdbcTemplate.queryForObject(count(), Long.class);
        } else {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
            return template.queryForObject(count() + " " + where, whereValues, Long.class);
        }
    }

    /**
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#model(java.lang.Object)
     */
    @Override
    public IModel<City> model(City object) {
        return new org.apache.wicket.model.Model<City>(object);
    }

    @Override
    public City getFilterState() {
        return this.filter;
    }

    @Override
    public void setFilterState(City state) {
        this.filter = state;
    }

}