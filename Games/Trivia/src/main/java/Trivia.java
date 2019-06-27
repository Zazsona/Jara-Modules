import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import commands.CmdUtil;
import commands.GameCommand;
import jara.MessageManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.text.StringEscapeUtils;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Trivia extends GameCommand
{
    private int correctOptionId = -1;

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            Gson gson = new Gson();
            String json = CmdUtil.sendHTTPRequest("https://opentdb.com/api.php?amount=1");
            TriviaJson tj = gson.fromJson(json, TriviaJson.class);
            EmbedBuilder embed = buildEmbed(tj);
            msgEvent.getChannel().sendMessage(embed.build()).queue();

            MessageManager mm = new MessageManager();
            boolean isAnswerValid = false;
            String msgContent = "";
            do
            {
                Message message = mm.getNextMessage(msgEvent.getChannel());
                msgContent = message.getContentDisplay().toLowerCase().trim();
                isAnswerValid = validateAnswer(msgContent, tj);
            } while (!isAnswerValid);

            if (msgContent.equals(StringEscapeUtils.unescapeHtml4(tj.results[0].correct_answer).toLowerCase()) || msgContent.equals("option "+correctOptionId))
            {
                msgEvent.getChannel().sendMessage("Correct! Nicely done.").queue();
            }
            else
            {
                msgEvent.getChannel().sendMessage("Sorry, that wasn't it. Better luck next time!").queue();
            }
        }
        catch (JsonSyntaxException e)
        {
            //The provided question is probably trying to use "s inside the question.
            run(msgEvent, parameters);
        }
    }


    private EmbedBuilder buildEmbed(TriviaJson tj)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(tj.results[0].category);
        embed.setDescription(StringEscapeUtils.unescapeHtml4(tj.results[0].question));
        switch (tj.results[0].difficulty)
        {
            case "easy":
                embed.setTitle("Easy");
                embed.setThumbnail("https://i.imgur.com/M0axget.png");
                embed.setColor(Color.decode("#38BC23"));
                break;
            case "medium":
                embed.setTitle("Medium");
                embed.setThumbnail("https://i.imgur.com/IlYb9PC.png");
                embed.setColor(Color.decode("#247AAF"));
                break;
            case "hard":
                embed.setTitle("Hard");
                embed.setThumbnail("https://i.imgur.com/0hdxuiX.png");
                embed.setColor(Color.decode("#FF2626"));
                break;
        }
        ArrayList<String> answers = new ArrayList<>();
        answers.add(StringEscapeUtils.unescapeHtml4(tj.results[0].correct_answer));
        for (String incorrectanswer : tj.results[0].incorrect_answers)
        {
            answers.add(StringEscapeUtils.unescapeHtml4(incorrectanswer));
        }
        Collections.shuffle(answers);
        for (int i = 0; i<answers.size(); i++)
        {
            embed.addField("Option "+(i+1), answers.get(i), true);
            if (answers.get(i).equals(tj.results[0].correct_answer))
            {
                correctOptionId = (i+1);    //This allows the user to also select an answer by inputting "Option X"
            }
        }
        return embed;
    }

    private boolean validateAnswer(String answer, TriviaJson tj)
    {
        if (answer.matches("option [1-4]") && tj.results[0].type.equals("multiple"))
        {
            return true;
        }
        if (answer.matches("option [1-2]") && tj.results[0].type.equals("boolean"))
        {
            return true;
        }
        if (answer.equals(StringEscapeUtils.unescapeHtml4(tj.results[0].correct_answer).toLowerCase()))
        {
            return true;
        }
        for (String incorrectAnswer : tj.results[0].incorrect_answers)
        {
            if (answer.equals(StringEscapeUtils.unescapeHtml4(incorrectAnswer).toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }
}
