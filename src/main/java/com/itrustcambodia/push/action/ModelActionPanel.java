package com.itrustcambodia.push.action;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.push.entity.Model;
import com.itrustcambodia.push.page.model.EditModelPage;
import com.itrustcambodia.push.page.model.ModelManagementPage;

public class ModelActionPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public ModelActionPanel(String id, IModel<Model> model) {
        super(id);
        addEditLink(model);
        addDeleteLink(model);

    }

    private void addDeleteLink(IModel<Model> model) {
        Link<Model> delete = new Link<Model>("deleteLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Delete page, passing this page and the id of the
             * Contact involved.
             */
            @Override
            public void onClick() {
                JdbcTemplate jdbcTemplate = ((AbstractWebApplication) getApplication()).getJdbcTemplate();
                Model model = getModelObject();
                jdbcTemplate.update("delete from " + TableUtilities.getTableName(Model.class) + " where " + Model.ID + " = ?", model.getId());
                setResponsePage(ModelManagementPage.class);
            }
        };

        delete.add(AttributeModifier.replace("onclick", "return confirm('Are you sure ?');"));
        add(delete);
    }

    private void addEditLink(IModel<Model> model) {
        add(new Link<Model>("editLink", model) {
            private static final long serialVersionUID = 1L;

            /**
             * Go to the Edit page, passing this page and the id of the Contact
             * involved.
             */
            @Override
            public void onClick() {
                setResponsePage(new EditModelPage(getModelObject()));
            }
        });
    }

}