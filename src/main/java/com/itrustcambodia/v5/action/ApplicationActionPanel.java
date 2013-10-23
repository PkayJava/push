package com.itrustcambodia.v5.action;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.v5.entity.Application;
import com.itrustcambodia.v5.entity.Device;
import com.itrustcambodia.v5.page.ApplicationManagementPage;
import com.itrustcambodia.v5.page.EditApplicationPage;

public class ApplicationActionPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public ApplicationActionPanel(String id, IModel<Application> model) {
        super(id);
        addEditLink(model);
        addDeleteLink(model);

    }

    private void addDeleteLink(IModel<Application> model) {

        Link<Application> delete = new Link<Application>("deleteLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Delete page, passing this page and the id of the
             * Contact involved.
             */
            @Override
            public void onClick() {
                JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
                Application model = getModelObject();
                jdbcTemplate.update("delete from " + TableUtilities.getTableName(Application.class) + " where " + Application.ID + " = ?", model.getId());
                jdbcTemplate.update("delete from " + TableUtilities.getTableName(Device.class) + " where " + Device.APPLICATION_ID + " = ?", model.getId());
                setResponsePage(ApplicationManagementPage.class);
            }
        };
        delete.add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));
        add(delete);
    }

    private void addEditLink(IModel<Application> model) {
        add(new Link<Application>("editLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Edit page, passing this page and the id of the Contact
             * involved.
             */
            @Override
            public void onClick() {
                setResponsePage(new EditApplicationPage(getModelObject()));
            }
        });
    }

}