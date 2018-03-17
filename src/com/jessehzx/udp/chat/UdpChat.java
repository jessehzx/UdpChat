package com.jessehzx.udp.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class UdpSender implements Runnable {

    private DatagramSocket ds;

    public UdpSender(DatagramSocket ds) {
        this.ds = ds;
    }

    @Override
    public void run() {
        try {
            BufferedReader buffr = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            while ((line = buffr.readLine()) != null) {
                byte[] buf = line.getBytes();
                DatagramPacket dp = new DatagramPacket(buf, buf.length, InetAddress.getByName("192.168.0.255"), 10001);

                ds.send(dp);

                if ("bye".equals(line)) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("send service fail...");
        }
    }

}

class UdpReceiver implements Runnable {

    private DatagramSocket ds;

    public UdpReceiver(DatagramSocket ds) {
        this.ds = ds;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);

                ds.receive(dp);

                String ip = dp.getAddress().getHostAddress();
                String data = new String(dp.getData(), 0, dp.getLength());
                System.out.println(ip + "::" + data);
            }
        } catch (IOException e) {
            throw new RuntimeException("receive service fail...");
        }
    }

}

public class UdpChat {
    public static void main(String[] args) {
        DatagramSocket sendSocket = null;
        DatagramSocket receiveSocket = null;
        try {
            sendSocket = new DatagramSocket();
            receiveSocket = new DatagramSocket(10001);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        new Thread(new UdpSender(sendSocket)).start();
        new Thread(new UdpReceiver(receiveSocket)).start();
    }
}

