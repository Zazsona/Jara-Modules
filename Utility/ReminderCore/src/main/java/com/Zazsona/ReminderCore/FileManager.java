package com.Zazsona.ReminderCore;

import configuration.SettingsUtil;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;

public class FileManager
{
    private static File getDataDirectory()
    {
        File dir = new File(SettingsUtil.getModuleDataDirectory()+"/Reminders/");
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getRemindersFile() throws IOException
    {
        File remindersFile = new File(getDataDirectory().getPath()+"/Reminders.jara");
        if (!remindersFile.exists())
        {
            remindersFile.createNewFile();
        }
        return remindersFile;
    }

    private static File getDateTreeFile() throws IOException
    {
        File dateTreeFile = new File(getDataDirectory().getPath()+"/RemindersDateTree.jara");
        if (!dateTreeFile.exists())
        {
            dateTreeFile.createNewFile();
        }
        return dateTreeFile;
    }

    private static File getFutureRemindersFile() throws IOException
    {
        File futureFile = new File(getDataDirectory().getPath()+"/FutureReminders.jara");
        if (!futureFile.exists())
        {
            futureFile.createNewFile();
        }
        return futureFile;
    }

    protected static ReminderDateTree getReminderDateTree()
    {
        try
        {
            FileInputStream fis = new FileInputStream(getDateTreeFile());
            ObjectInputStream ois = new ObjectInputStream(fis);
            ReminderDateTree rdt = (ReminderDateTree) ois.readObject();
            ois.close();
            fis.close();
            return rdt;
        }
        catch (ClassNotFoundException | IOException e)
        {
            LoggerFactory.getLogger("Reminders-File-Manager").error("Unable to read reminders date tree file.\n"+e.toString());
            return new ReminderDateTree();
        }
    }

    protected static HashMap<String, Reminder> getReminders()
    {
        try
        {
            FileInputStream fis = new FileInputStream(getRemindersFile());
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashMap<String, Reminder> reminders = (HashMap<String, Reminder>) ois.readObject();
            ois.close();
            fis.close();
            return reminders;
        }
        catch (ClassNotFoundException | IOException e)
        {
            LoggerFactory.getLogger("Reminders-File-Manager").error("Unable to read reminders file.\n"+e.toString());
            return new HashMap<>();
        }
    }

    protected static HashMap<String, Integer> getFutureReminders()
    {
        try
        {
            FileInputStream fis = new FileInputStream(getFutureRemindersFile());
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashMap<String, Integer> reminders = (HashMap<String, Integer>) ois.readObject();
            ois.close();
            fis.close();
            return reminders;
        }
        catch (ClassNotFoundException | IOException e)
        {
            LoggerFactory.getLogger("Reminders-File-Manager").error("Unable to read future reminders file.\n"+e.toString());
            return new HashMap<>();
        }
    }

    protected static void saveRemindersDateTree(ReminderDateTree rdt) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(getDateTreeFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(rdt);
        oos.close();
        fos.close();
    }

    protected static void saveReminders(HashMap<String, Reminder> reminders) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(getRemindersFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(reminders);
        oos.close();
        fos.close();
    }

    protected static void saveFutureReminders(HashMap<String, Integer> futureReminders) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(getFutureRemindersFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(futureReminders);
        oos.close();
        fos.close();
    }
}
