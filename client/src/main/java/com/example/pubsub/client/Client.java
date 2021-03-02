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

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        //Reading from the terminal
        terminalReader = new BufferedReader(new InputStreamReader(System.in));
        terminalWriter = new PrintWriter(System.out);

        String terminalInput;
        while ((terminalInput = terminalReader.readLine()) != null) {
             System.out.println(sendMessage(terminalInput));
             if(terminalInput.equals("bye")){
                 stopConnection();
                 break;
             }
        }


        terminalWriter.println("Method 2");
        terminalWriter.flush();
        terminalWriter.close();



    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
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
