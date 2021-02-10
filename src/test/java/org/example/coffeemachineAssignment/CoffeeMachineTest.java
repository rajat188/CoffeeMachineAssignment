package org.example.coffeemachineAssignment;

import org.example.coffeemachineassignment.CoffeeMachine;
import org.example.coffeemachineassignment.thread.MachineThread;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 By Default Intellij doesnt show System.out.println statement in Junit. To enable that in Run Tab switch off
 "Track Running Test". In case it doesnt work I have also added a MAin class where all tests can be run
 */



public class CoffeeMachineTest {

    //Coffee Machine having 3 Outlets and 4 beverages to prepare with insufficient ingredients
    @Test
    public void threeOutlet4BeverageInsufficientIngredients(){
        List<String> beveragesToPrepare = new ArrayList<>(Arrays.asList("hot_tea","hot_coffee","black_tea", "green_tea"));
        CoffeeMachine coffeeMachine = null;
        try {
            coffeeMachine = new CoffeeMachine(3);
            runTestForGivenOutletsAndBeverages(3, beveragesToPrepare,coffeeMachine);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Coffee Machine having 3 Outlets and 4 beverages to prepare with sufficient ingredients
    @Test
    public void threeOutlet4BeverageSufficientIngredients(){
        List<String> beveragesToPrepare = new ArrayList<>(Arrays.asList("hot_tea","hot_coffee","black_tea", "green_tea"));
        CoffeeMachine coffeeMachine = null;
        try {
            coffeeMachine = new CoffeeMachine(3);
            coffeeMachine.refillAllIngredient( 1000);
            runTestForGivenOutletsAndBeverages(3, beveragesToPrepare, coffeeMachine);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Coffee Machine having 3 Outlets and 4 beverages and adding missing green_mixture ingredients
    @Test
    public void threeOutlet4BeverageMissingIngredientsAdded(){
        List<String> beveragesToPrepare = new ArrayList<>(Arrays.asList("hot_tea","hot_coffee","black_tea", "elaichi_tea"));
        CoffeeMachine coffeeMachine = null;
        try {
            coffeeMachine = new CoffeeMachine(3);
            coffeeMachine.refillAllIngredient(1000);
            coffeeMachine.refillIngredient("green_mixture", 1000);
            runTestForGivenOutletsAndBeverages(3, beveragesToPrepare,coffeeMachine);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Coffee Machine having 3 Outlets and 4 beverages and adding missing green_mixture ingredients
    //will throw exception as more ingredients than available container
    @Test
    public void threeOutlet4BeverageWithContainerCountAndSize(){
        List<String> beveragesToPrepare = new ArrayList<>(Arrays.asList("hot_tea","hot_coffee","black_tea", "elaichi_tea"));
        CoffeeMachine coffeeMachine = null;
        try {
            coffeeMachine = new CoffeeMachine(3,10,1000);
            coffeeMachine.refillAllIngredient( 1000);
            coffeeMachine.refillIngredient("green_mixture", 1000);
            runTestForGivenOutletsAndBeverages(3, beveragesToPrepare,coffeeMachine);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Coffee Machine having 3 Outlets and 4 beverages and initializing with all parmas
    //will throw exception as more ingredients than available container
    @Test
    public void threeOutlet4BeverageWithAllParams(){
        List<String> beveragesToPrepare = new ArrayList<>(Arrays.asList("hot_tea","hot_coffee","black_tea", "elaichi_tea"));
        CoffeeMachine coffeeMachine = null;
        try {
            coffeeMachine = new CoffeeMachine(3,4,1000);
            coffeeMachine.refillAllIngredient( 1000);
            coffeeMachine.refillIngredient("green_mixture", 1000);
            runTestForGivenOutletsAndBeverages(3, beveragesToPrepare,coffeeMachine);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runTestForGivenOutletsAndBeverages(int n, List<String> beveragesToPrepare, CoffeeMachine coffeeMachine){

        //Making executor service equal to outlets, so n beverages can be processsed in parallel
        ExecutorService executorService = Executors.newFixedThreadPool(n);

        for(int i=0;i<beveragesToPrepare.size();i++){
            MachineThread machineThread = new MachineThread(beveragesToPrepare.get(i), coffeeMachine);
            executorService.execute(machineThread);
        }

        executorService.shutdown();
    }
}
