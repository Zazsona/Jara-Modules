package com.Zazsona.Reminder;

import com.Zazsona.ReminderCore.ReminderManager;
import module.ModuleLoad;
import org.slf4j.LoggerFactory;

public class ReminderCoreCheck extends ModuleLoad
{
    @Override
    public void load()
    {
        try
        {
            Class checkClass = ReminderManager.class;
        }
        catch (NoClassDefFoundError e)
        {
            LoggerFactory.getLogger(getClass()).error("Reminder Core is missing. Reminders WILL NOT work.");
        }
    }
}
