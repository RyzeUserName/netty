package com.lft.bio.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);
        Socket accept = serverSocket.accept();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
        PrintWriter printWriter = new PrintWriter(accept.getOutputStream(), true);
        String request;
        while ((request = bufferedReader.readLine()) != null) {
            System.out.println("接收到" + request);
            printWriter.println("响应：" + request);
        }
        printWriter.flush();
        if (printWriter != null) {
            printWriter.close();
        }

        if (bufferedReader != null) {
            bufferedReader.close();
        }
    }
}
