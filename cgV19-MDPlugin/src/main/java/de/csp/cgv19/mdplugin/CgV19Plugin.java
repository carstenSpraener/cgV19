package de.csp.cgv19.mdplugin;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.plugins.Plugin;

public class CgV19Plugin  extends Plugin     {

    public void init() {
        ActionsConfiguratorsManager.getInstance().addContainmentBrowserContextConfigurator(new ExportBrowserActionConfigurator());
        new CgV19RESTServer().init();
    }

    public boolean close() {
        return true;
    }

    public boolean isSupported() {
        return true;
    }
}

