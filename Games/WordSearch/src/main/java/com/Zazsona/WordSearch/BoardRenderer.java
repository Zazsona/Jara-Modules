package com.Zazsona.WordSearch;

import configuration.SettingsUtil;
import jara.ModuleResourceLoader;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class BoardRenderer
{
    private static final WordSearch MODULE_CONTEXT = new WordSearch();
    private static final Coordinates WORDLIST_COORDINATES = new Coordinates(1107/2, 630/2);
    private File boardFile;
    private String uuid;
    private Board board;
    private BufferedImage boardImage;
    private Graphics2D gfx;

    public BoardRenderer(Board board) throws IOException
    {
        this.uuid = UUID.randomUUID().toString();
        this.board = board;
        this.boardFile = new File(SettingsUtil.getModuleDataDirectory().getPath()+"/Wordsearch/"+uuid+"-board.png");
        boardFile.mkdirs();
        boardFile.createNewFile();
        createBaseBoard();
    }

    private void createBaseBoard() throws IOException
    {
        try
        {
            boardImage = ImageIO.read(ModuleResourceLoader.getResourceStream(MODULE_CONTEXT.getModuleAttributes().getKey(), "com/zazsona/wordsearch/BoardBackground.png"));
            gfx = (Graphics2D) boardImage.getGraphics();
            Font font = Font.createFont(Font.TRUETYPE_FONT, ModuleResourceLoader.getResourceStream(MODULE_CONTEXT.getModuleAttributes().getKey(), "com/zazsona/wordsearch/JetBrainsMono-ExtraBold.ttf"));
            gfx.setFont(font.deriveFont(24.0f));
            int fontHeight = gfx.getFontMetrics().getHeight();
            for (int x = 0; x<board.getBoardWidth(); x++)
            {
                for (int y = 0; y<board.getBoardHeight(); y++)
                {
                    Coordinates coordinates = getCoordinatesForBoardPosition(x, y);
                    String letter = board.getLetter(x, y);
                    int fontWidth = gfx.getFontMetrics().stringWidth(letter);
                    gfx.drawString(letter, (coordinates.x-fontWidth/2), (coordinates.y+fontHeight/3));
                }
            }
            for (int i = 0; i<board.getWords().length; i++)
            {
                gfx.drawString(board.getWords()[i].getWord(), WORDLIST_COORDINATES.x, (WORDLIST_COORDINATES.y+(fontHeight*(i+1))));
            }
            ImageIO.write(boardImage, "PNG", boardFile);
        }
        catch (FontFormatException e)
        {
            throw new IOException(e);
        }
    }

    public void markWord(Word word) throws IOException
    {
        gfx.setColor(Color.decode("#738BDC"));
        gfx.setStroke(new BasicStroke(10/2));
        Coordinates startCoordinates = getCoordinatesForBoardPosition(word.getStartX(), word.getStartY());
        Coordinates endCoordinates = getCoordinatesForBoardPosition(word.getEndX(), word.getEndY());        //Draw line on board
        gfx.drawLine(startCoordinates.x, startCoordinates.y, endCoordinates.x, endCoordinates.y);

        int wordIndex = 0;
        for (Word boardWord : board.getWords())
        {
            if (!boardWord.equals(word))
                wordIndex++;
            else
                break;
        }
        int fontHeight = gfx.getFontMetrics().getHeight();
        int wordListY = WORDLIST_COORDINATES.y+(fontHeight*wordIndex)+(int) (fontHeight/1.5);
        gfx.drawLine(WORDLIST_COORDINATES.x, wordListY, WORDLIST_COORDINATES.x+gfx.getFontMetrics().stringWidth(word.getWord()), wordListY);

        ImageIO.write(boardImage, "PNG", boardFile);
    }

    public File getBoardImageFile() throws IOException
    {
        if (!boardFile.exists())
        {
            boardFile.mkdirs();
            boardFile.createNewFile();
            createBaseBoard();
        }
        return boardFile;
    }

    public boolean dispose()
    {
        gfx.dispose();
        return boardFile.delete();
    }

    private Coordinates getCoordinatesForBoardPosition(int x, int y)
    {
        int graphicsX = 0;
        int graphicsY = 0;
        switch (x)
        {
            case 0:
                graphicsX = 178;
                break;
            case 1:
                graphicsX = 282;
                break;
            case 2:
                graphicsX = 388;
                break;
            case 3:
                graphicsX = 494;
                break;
            case 4:
                graphicsX = 600;
                break;
            case 5:
                graphicsX = 707;
                break;
            case 6:
                graphicsX = 813;
                break;
            case 7:
                graphicsX = 919;
                break;
            case 8:
                graphicsX = 1023;
                break;
        }
        switch (y)
        {
            case 0:
                graphicsY = 118;
                break;
            case 1:
                graphicsY = 222;
                break;
            case 2:
                graphicsY = 327;
                break;
            case 3:
                graphicsY = 432;
                break;
            case 4:
                graphicsY = 537;
                break;
            case 5:
                graphicsY = 641;
                break;
            case 6:
                graphicsY = 746;
                break;
            case 7:
                graphicsY = 851;
                break;
            case 8:
                graphicsY = 947;
                break;
        }
        return new Coordinates(graphicsX/2, graphicsY/2); //Divide by 2 as these values are based on original quality, but we're using a downscaled version to save on processing/server/upload/download costs
    }

    private static class Coordinates
    {
        public Coordinates(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
        int x;
        int y;
    }

}
