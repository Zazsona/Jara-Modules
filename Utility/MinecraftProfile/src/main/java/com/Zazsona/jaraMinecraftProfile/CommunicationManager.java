package com.zazsona.jaraminecraftprofile;

import com.zazsona.jaraminecraftprofile.data.RequestData;
import com.zazsona.jaraminecraftprofile.data.ResponseData;
import com.zazsona.jaraminecraftprofile.data.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class CommunicationManager
{
    private static Logger logger = LoggerFactory.getLogger(CommunicationManager.class);

    public static ResponseData requestPlayerData(String host, String uuid)
    {
        try
        {
            Socket connection = new Socket();
            connection.connect(new InetSocketAddress(host, 25500), 1000);
            ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            ObjectInputStream input = new ObjectInputStream(connection.getInputStream());

            RequestData requestData = new RequestData(uuid, StatusCode.OK);
            output.writeObject(requestData);
            output.flush();

            ResponseData responseData = (ResponseData) input.readObject();
            output.close();
            input.close();
            connection.close();

            return responseData;
        }
        catch (SocketTimeoutException e)
        {
            return new ResponseData(StatusCode.SERVER_OFFLINE, null);
        }
        catch (IOException e)
        {
            logger.error(e.toString());
            return null;
        }
        catch (ClassNotFoundException e)
        {
            logger.error(e.toString());
            return null;
        }
    }
}
