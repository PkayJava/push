package com.itrustcambodia.v5.action;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.v5.entity.City;
import com.itrustcambodia.v5.page.city.CityManagementPage;
import com.itrustcambodia.v5.page.city.EditCityPage;

public class CityActionPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public CityActionPanel(String id, IModel<City> model) {
        super(id);
        addEditLink(model);
        addDeleteLink(model);

    }

    private void addDeleteLink(IModel<City> model) {
        Link<City> delete = new Link<City>("deleteLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Delete page, passing this page and the id of the
             * Contact involved.
             */
            @Override
            public void onClick() {
                JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
                City model = getModelObject();
                jdbcTemplate.update("delete from " + TableUtilities.getTableName(City.class) + " where " + City.ID + " = ?", model.getId());
                setResponsePage(CityManagementPage.class);
            }
        };
        delete.add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));
        add(delete);
    }

    private void addEditLink(IModel<City> model) {
        add(new Link<City>("editLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Edit page, passing this page and the id of the Contact
             * involved.
             */
            @Override
            public void onClick() {
                setResponsePage(new EditCityPage(getModelObject()));
            }
        });
    }

}