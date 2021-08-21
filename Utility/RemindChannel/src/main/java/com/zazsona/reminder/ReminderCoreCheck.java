package com.zazsona.reminder;

import com.zazsona.jara.module.ModuleLoad;
import com.zazsona.remindercore.ReminderManager;
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
