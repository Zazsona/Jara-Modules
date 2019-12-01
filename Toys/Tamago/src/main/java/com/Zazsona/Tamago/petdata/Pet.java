package com.Zazsona.Tamago.petdata;

import com.Zazsona.Tamago.PetFactory;

import java.io.Serializable;
import java.time.Instant;
import java.util.Random;

public class Pet implements Serializable
{
    private long lastVisitSecond;
    private long birthSecond;
    private String ownerID;
    private String petName;
    private PetType petType;
    private LifeStage lifeStage;
    private int hunger;
    private int happiness;
    private int discipline;
    private static final int MAX_HUNGER = 10;
    private static final int MAX_HAPPINESS = 10;
    private static final int MAX_DISCIPLINE = 10;
    private transient Random r = new Random();

    private Pet()
    {
    }

    public Pet(String userID, PetType petType)
    {
        long timestamp = Instant.now().getEpochSecond();
        this.lastVisitSecond = timestamp;
        this.birthSecond = timestamp;
        this.ownerID = userID;
        this.petName = "Unnamed Pet";
        this.petType = petType;
        this.lifeStage = LifeStage.EGG;
        this.hunger = MAX_HUNGER/2;
        this.happiness = MAX_HAPPINESS/2;
        this.discipline = MAX_DISCIPLINE/2;
    }

    public void save()
    {
        PetFactory.save();
    }

    public void feed()
    {
        if (hunger < MAX_HUNGER)
        {
            modifyHunger(1);
            if (r.nextInt(10) == 0)
            {
                modifyHappiness(1);
            }
        }
        else
        {
            if (r.nextInt(5) == 0)
            {
                modifyHappiness(-1);
            }
        }
    }

    public void play()
    {
        if (happiness < MAX_HAPPINESS)
        {
            modifyHappiness(1);
        }
        if (r.nextInt(15) == 0)
        {
            modifyDiscipline(-1);
        }
        if (r.nextInt(7) == 0)
        {
            modifyHunger(-1);
        }
    }

    public void scold()
    {
        if (discipline < MAX_DISCIPLINE)
        {
            modifyDiscipline(1);
            if (r.nextInt(3) == 0)
            {
                modifyHappiness(-1);
            }
        }
        else
        {
            if (r.nextInt(2) == 0)
            {
                modifyHappiness(-2);
            }
        }

    }

    public PetMood getPetMood()
    {
        if (hunger <= MAX_HUNGER/2)
        {
            return PetMood.HUNGRY;
        }
        else if (discipline <= MAX_DISCIPLINE/2.5)
        {
            return PetMood.NOISY;
        }
        else if (happiness <= MAX_HAPPINESS/2.5)
        {
            return PetMood.SLEEPING;
        }
        else
        {
            return PetMood.HAPPY;
        }
    }

    private void modifyHunger(int amount)
    {
        hunger = hunger+amount;
        if (hunger < 0)
            hunger = 0;
        if (hunger > MAX_HUNGER)
            hunger = MAX_HUNGER;
    }

    private void modifyHappiness(int amount)
    {
        happiness = happiness+amount;
        if (happiness < 0)
            happiness = 0;
        if (happiness > MAX_HAPPINESS)
            happiness = MAX_HAPPINESS;
    }

    private void modifyDiscipline(int amount)
    {
        discipline = discipline+amount;
        if (discipline < 0)
            discipline = 0;
        if (discipline > MAX_DISCIPLINE)
            discipline = MAX_DISCIPLINE;
    }

    public void setLastVisitSecond(long lastVisitSecond)
    {
        this.lastVisitSecond = lastVisitSecond;
    }

    public void setPetName(String petName)
    {
        this.petName = petName;
    }

    public void setLifeStage(LifeStage lifeStage)
    {
        this.lifeStage = lifeStage;
    }

    public void setHunger(int hunger)
    {
        this.hunger = hunger;
    }

    public void setHappiness(int happiness)
    {
        this.happiness = happiness;
    }

    public void setDiscipline(int discipline)
    {
        this.discipline = discipline;
    }

    public long getLastVisitSecond()
    {
        return lastVisitSecond;
    }

    public long getBirthSecond()
    {
        return birthSecond;
    }

    public String getOwnerID()
    {
        return ownerID;
    }

    public String getPetName()
    {
        return petName;
    }

    public PetType getPetType()
    {
        return petType;
    }

    public LifeStage getLifeStage()
    {
        return lifeStage;
    }

    public int getHunger()
    {
        return hunger;
    }

    public int getHappiness()
    {
        return happiness;
    }

    public int getDiscipline()
    {
        return discipline;
    }

    public float getHungerPercentage()
    {
        return (hunger/(float) MAX_HUNGER)*100;
    }

    public float getHappinessPercentage()
    {
        return (happiness/(float) MAX_HAPPINESS)*100;
    }

    public float getDisciplinePercentage()
    {
        return (discipline/(float) MAX_DISCIPLINE)*100;
    }
}
