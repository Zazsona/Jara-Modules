package com.Zazsona.Tamago;

import jara.Core;
import module.ModuleLoad;

public class TamagoLoad extends ModuleLoad
{
    @Override
    public void load()
    {
        Core.getShardManagerNotNull().addEventListener(new InteractionHandler());
    }
}
