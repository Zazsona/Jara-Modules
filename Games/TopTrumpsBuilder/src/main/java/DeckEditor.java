import cards.Card;
import cards.Deck;
import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.MessageManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.xml.soap.Text;

public class DeckEditor
{
    private Deck deck;
    private Member user;
    private TextChannel channel;
    private MessageManager mm;

    public DeckEditor(Deck deck, Member user, TextChannel channel)
    {
        this.deck = deck;
        this.user = user;
        this.channel = channel;
        mm = new MessageManager();
    }

    public Deck run()
    {
        return runMainMenu();
    }

    private Message getInput()
    {
        Message msg = null;
        while (msg == null || !msg.getMember().equals(user))
        {
            msg = mm.getNextMessage(channel);
            if (msg.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"));
            {
                return null;
            }
        }
        return msg;
    }

    private void sendEmbed(String descriptionText)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        embed.setDescription(descriptionText);
        channel.sendMessage(embed.build()).queue();
    }

    private Deck runMainMenu()
    {
        try
        {
            sendEmbed("- Name\n- Stats\n- Cards\n- Quit");
            String input = getInput().getContentDisplay();
            if (input.equalsIgnoreCase("name"))
            {
                editName();
            }
            else if (input.equalsIgnoreCase("stats") || input.equalsIgnoreCase("categories") || input.equalsIgnoreCase("category") || input.equalsIgnoreCase("categorys"))
            {
                editCategories();
            }
            else if (input.equalsIgnoreCase("card") || input.equalsIgnoreCase("cards"))
            {
                runCardsMenu();
            }
            else if (input.equalsIgnoreCase("quit"))
            {
                return deck;
            }
        }
        catch (NullPointerException e)
        {
            sendEmbed("Menu closed.");
        }
        return deck;
    }

    private void runCardsMenu()
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        embed.setDescription("- Add\n- Edit [Name]\n- Delete [Name]");
        StringBuilder valueBuilder = new StringBuilder();
        int cardNo = 0;
        for (Card card : deck.getCards())
        {
            if (cardNo == deck.getCards().size()/2)
            {
                embed.addField("Cards", valueBuilder.toString(), true);
                valueBuilder.setLength(0);
            }
            cardNo++;
            valueBuilder.append(card.getName()).append("\n");
        }
        embed.addField("", valueBuilder.toString(), true);
        channel.sendMessage(embed.build()).queue();

        String[] input = getInput().getContentDisplay().split(" ");
        if (!input[0].equalsIgnoreCase("add") && input.length < 2)
        {
            sendEmbed("Please input a valid operation and card name.");
        }
        else
        {
            if (input[0].equalsIgnoreCase("add"))
            {
                addCard();
            }
            else if (input[0].equalsIgnoreCase("edit"))
            {
                editCard(input[1]);
            }
            else if (input[0].equalsIgnoreCase("delete"))
            {
                deleteCard(input[1]);
            }
        }
    }

    private void editName()
    {
        try
        {
            boolean success = false;
            sendEmbed("Current Name: **"+deck.getName()+"**\n\nPlease enter a new name.");
            while (!success)
            {
                String newName = getInput().getContentDisplay();
                success = deck.setName(newName);
                sendEmbed((success) ? "Successfully renamed to "+deck.getName()+"." : "ERROR: Names must be between 2-25 characters.\nPlease try again.");
            }
        }
        catch (NullPointerException e)
        {
            sendEmbed("Operation cancelled.");
        }
        finally
        {
            runMainMenu();
        }
    }

    private void editCategories()
    {
        try
        {
            boolean success = false;
            StringBuilder categoriesBuilder = new StringBuilder();
            for (String category : deck.getStatNames())
            {
                categoriesBuilder.append(category).append("\n");
            }
            sendEmbed("Current Categories: "+categoriesBuilder.toString()+"\n\nPlease enter the new categories, separated by commas (,).");
            int oldCategoryCount = deck.getStatNames().length;
            while (!success)
            {
                String[] newCategories = getInput().getContentDisplay().split(",");
                if (newCategories.length <= 1 || newCategories.length > 6)
                {
                    sendEmbed("There must be between 2 and 6 categories. Please enter new categories, separated by commas (,).");
                    continue;
                }
                if (newCategories.length != oldCategoryCount)
                {
                    sendEmbed((newCategories.length > oldCategoryCount) ? "This will add a new category, all cards will have a value of 0 in this category. Continue? (Y/N)" : "This will remove at least one category, and all card data for these categories will be deleted. Continue? (Y/N)");
                    while (true)
                    {
                        String answer = getInput().getContentDisplay().toLowerCase();
                        if (answer.equals("y") || answer.equals("yes") || answer.equals("yeah") || answer.equals("yup") || answer.equals("continue"))
                        {
                            break;
                        }
                        else if (answer.equals("n") || answer.equals("no") || answer.equals("nope"))
                        {
                            throw new NullPointerException("User cancelled operation");
                        }
                    }
                }
                success = deck.setStatNames(newCategories);
                sendEmbed((success) ? "Successfully modified stats." : "ERROR: Names must be between 2-25 characters.\nPlease try again.");
            }
        }
        catch (NullPointerException e)
        {
            sendEmbed("Operation cancelled.");
        }
        finally
        {
            runMainMenu();
        }
    }

    private void addCard()
    {
        try
        {
            if (deck.getCards().size() >= 30)
            {
                sendEmbed("This deck has reached the card limit.");
                return;
            }
            else
            {

                Card card = editCardWizard(new Card());
                deck.addCard(card);
            }
        }
        catch (NullPointerException e)
        {
            sendEmbed("Operation cancelled.");
        }
        finally
        {
            runCardsMenu();
        }
    }

    private void editCard(String cardName)
    {
        try
        {
            Card card = deck.getCard(cardName);
            if (card == null)
            {
                sendEmbed("That card does not exist.");
                return;
            }
            else
            {
                Card newCard = editCardWizard(card);
                deck.removeCard(card);
                deck.addCard(newCard);
            }
        }
        catch (NullPointerException e)
        {
            sendEmbed("Operation cancelled.");
        }
        finally
        {
            runCardsMenu();
        }
    }

    private void deleteCard(String cardName)
    {
        try
        {
            Card card = deck.getCard(cardName);
            if (card == null)
            {
                sendEmbed("That card does not exist.");
                return;
            }
            else
            {
                deck.removeCard(card);
            }
        }
        catch (NullPointerException e)
        {
            sendEmbed("Operation cancelled.");
        }
        finally
        {
            runCardsMenu();
        }
    }

    private Card editCardWizard(Card card) throws NullPointerException
    {
        boolean success = false;
        sendEmbed("Please input the card name.\nCurrent: "+card.getName());
        while (!success)
        {
            String name = getInput().getContentDisplay();
            if (deck.getCard(name) != null)
            {
                sendEmbed("That name is already taken. Please try again.");
            }
            success = card.setName(name);
            if (!success)
            {
                sendEmbed("Names must be between 1 and 25 characters. Please try again.");
            }
        }
        sendEmbed("Please input the image URL.\nCurrent: "+card.getImageURL());
        while (!success)
        {
            String url = getInput().getContentDisplay();
            success = card.setImageURL(url);
            if (!success)
            {
                sendEmbed("Invalid image URL. Please try again.");
            }
        }
        double[] stats = new double[deck.getStatNames().length];
        for (int i = 0; i<stats.length; i++)
        {
            success = false;
            sendEmbed("Please input a numeric value for the stat \""+deck.getStatNames()[i]+"\".\nCurrent: "+card.getStats()[i]);
            while (!success)
            {
                try
                {
                    stats[i] = Double.parseDouble(getInput().getContentDisplay());
                    success = true;
                }
                catch (NumberFormatException e)
                {
                    success = false;
                    sendEmbed("Invalid value. Please enter a numeric value for \""+deck.getStatNames()[i]+"\".");
                }
            }
        }
        card.setStats(stats);
        return card;
    }


}
