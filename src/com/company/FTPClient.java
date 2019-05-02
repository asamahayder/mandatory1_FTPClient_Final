package com.company;

import java.io.*;
import java.net.*;
import java.util.*;

public class FTPClient
{
    private Socket                          clientSocket                    = null;
    private PrintStream                     output                          = null;
    private BufferedReader                  input                           = null;
    private InputStreamReader               isr                             = null;

    public FTPClient(String hostname, String username, String password) {
        try {
            getAccess(hostname,username,password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String replyFromServer() throws IOException {
        while (true) {
            String                          reply                           = input.readLine();
            System.out.println("Server: " + reply);
            if (reply.length() >= 3 && reply.charAt(3) != '-' && Character.isDigit(reply.charAt(0))
                    && Character.isDigit(reply.charAt(1)) && Character.isDigit(reply.charAt(2)))
                return reply;
        }
    }

    private Socket getDataConnection() throws IOException {
        String                              newSocketPortConnection         = commandToServer("PASV");
        StringTokenizer                     stringToken                     = new StringTokenizer(newSocketPortConnection, "(,)");
        if (stringToken.countTokens() < 7) throw new IOException("Not logged in...");
        stringToken.nextToken();
        stringToken.nextToken();
        stringToken.nextToken();
        stringToken.nextToken();
        stringToken.nextToken();
        int portNumber = 256*Integer.parseInt(stringToken.nextToken())+ Integer.parseInt(stringToken.nextToken());
        Socket                              socket                          = new Socket(clientSocket.getInetAddress(),portNumber);
        return socket;
    }

    public String commandToServer(String commandLine) throws IOException {
        System.out.println("Client: " + commandLine);
        output.println(commandLine);
        output.flush();
        return replyFromServer();
    }

    public void getAccess(String hostname, String username, String password)throws IOException {
        clientSocket                                                        = new Socket(hostname,21);
        output                                                              = new PrintStream(clientSocket.getOutputStream());
        isr                                                                 = new InputStreamReader(clientSocket.getInputStream());
        input                                                               = new BufferedReader(isr);
        replyFromServer();
        commandToServer("USER " + username);
        commandToServer("PASS " + password);
    }

    public String receiveText(String commandLine) throws IOException {
        Socket                              dataConnection                  = getDataConnection();
        InputStream                         inputS                          = dataConnection.getInputStream();
        InputStreamReader                   inputSR                         = new InputStreamReader(inputS);
        BufferedReader                      buffR                           = new BufferedReader(inputSR);
        commandToServer(commandLine);
        StringBuilder                       stringBuilder                   = new StringBuilder();
        String                              nextString                      = buffR.readLine();

        while (nextString != null) {
            stringBuilder.append(nextString+"\n");
            nextString = buffR.readLine();
        }

        buffR.close();
        dataConnection.close();
        replyFromServer();
        return stringBuilder.toString();
    }

    public void uploadTextFile (String commandLine, String textLines) throws IOException
    {
        Socket                              dataConnection                  = getDataConnection();
        OutputStream                        outputS                         = dataConnection.getOutputStream();
        PrintStream                         dataOutPut                      = new PrintStream(outputS);
        commandToServer(commandLine);
        dataOutPut.print(textLines);
        dataOutPut.close();
        dataConnection.close();
        replyFromServer();
    }

    public void retrieveFile (String command, String fileName){
        try {
            String                          file                            = receiveText(command);
            try (PrintWriter printWriter = new PrintWriter(System.getProperty("user.home") + "/Desktop/"+fileName)) {
                printWriter.println(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile(String path){
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            StringBuilder                   text                            = new StringBuilder();
            String                          line;
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
                text.append(System.lineSeparator());
            }

            String                          data                            = text.toString();
            if (data.length() >= 1024) {
                data = data.substring(0, 1024);
            }
            System.out.println("###########################################################################################");
            System.out.println("#####################################Start#################################################");
            System.out.println("###########################################################################################");
            System.out.println(data);
            System.out.println("###########################################################################################");
            System.out.println("#######################################End#################################################");
            System.out.println("###########################################################################################");
            System.out.println(" ");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
