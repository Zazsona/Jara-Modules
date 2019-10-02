package com.Zazsona.ReminderCore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import configuration.SettingsUtil;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
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
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = new String(Files.readAllBytes(getDateTreeFile().toPath()));
            ReminderDateTree rdt = gson.fromJson(json, ReminderDateTree.class);
            if (rdt == null)
            {
                rdt = new ReminderDateTree();
            }
            return rdt;
        }
        catch (IOException e)
        {
            LoggerFactory.getLogger("Reminders-File-Manager").error("Unable to read reminders date tree file.\n"+e.toString());
            return new ReminderDateTree();
        }
    }

    protected static HashMap<String, Reminder> getReminders()
    {
        try
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = new String(Files.readAllBytes(getRemindersFile().toPath()));
            TypeToken<HashMap<String, Reminder>> token = new TypeToken<HashMap<String, Reminder>>() {};
            HashMap<String, Reminder> reminders = gson.fromJson(json, token.getType());
            if (reminders == null)
            {
                reminders = new HashMap<>();
            }
            return reminders;
        }
        catch (IOException e)
        {
            LoggerFactory.getLogger("Reminders-File-Manager").error("Unable to read reminders file.\n"+e.toString());
            return new HashMap<>();
        }
    }

    protected static HashMap<String, Integer> getFutureReminders()
    {
        try
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = new String(Files.readAllBytes(getFutureRemindersFile().toPath()));
            TypeToken<HashMap<String, Integer>> token = new TypeToken<HashMap<String, Integer>>() {};
            HashMap<String, Integer> reminders = gson.fromJson(json, token.getType());
            if (reminders == null)
            {
                reminders = new HashMap<>();
            }
            return reminders;
        }
        catch (IOException e)
        {
            LoggerFactory.getLogger("Reminders-File-Manager").error("Unable to read future reminders file.\n"+e.toString());
            return new HashMap<>();
        }
    }

    protected static void saveRemindersDateTree(ReminderDateTree rdt) throws IOException
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(rdt);
        FileOutputStream fos = new FileOutputStream(getDateTreeFile());
        PrintWriter pw = new PrintWriter(fos);
        pw.print(json);
        pw.close();
        fos.close();
    }

    protected static void saveReminders(HashMap<String, Reminder> reminders) throws IOException
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(reminders);
        FileOutputStream fos = new FileOutputStream(getRemindersFile());
        PrintWriter pw = new PrintWriter(fos);
        pw.print(json);
        pw.close();
        fos.close();
    }

    protected static void saveFutureReminders(HashMap<String, Integer> futureReminders) throws IOException
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(futureReminders);
        FileOutputStream fos = new FileOutputStream(getFutureRemindersFile());
        PrintWriter pw = new PrintWriter(fos);
        pw.print(json);
        pw.close();
        fos.close();
    }
}
