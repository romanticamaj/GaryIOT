package com.example.romanticamaj.garyiot;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class IotConnection {
    private static final String TAG = "IotConnection";

    private Handler mSentMsgHandler;
    private IotClient mClient;
    private Socket mSocket;

    public IotConnection(Handler handler) {
        mSentMsgHandler = handler;
    }

    public void tearDown() {
        mClient.tearDown();
    }

    public void connectToServer(InetAddress address, int port) {
        mClient = new IotClient(address, port);
    }

    public void sendMessage(String msg) {
        if (mClient != null) {
            mClient.sendMessage(msg);
        }
    }

    public synchronized void handleSentMsg(String msg) {
        Log.e(TAG, "Sent message: " + msg);

        Message message = new Message();
        Bundle messageBundle = new Bundle();

        messageBundle.putString("msg", msg);
        message.setData(messageBundle);

        mSentMsgHandler.sendMessage(message);
    }

    private void setSocket(Socket socket) {
        Log.d(TAG, "setSocket being called.");

        if (socket == null) {
            Log.d(TAG, "Setting a null socket.");
        }

        if (mSocket != null) {
            if (mSocket.isConnected()) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        mSocket = socket;
    }

    private Socket getSocket() {
        return mSocket;
    }

    private class IotClient {
        private static final String CLIENT_TAG = "IotClient";

        private InetAddress mAddress;
        private int mPort;
        private Thread mPrepareThread;

        public IotClient(InetAddress address, int port) {
            Log.d(CLIENT_TAG, String.format("Creating IotClient with address=[%s] port=[%s]",
                    address.getAddress(), port));

            mAddress = address;
            mPort = port;

            mPrepareThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    prepareSocket();
                }

                private void prepareSocket() {
                    try {
                        if (getSocket() == null) {
                            setSocket(new Socket(mAddress, mPort));
                            Log.d(CLIENT_TAG, "Client-side socket initialized.");
                        } else {
                            Log.d(CLIENT_TAG, "Socket already initialized. skipping!");
                        }
                    } catch (UnknownHostException e) {
                        Log.d(CLIENT_TAG, "Initializing socket failed, UHE", e);
                    } catch (IOException e) {
                        Log.d(CLIENT_TAG, "Initializing socket failed, IOE.", e);
                    }
                }
            });

            mPrepareThread.start();
        }

        public void tearDown() {
            mPrepareThread.interrupt();

            try {
                getSocket().close();
            } catch (IOException ioe) {
                Log.e(CLIENT_TAG, "Error when closing server socket.");
            }
        }

        public void sendMessage(String msg) {
            Log.d(CLIENT_TAG, "Ready to send message " + msg);

            try {
                Socket socket = getSocket();

                if (socket == null) {
                    Log.d(CLIENT_TAG, "Socket is null.");
                } else if (socket.getOutputStream() == null) {
                    Log.d(CLIENT_TAG, "Socket output stream is null.");
                }

                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(getSocket().getOutputStream())), true);
                out.println(msg);
                out.flush();

                handleSentMsg(msg);
            } catch (UnknownHostException e) {
                Log.d(CLIENT_TAG, "Unknown Host", e);
            } catch (IOException e) {
                Log.d(CLIENT_TAG, "I/O Exception", e);
            } catch (Exception e) {
                Log.d(CLIENT_TAG, "Error3", e);
            }
            Log.d(CLIENT_TAG, "Client sent message: " + msg);
        }
    }
}
