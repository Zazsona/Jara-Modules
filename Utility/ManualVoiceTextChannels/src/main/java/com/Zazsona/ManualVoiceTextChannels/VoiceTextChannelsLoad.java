package com.Zazsona.ManualVoiceTextChannels;

import commands.CmdUtil;
import module.Load;

public class VoiceTextChannelsLoad extends Load
{
    @Override
    public void load()
    {
        VoiceTextChannels.VoiceChannelListener vcl = new VoiceTextChannels.VoiceChannelListener();
        CmdUtil.getJDA().addEventListener(vcl);
    }
}
