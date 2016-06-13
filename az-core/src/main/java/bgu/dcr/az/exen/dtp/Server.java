/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.dtp;

import bgu.dcr.az.exen.util.TaskMonitor;
import bgu.dcr.az.exen.util.TaskMonitor.TaskMonitorHandler;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class Server implements Runnable{
    
    ConcurrentLinkedQueue<Client> connected = new ConcurrentLinkedQueue<Client>();
    LinkedList<ServerListener> listeners = new LinkedList<ServerListener>();
    
    int port = 7000;

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void addListener(ServerListener listener){
        this.listeners.add(listener);
    }
    
    public void removeListener(ServerListener listener){
        this.listeners.remove(listener);
    }
    
    public ImmutableList<Client> connectedClients(){
        return ImmutableList.copyOf(connected);
    }
    
    @Override
    public void run() {
        Socket s;
        
        try {
            ServerSocket ss = new ServerSocket(port);
            while (!Thread.currentThread().isInterrupted()){
                s = ss.accept();
                final Client c = Client.start(s);
                connected.add(c);
                c.getMonitor().addListener(new TaskMonitorHandler<Client>() {

                    @Override
                    public void onTerminated(TaskMonitor<Client> source) {
                        connected.remove(source.getMonitored());
                    }
                });
                
                for (ServerListener l : listeners) l.onClientConnected(this, c);
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static interface ServerListener{
        void onClientConnected(Server server, Client client);
    }
    
    public static void main(String[] args){
        Server srv = new Server();
        srv.addListener(new ServerListener() {

            @Override
            public void onClientConnected(Server server, Client client) {
                client.addHandler("*", new Handler() {

                    @Override
                    public void handle(Client c, AzDTPMessage message) {
                        System.out.println("got message: " + message.toString());
                    }
                });
            }
        });
        
        srv.run();
    }
    
}
