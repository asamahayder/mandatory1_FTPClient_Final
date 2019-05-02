package com.company;
import java.io.*;

//this is a program that retrieves two files from two different dirctories in a FTP server.
//The default save directory is the desktop
//The program also uploads a file to the server


public class Main {

    public static void main(String[] args) {
        try {
            //ESTABLISHING CONNECTION AND LOGIN
            FTPClient client = new FTPClient("ftp.cs.brown.edu","anonymous","password");

            //DOWNLOADING FILES TO DESKTOP
            client.retrieveFile("RETR pub/gp/readme.txt","file1.txt");
            client.retrieveFile("RETR pub/alt.quotations/README","file2.txt"); //the second parameter

            //READING FILES
            client.readFile(System.getProperty("user.home") + "/Desktop/file1.txt");
            client.readFile(System.getProperty("user.home") + "/Desktop/file2.txt");

            //UPLOADING
            String message = "This is a testfile for a FTPClient";
            client.uploadTextFile("STOR incoming/testfilnummer1.txt", message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
