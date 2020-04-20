package com.websocket;

import android.hardware.Camera;
import android.os.Vibrator;
import android.util.Log;


import com.zxing.cameraapplication.CameraApplication;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Created by Roc on 2018/10/9.
 */
public class ServerSocket extends WebSocketServer {

    public static ServerManager _serverManager;
    private Vibrator vibrator;

    public ServerSocket(ServerManager serverManager,int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        _serverManager=serverManager;
    }




    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.i("TAG","Some one Connected...");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        _serverManager.UserLeave(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.i("TAG","OnMessage:"+message.toString());

        if (message.equals("1")) {
            _serverManager.SendMessageToUser(conn, "What?");
            _serverManager.takePhoto();

        }
        if(message.equals("open")){
            try{
                Camera m_Camera = Camera.open();
                Camera.Parameters mParameters;
                mParameters = m_Camera.getParameters();
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                m_Camera.setParameters(mParameters);
                vibrator = (Vibrator)_serverManager.activity.getSystemService(_serverManager.activity.VIBRATOR_SERVICE);
                long[] patter = {0, 1000, 0, 1000};
                vibrator.vibrate(patter, 0);
            } catch(Exception ex){}
        }
        if(message.equals("close")){
            try{
                Camera.Parameters mParameters;
                Camera m_Camera = Camera.open();
                mParameters = m_Camera.getParameters();
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                m_Camera.setParameters(mParameters);
                m_Camera.release();
                vibrator.cancel();
            } catch(Exception ex){}
        }

        String[] result=message.split(":");
        if (result.length==2) {
            if (result[0].equals("user")) {
                _serverManager.UserLogin(result[1], conn);
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.i("TAG","Socket Exception:"+ex.toString());
    }

    @Override
    public void onStart() {

    }
}
