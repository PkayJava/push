package com.itrustcambodia.v5.page.manufacture;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
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
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter.GoAndClearFilter;
import com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import com.itrustcambodia.v5.MenuUtils;
import com.itrustcambodia.v5.action.ManufactureActionPanel;
import com.itrustcambodia.v5.entity.Manufacture;
import com.itrustcambodia.v5.provider.ManufactureSortableDataProvider;

@Mount("/manufactures")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_MANUFACTURE_MANAGEMENT", description = "Access Manufacture Management Page") })
public class ManufactureManagementPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private ManufactureSortableDataProvider dataProvider;

    private FilterForm<Manufacture> filterForm;

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

        this.dataProvider = new ManufactureSortableDataProvider();

        this.filterForm = new FilterForm<Manufacture>("filter-form", this.dataProvider);
        layout.add(filterForm);

        List<IColumn<Manufacture, String>> columns = new ArrayList<IColumn<Manufacture, String>>();
        if (FrameworkUtilities.hasAccess(roles, EditManufacturePage.class)) {
            columns.add(createActionsColumn());
            columns.add(new PropertyColumn<Manufacture, String>(Model.<String> of("ID"), Manufacture.ID, "id"));
        } else {
            columns.add(createFilterColumn());
        }

        columns.add(createColumn("Name", Manufacture.NAME, "name"));

        DataTable<Manufacture, String> dataTable = new DefaultDataTable<Manufacture, String>("table", columns, dataProvider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm, dataProvider));

        //
        filterForm.add(dataTable);
    }

    private TextFilteredPropertyColumn<Manufacture, Manufacture, String> createColumn(String key, String sortProperty, String propertyExpression) {
        return new TextFilteredPropertyColumn<Manufacture, Manufacture, String>(Model.<String> of(key), sortProperty, propertyExpression);
    }

    private FilteredAbstractColumn<Manufacture, String> createFilterColumn() {
        return new FilteredAbstractColumn<Manufacture, String>(new org.apache.wicket.model.Model<String>("ID / Filter")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, org.apache.wicket.model.Model.<String> of("Filter"), org.apache.wicket.model.Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<Manufacture>> cellItem, String componentId, IModel<Manufacture> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getId()));
            }
        };
    }

    private FilteredAbstractColumn<Manufacture, String> createActionsColumn() {
        return new FilteredAbstractColumn<Manufacture, String>(new Model<String>("Action / Filter")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, Model.<String> of("Filter"), Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<Manufacture>> cellItem, String componentId, IModel<Manufacture> rowModel) {
                cellItem.add(new ManufactureActionPanel(componentId, rowModel));
            }
        };
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getReferences();
    }

}
