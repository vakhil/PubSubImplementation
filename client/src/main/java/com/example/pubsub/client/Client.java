package com.example.pubsub.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader terminalReader;
    private PrintWriter terminalWriter;

    private static class ReceiveMessageHandler extends Thread{
        Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ReceiveMessageHandler(Socket socket) throws IOException {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        }

        @Override
        public void run()  {
            try {
                String serverReply;
                while ((serverReply = in.readLine()) != null      ) {
                    System.out.println(serverReply);
                }

            } catch (IOException ex){

            }

            }
    }

    public static class TerminalMessageHandler extends Thread {
        Socket socket;
        private PrintWriter out;
        private BufferedReader terminalReader;

        public TerminalMessageHandler(Socket socket) throws IOException {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            terminalReader = new BufferedReader(new InputStreamReader(System.in));

        }

        public void stopConnection() throws IOException {
            out.close();
            socket.close();
        }

        @Override
        public void run() {

            String terminalInput;
            //We are busy waiting for messages to come from

            try {
                while ((terminalInput = terminalReader.readLine()) != null      ) {
                    //System.out.println(sendMessage(terminalInput));
                    out.println(terminalInput);
                    if(terminalInput.equals("bye")){
                        stopConnection();
                        break;
                    }
                }
            } catch (Exception ex){

            }


        }
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        //Reading from the terminal
        terminalReader = new BufferedReader(new InputStreamReader(System.in));
        terminalWriter = new PrintWriter(System.out);

        ReceiveMessageHandler receiveMessageHandler = new ReceiveMessageHandler(clientSocket);
        receiveMessageHandler.start();

        TerminalMessageHandler terminalMessageHandler = new TerminalMessageHandler(clientSocket);
        terminalMessageHandler.start();


    }

    public void sendMessage(String msg) throws IOException {
        out.println(msg);
   //     String resp = in.readLine();
     //   return resp;
    }

    public void stopConnection() throws IOException {

        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.startConnection("127.0.0.1", 8888);

    }

}
