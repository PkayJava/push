package com.itrustcambodia.v5.action;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.v5.entity.Manufacture;
import com.itrustcambodia.v5.page.manufacture.EditManufacturePage;
import com.itrustcambodia.v5.page.manufacture.ManufactureManagementPage;

public class ManufactureActionPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public ManufactureActionPanel(String id, IModel<Manufacture> model) {
        super(id);
        addEditLink(model);
        addDeleteLink(model);

    }

    private void addDeleteLink(IModel<Manufacture> model) {

        Link<Manufacture> delete = new Link<Manufacture>("deleteLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Delete page, passing this page and the id of the
             * Contact involved.
             */
            @Override
            public void onClick() {
                JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
                Manufacture model = getModelObject();
                jdbcTemplate.update("delete from " + TableUtilities.getTableName(Manufacture.class) + " where " + Manufacture.ID + " = ?", model.getId());
                setResponsePage(ManufactureManagementPage.class);
            }
        };
        delete.add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));

        add(delete);
    }

    private void addEditLink(IModel<Manufacture> model) {
        add(new Link<Manufacture>("editLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Edit page, passing this page and the id of the Contact
             * involved.
             */
            @Override
            public void onClick() {
                setResponsePage(new EditManufacturePage(getModelObject()));
            }
        });
    }

}