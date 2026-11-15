package com.phoen1x.culturaldelightpatch.impl.polydex.pages;

import com.baisylia.culturaldelights.recipes.VatRecipe;
import com.phoen1x.culturaldelightpatch.impl.polydex.PolydexTextures;
import com.phoen1x.culturaldelightpatch.impl.res.GuiTextures;
import eu.pb4.polydex.api.v1.recipe.AbstractRecipePolydexPage;
import eu.pb4.polydex.api.v1.recipe.PageBuilder;
import eu.pb4.polydex.api.v1.recipe.PolydexEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class VatRecipePage extends AbstractRecipePolydexPage<VatRecipe> {
    private static final ItemStack ICON = Registries.ITEM.get(Identifier.of("culturaldelights", "vat")).getDefaultStack();

    public VatRecipePage(RecipeEntry<VatRecipe> recipe) {
        super(recipe);
    }

    @Override
    public @Nullable Text texture(ServerPlayerEntity player) {
        return PolydexTextures.VAT;
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
            pageBuilder.setIngredient(i % 2 + 2, i / 2 + 1, ingredient);
            i++;
        }

        var tempBuilder = switch (this.recipe.getTemperature()) {
            case COLD -> GuiTextures.TEMP_COLD.get()
                    .setName(Text.translatable("container.culturaldelights.vat.cold").formatted(Formatting.AQUA));
            case NORMAL -> GuiTextures.TEMP_NORMAL.get()
                    .setName(Text.translatable("container.culturaldelights.vat.normal").formatted(Formatting.YELLOW));
            case HOT -> GuiTextures.TEMP_HOT.get()
                    .setName(Text.translatable("container.culturaldelights.vat.hot").formatted(Formatting.GOLD));
        };
        pageBuilder.set(1, 2, tempBuilder);

        pageBuilder.setOutput(6, 1, this.recipe.getResultItem().copy());
        this.recipe.getContainer().ifPresent(container -> pageBuilder.setIngredient(6, 3, container));
    }
}