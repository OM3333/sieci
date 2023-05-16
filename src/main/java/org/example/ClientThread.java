package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket socket;
    private Server server;
    private PrintWriter writer;

    private String login;

    public ClientThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void login(){

    }

    @Override
    public void run() {
        try {
            InputStream input  = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            this.writer = new PrintWriter(output, true);

            System.out.println("New client!");
            String message;
            while ((message = reader.readLine()) != null) {
                Message messageIn = Message.fromJson(message);
                server.broadCastMessage(new Message("message",messageIn.text), this);
                //writer.flush();
            }
            System.out.println("client disconnected");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMessage(Message message){
        try {
            writer.println(message.toJson());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}