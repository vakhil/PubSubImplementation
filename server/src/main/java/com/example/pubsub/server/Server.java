package com.example.pubsub.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Server {

    private ServerSocket serverSocket;

    public void stop() throws IOException {
        serverSocket.close();
    }

    public void start(int port) throws IOException {
        HashMap<String, List<String>> pubsubChannel = new HashMap<>();
        serverSocket = new ServerSocket(port);
        while (true)
            new EchoClientHandler(serverSocket.accept(),pubsubChannel).start();
    }

    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private HashMap<String, List<String>> pubsubChannels;


        public EchoClientHandler(Socket socket, HashMap<String , List<String>> pubsubChannels) {
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

                    if(inputLine.length() > Constants.SUBSRIBE_LENGTH){

                        if(inputLine.substring(0,Constants.SUBSRIBE_LENGTH).toLowerCase(Locale.ROOT).equals(Constants.SUBSCRIBE)){
                            //This means Subscribe. If the topic does not exits, create one, else
                            String channelName = inputLine.substring(Constants.SUBSRIBE_LENGTH+1,inputLine.length());
                            if(pubsubChannels.containsKey(channelName)){
                                List<String> clientsList = pubsubChannels.get(channelName);
                                //How would you name this client ?
                                if(clientsList.contains(clientName)){
                                    continue;
                                } else {
                                    clientsList.add(clientName);
                                }
                            }else {
                                List<String> clientsList = Arrays.asList(clientName);
                                pubsubChannels.put(channelName,clientsList);
                            }
                            out.println("You have been subscribed to the channel \""+channelName+"\"");
                            System.out.println("You have been subscribed to the channel \""+channelName+"\"");
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
