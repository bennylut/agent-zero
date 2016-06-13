/* 
 * The MIT License
 *
 * Copyright 2016 Benny Lutati.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
