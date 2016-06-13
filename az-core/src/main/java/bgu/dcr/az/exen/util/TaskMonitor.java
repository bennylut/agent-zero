/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class TaskMonitor<T> {

    private T monitored;
    private String state;
    private int progress;
    private int maxProgress;
    private boolean running;
    private boolean started;
    private Multimap<String, TaskMonitor> children;
    private List<TaskMonitorListener<T>> listeners;

    public TaskMonitor(T monitored) {
        this.monitored = monitored;
        this.state = "";
        this.progress = 0;
        this.maxProgress = 0;
        this.running = false;
        this.started = false;
        this.children = LinkedListMultimap.create();
        this.listeners = new LinkedList<TaskMonitorListener<T>>();
    }

    public T getMonitored() {
        return monitored;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        for (TaskMonitorListener l : listeners) l.onStateChanged(this);
    }

    public List<TaskMonitor> getChildren(String name) {
        return ImmutableList.copyOf(children.get(name));
    }
    
    public TaskMonitor newChild(String name){
        TaskMonitor child = new TaskMonitor(name);
        children.put(name, child);
        for (TaskMonitorListener l : listeners) l.onNewChild(this, child);
        return child;
    }
    
    public void addListener(TaskMonitorListener<T> l){
        this.listeners.add(l);
    }
    
    public void removeListener(TaskMonitorListener<T> l){
        this.listeners.remove(l);
    }

    public int getMaxProgress() {
        return maxProgress;
    }
    
    public void addWork(int work){
        maxProgress += work;
        for (TaskMonitorListener l : listeners) l.onWorkAdded(this);
    }

    public int getProgress() {
        return progress;
    }
    
    public void incProgress(int amount){
        progress += amount;
        if (progress > maxProgress) progress = maxProgress;
        
        for (TaskMonitorListener l : listeners) l.onProgressChanged(this);
    }
    
    public void setTaskStarted(){
        started = true;
        running = true;
        
        for (TaskMonitorListener l : listeners) l.onStarted(this);
    }
    
    public boolean isTaskRunning(){
        return running;
    }
    
    public boolean isTaskStarted(){
        return started;
    }
    
    public void setTaskEnded(){
        this.running = false;
        for (TaskMonitorListener l : listeners) l.onTerminated(this);
    }
    public static interface TaskMonitorListener<T> {

        void onProgressChanged(TaskMonitor<T> source);

        void onWorkAdded(TaskMonitor<T> source);

        void onWorkProgress(TaskMonitor<T> source);

        void onStarted(TaskMonitor<T> source);

        void onTerminated(TaskMonitor<T> source);

        void onNewChild(TaskMonitor<T> source, TaskMonitor child);
        
        void onStateChanged(TaskMonitor<T> source);
    }

    public static abstract class TaskMonitorHandler<T> implements TaskMonitorListener<T> {

        @Override
        public void onProgressChanged(TaskMonitor<T> source) {
        }

        @Override
        public void onWorkAdded(TaskMonitor<T> source) {
        }

        @Override
        public void onWorkProgress(TaskMonitor<T> source) {
        }

        @Override
        public void onStarted(TaskMonitor<T> source) {
        }

        @Override
        public void onTerminated(TaskMonitor<T> source) {
        }

        @Override
        public void onNewChild(TaskMonitor<T> source, TaskMonitor child) {
        }

        @Override
        public void onStateChanged(TaskMonitor<T> source) {
        }
    }
}
