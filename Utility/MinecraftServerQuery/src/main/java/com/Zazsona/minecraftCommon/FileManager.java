package com.zazsona.minecraftCommon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zazsona.jara.configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;

public class FileManager
{
    private static HashMap<String, String> guildToIPMap;
    private transient static Logger logger = LoggerFactory.getLogger(FileManager.class);

    private static String getSavePath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/DefaultMinecraftServers.jara";
    }

    public static synchronized void save()
    {
        try
        {
            File saveFile = new File(getSavePath());
            if (!saveFile.exists())
            {
                saveFile.getParentFile().mkdirs();
                saveFile.createNewFile();
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(guildToIPMap);
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

    public static synchronized void restore()
    {
        try
        {
            File saveFile = new File(getSavePath());
            if (saveFile.exists())
            {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = new String(Files.readAllBytes(saveFile.toPath()));
                TypeToken<HashMap<String, String>> token = new TypeToken<HashMap<String, String>>() {};
                guildToIPMap = gson.fromJson(json, token.getType());
            }
            else
            {
                guildToIPMap = new HashMap<>();
            }

        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
            return;
        }
    }

    private static HashMap<String, String> getGuildToIPMap()
    {
        if (guildToIPMap == null)
        {
            restore();
        }
        return guildToIPMap;
    }

    public static String getIpForGuild(String guildID)
    {
        getGuildToIPMap();
        return guildToIPMap.get(guildID);
    }

    public static void setIpForGuild(String guildID, String ip)
    {
        getGuildToIPMap();
        guildToIPMap.put(guildID, ip);
        save();
    }

    public static void resetIpForGuild(String guildID)
    {
        getGuildToIPMap();
        guildToIPMap.remove(guildID);
        save();
    }

    public static String getIpWithoutPort(String guildID)
    {
        String ip = getIpForGuild(guildID);
        if (ip != null)
        {
            if (ip.contains(":"))
            {
                return ip.substring(0, ip.indexOf(":"));
            }
            else
            {
                return ip;
            }
        }
        return null;
    }

    public static String getPort(String guildID)
    {
        String ip = getIpForGuild(guildID);
        if (ip != null)
        {
            if (ip.contains(":"))
            {
                return ip.substring(ip.indexOf(":")+1);
            }
            else
            {
                return "25565";
            }
        }
        return null;
    }
}
