package com.zazsona.tamago;

import com.zazsona.jara.Core;
import com.zazsona.jara.module.ModuleLoad;

public class TamagoLoad extends ModuleLoad
{
    @Override
    public void load()
    {
        Core.getShardManagerNotNull().addEventListener(new InteractionHandler());
    }
}
