package com.Zazsona.Blockbusters.game;

import com.Zazsona.Blockbusters.game.objects.Team;

import java.io.File;

public interface BlockbustersUI
{
    void sendBoard(File boardImageFile);
    void sendQuestion(String question);
    void sendAnswerResponse(String response);
    void sendWinMessage(String message);
    void dispose();

    Team waitForBuzzIn(Team whiteTeam, Team blueTeam) throws BlockbustersQuitException;
    String getAnswer(Team answeringTeam, long secondsToAnswer) throws BlockbustersQuitException;
    String getLetterSelection(Team answeringTeam) throws BlockbustersQuitException;

    void listenForQuits(Team whiteTeam, Team blueTeam);
}
