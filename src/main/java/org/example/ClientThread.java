package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ClientThread extends Thread {
    private Socket socket;
    private Server server;
    private PrintWriter writer;

    public String getLogin() {
        return login;
    }

    private String login;

    public ClientThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void parseMessage(Message message){
        if(message.type.equals("login")){
            this.login = message.text;
            System.out.println(this.login+" joined");
            server.broadCastMessage(new Message("message","New user: "+this.login),this);
        }
        else {
            if(message.text.substring(0,2).equals("/w")){
                String[] fields = message.text.split(" ");
                ClientThread recipient = server.getClient(fields[1]);
                if(recipient != null){
                    recipient.sendMessage(new Message("message", Arrays.stream(fields).skip(2).toString()));
                }
            }
            server.broadCastMessage(new Message("message",login+": "+message.text), this);
        }
    }

    @Override
    public void run() {
        try {
            InputStream input  = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            this.writer = new PrintWriter(output, true);


            String message;
            while ((message = reader.readLine()) != null) {
                Message messageIn = Message.fromJson(message);
                parseMessage(messageIn);

            }
            server.broadCastMessage(new Message("message",String.format("%s left",this.login)),this);
            System.out.println(String.format("Client %s disconnected",this.login));
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