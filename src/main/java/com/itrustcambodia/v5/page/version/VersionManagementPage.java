package com.itrustcambodia.v5.page.version;

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
import com.itrustcambodia.v5.action.VersionActionPanel;
import com.itrustcambodia.v5.entity.Version;
import com.itrustcambodia.v5.provider.VersionSortableDataProvider;

@Mount("/versions")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_VERSION_MANAGEMENT", description = "Access Version Management Page") })
public class VersionManagementPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    private VersionSortableDataProvider dataProvider;

    private FilterForm<Version> filterForm;

    @Override
    public String getPageTitle() {
        return "Version Management";
    }

    public VersionManagementPage() {
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        AbstractLayout layout = requestLayout("layout");
        add(layout);

        WebSession session = (WebSession) getSession();
        Roles roles = session.getRoles();

        BookmarkablePageLink<Void> newPage = new BookmarkablePageLink<Void>("newPage", NewVersionPage.class);
        layout.add(newPage);

        newPage.setVisible(FrameworkUtilities.hasAccess(roles, NewVersionPage.class));

        this.dataProvider = new VersionSortableDataProvider();

        this.filterForm = new FilterForm<Version>("filter-form", this.dataProvider);
        layout.add(filterForm);

        List<IColumn<Version, String>> columns = new ArrayList<IColumn<Version, String>>();
        if (FrameworkUtilities.hasAccess(roles, EditVersionPage.class)) {
            columns.add(createActionsColumn());
            columns.add(new PropertyColumn<Version, String>(org.apache.wicket.model.Model.<String> of("ID"), Version.ID, "id"));
        } else {
            columns.add(createFilterColumn());
        }

        columns.add(createColumn("Name", Version.NAME, "name"));
        columns.add(new PropertyColumn<Version, String>(org.apache.wicket.model.Model.<String> of("Description"), "description"));

        DataTable<Version, String> dataTable = new DefaultDataTable<Version, String>("table", columns, dataProvider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm, dataProvider));

        //
        filterForm.add(dataTable);
    }

    private TextFilteredPropertyColumn<Version, Version, String> createColumn(String key, String sortProperty, String propertyExpression) {
        return new TextFilteredPropertyColumn<Version, Version, String>(org.apache.wicket.model.Model.<String> of(key), sortProperty, propertyExpression);
    }

    private FilteredAbstractColumn<Version, String> createActionsColumn() {
        return new FilteredAbstractColumn<Version, String>(new org.apache.wicket.model.Model<String>("Action / Filter")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, org.apache.wicket.model.Model.<String> of("Filter"), org.apache.wicket.model.Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<Version>> cellItem, String componentId, IModel<Version> rowModel) {
                cellItem.add(new VersionActionPanel(componentId, rowModel));
            }
        };
    }

    private FilteredAbstractColumn<Version, String> createFilterColumn() {
        return new FilteredAbstractColumn<Version, String>(new org.apache.wicket.model.Model<String>("ID / Filter")) {
            private static final long serialVersionUID = 1L;

            // return the go-and-clear filter for the filter toolbar
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new GoAndClearFilter(componentId, form, org.apache.wicket.model.Model.<String> of("Filter"), org.apache.wicket.model.Model.<String> of("Clear"));
            }

            // add the UserActionsPanel to the cell item
            public void populateItem(Item<ICellPopulator<Version>> cellItem, String componentId, IModel<Version> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getId()));
            }
        };
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return MenuUtils.getReferences();
    }

}
