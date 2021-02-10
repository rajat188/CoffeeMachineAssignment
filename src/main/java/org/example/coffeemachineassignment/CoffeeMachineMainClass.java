package org.example.coffeemachineassignment;

import org.example.coffeemachineassignment.thread.MachineThread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoffeeMachineMainClass {

    public static void main(String args[]){
        List<String> beveragesToPrepare = new ArrayList<>(Arrays.asList("hot_tea","hot_coffee","black_tea", "green_tea"));
        CoffeeMachine coffeeMachine = null;
        //Less outlets than beverages with not enough ingredients for each beverage
        System.out.println("========Test Case 1 Started=========");

        try {
            coffeeMachine = new CoffeeMachine(3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        runTestForGivenOutletsAndBeverages(3, beveragesToPrepare, coffeeMachine);
        System.out.println("========Test Case 1 End=========");

        //Less outlets than beverages with enough ingredients for each beverage
        System.out.println("========Test Case 2 Started=========");
        try {
            coffeeMachine = new CoffeeMachine(3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        coffeeMachine.refillIngredient("hot_water", 1000);
        coffeeMachine.refillIngredient("hot_milk", 1000);
        coffeeMachine.refillIngredient("ginger_syrup", 1000);
        coffeeMachine.refillIngredient("sugar_syrup", 1000);
        coffeeMachine.refillIngredient("tea_leaves_syrup", 1000);
        runTestForGivenOutletsAndBeverages(1, beveragesToPrepare,coffeeMachine);
        System.out.println("========Test Case 2 End=========");

        //Less outlets than beverages with green syrup added  ingredients missing for one beverage
        System.out.println("========Test Case 3 Started=========");
        try {
            coffeeMachine = new CoffeeMachine(3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        coffeeMachine.refillIngredient("hot_water", 1000);
        coffeeMachine.refillIngredient("hot_milk", 1000);
        coffeeMachine.refillIngredient("ginger_syrup", 1000);
        coffeeMachine.refillIngredient("sugar_syrup", 1000);
        coffeeMachine.refillIngredient("tea_leaves_syrup", 1000);

        //added green mixture
        coffeeMachine.refillIngredient("green_mixture", 500);
        runTestForGivenOutletsAndBeverages(1, beveragesToPrepare,coffeeMachine);
        System.out.println("========Test Case 3 End=========");

    }

    private static void runTestForGivenOutletsAndBeverages(int n, List<String> beveragesToPrepare, CoffeeMachine coffeeMachine){

        //Making executor service equal to outlets, so n beverages can be processsed in parallel
        ExecutorService executorService = Executors.newFixedThreadPool(n);

        for(int i=0;i<beveragesToPrepare.size();i++){
            MachineThread machineThread = new MachineThread(beveragesToPrepare.get(i), coffeeMachine);
            executorService.execute(machineThread);
        }

        executorService.shutdown();
        while(!executorService.isTerminated()){}
    }




}
