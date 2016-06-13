/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.dtp;

import bgu.dcr.az.exen.util.BufferedByteReader;
import bgu.dcr.az.exen.util.TaskMonitor;
import bgu.dcr.az.utils.RoadBlock;
import bgu.dcr.az.exen.exp.BadConnectionStatusException;
import bgu.dcr.az.exen.exp.UncheckedIOException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class Client implements Runnable {

    private ConcurrentHashMap<String, Handler> handlers = new ConcurrentHashMap<String, Handler>();
    private String serverAddress = "localhost";
    private int serverPort = 7000;
    private OutputStream serverOut;
    private boolean connected = false;
    private RoadBlock roadBlock = new RoadBlock();
    private Socket sock = null;
    private TaskMonitor<Client> monitor;

    public Client(String host, int port) {
        this.serverAddress = host;
        this.serverPort = port;
    }

    public Client(Socket sock) {
        this.sock = sock;
    }

    public TaskMonitor<Client> getMonitor() {
        return monitor;
    }

    @Override
    public void run() {
        monitor = new TaskMonitor<Client>(this);
        monitor.setTaskStarted();

        AzDTPMessage msg;
        Handler handler;
        try {
            if (sock == null) {
                sock = new Socket(serverAddress, serverPort);
            }

            connected = true;
            serverOut = sock.getOutputStream();
            roadBlock.remove();
            BufferedByteReader bbr = new BufferedByteReader(sock.getInputStream());
            while (true) {
                try {
                    msg = AzDTPMessage.scan(bbr);
                } catch (AzDTPScanException ex) {
                    if (bbr.isStreamClosed()) {
                        System.out.println("Server disconnected!");
                    } else {
                        System.out.println("Server Error");
                        ex.printStackTrace();
                    }
                    return;
                }

                handler = handlers.get(msg.getInfoSource() + "/" + msg.getInfoType());
                if (handler != null) {
                    handler.handle(this, msg);
                }
                handler = handlers.get(msg.getInfoSource() + "/*");
                if (handler != null) {
                    handler.handle(this, msg);
                }
                handler = handlers.get("*");
                if (handler != null) {
                    handler.handle(this, msg);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (sock != null) {
                try {
                    sock.close();
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            monitor.setTaskEnded();
            roadBlock.remove();
        }
    }

    public void addHandler(String infoUrl, Handler handler) {
        this.handlers.put(infoUrl, handler);
    }

    public boolean start() {
        new Thread(this).start();
        try {
            roadBlock.pass();
            return true;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void send(AzDTPMessage msg) {
        if (!connected) {
            throw new BadConnectionStatusException("trying to send message while not connected");
        }
        try {
            serverOut.write(msg.toBytes());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static Client start(String host, int port) {
        Client c = new Client(host, port);
        c.start();
        return c;
    }

    public static Client start(Socket sock) {
        Client c = new Client(sock);
        c.start();
        return c;
    }
}
