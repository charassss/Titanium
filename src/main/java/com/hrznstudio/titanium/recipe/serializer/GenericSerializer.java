/*
 * This file is part of Titanium
 * Copyright (C) 2023, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.recipe.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.network.CompoundSerializableDataHandler;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Generic recipe serializer, that will serialize a recipe from the Handlers in @{@link JSONSerializableDataHandler}.
 * All fields in the @{@link SerializableRecipe} must be public to be used properly.
 *
 * @param <T>
 */
public class GenericSerializer<T extends SerializableRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>, IRecipeSerializerReversed<T> {
    private final Class<T> recipeClass;
    private final IRecipeType<T> recipeType;

    public GenericSerializer(IRecipeType<T> recipeType, Class<T> recipeClass) {
        this.recipeType = recipeType;
        this.recipeClass = recipeClass;
    }

    public GenericSerializer(ResourceLocation resourceLocation, Class<T> recipeClass) {
        this(IRecipeType.register(resourceLocation.toString()), recipeClass);
        this.setRegistryName(resourceLocation);
    }

    // Reading the recipe from the json file
    @Override
    @Nonnull
    public T read(@Nonnull ResourceLocation recipeId, JsonObject json) {
        try {
            T recipe = recipeClass.getConstructor(ResourceLocation.class).newInstance(recipeId);
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (fieldExists(entry.getKey()) && JSONSerializableDataHandler.acceptField(recipeClass.getField(entry.getKey()), recipeClass.getField(entry.getKey()).getType())) {
                    recipeClass.getField(entry.getKey()).set(recipe, JSONSerializableDataHandler.read(recipeClass.getField(entry.getKey()).getType(), entry.getValue()));
                }
            }
            return recipe;
        } catch (Exception e) {
            Titanium.LOGGER.catching(e);
            throw new JsonParseException(e);
        }
    }

    // Writes a json object from a recipe object
    @Override
    public JsonObject write(T recipe) {
        JsonObject object = new JsonObject();
        try {
            for (Field field : recipeClass.getFields()) {
                if (JSONSerializableDataHandler.acceptField(field, field.getType())) {
                    object.add(field.getName(), JSONSerializableDataHandler.write(field.getType(), field.get(recipe)));
                }
            }
        } catch (Exception e) {
            Titanium.LOGGER.catching(e);
        }
        if (recipe.getOutputCondition() != null){
            JsonObject recipeCondition = new JsonObject();
            recipeCondition.addProperty("type", "forge:conditional");
            JsonArray recipes = new JsonArray();
            JsonObject filteredRecipe = new JsonObject();
            JsonArray conditions = new JsonArray();
            conditions.add(recipe.getOutputCondition().getRight().getJson(recipe.getOutputCondition().getLeft()));
            filteredRecipe.add("conditions", conditions);
            filteredRecipe.add("recipe", object);
            recipes.add(filteredRecipe);
            recipeCondition.add("recipes", recipes);
            return recipeCondition;
        }
        return object;
    }

    // Reading from a packet buffer
    @Override
    @ParametersAreNonnullByDefault
    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
        try {
            T recipe = recipeClass.getConstructor(ResourceLocation.class).newInstance(recipeId);
            for (Field field : recipeClass.getFields()) {
                if (CompoundSerializableDataHandler.acceptField(field, field.getType())) {
                    CompoundSerializableDataHandler.readField(field, field.getType(), buffer, recipe);
                }
            }
            return recipe;
        } catch (Exception e) {
            Titanium.LOGGER.error(recipeId);
            Titanium.LOGGER.catching(e);
        }
        return null;
    }

    // Writes a recipe to a buffer
    @Override
    @ParametersAreNonnullByDefault
    public void write(PacketBuffer buffer, T recipe) {
        try {
            for (Field field : recipeClass.getFields()) {
                if (CompoundSerializableDataHandler.acceptField(field, field.getType())) {
                    CompoundSerializableDataHandler.writeField(field, field.getType(), buffer, recipe);
                }
            }
        } catch (Exception e) {
            Titanium.LOGGER.catching(e);
        }
    }

    public IRecipeType<T> getRecipeType() {
        return recipeType;
    }

    private boolean fieldExists(String field) {
        for (Field recipeClassField : recipeClass.getFields()) {
            if (recipeClassField.getName().equalsIgnoreCase(field)) {
                return true;
            }
        }
        return false;
    }


}
