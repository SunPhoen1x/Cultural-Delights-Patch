package com.phoen1x.culturaldelightpatch.impl.polydex;

import com.baisylia.culturaldelights.recipes.OvenRecipe;
import com.baisylia.culturaldelights.recipes.VatRecipe;
import com.phoen1x.culturaldelightpatch.impl.polydex.pages.OvenRecipePage;
import com.phoen1x.culturaldelightpatch.impl.polydex.pages.VatRecipePage;
import com.phoen1x.culturaldelightpatch.impl.res.GuiTextures;
import eu.pb4.polydex.api.v1.recipe.PolydexCategory;
import eu.pb4.polydex.api.v1.recipe.PolydexPage;
import eu.pb4.polydex.api.v1.recipe.PolydexPageUtils;
import eu.pb4.sgui.api.elements.GuiElement;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class PolydexCompatImpl {
    public static void register() {
        PolydexPage.registerRecipeViewer(OvenRecipe.class, OvenRecipePage::new);
        PolydexPage.registerRecipeViewer(VatRecipe.class, VatRecipePage::new);
    }

    public static GuiElement getButton(RecipeType<?> type) {
        var category = PolydexCategory.of(type);
        return GuiTextures.POLYDEX_BUTTON.get()
                .setName(Text.translatable("text.culturaldelightspatch.recipes"))
                .setCallback((index, clickType, action, gui) -> {
                    PolydexPageUtils.openCategoryUi(gui.getPlayer(), category, gui::open);
                    gui.getPlayer().playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.5f, 1.0f);
                }).build();
    }
}