package com.zazsona.jaramobileapp;

import com.google.gson.Gson;
import com.zazsona.jara.module.ModuleLoad;
import com.zazsona.jaramobileapp.requests.Request;
import com.zazsona.jaramobileapp.requests.RequestType;
import com.zazsona.jaramobileapp.responses.ReportResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AppLoad extends ModuleLoad
{

    @Override
    public void load()
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket(42995);
            while (true)
            {
                try
                {
                    Socket connectedSocket = serverSocket.accept();
                    ObjectOutputStream oos = new ObjectOutputStream(connectedSocket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(connectedSocket.getInputStream());
                    String json = (String) ois.readObject();
                    Request request = new Gson().fromJson(json, Request.class);
                    if (request.getRequestType() == RequestType.REPORT)
                    {
                        ReportResponse rr = new ReportResponse();
                        oos.writeObject(new Gson().toJson(rr));
                        oos.flush();
                    }
                    connectedSocket.close();
                }
                catch (IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }
}
