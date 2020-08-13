package com.Zazsona.LastCommand;

import jara.Core;
import listeners.ListenerManager;
import module.ModuleLoad;

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
