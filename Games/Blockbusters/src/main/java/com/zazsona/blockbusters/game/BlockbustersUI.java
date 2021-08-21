package com.zazsona.blockbusters.game;

import com.zazsona.blockbusters.ai.AIPlayer;
import com.zazsona.blockbusters.game.objects.Team;

import java.io.File;

public interface BlockbustersUI
{
    void sendBoard(File boardImageFile);
    void sendQuestion(String question);
    void sendAnswerResponse(String response);
    void sendAIMessage(String message);
    void sendWinMessage(String message);
    void dispose();

    Team waitForBuzzIn(Team whiteTeam, Team blueTeam, AIPlayer aiPlayer) throws BlockbustersQuitException;
    String getAnswer(Team answeringTeam, long secondsToAnswer) throws BlockbustersQuitException;
    String getLetterSelection(Team answeringTeam) throws BlockbustersQuitException;

    void listenForQuits(Team whiteTeam, Team blueTeam);
}
