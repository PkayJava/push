package com.itrustcambodia.push.page.country;

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
import com.itrustcambodia.push.action.CountryActionPanel;
import com.itrustcambodia.push.entity.Country;
import com.itrustcambodia.push.provider.CountrySortableDataProvider;

@Mount("/countries")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_COUNTRY_MANAGEMENT", description = "Access Country Management Page") })
public class CountryManagementPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private CountrySortableDataProvider dataProvider;

    private FilterForm<Country> filterForm;

    @Override
    public String getPageTitle() {
        return "Country Management";
    }

    public CountryManagementPage() {
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        AbstractLayout layout = requestLayout("layout");
        add(layout);

        WebSession session = (WebSession) getSession();
        Roles roles = session.getRoles();

        BookmarkablePageLink<Void> newPage = new BookmarkablePageLink<Void>("newPage", NewCountryPage.class);
        layout.add(newPage);
        newPage.setVisible(FrameworkUtilities.hasAccess(roles, NewCountryPage.class));

        this.dataProvider = new CountrySortableDataProvider();

        this.filterForm = new FilterForm<Country>("filter-form", this.dataProvider);
        layout.add(filterForm);

        List<IColumn<Country, String>> columns = new ArrayList<IColumn<Country, String>>();
        if (FrameworkUtilities.hasAccess(roles, EditCountryPage.class)) {
            columns.add(createActionsColumn());
            columns.add(new PropertyColumn<Country, String>(Model.<String> of("ID"), Country.ID, "id"));
        } else {
            columns.add(createFilterColumn());
        }

        columns.add(createColumn("Name", Country.NAME, "name"));

        DataTable<Country, String> dataTable = new DefaultDataTable<Country, String>("table", columns, dataProvider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm, dataProvider));

        //
        filterForm.add(dataTable);
    }

    private TextFilteredPropertyColumn<Country, Country, String> createColumn(String key, String sortProperty, String propertyExpression) {
        return new TextFilteredPropertyColumn<Country, Country, String>(Model.<String> of(key), sortProperty, propertyExpression);
    }

    private FilteredAbstractColumn<Country, String> createFilterColumn() {
        return new FilteredAbstractColumn<Country, String>(new org.apache.wicket.model.Model<String>("ID / Filter")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, org.apache.wicket.model.Model.<String> of("Filter"), org.apache.wicket.model.Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<Country>> cellItem, String componentId, IModel<Country> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getId()));
            }
        };
    }

    private FilteredAbstractColumn<Country, String> createActionsColumn() {
        return new FilteredAbstractColumn<Country, String>(new Model<String>("Action / Filter")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, Model.<String> of("Filter"), Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<Country>> cellItem, String componentId, IModel<Country> rowModel) {
                cellItem.add(new CountryActionPanel(componentId, rowModel));
            }
        };
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getReferences();
    }

}
