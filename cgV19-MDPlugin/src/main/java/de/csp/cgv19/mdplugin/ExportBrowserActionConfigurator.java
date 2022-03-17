package de.csp.cgv19.mdplugin;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;

public class ExportBrowserActionConfigurator implements BrowserContextAMConfigurator {
    public void configure(ActionsManager mngr, Tree browser) {
        DefaultBrowserAction browserAction = new ExportBrowserAction();
        MDActionsCategory category = new MDActionsCategory("cgV19", "Exporter");
        category.addAction(browserAction);
        mngr.addCategory(category);
    }

    public int getPriority() {
        return AMConfigurator.MEDIUM_PRIORITY;
    }
}
