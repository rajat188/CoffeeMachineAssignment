package org.example.coffeemachineassignment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.internal.util.Maps;
import lombok.Getter;
import org.example.coffeemachineassignment.models.BeverageRecipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
*   Coffee Machine
*   Initialize machine with no of outlets(required), maximum ingredients container(optional : if not defined max will be taken) and maximum ingredients container size(optional : if not defined max will be taken)
*/

@Getter
public class CoffeeMachine {

    private final int machineOutlets;

    // Max Ingredients machine can hold
    private final int ingredientsContainer;

    // Max size of container , for now assuming all container are of equal size
    private final int ingredientsContainerSize;

    // Availabe recipes for machine
    private final ConcurrentHashMap<String, BeverageRecipe> availableRecipes = new ConcurrentHashMap<>();

    //Marking this as final is important because we would be acquiring lock on this and if this is non-final instance can be changed
    private final ConcurrentHashMap<String, Integer> availableIngredientsWithQuantity = new ConcurrentHashMap<>();

    //Constructor to initialize machine with max outlet
    public CoffeeMachine(int machineOutlets) throws Exception{
        this.machineOutlets = machineOutlets;
        this.ingredientsContainer = Integer.MAX_VALUE;
        this.ingredientsContainerSize = Integer.MAX_VALUE;
        initializeCoffeeMachine();
    }

    //Constructor to initialize machine with max outlet and max Ingredients Container
    public CoffeeMachine(int machineOutlets, int ingredientsContainer) throws Exception{
        this.machineOutlets = machineOutlets;
        this.ingredientsContainer = ingredientsContainer;
        this.ingredientsContainerSize = Integer.MAX_VALUE;
        initializeCoffeeMachine();
    }

    //Constructor to initialize machine with max outlet, max Ingredients Container, max Ingredients Container size
    public CoffeeMachine(int machineOutlets, int ingredientsContainer, int ingredientsContainerSize) throws Exception {
        this.machineOutlets = machineOutlets;
        this.ingredientsContainer = ingredientsContainer;
        this.ingredientsContainerSize = ingredientsContainerSize;
        initializeCoffeeMachine();
    }

    // Every time coffee Machine is started initialize it with given recipes and ingredients
    private void initializeCoffeeMachine() throws Exception {
        initializeRecipes();
        initializeIngreDients();
    }

    // Initializing Recipes when Coffee Machine is started
    // Recipes fetched from json file in resources/recipes.json
    private void initializeRecipes() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<BeverageRecipe> beverageRecipes = new ArrayList<>();
        try{
            beverageRecipes = objectMapper.readValue(new File("./src/main/resources/recipes.json"), new TypeReference<List<BeverageRecipe>>() {});
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        for(BeverageRecipe beverageRecipe : beverageRecipes)
        {
            if(availableRecipes.contains(beverageRecipe.getBeverageName())){
               System.out.println("Beverage Name should be unique for each beverage. "+beverageRecipe.getBeverageName() + "already exists. Not adding duplicate entry");
            }
            availableRecipes.put(beverageRecipe.getBeverageName(), beverageRecipe);
        }
    }

    // Initializing Ingredients when Coffee Machine is started
    // Recipes fetched from json file in resources/ingredients.json
    private void initializeIngreDients() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        ConcurrentHashMap<String, Integer> jsonIngredientsMap = objectMapper.readValue(new File("./src/main/resources/ingredients.json"), new TypeReference<ConcurrentHashMap<String, Integer>>() {});

        if(jsonIngredientsMap.size() > ingredientsContainer){
            throw new Exception("Max unique ingredients that Machine can hold is "+ ingredientsContainer + ". Try to reduce no. of ingredients or purchase machine with more ingredients container");
        }

        for(Map.Entry<String, Integer> jsonIngredientsMapEntry : jsonIngredientsMap.entrySet()){
            if(availableRecipes.contains(jsonIngredientsMapEntry.getKey())){
                throw new Exception("Ingredients Name should be unique for each ingredients. "+jsonIngredientsMapEntry.getKey() + "already exists.");
            }
            availableIngredientsWithQuantity.put(jsonIngredientsMapEntry.getKey(), Math.min(jsonIngredientsMapEntry.getValue(),ingredientsContainerSize));
        }

    }

    // Method to prepare beverages
    public void prepareBeverage(String beverageName){
        BeverageRecipe recipe = availableRecipes.getOrDefault(beverageName, null);

        if(recipe == null){
            System.out.println("No recipe exist for provided Beverage : " + beverageName +". Consider adding the recipe.");
        }

        synchronized (availableIngredientsWithQuantity){
            //Iterate over all the ingredients of recipe and check if beverage can be prepared
            for(Map.Entry<String, Integer> entry : recipe.getRecipe().entrySet()){
                String ingredientName = entry.getKey();
                int requiredQuantity = entry.getValue();

                int availableQuantity = availableIngredientsWithQuantity.getOrDefault(ingredientName, 0);

                if(availableQuantity == 0){
                    System.out.println(beverageName+" cannot be prepared beacause "+ingredientName+ " is not available");
                    return;
                }
                else if(availableQuantity < requiredQuantity){
                    System.out.println(beverageName+" cannot be prepared beacause "+ingredientName+ " is not sufficient");
                    return;
                }
            }

            //If code reaches this point beverage can be prepared
            for(Map.Entry<String, Integer> entry : recipe.getRecipe().entrySet()){
                String ingredientName = entry.getKey();
                int requiredQuantity = entry.getValue();

                int availableQuantity = availableIngredientsWithQuantity.get(ingredientName);

                availableIngredientsWithQuantity.put(ingredientName, availableQuantity - requiredQuantity);
                checkIfIngredientRunningLowAndSignal(availableQuantity - requiredQuantity, ingredientName);
            }

            System.out.println(beverageName + " is prepared");

        }
    }

    //This will signal that ingredient is running low if updated quantity is less than 10% of max size
    private void checkIfIngredientRunningLowAndSignal(int updatedQuantity, String ingredientName){
        int tenPercentOfMax = (int) (0.1 * ingredientsContainerSize);
        if(updatedQuantity <= tenPercentOfMax){
            System.out.println(ingredientName + " is running low. Consider Refilling.");
        }
    }

    //Method to refill a particular ingredient
    public void refillIngredient(String ingredientName, int qunatity){
        int availableQuantity = availableIngredientsWithQuantity.getOrDefault(ingredientName,0);
        if (ingredientsContainerSize - availableQuantity >= qunatity) {
            System.out.println(ingredientName + " refilled.");
            availableIngredientsWithQuantity.put(ingredientName, availableQuantity + qunatity);
        } else {
            System.out.println("Refilling caused spillage as qunatity added was more than container Size. " + ingredientName + " refilled.");
            availableIngredientsWithQuantity.put(ingredientName, ingredientsContainerSize);
        }

    }

    //Method to refill a all the ingredients
    public void refillAllIngredient(int qunatity){
        for(Map.Entry<String, Integer> availableIngredients : availableIngredientsWithQuantity.entrySet()) {
            refillIngredient(availableIngredients.getKey(), availableIngredients.getValue());
        }
    }


    //This method can be used if new Recipes are added via recipes.json file
    public void recalibrateCoffeeMachine() throws Exception {
        synchronized (availableRecipes){
            initializeRecipes();
        }

    }

}
