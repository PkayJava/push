package com.itrustcambodia.push.action;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.push.entity.Version;
import com.itrustcambodia.push.page.version.EditVersionPage;
import com.itrustcambodia.push.page.version.VersionManagementPage;

public class VersionActionPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public VersionActionPanel(String id, IModel<Version> model) {
        super(id);
        addEditLink(model);
        addDeleteLink(model);

    }

    private void addDeleteLink(IModel<Version> model) {
        Link<Version> delete = new Link<Version>("deleteLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Delete page, passing this page and the id of the
             * Contact involved.
             */
            @Override
            public void onClick() {
                JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
                Version model = getModelObject();
                jdbcTemplate.update("delete from " + TableUtilities.getTableName(Version.class) + " where " + Version.ID + " = ?", model.getId());
                setResponsePage(VersionManagementPage.class);
            }
        };
        delete.add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));
        add(delete);
    }

    private void addEditLink(IModel<Version> model) {
        add(new Link<Version>("editLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Edit page, passing this page and the id of the Contact
             * involved.
             */
            @Override
            public void onClick() {
                setResponsePage(new EditVersionPage(getModelObject()));
            }
        });
    }

}