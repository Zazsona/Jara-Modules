import configuration.SettingsUtil;
import net.dv8tion.jda.core.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FileManager
{
    private ArrayList<Quote> quoteList;
    private static transient Logger logger = LoggerFactory.getLogger("QuoteLoader");

    public FileManager(String guildID)
    {
        restore(getQuotesPath(guildID));
    }

    private String getQuotesPath(String guildID)
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/Quotes/"+guildID+".jara";
    }

    public ArrayList<Quote> getQuotes()
    {
        return quoteList;
    }

    public Quote addQuote(Message message, String quoteName)
    {
        if (!isQuoteNameTaken(quoteName))
        {
            Quote quote = null;
            if (message.getAttachments().size() > 0)
            {
                quote = new Quote(quoteName, message.getMember().getEffectiveName(), message.getContentRaw(), message.getAttachments().get(0).getUrl(), message.getCreationTime().atZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE));

            }
            else
            {
                quote = new Quote(quoteName, message.getMember().getEffectiveName(), message.getContentRaw(), message.getCreationTime().atZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
            quoteList.add(quote);
            save(getQuotesPath(message.getGuild().getId()));
            return quote;
        }
        return null;
    }

    public Quote addQuote(String guildID, Quote quote)
    {
        if (!isQuoteNameTaken(quote.name))
        {
            quoteList.add(quote);
            save(getQuotesPath(guildID));
            return quote;
        }
        return null;
    }

    public boolean isQuoteNameTaken(String name)
    {
        for (Quote quote : quoteList)
        {
            if (quote.name.equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return false;
    }

    public Quote getQuoteByName(String quoteName)
    {
        if (quoteList.size() > 0)
        {
            for (Quote quote : quoteList)
            {
                if (quote.name.equalsIgnoreCase(quoteName))
                {
                    return quote;
                }
            }
        }
        return null;
    }

    public boolean deleteQuote(String guildID, String quoteName)
    {
        if (quoteList.size() > 0)
        {
            for (Quote quote : quoteList)
            {
                if (quote.name.equalsIgnoreCase(quoteName))
                {
                    quoteList.remove(quote);
                    save(getQuotesPath(guildID));
                    return true;
                }
            }
        }
        return false;
    }

    public Quote renameQuote(String guildID, String originalQuoteName, String newQuoteName)
    {
        Quote quote = getQuoteByName(originalQuoteName);
        if (quote != null)
        {
            quote.name = newQuoteName;
            //deleteQuote(guildID, originalQuoteName);
            //addQuote(guildID, quote);
            save(getQuotesPath(guildID));
            return quote;
        }
        return null;
    }

    public Quote renameQuote(String guildID, Quote quote, String newQuoteName)
    {
        if (quote != null)
        {
            quote.name = newQuoteName;
            //deleteQuote(guildID, originalQuoteName);
            //addQuote(guildID, quote);
            save(getQuotesPath(guildID));
            return quote;
        }
        return null;
    }


    private synchronized void save(String path)
    {
        try
        {
            File quoteFile = new File(path);
            if (!quoteFile.exists())
            {
                quoteFile.getParentFile().mkdirs();
                quoteFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(quoteList);
            oos.close();
            fos.close();
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }

    private synchronized void restore(String path)
    {
        try
        {
            if (new File(path).exists())
            {
                FileInputStream fis = new FileInputStream(path);
                ObjectInputStream ois = new ObjectInputStream(fis);
                quoteList = (ArrayList<Quote>) ois.readObject();
                ois.close();
                fis.close();
            }
            else
            {
                quoteList = new ArrayList<>();
            }

        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
            return;
        }
        catch (ClassNotFoundException e)
        {
            logger.error(e.getMessage());
            return;
        }
    }
}
