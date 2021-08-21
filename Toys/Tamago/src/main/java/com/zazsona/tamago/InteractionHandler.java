package com.zazsona.tamago;

import com.zazsona.tamago.petdata.Pet;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.*;

public class InteractionHandler extends ListenerAdapter
{
    public static final String EMOJI_FEED = "\uD83C\uDF54";
    public static final String EMOJI_PLAY = "\u26BD";
    public static final String EMOJI_SCOLD = "\uD83D\uDC49";
    public static final String EMOJI_QUIT = "\uD83D\uDC4B";

    private static Thread cleanUpThread;
    private static HashMap<Message, Pet> msgToPetMap = new HashMap<>();
    private static HashMap<String, Message> msgIdToMsgMap = new HashMap<>();
    private static HashSet<String> activePetOwnerIds = new HashSet<>();
    private static boolean locked = false;

    public InteractionHandler()
    {
        if (cleanUpThread == null)
        {
            cleanUpThread = new Thread(() -> cleanActivePets());
            cleanUpThread.start();
        }
    }

    public static boolean isPetActive(String userID)
    {
        return activePetOwnerIds.contains(userID);
    }

    public static synchronized void addActivePetMessage(Message message, Pet pet)
    {
        waitOnUnlock();
        String ownerID = pet.getOwnerID();
        if (activePetOwnerIds.contains(ownerID))
        {
            locked = true;
            for (Map.Entry<Message, Pet> entry : msgToPetMap.entrySet())
            {
                if (entry.getValue().getOwnerID().equals(ownerID))
                {
                    locked = false;
                    removeActivePetMessage(entry.getKey());
                    break;
                }
            }
        }
        locked = true;
        activePetOwnerIds.add(ownerID);
        msgToPetMap.put(message, pet);
        msgIdToMsgMap.put(message.getId(), message);
        message.addReaction(InteractionHandler.EMOJI_FEED).queue();
        message.addReaction(InteractionHandler.EMOJI_PLAY).queue();
        message.addReaction(InteractionHandler.EMOJI_SCOLD).queue();
        message.addReaction(InteractionHandler.EMOJI_QUIT).queue();
        locked = false;
    }

    private static void removeActivePetMessage(Message message)
    {
        waitOnUnlock();
        locked = true;
        String ownerId = msgToPetMap.get(message).getOwnerID();
        msgToPetMap.remove(message);
        msgIdToMsgMap.remove(message.getId());
        activePetOwnerIds.remove(ownerId);
        message.delete().queue();
        locked = false;
    }

    private void cleanActivePets()
    {
        try
        {
            while (true)
            {
                Thread.sleep(1000 * 60 * 5);
                waitOnUnlock();
                ArrayList<Message> keysToDelete = new ArrayList<>();
                locked = true;
                long timestamp = Instant.now().getEpochSecond();
                for (Map.Entry<Message, Pet> entry : msgToPetMap.entrySet())
                {
                    if (timestamp - entry.getValue().getLastVisitSecond() >= 60*4.5)
                    {
                        keysToDelete.add(entry.getKey());
                    }
                }
                locked = false;
                for (Message message : keysToDelete)
                {
                    removeActivePetMessage(message);
                }
                PetFactory.save();
            }
        }
        catch (InterruptedException e)
        {
            LoggerFactory.getLogger(InteractionHandler.class).debug("Cleanup thread interrupted!");
        }
    }

    private static synchronized void waitOnUnlock()
    {
        try
        {
            Random r = new Random();
            while (locked)
            {
                Thread.sleep(50+r.nextInt(100));
            }
        }
        catch (InterruptedException e)
        {
            LoggerFactory.getLogger(InteractionHandler.class).debug("Thread interrupted!");
        }

    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event)
    {
        if (msgIdToMsgMap.containsKey(event.getMessageId()))
        {
            Message msg = msgIdToMsgMap.get(event.getMessageId());
            Pet pet = msgToPetMap.get(msg);
            if (event.getMember().getId().equals(pet.getOwnerID()))
            {
                String emojiCode = event.getReactionEmote().getEmoji();
                event.getReaction().removeReaction(event.getUser()).queue();
                switch (emojiCode)
                {
                    case EMOJI_FEED:
                        pet.feed();
                        pet.setLastVisitSecond(Instant.now().getEpochSecond());
                        msg.editMessage(PetView.getPetView(pet, event.getMember())).queue();
                        break;
                    case EMOJI_PLAY:
                        pet.play();
                        pet.setLastVisitSecond(Instant.now().getEpochSecond());
                        msg.editMessage(PetView.getPetView(pet, event.getMember())).queue();
                        break;
                    case EMOJI_SCOLD:
                        pet.scold();
                        pet.setLastVisitSecond(Instant.now().getEpochSecond());
                        msg.editMessage(PetView.getPetView(pet, event.getMember())).queue();
                        break;
                    case EMOJI_QUIT:
                        removeActivePetMessage(msg);
                        break;
                }
            }
        }

    }
}
