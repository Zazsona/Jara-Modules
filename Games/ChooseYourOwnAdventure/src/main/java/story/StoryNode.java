package story;

import java.io.Serializable;

/**
 * A tree node for the story.
 * Any references to "ID" refer to a position in the story tree array.
 */
public class StoryNode implements Serializable
{
    private int id;
    private String scenarioText;
    private String optionAText;
    private String optionBText;

    private int optionANodeID;
    private int optionBNodeID;

    public StoryNode(String scenario, String optionAText, String optionBText)
    {
        this.id = -1;
        this.optionANodeID = -1;
        this.optionBNodeID = -1;
        this.scenarioText = scenario;
        this.optionAText = optionAText;
        this.optionBText = optionBText;
    }

    public StoryNode(String scenario, String optionAText, String optionBText, int optionANodeID, int optionBNodeID)
    {
        this.id = -1;
        this.scenarioText = scenario;
        this.optionAText = optionAText;
        this.optionBText = optionBText;
        this.optionANodeID = optionANodeID;
        this.optionBNodeID = optionBNodeID;
    }

    public String getScenario()
    {
        return scenarioText;
    }

    public String getOptionAText()
    {
        return optionAText;
    }

    public String getOptionBText()
    {
        return optionBText;
    }

    public int getOptionANodeID()
    {
        return optionANodeID;
    }

    public int getOptionBNodeID()
    {
        return optionBNodeID;
    }

    public StoryNode getOptionANode()
    {
        if (optionANodeID != -1)
        {
            return StoryManager.getStoryNode(optionANodeID);
        }
        return null;
    }

    public StoryNode getOptionBNode()
    {
        if (optionBNodeID != -1)
        {
            return StoryManager.getStoryNode(optionBNodeID);
        }
        return null;
    }

    public void setOptionANode(StoryNode optionANode)
    {
        StoryManager.addNode(optionANode, id, true);
    }
    public void setOptionBNode(StoryNode optionBNode)
    {
        StoryManager.addNode(optionBNode, id, false);
    }
    public void setOptionANodeID(int id)
    {
        optionANodeID = id;
    }
    public void setOptionBNodeID(int id)
    {
        optionBNodeID = id;
    }

    public int getNodeID()
    {
        return id;
    }

    public void setNodeID(int id)
    {
        if (this.id == -1)
        {
            this.id = id;
        }
    }


}
