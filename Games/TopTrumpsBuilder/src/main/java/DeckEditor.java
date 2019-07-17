import cards.Card;
import cards.Deck;
import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.MessageManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import java.security.InvalidParameterException;
import java.util.concurrent.CancellationException;

public class DeckEditor
{
    private final Deck deck;
    private final Member user;
    private final TextChannel channel;
    private final MessageManager mm;

    public DeckEditor(Deck deck, Member user, TextChannel channel)
    {
        this.deck = deck;
        this.user = user;
        this.channel = channel;
        mm = new MessageManager();
    }

    /**
     * Runs the deck editor
     * @return the edited deck
     */
    public Deck run()
    {
        return runMainMenu();
    }

    /**
     * Gets the next valid response message
     * @return the response
     * @throws CancellationException user has quit
     */
    private Message getInput() throws CancellationException
    {
        Message msg = null;
        while (msg == null || !msg.getMember().equals(user))
        {
            msg = mm.getNextMessage(channel);
        }
        if (msg.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
        {
            throw new CancellationException("User has quit the operation.");
        }
        return msg;
    }

    /**
     * Sends an embed with a standardised style using the provided description text
     * @param descriptionText the text to display on the embed
     */
    private void sendEmbed(String descriptionText)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        embed.setDescription(descriptionText);
        channel.sendMessage(embed.build()).queue();
    }

    /**
     * Shows the main menu, and launches the sub-option based on user input.
     * @return The edited deck
     */
    private Deck runMainMenu()
    {
        try
        {
            sendEmbed("- Name\n- Stats\n- Cards\n- Quit");
            String input = getInput().getContentDisplay();
            if (input.equalsIgnoreCase("name"))
            {
                editName(true);
            }
            else if (input.equalsIgnoreCase("stats") || input.equalsIgnoreCase("categories") || input.equalsIgnoreCase("category") || input.equalsIgnoreCase("categorys"))
            {
                editCategories(true);
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
        catch (CancellationException e)
        {
            sendEmbed("Menu closed.");
        }
        return deck;
    }

    /**
     * Shows the cards menu, and launches the sub-option based on user input.
     */
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

    /**
     * Prompts the user to modify the name of the deck
     * @param showInstructions whether to show accepted inputs and the current value(s).
     */
    private void editName(boolean showInstructions)
    {
        try
        {
            if (showInstructions)
                sendEmbed("Current Name: **"+deck.getName()+"**\n\nPlease enter a new name.");

            String newName = getInput().getContentDisplay();
            deck.setName(newName);
            sendEmbed("Successfully renamed to "+deck.getName()+".");
        }
        catch (CancellationException e)
        {
            sendEmbed(e.getMessage());
        }
        catch (InvalidParameterException e)
        {
            sendEmbed(e.getMessage()+"\nPlease try again.");
            editName(false);
            return;
        }
        finally
        {
            runMainMenu();
        }
    }

    /**
     * Prompts the user to modify the categories of the deck
     * @param showInstructions whether to show accepted inputs and the current value(s).
     */
    private void editCategories(boolean showInstructions)
    {
        try
        {
            if (showInstructions)
            {
                StringBuilder categoriesBuilder = new StringBuilder();
                for (String category : deck.getStatNames())
                {
                    categoriesBuilder.append(category).append("\n");
                }
                sendEmbed("Current Categories: "+categoriesBuilder.toString()+"\n\nPlease enter the new categories, separated by commas (,).");
            }
            int oldCategoryCount = deck.getStatNames().length;

            String[] newCategories = getInput().getContentDisplay().split(",");
            if (newCategories.length != oldCategoryCount)
            {
                sendEmbed((newCategories.length > oldCategoryCount) ? "This will add a new category, all cards will have a value of 0 in this category. Continue? (Y/N)" : "This will remove at least one category, and all card data for these categories will be deleted. Continue? (Y/N)");
                while (true)
                {
                    String answer = getInput().getContentDisplay().toLowerCase();
                    if (answer.equals("y") || answer.equals("yes") || answer.equals("yeah") || answer.equals("yup") || answer.equals("continue"))
                    {
                        updateStatsToNewCategories(newCategories.length);
                        break;
                    }
                    else if (answer.equals("n") || answer.equals("no") || answer.equals("nope"))
                    {
                        throw new CancellationException("Cancelled setting categories.");
                    }
                }
            }
            deck.setStatNames(newCategories);
            sendEmbed("Successfully modified stats.");
        }
        catch (CancellationException e)
        {
            sendEmbed(e.getMessage());
        }
        catch (InvalidParameterException e)
        {
            sendEmbed(e.getMessage()+"\nPlease try again.");
            editCategories(false);
            return;
        }
        finally
        {
            runMainMenu();
        }
    }

    /**
     * Cycles through each card in the deck and modifies its stats to match the category quantity
     * @param newCategoryTotal the number of categories in the new category configuration
     */
    private void updateStatsToNewCategories(int newCategoryTotal)
    {
        for (Card card : deck.getCards())
        {
            int loopLimit = (card.getStats().length > newCategoryTotal) ? newCategoryTotal : card.getStats().length; //Loop by the lowest number
            double[] newStats = new double[newCategoryTotal];
            for (int i = 0; i<loopLimit; i++)
            {
                newStats[i] = card.getStats()[i];
            }
            card.setStats(newStats);
            //In the case of a greater new stat total, the new values will be 0.
        }
    }

    /**
     * Creates a new card and adds it to the deck
     */
    private void addCard()
    {
        try
        {
            if (deck.getCards().size() >= 30)
            {
                sendEmbed("This deck has reached the card limit.");
            }
            else
            {

                Card card = editCardWizard(new Card());
                boolean success = deck.addCard(card);
            }
        }
        catch (CancellationException | DuplicateName | IndexOutOfBoundsException e)
        {
            sendEmbed(e.getMessage());
        }
        finally
        {
            runCardsMenu();
        }
    }

    /**
     * Modifies the information for an existing card
     * @param cardName the name of the card to modify
     */
    private void editCard(String cardName)
    {
        try
        {
            Card card = deck.getCard(cardName);
            if (card == null)
            {
                sendEmbed("That card does not exist.");
            }
            else
            {
                Card newCard = editCardWizard(card);
                deck.removeCard(card);
                deck.addCard(newCard);
            }
        }
        catch (CancellationException | DuplicateName e)
        {
            sendEmbed(e.getMessage());
        }
        finally
        {
            runCardsMenu();
        }
    }

    /**
     * Removes a card from the deck.
     * @param cardName The name of the card to remove
     */
    private void deleteCard(String cardName)
    {
        Card card = deck.getCard(cardName);
        if (card == null)
        {
            sendEmbed("That card does not exist.");
        }
        else
        {
            deck.removeCard(card);
        }
    }

    /**
     * A wizard that walks the user through modifying/creating a card
     * @param card the card to edit
     * @return the edited card
     * @throws CancellationException user cancelled the editing
     */
    private Card editCardWizard(Card card) throws CancellationException
    {
        boolean success = false;
        sendEmbed("Please input the card name, or \"next\" to skip.\nCurrent: "+card.getName());
        while (!success)
        {
            try
            {
                String name = getInput().getContentDisplay();
                if (!name.equalsIgnoreCase("next"))
                {
                    if (deck.getCard(name) != null)
                    {
                        sendEmbed("That name is already taken. Please try again.");
                    }
                    else
                    {
                        card.setName(name);
                        success = true;
                    }
                }
                else
                {
                    break;
                }
            }
            catch (InvalidParameterException e)
            {
                sendEmbed(e.getMessage()+"\nPlease try again.");
            }
        }
        success = false;
        sendEmbed("Please input the image URL, or \"next\" to skip.\nCurrent: "+card.getImageURL());
        while (!success)
        {
            String url = getInput().getContentDisplay();
            if (!url.equalsIgnoreCase("next"))
            {
                success = card.setImageURL(url);
                if (!success)
                {
                    sendEmbed("Invalid image URL. Please try again.");
                }
            }
            else
            {
                break;
            }
        }
        success = false;
        double[] stats = new double[deck.getStatNames().length];
        for (int i = 0; i<stats.length; i++)
        {
            success = false;
            sendEmbed("Please input a numeric value for the stat \""+deck.getStatNames()[i]+"\", or \"next\" to skip.\nCurrent: "+card.getStats()[i]);
            while (!success)
            {
                String input = getInput().getContentDisplay();
                if (!input.equalsIgnoreCase("next"))
                {
                    try
                    {
                        stats[i] = Double.parseDouble(input);
                        success = true;
                    }
                    catch (NumberFormatException e)
                    {
                        success = false;
                        sendEmbed("Invalid value. Please enter a numeric value for \""+deck.getStatNames()[i]+"\".");
                    }
                }
                else
                {
                    stats[i] = card.getStats()[i];
                    break;
                }
            }
        }
        card.setStats(stats);
        return card;
    }


}
