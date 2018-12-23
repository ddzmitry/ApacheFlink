package com.ddzmitry.flink.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Random;

public class DataServerForValueStateExample
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket listener = new ServerSocket(9090);
        try{
            Socket socket = listener.accept();
            System.out.println("Got new connection: " + socket.toString());

            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\ddzmi\\Desktop\\Tutoring Trilogy\\ApacheFlink\\LearnApacheFlink\\src\\datasets\\avg"));

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Random rand = new Random();
                int count = 0;
                int tenSum = 0;
                Date d = new Date();

                for (int i = 0; i < 50; i++) {

                    int key = (i % 2) + 1;
                    String s = key + "," + i;
                    System.out.println(s);
                    out.println(s);
                    Thread.sleep(50);


                }

            } finally{
                socket.close();
            }

        } catch(Exception e ){
            e.printStackTrace();
        } finally{

            listener.close();
        }
    }
}
