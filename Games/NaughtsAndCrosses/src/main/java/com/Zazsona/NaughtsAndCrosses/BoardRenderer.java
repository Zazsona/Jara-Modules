package com.Zazsona.NaughtsAndCrosses;

import com.Zazsona.NaughtsAndCrosses.game.Board;
import com.Zazsona.NaughtsAndCrosses.game.Counter;
import configuration.SettingsUtil;
import jara.ModuleResourceLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class BoardRenderer
{
    private static final NaughtsAndCrosses moduleContext = new NaughtsAndCrosses();
    private String uuid;
    private Board board;

    private final File compositeFile;
    private final BufferedImage naughtImage;
    private final BufferedImage crossImage;
    private final BufferedImage boardImage;
    private final BufferedImage[][] positionTexts;

    public BoardRenderer(Board board) throws IOException
    {
        this.board = board;
        this.uuid = UUID.randomUUID().toString();
        this.board = board;
        this.compositeFile = new File(SettingsUtil.getModuleDataDirectory().getPath()+"/NoughtsAndCrosses/"+uuid+"-board.png");
        this.boardImage = ImageIO.read(ModuleResourceLoader.getResourceStream(moduleContext.getModuleAttributes().getKey(), "com/zazsona/naughtsandcrosses/BoardBackground.png"));
        this.naughtImage = ImageIO.read(ModuleResourceLoader.getResourceStream(moduleContext.getModuleAttributes().getKey(), "com/zazsona/naughtsandcrosses/O.png"));
        this.crossImage = ImageIO.read(ModuleResourceLoader.getResourceStream(moduleContext.getModuleAttributes().getKey(), "com/zazsona/naughtsandcrosses/X.png"));
        this.positionTexts = new BufferedImage[board.getBoardWidth()][board.getBoardHeight()];
        loadLetterImages();

        compositeFile.mkdirs();
        compositeFile.createNewFile();
    }

    public void render() throws IOException
    {
        BufferedImage overlayImage = new BufferedImage(boardImage.getWidth(), boardImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics gfx = overlayImage.getGraphics();
        gfx.drawImage(boardImage, 0, 0, null);
        for (int x = 0; x<board.getBoardWidth(); x++)
        {
            for (int y = 0; y<board.getBoardHeight(); y++)
            {
                DrawableTile drawableTile = getDrawableTile(x, y);
                gfx.drawImage(drawableTile.image, drawableTile.x, drawableTile.y, null);
            }
        }
        ImageIO.write(overlayImage, "PNG", compositeFile);
        gfx.dispose();
    }

    private DrawableTile getDrawableTile(int boardX, int boardY)
    {
        Counter counter = board.getCounterAtPosition(boardX, boardY);
        int boardImageX = 0;
        int boardImageY = 0;
        BufferedImage image = positionTexts[boardX][boardY];
        switch (boardX)
        {
            case 0:
                boardImageX = 120;
                break;
            case 1:
                boardImageX = 298;
                break;
            case 2:
                boardImageX = 476;
                break;
        }
        switch (boardY)
        {
            case 0:
                boardImageY = 27;
                break;
            case 1:
                boardImageY = 205;
                break;
            case 2:
                boardImageY = 383;
                break;
        }
        switch (counter)
        {

            case NAUGHT:
                image = naughtImage;
                break;
            case CROSS:
                image = crossImage;
                break;
        }
        return new DrawableTile(boardImageX, boardImageY, image);
    }

    /**
     * Gets board
     * @return board
     */
    public Board getBoard()
    {
        return board;
    }

    /**
     * Sets the value of board
     * @param board the value to set
     */
    public void setBoard(Board board)
    {
        this.board = board;
    }

    public File getBoardImageFile() throws IOException
    {
        if (!compositeFile.exists())
        {
            compositeFile.mkdirs();
            compositeFile.createNewFile();
            render();
        }
        return compositeFile;
    }

    public boolean deleteBoardImageFile()
    {
        return compositeFile.delete();
    }

    private void loadLetterImages() throws IOException
    {
        for (int x = 0; x<positionTexts.length; x++)
        {
            for (int y = 0; y<positionTexts[x].length; y++)
            {
                String letter = null;
                switch (x)
                {
                    case 0:
                        letter = "A";
                        break;
                    case 1:
                        letter = "B";
                        break;
                    case 2:
                        letter = "C";
                        break;
                }
                positionTexts[x][y] = ImageIO.read(ModuleResourceLoader.getResourceStream(moduleContext.getModuleAttributes().getKey(), "com/zazsona/naughtsandcrosses/letters/"+letter+(y+1)+".png"));
            }
        }
    }

    private class DrawableTile
    {
        public DrawableTile(int x, int y, BufferedImage image)
        {
            this.x = x;
            this.y = y;
            this.image = image;
        }

        private int x;
        private int y;
        private BufferedImage image;
    }
}
