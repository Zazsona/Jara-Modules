package com.Zazsona.Blockbusters.game;

import com.Zazsona.Blockbusters.game.objects.*;

import java.io.IOException;
import java.util.Random;

public class GameMaster
{
    private Team blueTeam;
    private Team whiteTeam;
    private Board board;
    private boolean isStarted;
    BlockbustersUI blockbustersUI;

    public GameMaster(BlockbustersUI blockbustersUI, Team whiteTeam, Team blueTeam)
    {
        this.blueTeam = blueTeam;
        this.whiteTeam = whiteTeam;
        this.board = new Board();
        this.isStarted = false;
        this.blockbustersUI = blockbustersUI;
    }

    /**
     * Gets blueTeam
     * @return blueTeam
     */
    public Team getBlueTeam()
    {
        return blueTeam;
    }

    /**
     * Gets whiteTeam
     * @return whiteTeam
     */
    public Team getWhiteTeam()
    {
        return whiteTeam;
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
     * Gets isStarted
     * @return isStarted
     */
    public boolean isStarted()
    {
        return isStarted;
    }

    public Team run() throws IOException
    {
        if (!isStarted)
        {
            isStarted = true;
            boolean isWhiteStart = new Random().nextBoolean();
            TileState winState = takeTurn(isWhiteStart);
            board.getBoardRenderer().render();
            blockbustersUI.sendBoard(board.getBoardRenderer().getBoardImageFile());
            board.getBoardRenderer().deleteBoardImageFile();
            isStarted = false;
            return (winState == TileState.WHITE) ? whiteTeam : blueTeam;
        }
        return null;
    }

    private TileState takeTurn(boolean isWhiteTurn) throws IOException
    {
        board.getBoardRenderer().render();
        blockbustersUI.sendBoard(board.getBoardRenderer().getBoardImageFile());

        Team activeTeam = (isWhiteTurn) ? whiteTeam : blueTeam;
        blockbustersUI.sendAnswerResponse(activeTeam.getTeamName()+", pick a letter!");
        String letter = null;
        Tile tile = null;
        while (!board.isLetterOnBoard(letter) || (tile != null && tile.getTileState() != TileState.UNCLAIMED))
        {
            letter = blockbustersUI.getLetterSelection(activeTeam);
            tile = board.getTile(letter);
        }
        Question question = QuestionSheet.getInstance().getRandomQuestion(letter);
        blockbustersUI.sendQuestion(question.getQuestionText());
        Team buzzedTeam = blockbustersUI.waitForBuzzIn(whiteTeam, blueTeam);
        TileState tileState = answerQuestion(buzzedTeam, (buzzedTeam.isWhiteTeam()) ? blueTeam : whiteTeam, question);
        tile.setTileState(tileState);

        TileState winState = board.getWinState();
        if (winState == TileState.UNCLAIMED)
            return takeTurn(!isWhiteTurn);
        else
            return winState;
    }

    private TileState answerQuestion(Team team, Team opposingTeam, Question question)
    {
        long answerSeconds = 10;
        long opposingAnswerSeconds = 30;
        blockbustersUI.sendAnswerResponse(team.getTeamName()+", what is your answer?\nYou've got "+answerSeconds+" seconds.");
        String answerAttempt = blockbustersUI.getAnswer(team, answerSeconds);
        if (answerAttempt == null || !question.getQuestionAnswer().equalsIgnoreCase(answerAttempt))
        {
            blockbustersUI.sendAnswerResponse("Sorry, " + team.getTeamName() + ", looks like you didn't get it.\n" + opposingTeam.getTeamName() + ", you've got " + opposingAnswerSeconds + " seconds to answer.");
            String opposingAnswerAttempt = blockbustersUI.getAnswer(opposingTeam, opposingAnswerSeconds);
            if (opposingAnswerAttempt == null || !question.getQuestionAnswer().equalsIgnoreCase(opposingAnswerAttempt))
            {
                blockbustersUI.sendAnswerResponse("Nope! That's not it either. Nobody claims this tile.");
                return TileState.UNCLAIMED;
            }
            else
            {
                blockbustersUI.sendAnswerResponse("That's it! "+opposingTeam.getTeamName()+" take the tile.");
                return (opposingTeam.isWhiteTeam()) ? TileState.WHITE : TileState.BLUE;
            }
        }
        else
        {
            blockbustersUI.sendAnswerResponse("That's it! "+team.getTeamName()+" take the tile.");
            return (team.isWhiteTeam()) ? TileState.WHITE : TileState.BLUE;
        }
    }


}
