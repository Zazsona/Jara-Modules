package com.zazsona.manualvoicetextchannels;

import com.zazsona.jara.Core;
import com.zazsona.jara.module.ModuleLoad;

public class VoiceTextChannelsLoad extends ModuleLoad
{
    @Override
    public void load()
    {
        VoiceTextChannels.VoiceChannelListener vcl = new VoiceTextChannels.VoiceChannelListener();
        Core.getShardManagerNotNull().addEventListener(vcl);
    }
}
