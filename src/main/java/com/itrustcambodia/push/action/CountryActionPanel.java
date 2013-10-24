package com.itrustcambodia.push.action;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.push.entity.Country;
import com.itrustcambodia.push.page.country.CountryManagementPage;
import com.itrustcambodia.push.page.country.EditCountryPage;

public class CountryActionPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public CountryActionPanel(String id, IModel<Country> model) {
        super(id);
        addEditLink(model);
        addDeleteLink(model);

    }

    private void addDeleteLink(IModel<Country> model) {
        Link<Country> delete = new Link<Country>("deleteLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Delete page, passing this page and the id of the
             * Contact involved.
             */
            @Override
            public void onClick() {
                JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
                Country model = getModelObject();
                jdbcTemplate.update("delete from " + TableUtilities.getTableName(Country.class) + " where " + Country.ID + " = ?", model.getId());
                setResponsePage(CountryManagementPage.class);
            }
        };
        delete.add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));
        add(delete);
    }

    private void addEditLink(IModel<Country> model) {
        add(new Link<Country>("editLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Edit page, passing this page and the id of the Contact
             * involved.
             */
            @Override
            public void onClick() {
                setResponsePage(new EditCountryPage(getModelObject()));
            }
        });
    }

}