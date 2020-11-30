package FiSy;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDPSearchServer {
    public static boolean SearchServer(int port, TableView tableView, ProgressBar progressBar){
        DatagramSocket dsocket = null;
        AtomicBoolean flag = new AtomicBoolean();
        flag.set(false);
        try {
            dsocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            Random r = new Random();
            port = 49152 + r.nextInt(65535 - 49152 + 1);
            SearchServer(port, tableView, progressBar);
            return false;
        }
        DatagramSocket finalDsocket1 = dsocket;
        new Thread(() -> {
                String stringport;
                try {

                    Thread.sleep(1000);
                    byte[] buffer = new byte[2048];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    finalDsocket1.setSoTimeout(60000);
                    while (true) {
                        System.out.println("Receiving...");
                        finalDsocket1.receive(packet);
                        flag.set(true);
                        String msg = new String(buffer, 0, packet.getLength());
                        System.out.println(packet.getAddress().getHostName()
                                + ": " + msg);
                        stringport = msg.replace("FiSy Server:", "");
                        msg = msg.replace(stringport, "");
                        if (msg.equals("FiSy Server:"))
                            ServerList.toTableView(tableView, stringport, packet.getAddress().getHostAddress());
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }).start();

        DatagramSocket finalDsocket = dsocket;
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        int finalPort = port;
        Task<Void> sendi = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try {
                    Thread.sleep(2000);
                    byte[] message = ("FiSy Client:" + finalPort).getBytes();
                    InetAddress address = InetAddress.getByName("255.255.255.255");
                    DatagramSocket dasocket = new DatagramSocket();
                    //dasocket.setBroadcast(true);
                    for (int i = 1; i < 65536; i++) {
                        if (flag.get()) {
                            Thread.sleep(10);
                            flag.set(false);
                        }
                        DatagramPacket packet = new DatagramPacket(message, message.length,
                                address, i);
                        dasocket.send(packet);
                        updateProgress(i, 65536);
                        threadPause(50000);
                        // Thread.sleep(0,1);
                        // System.out.println(i);
                    }
                    dasocket.close();
                } catch (Exception e) {
                    System.err.println(e);
                }
                try {
                    Thread.sleep(1000);
                    finalDsocket.close();
                    //ServerList.chooseOneOption(observableList, pane, c);

                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            if (ServerList.data.size()==1&&ServerList.autotologin){
                            Login.serverAP[0]=ServerList.data.get(0).getAddress();
                            Login.serverAP[1]=ServerList.data.get(0).getPort();
                            animation.toogleNode((Button)((BorderPane)(mainfx.mainpane.getChildren().get(0))).getChildren().get(0),1);
                            animation.SlideLeft(getClass(),mainfx.mainpane,"Login.fxml");
                        } else {
                                ServerList.resButtonEnabled=true;
                                animation.toogleNode(ServerList.resButton,1);
                            }}});

                        } catch (Exception e) {
                    System.err.println(e);
                }
                executorService.shutdownNow();
                return null;
            }
        };
        //Executors.newCachedThreadPool().submit(sendi);
        executorService.submit(sendi);
        progressBar.progressProperty().bind(sendi.progressProperty());
        return true;
    }

    private static void threadPause(int nanosec){
        long stopPause = System.nanoTime();
        long start = stopPause+nanosec;
        while(start>stopPause){
            stopPause = System.nanoTime();
        }
    }
}