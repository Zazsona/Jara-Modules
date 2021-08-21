package com.zazsona.lastcommand;

import com.zazsona.jara.Core;
import com.zazsona.jara.listeners.ListenerManager;
import com.zazsona.jara.module.ModuleLoad;

public class Initialiser extends ModuleLoad
{
    @Override
    public void load()
    {
        Core.getShardManagerNotNull();
        LastCommandListener listener = LastCommandListener.getInstance();
        listener.setLastCommandAttributes(getModuleAttributes());
        ListenerManager.registerListener(listener);
    }
}
