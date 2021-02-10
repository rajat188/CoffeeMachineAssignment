package org.example.coffeemachineassignment.thread;

import org.example.coffeemachineassignment.CoffeeMachine;

public class MachineThread implements Runnable{

    private String beverageName;
    private CoffeeMachine coffeeMachine;
    public MachineThread(String beverageName, CoffeeMachine coffeeMachine){
        this.beverageName = beverageName;
        this.coffeeMachine = coffeeMachine;
    }

    @Override
    public void run() {
            coffeeMachine.prepareBeverage(beverageName);
    }
}
