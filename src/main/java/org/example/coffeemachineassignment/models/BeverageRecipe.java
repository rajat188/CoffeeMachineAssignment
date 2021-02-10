package org.example.coffeemachineassignment.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;

@Builder
@Getter
public class BeverageRecipe {

    //name of Beverage will be used as Unique Identifier
    private final String beverageName;

    //Map to maintain Recipe of beverage
    private final HashMap<String, Integer> recipe;

    @JsonCreator
    public BeverageRecipe(@JsonProperty("beverageName") String name, @JsonProperty("recipe") HashMap<String, Integer> recipe){
        this.beverageName = name;
        this.recipe = recipe;
    }
}
