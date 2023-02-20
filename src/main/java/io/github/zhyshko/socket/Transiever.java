package io.github.zhyshko.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import io.github.zhyshko.dto.Application;

public class Transiever {

    private int port = 5001;
    private ServerSocket serverSocket;

    public Transiever() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                System.out.println("Global transiever thread: "+Thread.currentThread().getId());
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(100);
                ClientTransiever clientTransiever = new ClientTransiever(clientSocket);
                Thread thread = new Thread(clientTransiever);
                System.out.println("Created thread: "+thread.getId());
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
