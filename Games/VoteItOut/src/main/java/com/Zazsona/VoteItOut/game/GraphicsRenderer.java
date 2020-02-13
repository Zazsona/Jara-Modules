package com.Zazsona.VoteItOut.game;

import configuration.SettingsUtil;
import jara.ModuleResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GraphicsRenderer
{
    private static final String MODULE_KEY = "VoteItOut";
    private static final float SCALE = 0.60f;
    private String uuid;
    private File boardFile;

    public GraphicsRenderer(String uuid, Player... players) throws IOException
    {
        this.uuid = uuid;
        for (Player player : players)
            player.setPlayerCard(renderPlayerCard(player));
        renderBoard(players);
    }


    private BufferedImage renderPlayerCard(Player player)
    {
        try
        {
            Logger logger = LoggerFactory.getLogger(getClass());
            URL playerAvatarURL = new URL(player.getMember().getUser().getAvatarUrl());
            HttpURLConnection connection = (HttpURLConnection) playerAvatarURL.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31"); //For some reason, Linux instances return HTML 403 if the User-Agent is not specified.
            BufferedImage avatar = ImageIO.read(connection.getInputStream());
            BufferedImage avatarCircle = new BufferedImage(avatar.getWidth(), avatar.getWidth(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D avatarCircleGraphics = avatarCircle.createGraphics();
            avatarCircleGraphics.setClip(new Ellipse2D.Float(0, 0, avatar.getWidth(), avatar.getWidth()));
            avatarCircleGraphics.drawImage(avatar, 0, 0, avatar.getWidth(), avatar.getWidth(), null);
            String baseCardURL = "com/zazsona/voteitout/"+player.getPlayerNo()+".png";
            BufferedImage card = ImageIO.read(ModuleResourceLoader.getResourceStream(MODULE_KEY, baseCardURL));
            Graphics gfx = card.getGraphics();
            int avatarSize = Math.round(400*SCALE);
            int avatarX = Math.round(300*SCALE)-(avatarSize/2);
            int avatarY = Math.round(420*SCALE)-(avatarSize/2);
            gfx.drawImage(avatarCircle, avatarX, avatarY, avatarSize, avatarSize, null);
            gfx.dispose();
            return card;
        }
        catch (IOException e)
        {
            LoggerFactory.getLogger(getClass()).error("Something got fucked", e);
            return null;
        }
    }

    protected void renderBoard(Player... players) throws IOException
    {
        boardFile = new File(SettingsUtil.getModuleDataDirectory()+"/"+MODULE_KEY+"/"+uuid+"-Board.png");
        boardFile.getParentFile().mkdirs();
        boardFile.createNewFile();
        boolean useTwoTiers = (players.length > 3);
        String baseBackgroundURL = (!useTwoTiers) ? "com/zazsona/voteitout/BoardBackgroundTier1.png" : "com/zazsona/voteitout/BoardBackgroundTier2.png";
        BufferedImage board = ImageIO.read(ModuleResourceLoader.getResourceStream(MODULE_KEY, baseBackgroundURL));
        Graphics gfx = board.getGraphics();
        int rowLength = 3;
        int startX = Math.round(93*SCALE);
        int startYTier1 = Math.round(47*SCALE);
        int startYTier2 = Math.round(958*SCALE);
        int distance = Math.round(66*SCALE);
        for (int playerIndex = 0; playerIndex<players.length; playerIndex++)
        {
            int x = startX+((players[playerIndex].getPlayerCard().getWidth()+distance)*(playerIndex % rowLength));
            int y = (playerIndex <= 2) ? startYTier1 : startYTier2;
            gfx.drawImage(players[playerIndex].getPlayerCard(), x, y, null);
        }
        gfx.dispose();
        ImageIO.write(board, "PNG", boardFile);
    }

    public void dispose()
    {
        boardFile.delete();
    }

    /**
     * Gets boardFile
     *
     * @return boardFile
     */
    public File getBoardFile()
    {
        return boardFile;
    }
}
