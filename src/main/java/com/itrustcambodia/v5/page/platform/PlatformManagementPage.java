package com.itrustcambodia.v5.page.platform;

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
import com.itrustcambodia.v5.action.PlatformActionPanel;
import com.itrustcambodia.v5.entity.Platform;
import com.itrustcambodia.v5.provider.PlatformSortableDataProvider;

@Mount("/platforms")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_PLATFORM_MANAGEMENT", description = "Access Platform Management Page") })
public class PlatformManagementPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private PlatformSortableDataProvider dataProvider;

    private FilterForm<Platform> filterForm;

    @Override
    public String getPageTitle() {
        return "Platform Management";
    }

    public PlatformManagementPage() {
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        AbstractLayout layout = requestLayout("layout");
        add(layout);

        WebSession session = (WebSession) getSession();
        Roles roles = session.getRoles();
        BookmarkablePageLink<Void> newPage = new BookmarkablePageLink<Void>("newPage", NewPlatformPage.class);
        layout.add(newPage);
        newPage.setVisible(FrameworkUtilities.hasAccess(roles, NewPlatformPage.class));

        this.dataProvider = new PlatformSortableDataProvider();

        this.filterForm = new FilterForm<Platform>("filter-form", this.dataProvider);
        layout.add(filterForm);

        List<IColumn<Platform, String>> columns = new ArrayList<IColumn<Platform, String>>();
        if (FrameworkUtilities.hasAccess(roles, EditPlatformPage.class)) {
            columns.add(createActionsColumn());
            columns.add(new PropertyColumn<Platform, String>(org.apache.wicket.model.Model.<String> of("ID"), Platform.ID, "id"));
        } else {
            columns.add(createFilterColumn());
        }

        columns.add(createColumn("Name", Platform.NAME, "name"));

        DataTable<Platform, String> dataTable = new DefaultDataTable<Platform, String>("table", columns, dataProvider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm, dataProvider));

        //
        filterForm.add(dataTable);
    }

    private TextFilteredPropertyColumn<Platform, Platform, String> createColumn(String key, String sortProperty, String propertyExpression) {
        return new TextFilteredPropertyColumn<Platform, Platform, String>(org.apache.wicket.model.Model.<String> of(key), sortProperty, propertyExpression);
    }

    private FilteredAbstractColumn<Platform, String> createActionsColumn() {
        return new FilteredAbstractColumn<Platform, String>(new org.apache.wicket.model.Model<String>("Action / Filter")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, org.apache.wicket.model.Model.<String> of("Filter"), org.apache.wicket.model.Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<Platform>> cellItem, String componentId, IModel<Platform> rowModel) {
                cellItem.add(new PlatformActionPanel(componentId, rowModel));
            }
        };
    }

    private FilteredAbstractColumn<Platform, String> createFilterColumn() {
        return new FilteredAbstractColumn<Platform, String>(new org.apache.wicket.model.Model<String>("ID / Filter")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, org.apache.wicket.model.Model.<String> of("Filter"), org.apache.wicket.model.Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<Platform>> cellItem, String componentId, IModel<Platform> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getId()));
            }
        };
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getReferences();
    }

}
