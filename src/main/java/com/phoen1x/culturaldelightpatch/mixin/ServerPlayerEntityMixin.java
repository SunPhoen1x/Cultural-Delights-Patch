package com.phoen1x.culturaldelightpatch.mixin;

import com.baisylia.culturaldelights.screens.OvenMenu;
import com.baisylia.culturaldelights.screens.VatMenu;
import com.phoen1x.culturaldelightpatch.impl.ui.OvenUi;
import com.phoen1x.culturaldelightpatch.impl.ui.VatUi;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "openHandledScreen", at = @At("HEAD"), cancellable = true)
    private void openCustomPolymerUi(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> cir) {
        if (factory == null) return;
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ScreenHandler handler = factory.createMenu(0, player.getInventory(), player);

        if (handler instanceof VatMenu vatMenu) {
            new VatUi(player, vatMenu);
            cir.setReturnValue(OptionalInt.empty());
        } else if (handler instanceof OvenMenu ovenMenu) {
            new OvenUi(player, ovenMenu);
            cir.setReturnValue(OptionalInt.empty());
        }
    }
}