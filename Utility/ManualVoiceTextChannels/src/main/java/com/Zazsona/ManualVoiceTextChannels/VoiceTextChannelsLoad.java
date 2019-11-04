package com.Zazsona.ManualVoiceTextChannels;

import jara.Core;
import module.ModuleLoad;

public class VoiceTextChannelsLoad extends ModuleLoad
{
    @Override
    public void load()
    {
        VoiceTextChannels.VoiceChannelListener vcl = new VoiceTextChannels.VoiceChannelListener();
        Core.getShardManagerNotNull().addEventListener(vcl);
    }
}
