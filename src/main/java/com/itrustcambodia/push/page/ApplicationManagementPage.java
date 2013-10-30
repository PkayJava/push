package com.itrustcambodia.push.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilteredAbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.core.WebSession;
import com.itrustcambodia.pluggable.layout.AbstractLayout;
import com.itrustcambodia.pluggable.page.WebPage;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter.GoAndClearFilter;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.util.MapSortableDataProvider;
import com.itrustcambodia.push.MenuUtils;
import com.itrustcambodia.push.action.ApplicationActionPanel;
import com.itrustcambodia.push.entity.Application;

@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_APPLICATION_MANAGEMENT", description = "Access Application Management Page") })
@Mount("/apps")
public class ApplicationManagementPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private MapSortableDataProvider dataProvider;

    private FilterForm<Map<String, Object>> filterForm;

    @Override
    public String getPageTitle() {
        return "Application Management";
    }

    public ApplicationManagementPage() {
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        AbstractLayout layout = requestLayout("layout");
        add(layout);
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        WebSession session = (WebSession) getSession();

        Long userId = jdbcTemplate.queryForObject("select " + com.itrustcambodia.push.entity.User.ID + " from " + TableUtilities.getTableName(com.itrustcambodia.push.entity.User.class) + " where " + com.itrustcambodia.push.entity.User.LOGIN + " = ?", Long.class, session.getUsername());

        this.dataProvider = new MapSortableDataProvider(TableUtilities.getTableName(Application.class));
        this.dataProvider.addWhere(TableUtilities.getTableName(Application.class) + "." + Application.USER_ID, userId);

        this.filterForm = new FilterForm<Map<String, Object>>("filter-form", this.dataProvider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<IColumn<Map<String, Object>, String>>();
        columns.add(createActionsColumn());

        columns.add(new PropertyColumn<Map<String, Object>, String>(Model.<String> of("ID"), TableUtilities.getTableName(Application.class) + "." + Application.ID, TableUtilities.getTableName(Application.class) + "." + Application.ID));
        columns.add(createColumn("Name", TableUtilities.getTableName(Application.class) + "." + Application.NAME, TableUtilities.getTableName(Application.class) + "." + Application.NAME));
        columns.add(createColumn("Android Package ID", TableUtilities.getTableName(Application.class) + "." + Application.PACKAGE_ID, TableUtilities.getTableName(Application.class) + "." + Application.PACKAGE_ID));
        columns.add(createColumn("Sender/Project ID ", TableUtilities.getTableName(Application.class) + "." + Application.SENDER_ID, TableUtilities.getTableName(Application.class) + "." + Application.SENDER_ID));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<Map<String, Object>, String>("table", columns, dataProvider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm, dataProvider));

        //
        filterForm.add(dataTable);
    }

    private TextFilteredPropertyColumn<Map<String, Object>, Map<String, Object>, String> createColumn(String key, String sortProperty, String propertyExpression) {
        return new TextFilteredPropertyColumn<Map<String, Object>, Map<String, Object>, String>(Model.<String> of(key), sortProperty, propertyExpression);
    }

    private FilteredAbstractColumn<Map<String, Object>, String> createActionsColumn() {
        return new FilteredAbstractColumn<Map<String, Object>, String>(new Model<String>("Actions")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, Model.<String> of("Filter"), Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<Map<String, Object>>> cellItem, String componentId, IModel<Map<String, Object>> rowModel) {
                cellItem.add(new ApplicationActionPanel(componentId, rowModel));
            }
        };
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getApps();
    }

}
