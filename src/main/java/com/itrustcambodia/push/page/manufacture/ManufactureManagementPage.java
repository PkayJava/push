package com.itrustcambodia.push.page.manufacture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilteredAbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.core.WebSession;
import com.itrustcambodia.pluggable.layout.AbstractLayout;
import com.itrustcambodia.pluggable.page.WebPage;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;
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
import com.itrustcambodia.push.action.ManufactureActionPanel;
import com.itrustcambodia.push.entity.Manufacture;

@Mount("/manufactures")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_MANUFACTURE_MANAGEMENT", description = "Access Manufacture Management Page") })
public class ManufactureManagementPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private MapSortableDataProvider dataProvider;

    private FilterForm<Map<String, Object>> filterForm;

    @Override
    public String getPageTitle() {
        return "Manufacture Management";
    }

    public ManufactureManagementPage() {
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        AbstractLayout layout = requestLayout("layout");
        add(layout);

        WebSession session = (WebSession) getSession();
        Roles roles = session.getRoles();

        BookmarkablePageLink<Void> newPage = new BookmarkablePageLink<Void>("newPage", NewManufacturePage.class);
        layout.add(newPage);
        newPage.setVisible(FrameworkUtilities.hasAccess(roles, NewManufacturePage.class));

        this.dataProvider = new MapSortableDataProvider(TableUtilities.getTableName(Manufacture.class));

        this.filterForm = new FilterForm<Map<String, Object>>("filter-form", this.dataProvider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<IColumn<Map<String, Object>, String>>();
        if (FrameworkUtilities.hasAccess(roles, EditManufacturePage.class)) {
            columns.add(createActionsColumn());
            columns.add(new PropertyColumn<Map<String, Object>, String>(Model.<String> of("ID"), TableUtilities.getTableName(Manufacture.class) + "." + Manufacture.ID, TableUtilities.getTableName(Manufacture.class) + "." + Manufacture.ID));
        } else {
            columns.add(createFilterColumn());
        }

        columns.add(createColumn("Name", TableUtilities.getTableName(Manufacture.class) + "." + Manufacture.NAME, TableUtilities.getTableName(Manufacture.class) + "." + Manufacture.NAME));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<Map<String, Object>, String>("table", columns, dataProvider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm, dataProvider));

        //
        filterForm.add(dataTable);
    }

    private TextFilteredPropertyColumn<Map<String, Object>, Map<String, Object>, String> createColumn(String key, String sortProperty, String propertyExpression) {
        return new TextFilteredPropertyColumn<Map<String, Object>, Map<String, Object>, String>(Model.<String> of(key), sortProperty, propertyExpression);
    }

    private FilteredAbstractColumn<Map<String, Object>, String> createFilterColumn() {
        return new FilteredAbstractColumn<Map<String, Object>, String>(new org.apache.wicket.model.Model<String>("ID / Filter")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, org.apache.wicket.model.Model.<String> of("Filter"), org.apache.wicket.model.Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<Map<String, Object>>> cellItem, String componentId, IModel<Map<String, Object>> rowModel) {
                cellItem.add(new Label(componentId, (Number) rowModel.getObject().get(TableUtilities.getTableName(Manufacture.class) + "." + Manufacture.ID)));
            }
        };
    }

    private FilteredAbstractColumn<Map<String, Object>, String> createActionsColumn() {
        return new FilteredAbstractColumn<Map<String, Object>, String>(new Model<String>("Action / Filter")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, Model.<String> of("Filter"), Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<Map<String, Object>>> cellItem, String componentId, IModel<Map<String, Object>> rowModel) {
                cellItem.add(new ManufactureActionPanel(componentId, rowModel));
            }
        };
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getReferences();
    }

}
