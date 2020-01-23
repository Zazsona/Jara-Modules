package com.Zazsona.Blockbusters.game;

import com.Zazsona.Blockbusters.BlockbustersCommand;
import com.Zazsona.Blockbusters.game.objects.Board;
import com.Zazsona.Blockbusters.game.objects.Tile;
import com.Zazsona.Blockbusters.game.objects.TileState;
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
    private static final BlockbustersCommand moduleContext = new BlockbustersCommand();
    private File compositeFile;
    private String uuid;
    private Board board;

    public BoardRenderer(Board board) throws IOException
    {
         this.uuid = UUID.randomUUID().toString();
         this.board = board;
         this.compositeFile = new File(SettingsUtil.getModuleDataDirectory().getPath()+"/Blockbusters/"+uuid+"-board.png");
        compositeFile.mkdirs();
        compositeFile.createNewFile();
    }

    public void render() throws IOException
    {
        try
        {

            BufferedImage blueHexImage = ImageIO.read(ModuleResourceLoader.getResourceStream(moduleContext.getModuleAttributes().getKey(), "com/zazsona/blockbusters/BlueHex.png"));
            BufferedImage whiteHexImage = ImageIO.read(ModuleResourceLoader.getResourceStream(moduleContext.getModuleAttributes().getKey(), "com/zazsona/blockbusters/WhiteHex.png"));
            BufferedImage boardImage = ImageIO.read(ModuleResourceLoader.getResourceStream(moduleContext.getModuleAttributes().getKey(), "com/zazsona/blockbusters/Board.png"));

            BufferedImage overlayImage = new BufferedImage(746, 575, BufferedImage.TYPE_INT_ARGB);
            Graphics gfx = overlayImage.getGraphics();
            Font font = Font.createFont(Font.TRUETYPE_FONT, ModuleResourceLoader.getResourceStream(moduleContext.getModuleAttributes().getKey(), "com/zazsona/blockbusters/Square.ttf"));
            gfx.setFont(font.deriveFont(50.0f));
            gfx.setColor(Color.BLACK);
            int fontHeight = gfx.getFontMetrics().getHeight();

            gfx.drawImage(boardImage, 0, 0, null);
            for (int x = 0; x<board.getBoardXLength(); x++)
            {
                for (int y = 0; y<board.getBoardYLength(); y++)
                {
                    Tile currentTile = board.getTile(x, y);
                    int fontWidth = gfx.getFontMetrics().stringWidth(currentTile.getTileChar());
                    gfx.drawString(currentTile.getTileChar(), (currentTile.getTileX()-fontWidth/2), (currentTile.getTileY()+fontHeight/3));
                    if (currentTile.getTileState() == TileState.BLUE)
                    {
                        gfx.drawImage(blueHexImage, currentTile.getTileX()-(blueHexImage.getWidth()/2), currentTile.getTileY()-(blueHexImage.getHeight()/2), null);
                    }
                    else if (currentTile.getTileState() == TileState.WHITE)
                    {
                        gfx.drawImage(whiteHexImage, currentTile.getTileX()-(whiteHexImage.getWidth()/2), currentTile.getTileY()-(whiteHexImage.getHeight()/2), null);
                    }
                }
            }
            ImageIO.write(overlayImage, "PNG", compositeFile);
            gfx.dispose();
        }
        catch (FontFormatException e)
        {
            LoggerFactory.getLogger(this.getClass()).error(e.toString());
        }
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
}
