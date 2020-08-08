package com.Zazsona.wwtbam.lifelines;

import com.Zazsona.wwtbam.TriviaDatabase;
import com.Zazsona.wwtbam.Trivia;
import net.dv8tion.jda.api.entities.TextChannel;

public class PhoneAFriend
{
    public static void usePhoneAFriend(TextChannel channel, TriviaDatabase tdb, Trivia trivia)
    {
        channel.sendMessage("Ha! I'm your only friend, because this hasn't been properly programmed yet.").queue();
        AskTheHost.useAskTheHost(channel, tdb, trivia);
    }
}
