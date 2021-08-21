package com.Zazsona.ChooseYourOwnAdventure.story;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zazsona.jara.configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class StoryManager
{
    private static ArrayList<StoryNode> storyTree;
    private static Logger logger = LoggerFactory.getLogger(StoryManager.class);

    private static String getConfigPath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/CYOAStoryTree.jara";
    }
    private static synchronized void save()
    {
        try
        {
            File configFile = new File(getConfigPath());
            if (!configFile.exists())
            {
                configFile.createNewFile();
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(storyTree);
            FileOutputStream fos = new FileOutputStream(getConfigPath());
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
            File configFile = new File(getConfigPath());
            if (configFile.exists())
            {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = new String(Files.readAllBytes(configFile.toPath()));
                TypeToken<ArrayList<StoryNode>> token = new TypeToken<ArrayList<StoryNode>>() {};
                storyTree = gson.fromJson(json, token.getType());
            }
            else
            {
                storyTree = new ArrayList<>();
                StoryNode rootNode = new StoryNode("Everything here... is darkness.", "Walk forwards.", "Turn around.");
                rootNode.setNodeID(0);
                storyTree.add(rootNode);
            }
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
            return;
        }
    }

    private static synchronized ArrayList<StoryNode> getStoryTree()
    {
        if (storyTree == null)
        {
            restore();
        }
        return storyTree;
    }

    /**
     * Gets the story node as specified by nodeID
     * @param nodeID
     * @return
     */
    public static synchronized StoryNode getStoryNode(int nodeID)
    {
        return getStoryTree().get(nodeID);
    }

    /**
     * Adds a story node.
     * @param node the {@link StoryNode} to add.
     * @return the {@link StoryNode}
     */
    public static synchronized StoryNode addNode(StoryNode node, int parentNodeID, boolean isOptionA)
    {
        getStoryTree();
        node.setNodeID(storyTree.size());
        storyTree.add(node);
        if (isOptionA)
            getStoryNode(parentNodeID).setOptionANodeID(node.getNodeID());
        else
            getStoryNode(parentNodeID).setOptionBNodeID(node.getNodeID());

        save();
        return node;

    }
}
