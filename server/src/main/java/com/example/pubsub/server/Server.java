package com.example.pubsub.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private ServerSocket serverSocket;

    public void stop() throws IOException {
        serverSocket.close();
    }

    public void start(int port) throws IOException {
        HashMap<String, List<Socket>> pubsubChannel = new HashMap<>();
        serverSocket = new ServerSocket(port);
        while (true)
            new EchoClientHandler(serverSocket.accept(),pubsubChannel).start();
    }

    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private HashMap<String, List<Socket>> pubsubChannels;


        public EchoClientHandler(Socket socket, HashMap<String , List<Socket>> pubsubChannels) {
            this.clientSocket = socket;
            this.pubsubChannels = pubsubChannels;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                String clientName = clientSocket.getRemoteSocketAddress().toString();
                System.out.println(clientName +" is connected right now !!!");
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    //Write code for handling subscribe and various other commands!!!

                    if(inputLine.length() > Constants.SUBSRIBE_LENGTH &&
                            inputLine.substring(0,Constants.SUBSRIBE_LENGTH).toLowerCase(Locale.ROOT).equals(Constants.SUBSCRIBE)){
                            //This means Subscribe. If the topic does not exits, create one, else
                            String channelName = inputLine.substring(Constants.SUBSRIBE_LENGTH+1,inputLine.length());
                            if(pubsubChannels.containsKey(channelName)){
                                List<Socket> clientsList = pubsubChannels.get(channelName);
                                //How would you name this client ?
                                if(clientsList.contains(clientSocket)){
                                    continue;
                                } else {
                                    clientsList.add(clientSocket);
                                }
                            }else {
                                List<Socket> clientsList = new ArrayList<>();
                                clientsList.add(clientSocket);
                                pubsubChannels.put(channelName,clientsList);
                            }
                            out.println("You have been subscribed to the channel \""+channelName+"\"");
                            System.out.println(clientName + " have been subscribed to the channel \""+channelName+"\"");

                    }



                    else if(inputLine.length() > Constants.PUBLISH_LENGTH &&
                            inputLine.substring(0,Constants.PUBLISH_LENGTH).toLowerCase(Locale.ROOT).equals(Constants.PUBLISH)) {
                        String[] publishCommand = inputLine.split(" ");
                        String channelName = publishCommand[1];
                        String message = publishCommand[2];
                        List<Socket> clientList = pubsubChannels.get(channelName);

                        for (Socket client : clientList){
                            PrintWriter clientOutput = new PrintWriter(client.getOutputStream(), true);
                            clientOutput.println(message);
                            out.println(clientSocket.getRemoteSocketAddress().toString()+" has been notified");
                               //Send them all a message since both have sockets connected with them !!!!
                        }

                    }


                    else if (".".equals(inputLine)) {
                        out.println("bye");
                        break;
                    }

                    else {
                        out.println("Could not understand the message");
                    }
                }

                    in.close();
                    out.close();
                    clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws IOException {
        Server server=new Server();
        server.start(8888);
    }
}
