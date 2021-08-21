package com.zazsona.blockbusters.game;

import com.zazsona.blockbusters.ai.AIDifficulty;
import com.zazsona.blockbusters.ai.AIPlayer;
import com.zazsona.blockbusters.game.objects.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Instant;
import java.util.Random;

public class GameMaster
{
    private Team blueTeam;
    private Team whiteTeam;
    private Board board;
    private boolean isStarted;
    private BlockbustersUI blockbustersUI;
    private QuestionSheet questionSheet;
    private AIPlayer aiPlayer;

    public GameMaster(BlockbustersUI blockbustersUI, Team whiteTeam, Team blueTeam) throws IOException
    {
        this.blueTeam = blueTeam;
        this.whiteTeam = whiteTeam;
        this.board = new Board();
        this.isStarted = false;
        this.blockbustersUI = blockbustersUI;
        this.questionSheet = new QuestionSheet();
    }

    public GameMaster(BlockbustersUI blockbustersUI, Team whiteTeam, Team blueTeam, @Nullable AIDifficulty difficulty) throws IOException
    {
        this.blueTeam = blueTeam;
        this.whiteTeam = whiteTeam;
        this.board = new Board();
        this.isStarted = false;
        this.blockbustersUI = blockbustersUI;
        this.questionSheet = new QuestionSheet();
        if (difficulty != null)
            this.aiPlayer = new AIPlayer(board, (whiteTeam.isAITeam()) ? whiteTeam : blueTeam, difficulty);
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

    public Team run() throws IOException, BlockbustersQuitException
    {
        try
        {
            if (!isStarted)
            {
                isStarted = true;
                blockbustersUI.listenForQuits(whiteTeam, blueTeam);
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
        catch (BlockbustersQuitException e)
        {
            blockbustersUI.dispose();
            board.getBoardRenderer().deleteBoardImageFile();
            isStarted = false;
            throw e;
        }
    }

    private TileState takeTurn(boolean isWhiteTurn) throws IOException, BlockbustersQuitException
    {
        board.getBoardRenderer().render();
        blockbustersUI.sendBoard(board.getBoardRenderer().getBoardImageFile());

        String letter = null;
        Tile tile = null;
        Team activeTeam = (isWhiteTurn) ? whiteTeam : blueTeam;
        if (activeTeam.isAITeam() && aiPlayer != null)
        {

            long startingMs = Instant.now().toEpochMilli();
            tile = aiPlayer.getTile();
            letter = tile.getTileChar();
            long calculationTime = Instant.now().toEpochMilli()-startingMs;
            if (calculationTime < 1000)
                try {Thread.sleep(1000-calculationTime);} catch (InterruptedException e) {}; //2s wait so the player has time to prepare for the next question.
            blockbustersUI.sendAIMessage("I'll take "+letter+"!");
            try {Thread.sleep(500);} catch (InterruptedException e) {};

        }
        else
        {
            blockbustersUI.sendAnswerResponse(activeTeam.getTeamName()+", pick a letter!");
            while (!board.isLetterOnBoard(letter) || (tile != null && tile.getTileState() != TileState.UNCLAIMED))
            {
                letter = blockbustersUI.getLetterSelection(activeTeam);
                tile = board.getTile(letter);
            }
        }
        Question question = questionSheet.getRandomQuestion(letter);
        blockbustersUI.sendQuestion(question.getQuestionText());
        Team buzzedTeam = blockbustersUI.waitForBuzzIn(whiteTeam, blueTeam, aiPlayer);
        TileState tileState = answerQuestion(buzzedTeam, (buzzedTeam.isWhiteTeam()) ? blueTeam : whiteTeam, question);
        tile.setTileState(tileState);

        TileState winState = board.getWinner();
        if (winState == TileState.UNCLAIMED)
            return takeTurn(!isWhiteTurn);
        else
            return winState;
    }

    private TileState answerQuestion(Team team, Team opposingTeam, Question question) throws BlockbustersQuitException
    {
        long answerSeconds = 10;
        long opposingAnswerSeconds = 30;
        blockbustersUI.sendAnswerResponse(team.getTeamName()+", what is your answer?\nYou've got "+answerSeconds+" seconds.");
        String answerAttempt = (team.isAITeam()) ? aiPlayer.getQuestionAnswer(blockbustersUI, question) : blockbustersUI.getAnswer(team, answerSeconds);
        if (answerAttempt == null || !question.isAnswerCorrect(answerAttempt))
        {
            blockbustersUI.sendAnswerResponse("Sorry, " + team.getTeamName() + ", looks like you didn't get it.\n" + opposingTeam.getTeamName() + ", you've got " + opposingAnswerSeconds + " seconds to answer.");
            String opposingAnswerAttempt = (opposingTeam.isAITeam()) ? aiPlayer.getQuestionAnswer(blockbustersUI, question) : blockbustersUI.getAnswer(opposingTeam, answerSeconds);
            if (opposingAnswerAttempt == null || !question.isAnswerCorrect(opposingAnswerAttempt))
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
