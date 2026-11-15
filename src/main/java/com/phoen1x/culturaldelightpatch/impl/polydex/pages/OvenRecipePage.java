package com.phoen1x.culturaldelightpatch.impl.polydex.pages;

import com.baisylia.culturaldelights.recipes.OvenRecipe;
import com.phoen1x.culturaldelightpatch.impl.polydex.PolydexTextures;
import eu.pb4.polydex.api.v1.recipe.AbstractRecipePolydexPage;
import eu.pb4.polydex.api.v1.recipe.PageBuilder;
import eu.pb4.polydex.api.v1.recipe.PolydexEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class OvenRecipePage extends AbstractRecipePolydexPage<OvenRecipe> {
    private static final ItemStack ICON = Registries.ITEM.get(Identifier.of("culturaldelights", "oven")).getDefaultStack();

    public OvenRecipePage(RecipeEntry<OvenRecipe> recipe) {
        super(recipe);
    }

    @Override
    public @Nullable Text texture(ServerPlayerEntity player) {
        return PolydexTextures.OVEN;
    }

    @Override
    public ItemStack getOutput(@Nullable PolydexEntry polydexEntry, MinecraftServer minecraftServer) {
        return this.recipe.getResultItem();
    }

    @Override
    public ItemStack typeIcon(ServerPlayerEntity serverPlayerEntity) {
        return ICON;
    }

    @Override
    public void createPage(@Nullable PolydexEntry polydexEntry, ServerPlayerEntity serverPlayerEntity, PageBuilder pageBuilder) {
        int i = 0;
        for (var ingredient : this.recipe.getIngredients()) {
            pageBuilder.setIngredient(i % 3 + 1, i / 3 + 1, ingredient);
            i++;
        }

        pageBuilder.setOutput(6, 2, this.recipe.getResultItem().copy());
    }
}