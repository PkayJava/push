package com.itrustcambodia.push.page.city;

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
import com.itrustcambodia.push.MenuUtils;
import com.itrustcambodia.push.action.CityActionPanel;
import com.itrustcambodia.push.entity.City;
import com.itrustcambodia.push.provider.CitySortableDataProvider;

@Mount("/cities")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_CITY_MANAGEMENT", description = "Access City Management Page") })
public class CityManagementPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private CitySortableDataProvider dataProvider;

    private FilterForm<City> filterForm;

    @Override
    public String getPageTitle() {
        return "City Management";
    }

    public CityManagementPage() {
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        AbstractLayout layout = requestLayout("layout");
        add(layout);

        WebSession session = (WebSession) getSession();
        Roles roles = session.getRoles();

        BookmarkablePageLink<Void> newPage = new BookmarkablePageLink<Void>("newPage", NewCityPage.class);
        layout.add(newPage);
        newPage.setVisible(FrameworkUtilities.hasAccess(roles, NewCityPage.class));

        this.dataProvider = new CitySortableDataProvider();

        this.filterForm = new FilterForm<City>("filter-form", this.dataProvider);
        layout.add(filterForm);

        List<IColumn<City, String>> columns = new ArrayList<IColumn<City, String>>();
        if (FrameworkUtilities.hasAccess(roles, EditCityPage.class)) {
            columns.add(createActionsColumn());
            columns.add(new PropertyColumn<City, String>(Model.<String> of("ID"), City.ID, "id"));
        } else {
            columns.add(createFilterColumn());
        }

        columns.add(createColumn("Name", City.NAME, "name"));

        DataTable<City, String> dataTable = new DefaultDataTable<City, String>("table", columns, dataProvider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm, dataProvider));

        //
        filterForm.add(dataTable);
    }

    private TextFilteredPropertyColumn<City, City, String> createColumn(String key, String sortProperty, String propertyExpression) {
        return new TextFilteredPropertyColumn<City, City, String>(Model.<String> of(key), sortProperty, propertyExpression);
    }

    private FilteredAbstractColumn<City, String> createFilterColumn() {
        return new FilteredAbstractColumn<City, String>(new org.apache.wicket.model.Model<String>("ID / Filter")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, org.apache.wicket.model.Model.<String> of("Filter"), org.apache.wicket.model.Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<City>> cellItem, String componentId, IModel<City> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getId()));
            }
        };
    }

    private FilteredAbstractColumn<City, String> createActionsColumn() {
        return new FilteredAbstractColumn<City, String>(new Model<String>("Action / Filter")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, Model.<String> of("Filter"), Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<City>> cellItem, String componentId, IModel<City> rowModel) {
                cellItem.add(new CityActionPanel(componentId, rowModel));
            }
        };
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getReferences();
    }

}
