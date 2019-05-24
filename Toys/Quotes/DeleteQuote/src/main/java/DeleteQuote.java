import Json.QuoteJson;
import Json.QuoteListJson;
import com.google.gson.Gson;
import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class DeleteQuote extends Command
{
    private QuoteListJson quoteListJson;

    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            if (parameters.length > 2)
            {
                for (int i = 2; i<parameters.length; i++)
                {
                    parameters[1] += " "+parameters[i];
                }
            }

            if (parameters.length > 1)
            {
                deleteQuote(msgEvent.getGuild().getId(), parameters[1]);
                saveDeletion(msgEvent);
                msgEvent.getChannel().sendMessage("Quote \""+parameters[1]+"\" deleted.").queue();
            }
            else
            {
                CmdUtil.sendHelpInfo(msgEvent, getClass());
            }
        }
        catch (IOException e)
        {
            msgEvent.getChannel().sendMessage("Error: Failed to delete quote. Does it exist?").queue();
        }

    }

    private boolean deleteQuote(String guildID, String quoteName) throws IOException
    {
        String JSON = new String(Files.readAllBytes(getQuoteFile(guildID).toPath()));
        Gson gson = new Gson();
        QuoteListJson quoteListJson = gson.fromJson(JSON, QuoteListJson.class);
        if (quoteListJson != null && quoteListJson.QuoteList.size() > 0)
        {
            for (QuoteJson quote : quoteListJson.QuoteList)
            {
                if (quote.name.equalsIgnoreCase(quoteName))
                {
                    quoteListJson.QuoteList.remove(quote);
                    return true;
                }
            }
        }
        throw new IOException("The quote does not exist.");
    }

    private void saveDeletion(GuildMessageReceivedEvent msgEvent) throws IOException
    {
        Gson gson = new Gson();
        File quoteFile = getQuoteFile(msgEvent.getGuild().getId());

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(quoteFile, false));
        printWriter.print(gson.toJson(quoteListJson));
        printWriter.close();
    }


    private File getQuoteFile(String guildID) throws IOException
    {
        File quoteFile;
        String operatingSystem = System.getProperty("os.name").toLowerCase();
        if (operatingSystem.startsWith("windows"))
        {
            quoteFile = new File(System.getProperty("user.home")+"\\AppData\\Roaming\\Jara\\Quotes\\"+guildID+".json");
        }
        else
        {
            quoteFile = new File(System.getProperty("user.home")+"/.Jara/Quotes/"+guildID+".json");
        }
        if (!quoteFile.exists())
        {
            quoteFile.getParentFile().mkdirs();
            quoteFile.createNewFile();
        }
        return quoteFile;
    }
}
