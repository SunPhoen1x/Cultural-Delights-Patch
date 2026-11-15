package com.phoen1x.culturaldelightpatch.impl;

import com.baisylia.culturaldelights.CulturalDelights;
import com.phoen1x.culturaldelightpatch.impl.polydex.PolydexCompat;
import com.phoen1x.culturaldelightpatch.impl.res.GuiTextures;
import com.phoen1x.culturaldelightpatch.impl.res.ResourcePackGenerator;
import com.phoen1x.culturaldelightpatch.impl.res.UiResourceCreator;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CulturalDelightsPatch implements ModInitializer {
    public static final String MOD_ID = "culturaldelights";
    public static final String PATCH_MOD_ID = "culturaldelightspatch";
    public static final List<Runnable> LATE_INIT = new ArrayList<>();

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.addModAssets(PATCH_MOD_ID);
        ResourcePackExtras.forDefault().addBridgedModelsFolder(Identifier.of(MOD_ID, "block"));
        ResourcePackExtras.forDefault().addBridgedModelsFolder(Identifier.of(PATCH_MOD_ID, "block"));
        ResourcePackExtras.forDefault().addBridgedModelsFolder(Identifier.of(CulturalDelights.MOD_ID, "sgui"), (id, b) -> new ItemAsset(new BasicItemModel(id), new ItemAsset.Properties(true, true)));
        ResourcePackExtras.forDefault().addBridgedModelsFolder(Identifier.of(PATCH_MOD_ID, "sgui"), (id, b) -> new ItemAsset(new BasicItemModel(id), new ItemAsset.Properties(true, true)));
        GuiTextures.register();
        UiResourceCreator.setup();
        ResourcePackGenerator.setup();
        PolydexCompat.register();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            LATE_INIT.forEach(Runnable::run);
            LATE_INIT.clear();
        });
    }

    public static Identifier id(String path) {
        return Identifier.of(PATCH_MOD_ID, path);
    }
}