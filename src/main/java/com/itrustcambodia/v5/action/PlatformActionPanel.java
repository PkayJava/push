package com.itrustcambodia.v5.action;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.v5.entity.Platform;
import com.itrustcambodia.v5.page.platform.EditPlatformPage;
import com.itrustcambodia.v5.page.platform.PlatformManagementPage;

public class PlatformActionPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public PlatformActionPanel(String id, IModel<Platform> model) {
        super(id);
        addEditLink(model);
        addDeleteLink(model);

    }

    private void addDeleteLink(IModel<Platform> model) {
        Link<Platform> delete = new Link<Platform>("deleteLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Delete page, passing this page and the id of the
             * Contact involved.
             */
            @Override
            public void onClick() {
                JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
                Platform model = getModelObject();
                jdbcTemplate.update("delete from " + TableUtilities.getTableName(Platform.class) + " where " + Platform.ID + " = ?", model.getId());
                setResponsePage(PlatformManagementPage.class);
            }
        };
        delete.add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));
        add(delete);
    }

    private void addEditLink(IModel<Platform> model) {
        add(new Link<Platform>("editLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Edit page, passing this page and the id of the Contact
             * involved.
             */
            @Override
            public void onClick() {
                setResponsePage(new EditPlatformPage(getModelObject()));
            }
        });
    }

}