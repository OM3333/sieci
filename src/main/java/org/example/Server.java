package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientThread> clients = new ArrayList<>();

    public Server(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUser() throws IOException {
        Socket socket = serverSocket.accept();
        ClientThread thread = new ClientThread(socket, this);
        clients.add(thread);
        thread.start();
    }

    public void listen() throws IOException {
        while(true) {
            addUser();
            addUser();


        }
    }


    public ClientThread getClient(String login){
        for(ClientThread client : clients){
            if(client.getLogin().equals(login)){
                return client;
            }
        }
        return null;
    }
    public void broadCastMessage(Message message, ClientThread sender){
        for(ClientThread client : clients){
            if(!client.equals(sender)){
                client.sendMessage(message);
            }
        }
    }
}