package com.phoen1x.culturaldelightpatch.impl.ui;

import com.baisylia.culturaldelights.screens.OvenMenu;
import com.phoen1x.culturaldelightpatch.impl.polydex.PolydexCompat;
import com.phoen1x.culturaldelightpatch.impl.res.GuiTextures;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class OvenUi extends SimpleGui {
    private final OvenMenu wrapped;

    public OvenUi(ServerPlayerEntity player, OvenMenu wrapped) {
        super(ScreenHandlerType.GENERIC_9X3, player, false);
        this.wrapped = wrapped;
        player.swingHand(Hand.MAIN_HAND, true);
        this.setTitle(GuiTextures.OVEN.apply(Text.translatable("block.culturaldelights.oven")));
        this.setSlot(1, wrapped.getSlot(36));
        this.setSlot(2, wrapped.getSlot(37));
        this.setSlot(3, wrapped.getSlot(38));
        this.setSlot(18, PolydexCompat.getButton(Registries.RECIPE_TYPE.get(Identifier.of("culturaldelights", "baking"))));
        this.setSlot(10, wrapped.getSlot(39));
        this.setSlot(11, wrapped.getSlot(40));
        this.setSlot(12, wrapped.getSlot(41));
        this.setSlot(19, wrapped.getSlot(42));
        this.setSlot(20, wrapped.getSlot(43));
        this.setSlot(21, wrapped.getSlot(44));
        this.setSlot(15, wrapped.getSlot(45));

        this.updateState();
        this.open();
    }

    private void updateState() {
        this.setSlot(14, GuiTextures.PROGRESS_OVEN.getCeil(this.wrapped.getScaledProgress() / 26f));
        this.setSlot(23, (this.wrapped.isFueled() ? GuiTextures.HEATED.get() : GuiTextures.EMPTY_BUILDER.get())
                .setName(Text.translatable("container.farmersdelight.cooking_pot." + (this.wrapped.isFueled() ? "heated" : "not_heated"))));
    }

    @Override
    public void onTick() {
        super.onTick();
        if (!this.wrapped.canUse(this.player)) {
            this.close();
            return;
        }
        this.updateState();
    }

    @Override
    public void onManualClose() {
        super.onManualClose();
        this.wrapped.onClosed(this.player);
    }
}