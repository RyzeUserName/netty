package com.lft.bio.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        writer.println("第一横");
        writer.println("第二横");
        writer.println("第三横");
        writer.flush();
        String request;
        while ((request = reader.readLine()) != null) {
            System.out.println("处理完的结果" + request);
        }
        if (writer != null) {
            writer.close();
        }

        if (writer != null) {
            writer.close();
        }
        if (socket != null) {
            socket.close();
        }
    }
}
