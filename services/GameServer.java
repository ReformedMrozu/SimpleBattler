package com.company.main.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ServerError;

public class GameServer {
    private ServerSocket serverSocket; //socket
    private ServerSideCon player1; // inner classes for handling player connections
    private ServerSideCon player2;
    private int playerId = 0;

    //game variables
    private int connectedPlayers;
    public GameServer(){
        System.out.println("---running server---");
        connectedPlayers = 0;

        try{
            serverSocket = new ServerSocket(6968); // we will use port 69686 for game
        }catch(IOException e){
            System.out.println("From Server constructor :\n" + e);
        }
    }
    // connections handling
    public void acceptConnections(){
        try{
            while(connectedPlayers < 3) {
                Socket soc = serverSocket.accept();
                ServerSideCon ssc = new ServerSideCon(soc);
                ++connectedPlayers;
                System.out.println("I'm player no : " + connectedPlayers);

                if (connectedPlayers == 1) {
                    player1 = ssc;
                } else {
                    player2 = ssc;
                    System.out.println("Both players connected");
                }
                Thread th = new Thread(ssc); // Initialize new thread handling Server Side Connections
                th.start();
            }
        } catch(IOException e) {
            System.out.println("Exception form AcceptConnections : " + e);
        }
    }

    private class ServerSideCon implements Runnable{
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ServerSideCon(Socket socketIn){
            socket = socketIn;
            try{
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch(IOException e) {
                System.out.println("Exception in SSC constructor : " + e);
            }
        }

        public void run(){
            try{
                ++playerId;
                //dataOut.writeChars("Hello there general kenobi");
                System.out.println("sending player id to client");
                dataOut.writeInt(playerId);

                dataOut.flush();
                //System.out.println("xd");
            } catch(IOException e){
                System.out.println("Exception in SSC run(f) : " + e);
            }
        }
    }

    public static void main(String[] args){
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}