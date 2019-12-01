package com.Zazsona.Tamago;

import com.Zazsona.Tamago.petdata.LifeStage;
import com.Zazsona.Tamago.petdata.Pet;
import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleCommand;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class TamagoCommand extends ModuleCommand
{
    private Message petMessage;
    private long lastInteractionTime;

    //TODO: Pet art


    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length == 1)
        {
            Pet pet = PetFactory.getPet(msgEvent.getMember().getId());
            if (pet.getLifeStage() == LifeStage.EGG)
            {
                boolean adopted = introduceNewPet(msgEvent.getChannel(), msgEvent.getMember(), pet);
                if (!adopted) return;
            }
            petMessage = msgEvent.getChannel().sendMessage(PetView.getPetView(pet, msgEvent.getMember())).complete();
            InteractionHandler.addActivePetMessage(petMessage, pet);
        }
        else if (parameters.length > 1)
        {
            if (parameters[1].equalsIgnoreCase("release"))
            {
                if (!InteractionHandler.isPetActive(msgEvent.getMember().getId()))
                {
                    if (getConfirmation("Are you sure you want to release your pet?", msgEvent.getMember(), msgEvent.getChannel()))
                    {
                        PetFactory.releasePet(msgEvent.getMember().getId());
                        msgEvent.getChannel().sendMessage(getDefaultEmbed(msgEvent.getMember()).setDescription("Farewell!").build()).queue();
                    }
                    else
                    {
                        msgEvent.getChannel().sendMessage(getDefaultEmbed(msgEvent.getMember()).setDescription("Release cancelled.").build()).queue();
                    }
                }
                else
                {
                    msgEvent.getChannel().sendMessage(getDefaultEmbed(msgEvent.getMember()).setDescription("Please dismiss your pet first.").build()).queue();
                }

            }
            else if (parameters[1].equalsIgnoreCase("rename"))
            {
                EmbedBuilder embed = getDefaultEmbed(msgEvent.getMember());
                embed.setDescription("Please enter a new name for your pet.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
                Pet pet = PetFactory.getPet(msgEvent.getMember().getId());
                boolean success = setPetName(msgEvent.getChannel(), msgEvent.getMember(), pet);
                if (success)
                    msgEvent.getChannel().sendMessage(getDefaultEmbed(msgEvent.getMember()).setDescription("Your pet is now called "+pet.getPetName()+"!").build()).queue();
            }
        }

    }


    private boolean getConfirmation(String messageToDisplay, Member member, TextChannel channel)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("====== Release Pet ======");
        embed.setColor(CmdUtil.getHighlightColour(member));
        embed.setDescription(messageToDisplay);
        channel.sendMessage(embed.build()).queue();
        MessageManager mm = new MessageManager();
        while (true)
        {
            Message msg = mm.getNextMessage(channel);
            if (msg.getMember().equals(member))
            {
                String messageContent = msg.getContentDisplay();
                if (messageContent.equalsIgnoreCase("yes") || messageContent.equalsIgnoreCase("confirm") || messageContent.equalsIgnoreCase("y"))
                {
                    return true;
                }
                else if (messageContent.equalsIgnoreCase("no") || messageContent.equalsIgnoreCase("quit") || messageContent.equalsIgnoreCase("cancel") || messageContent.equalsIgnoreCase("n"))
                {
                    return false;
                }
                else
                {
                    embed.setDescription("Unrecognised option. Yes/No expected.");
                    channel.sendMessage(embed.build()).queue();
                }
            }
        }
    }

    private boolean introduceNewPet(TextChannel channel, Member member, Pet pet)
    {
        channel.sendMessage(getDefaultEmbed(member).setDescription("Looks like you've got a new pet coming.\nWhat's their name?").setThumbnail("https://i.imgur.com/Hfm5CRB.png").build()).queue();
        if (!setPetName(channel, member, pet)) return false;
        pet.setLifeStage(LifeStage.BABY);
        pet.save();
        return true;
    }

    private boolean setPetName(TextChannel channel, Member member, Pet pet)
    {
        String petName = null;
        MessageManager mm = new MessageManager();
        while (petName == null)
        {
            Message msg = mm.getNextMessage(channel);
            if (msg.getMember().equals(member))
            {
                String messageContent = msg.getContentDisplay();
                if (messageContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
                {
                    channel.sendMessage(getDefaultEmbed(member).setDescription("Pet naming cancelled.").build()).queue();
                    return false;
                }
                else if (messageContent.length() < 40)
                {
                    petName = messageContent;
                }
                else
                {
                    channel.sendMessage(getDefaultEmbed(member).setDescription("Sorry, that name's too long.\nPlease try again, or use quit to cancel.").build()).queue();
                }
            }
        }
        pet.setPetName(petName);
        return true;
    }

    private EmbedBuilder getDefaultEmbed(Member owner)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("====== Pet ======");
        embed.setColor(CmdUtil.getHighlightColour(owner));
        return embed;
    }
}
