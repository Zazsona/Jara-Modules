package com.Zazsona.Blockbusters.game;

import com.Zazsona.Blockbusters.game.objects.Team;

import java.io.File;

public interface BlockbustersUI
{
    void sendBoard(File boardImageFile);
    void sendQuestion(String question);
    void sendAnswerResponse(String response);
    void sendWinMessage(String message);

    Team waitForBuzzIn(Team whiteTeam, Team blueTeam);
    String getAnswer(Team answeringTeam, long secondsToAnswer);
    String getLetterSelection(Team answeringTeam);
}
