package com.Zazsona.ChooseYourOwnAdventure.story;

import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

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
            FileOutputStream fos = new FileOutputStream(getConfigPath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(storyTree);
            oos.close();
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
            if (new File(getConfigPath()).exists())
            {
                FileInputStream fis = new FileInputStream(getConfigPath());
                ObjectInputStream ois = new ObjectInputStream(fis);
                storyTree = (ArrayList<StoryNode>) ois.readObject();
                ois.close();
                fis.close();
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
        catch (ClassNotFoundException e)
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
