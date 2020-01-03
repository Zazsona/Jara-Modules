package com.Zazsona.Tamago;

import com.Zazsona.Tamago.petdata.LifeStage;
import com.Zazsona.Tamago.petdata.Pet;
import com.Zazsona.Tamago.petdata.PetType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.Instant;
import java.util.HashMap;
import java.util.Random;

public class PetFactory
{
    private static transient Logger logger = LoggerFactory.getLogger("Tamago");
    private static HashMap<String, Pet> petMap;

    private static final int STAT_TIME = 259200; //3 days
    private static final int CHILD_TIME = 604800; //7 days
    private static final int TEEN_TIME = 3283200; //38 days
    private static final int ADULT_TIME = 5443200; //63 days

    public static Pet getPet(String userID)
    {
        if (petMap == null)
            restore();

        Pet pet = petMap.get(userID);
        if (pet != null && pet.getLifeStage() != LifeStage.EGG)
        {
            updatePetState(pet);
        }
        else
        {
            pet = createPet(userID);
            petMap.put(userID, pet);
        }
        return pet;
    }

    public static boolean releasePet(String userID)
    {
        if (petMap == null)
            restore();

        if (!InteractionHandler.isPetActive(userID))
        {
            petMap.remove(userID);
            return true;
        }
        else
        {
            return false;
        }

    }

    private static Pet updatePetState(Pet pet)
    {
        long timestamp = Instant.now().getEpochSecond();
        pet.setLifeStage(getPetLifeStage(pet.getBirthSecond(), timestamp));
        pet.setHunger(getPetStatLevel(pet.getLastVisitSecond(), timestamp, pet.getHunger()));
        pet.setHappiness(getPetStatLevel(pet.getLastVisitSecond(), timestamp, pet.getHappiness()));
        pet.setDiscipline(getPetStatLevel(pet.getLastVisitSecond(), timestamp, pet.getDiscipline()));
        pet.setLastVisitSecond(timestamp);
        return pet;
    }

    private static Pet createPet(String userID)
    {
        PetType[] petTypes = PetType.values();
        PetType petType = petTypes[new Random().nextInt(petTypes.length)];
        Pet pet = new Pet(userID, petType);
        return pet;
    }

    private static LifeStage getPetLifeStage(long petBirthSecond, long currentEpochSecond)
    {
        long lifeTime = currentEpochSecond-petBirthSecond;
        if (lifeTime < CHILD_TIME)
        {
            return LifeStage.BABY;
        }
        else if (lifeTime >= CHILD_TIME && lifeTime < TEEN_TIME)
        {
            return LifeStage.CHILD;
        }
        else if (lifeTime >= TEEN_TIME && lifeTime < ADULT_TIME)
        {
            return LifeStage.TEEN;
        }
        else
        {
            return LifeStage.ADULT;
        }
    }

    private static int getPetStatLevel(long lastVisitSecond, long currentEpochSecond, int statValue)
    {
        long timeSinceLastVisit = currentEpochSecond-lastVisitSecond;
        int valueLoss = (int) Math.floor(timeSinceLastVisit/STAT_TIME);
        int newStatValue = (statValue-valueLoss < 0) ? 0 : statValue-valueLoss;
        return newStatValue;
    }

    private static String getSavePath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/Tamago.jara";
    }

    public static synchronized void save()
    {
        try
        {
            if (petMap == null)
                restore();

            File petFile = new File(getSavePath());
            if (!petFile.exists())
            {
                petFile.getParentFile().mkdirs();
                petFile.createNewFile();
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(petMap);
            FileOutputStream fos = new FileOutputStream(getSavePath());
            PrintWriter pw = new PrintWriter(fos);
            pw.print(json);
            pw.close();
            fos.close();
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }

    private static synchronized void restore()
    {
        try
        {
            File petFile = new File(getSavePath());
            if (petFile.exists())
            {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = new String(Files.readAllBytes(petFile.toPath()));
                TypeToken<HashMap<String, Pet>> token = new TypeToken<HashMap<String, Pet>>() {};
                petMap = gson.fromJson(json, token.getType());
            }
            else
            {
                petMap = new HashMap<>();
            }
        }
        catch (IOException e)
        {
            petMap = new HashMap<>();
            logger.error(e.getMessage());
        }
    }
}
