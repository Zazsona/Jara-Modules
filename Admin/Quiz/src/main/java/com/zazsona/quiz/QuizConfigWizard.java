package com.zazsona.quiz;

import com.zazsona.jara.MessageManager;
import com.zazsona.jara.configuration.GuildSettings;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.module.ModuleConfig;
import com.zazsona.quiz.config.QuizBuilder;
import com.zazsona.quiz.config.SettingsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;

public class QuizConfigWizard extends ModuleConfig
{
    private MessageManager mm = new MessageManager();
    private QuizBuilder quizBuilder;


    @Override
    public void run(GuildMessageReceivedEvent msgEvent, GuildSettings guildSettings, TextChannel textChannel, boolean isSetup) throws IOException
    {
        quizBuilder = SettingsManager.getInstance().getGuildQuizBuilder(textChannel.getGuild().getId());
        while (true)
        {
            EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
            embed.setDescription("Configure quizzes\n\n**Schedule** - Add a regular quiz\n**Roles** - Set who can join quizzes\n**Ping** - Ping everyone on quiz start\n**Categories** - Set question categories\n**Quit** - Close config");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            Message input = getInput(msgEvent, textChannel);
            if (input == null)
            {
                if (!isSetup)
                {
                    embed.setDescription("Quiz config closed.");
                    textChannel.sendMessage(embed.build()).queue();
                }
                return;
            }
            else if (input.getContentDisplay().equalsIgnoreCase("schedule"))
            {
                QuizConfigScheduleWizard qcsw = new QuizConfigScheduleWizard();
                qcsw.run(msgEvent, textChannel, guildSettings, quizBuilder, embed);
            }
            else if (input.getContentDisplay().equalsIgnoreCase("roles"))
            {
                modifyRoles(msgEvent, textChannel, embed);
            }
            else if (input.getContentDisplay().equalsIgnoreCase("ping"))
            {
                togglePing(msgEvent, textChannel, embed);
            }
            else if (input.getContentDisplay().equalsIgnoreCase("categories"))
            {
                modifyCategories(msgEvent, textChannel, embed);
            }
            else
            {
                embed.setDescription("Unknown submenu. Please try again.");
                textChannel.sendMessage(embed.build()).queue();
            }
        }
    }

    @Override
    public void parseAsParameters(GuildMessageReceivedEvent msgEvent, Collection<String> collection, GuildSettings guildSettings, TextChannel textChannel) throws IOException
    {

    }


    private Message getInput(GuildMessageReceivedEvent msgEvent, TextChannel textChannel) throws InvalidParameterException
    {
        Message message = null;
        while (true)
        {
            message = mm.getNextMessage(textChannel);
            if (message.getMember().equals(msgEvent.getMember()))
            {
                if (message.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(message.getGuild().getId())+"quit") || message.getContentDisplay().equalsIgnoreCase("quit"))
                {
                    return null;
                }
                else if (!message.getContentDisplay().startsWith(String.valueOf(SettingsUtil.getGuildCommandPrefix(message.getGuild().getId()))))
                {
                    return message;
                }
            }
        }
    }

    private void togglePing(GuildMessageReceivedEvent msgEvent, TextChannel textChannel, EmbedBuilder embed)
    {
        embed.setDescription("Would you like an everyone ping to occur when a scheduled quiz is about to begin? (Y/N)\nCurrent status: "+((quizBuilder.isPingOnCountdown()) ? "Enabled" : "Disabled"));
        textChannel.sendMessage(embed.build()).queue();
        Message input = getInput(msgEvent, textChannel);
        if (input != null)
        {
            String response = input.getContentDisplay().toLowerCase();
            response = response.toLowerCase();
            if (response.equals("y") || response.equals("enable") || response.equals("yes"))
            {
                quizBuilder.setPingOnCountdown(true);
                SettingsManager.getInstance().save();
                embed.setDescription("Ping enabled.");
                textChannel.sendMessage(embed.build()).queue();
            }
            else if (response.equals("n") || response.equals("disable") || response.equals("no"))
            {
                quizBuilder.setPingOnCountdown(false);
                SettingsManager.getInstance().save();
                embed.setDescription("Ping disabled.");
                textChannel.sendMessage(embed.build()).queue();
            }
            else
            {
                throw new InvalidParameterException();
            }
        }
        else
        {
            return;
        }
    }

    private String getCurrentPermittedRolesList(Guild guild)
    {
           StringBuilder sb = new StringBuilder();
           if (quizBuilder.getRolesPermittedToJoin().size() > 0)
           {
               for (String roleID : quizBuilder.getRolesPermittedToJoin())
               {
                   sb.append(guild.getRoleById(roleID).getName()+", ");
               }
               sb.setLength(sb.length()-2);
           }
           else
           {
               sb.append("everyone");
           }
           return sb.toString();
    }

    private void modifyRoles(GuildMessageReceivedEvent msgEvent, TextChannel textChannel, EmbedBuilder embed) throws IOException
    {
        embed.setDescription("Please enter whether you would like to add or remove roles, and then mention the roles to modify.\nE.g: `add @admins @moderators` or `remove @members`\n\nCurrently permitted: "+getCurrentPermittedRolesList(msgEvent.getGuild()));
        textChannel.sendMessage(embed.build()).queue();
        Message input = getInput(msgEvent, textChannel);
        if (input != null)
        {
            String inputContent = input.getContentDisplay();
            boolean addRoles = inputContent.toLowerCase().startsWith("add");
            for (Role role : input.getMentionedRoles())
            {
                if (addRoles)
                    quizBuilder.addAllowedRole(role.getId());
                else
                    quizBuilder.removeAllowedRole(role.getId());
            }
            SettingsManager.getInstance().save();
            embed.setDescription("Roles "+((addRoles) ? "added." : "removed."));
            textChannel.sendMessage(embed.build()).queue();
        }
    }

    private void modifyCategories(GuildMessageReceivedEvent msgEvent, TextChannel textChannel, EmbedBuilder embed)
    {
        ArrayList<Integer> categoryIDBlacklist = quizBuilder.getCategoriesBlacklist();
        ArrayList<Integer> categoryIDWhitelist = new ArrayList<>();
        for (int categoryID : quizBuilder.CATEGORY_IDs)
            categoryIDWhitelist.add(categoryID);
        categoryIDWhitelist.removeAll(categoryIDBlacklist);

        StringBuilder blacklistedCategoriesListBuilder = new StringBuilder();
        if (categoryIDBlacklist.size() > 0)
        {
            for (int categoryID : categoryIDBlacklist)
                blacklistedCategoriesListBuilder.append(QuizBuilder.getCategoryName(categoryID)).append(", ");
            blacklistedCategoriesListBuilder.setLength(blacklistedCategoriesListBuilder.length()-2);
        }
        else
            blacklistedCategoriesListBuilder.append("None.");

        StringBuilder whitelistedCategoriesListBuilder = new StringBuilder();
        if (categoryIDWhitelist.size() > 0)
        {
            for (int categoryID : categoryIDWhitelist)
                whitelistedCategoriesListBuilder.append(QuizBuilder.getCategoryName(categoryID)).append(", ");
                whitelistedCategoriesListBuilder.setLength(whitelistedCategoriesListBuilder.length()-2);
        }
        else
            whitelistedCategoriesListBuilder.append("None - Blacklist will not apply.");

        embed.setDescription("Please enter `Enable [Category]` or `Disable [Category]`\nEnabled Categories: "+whitelistedCategoriesListBuilder.toString()+"\n\nDisabled Categories: "+blacklistedCategoriesListBuilder.toString());
        textChannel.sendMessage(embed.build()).queue();
        while (true)
        {
            Message input = getInput(msgEvent, textChannel);
            if (input == null)
                break;
            else
            {
                String inputContent = input.getContentDisplay().toLowerCase();
                if (inputContent.startsWith("enable"))
                {
                    String[] inputTokens = inputContent.split(" ");
                    StringBuilder categoryNameBuilder = new StringBuilder();
                    for (int i = 1; i<inputTokens.length; i++)
                        categoryNameBuilder.append(inputTokens[i]).append(" ");
                    int categoryID = QuizBuilder.getCategoryID(categoryNameBuilder.toString().trim());
                    if (categoryID > -1)
                    {
                        quizBuilder.removeCategoriesFromBlacklist(categoryID);
                        embed.setDescription("Enabled "+categoryNameBuilder.toString().trim());
                        textChannel.sendMessage(embed.build()).queue();
                        SettingsManager.getInstance().save();
                    }
                }
                else if (inputContent.startsWith("disable"))
                {
                    String[] inputTokens = inputContent.split(" ");
                    StringBuilder categoryNameBuilder = new StringBuilder();
                    for (int i = 1; i<inputTokens.length; i++)
                        categoryNameBuilder.append(inputTokens[i]).append(" ");
                    int categoryID = QuizBuilder.getCategoryID(categoryNameBuilder.toString().trim());
                    if (categoryID > -1)
                    {
                        quizBuilder.addCategoriesToBlacklist(categoryID);
                        embed.setDescription("Disabled "+categoryNameBuilder.toString().trim());
                        textChannel.sendMessage(embed.build()).queue();
                        SettingsManager.getInstance().save();
                    }
                }
                else
                {
                    embed.setDescription("Unrecognised input.\nUse `"+SettingsUtil.getGuildCommandPrefix(input.getGuild().getId())+"quit` to quit the category menu.");
                    textChannel.sendMessage(embed.build()).queue();
                }
            }
        }

    }
}
