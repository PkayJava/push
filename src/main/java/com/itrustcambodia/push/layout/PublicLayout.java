package com.itrustcambodia.push.layout;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.layout.Bootstrap3Layout;
import com.itrustcambodia.push.page.ContactPage;
import com.itrustcambodia.push.page.FeaturePage;
import com.itrustcambodia.push.page.HomePage;

public class PublicLayout extends Bootstrap3Layout {

    /**
     * 
     */
    private static final long serialVersionUID = -855838941457528614L;

    public static final CssResourceReference PUBLIC_LAYOUT_CSS = new CssResourceReference(PublicLayout.class, "PublicLayout.css");

    public PublicLayout(String id) {
        super(id);
    }

    public PublicLayout(String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        BookmarkablePageLink<Void> homePage = new BookmarkablePageLink<Void>("homePage", HomePage.class);
        addToBorder(homePage);

        AbstractWebApplication application = (AbstractWebApplication) getApplication();

        BookmarkablePageLink<Void> loginPage = new BookmarkablePageLink<Void>("loginPage", application.getDashboardPage());
        addToBorder(loginPage);

        BookmarkablePageLink<Void> contactPage = new BookmarkablePageLink<Void>("contactPage", ContactPage.class);
        addToBorder(contactPage);

        BookmarkablePageLink<Void> featurePage = new BookmarkablePageLink<Void>("featurePage", FeaturePage.class);
        addToBorder(featurePage);

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(PUBLIC_LAYOUT_CSS));
    }

}
