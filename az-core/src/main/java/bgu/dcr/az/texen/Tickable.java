/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.texen;

/**
 *
 * @author User
 */
public interface Tickable {

    int getId();
    
    boolean isTerminated();

    boolean needTicking();

    void tick();
}
