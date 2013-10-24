package com.itrustcambodia.push.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
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
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter.GoAndClearFilter;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import com.itrustcambodia.push.MenuUtils;
import com.itrustcambodia.push.action.ApplicationActionPanel;
import com.itrustcambodia.push.entity.Application;
import com.itrustcambodia.push.provider.ApplicationSortableDataProvider;

@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_APPLICATION_MANAGEMENT", description = "Access Application Management Page") })
@Mount("/apps")
public class ApplicationManagementPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private ApplicationSortableDataProvider dataProvider;

    private FilterForm<Application> filterForm;

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

        Application filter = new Application();
        filter.setUserId(userId);
        this.dataProvider = new ApplicationSortableDataProvider(filter);

        this.filterForm = new FilterForm<Application>("filter-form", this.dataProvider);
        layout.add(filterForm);

        List<IColumn<Application, String>> columns = new ArrayList<IColumn<Application, String>>();
        columns.add(createActionsColumn());

        columns.add(new PropertyColumn<Application, String>(Model.<String> of("ID"), Application.ID, "id"));
        columns.add(createColumn("Name", Application.NAME, "name"));
        columns.add(createColumn("Android Package Id", Application.PACKAGE_ID, "packageId"));
        columns.add(createColumn("Sender/Project Id ", Application.SENDER_ID, "senderId"));

        DataTable<Application, String> dataTable = new DefaultDataTable<Application, String>("table", columns, dataProvider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm, dataProvider));

        //
        filterForm.add(dataTable);
    }

    private TextFilteredPropertyColumn<Application, Application, String> createColumn(String key, String sortProperty, String propertyExpression) {
        return new TextFilteredPropertyColumn<Application, Application, String>(Model.<String> of(key), sortProperty, propertyExpression);
    }

    private FilteredAbstractColumn<Application, String> createActionsColumn() {
        return new FilteredAbstractColumn<Application, String>(new Model<String>("Actions")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, Model.<String> of("Filter"), Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<Application>> cellItem, String componentId, IModel<Application> rowModel) {
                cellItem.add(new ApplicationActionPanel(componentId, rowModel));
            }
        };
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getApps();
    }

}
