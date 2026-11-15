package com.phoen1x.culturaldelightpatch.impl.ui;

import com.baisylia.culturaldelights.screens.VatMenu;
import com.baisylia.culturaldelights.util.VatTemperature;
import com.phoen1x.culturaldelightpatch.impl.polydex.PolydexCompat;
import com.phoen1x.culturaldelightpatch.impl.res.GuiTextures;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class VatUi extends SimpleGui {
    private final VatMenu wrapped;

    public VatUi(ServerPlayerEntity player, VatMenu wrapped) {
        super(ScreenHandlerType.GENERIC_9X3, player, false);
        this.wrapped = wrapped;
        player.swingHand(Hand.MAIN_HAND, true);
        this.setTitle(GuiTextures.VAT.apply(Text.translatable("block.culturaldelights.vat")));
        this.setSlot(2, wrapped.getSlot(36));
        this.setSlot(3, wrapped.getSlot(37));
        this.setSlot(18, PolydexCompat.getButton(Registries.RECIPE_TYPE.get(Identifier.of("culturaldelights", "aging"))));
        this.setSlot(11, wrapped.getSlot(38));
        this.setSlot(12, wrapped.getSlot(39));
        this.setSlot(20, wrapped.getSlot(40));
        this.setSlot(21, wrapped.getSlot(41));

        this.setSlot(6, wrapped.getSlot(43));
        this.setSlot(24, wrapped.getSlot(42));

        this.updateState();
        this.open();
    }

    private void updateState() {
        VatTemperature temp = this.wrapped.getTemperature();
        GuiElementBuilder tempBuilder = switch (temp) {
            case COLD -> GuiTextures.TEMP_COLD.get()
                    .setName(Text.translatable("container.culturaldelights.vat.cold").formatted(Formatting.AQUA));
            case NORMAL -> GuiTextures.TEMP_NORMAL.get()
                    .setName(Text.translatable("container.culturaldelights.vat.normal").formatted(Formatting.YELLOW));
            case HOT -> GuiTextures.TEMP_HOT.get()
                    .setName(Text.translatable("container.culturaldelights.vat.hot").formatted(Formatting.GOLD));
        };
        this.setSlot(10, tempBuilder);
        this.setSlot(5, GuiTextures.PROGRESS_VAT.getCeil(this.wrapped.getScaledProgress() / 26f));
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