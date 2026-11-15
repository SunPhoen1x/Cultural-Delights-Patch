package com.phoen1x.culturaldelightpatch.mixin.mod;

import com.baisylia.culturaldelights.CulturalDelights;
import com.baisylia.culturaldelights.block.custom.*;
import com.baisylia.culturaldelights.item.ModBoatTypes;
import com.baisylia.culturaldelights.item.ModDataComponents;
import com.baisylia.culturaldelights.refabricated.event.BuildCreativeModeTabContentsEvent;
import com.phoen1x.culturaldelightpatch.impl.CulturalDelightsPatch;
import com.phoen1x.culturaldelightpatch.impl.block.*;
import com.phoen1x.culturaldelightpatch.impl.item.PolyBaseItem;
import eu.pb4.factorytools.api.block.model.SignModel;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.core.api.item.PolymerCreativeModeTabUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import eu.pb4.polymer.core.api.other.PolymerMobEffect;
import eu.pb4.polymer.core.api.other.PolymerSoundEvent;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.*;
import net.minecraft.entity.EntityTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.block.CabinetBlock;
import vectorwing.farmersdelight.common.block.PieBlock;
import vectorwing.farmersdelight.common.block.WildCropBlock;

@Mixin(value = CulturalDelights.class, remap = false)
public class CulturalDelightsMixin {

    @Inject(method = "buildCreativeTab", at = @At("HEAD"), cancellable = true)
    private void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event, CallbackInfo ci) {
        if (event.getTabKey() == null) {
            ci.cancel();
        }
    }

    @Inject(method = "onInitialize", at = @At("TAIL"))
    private void onInitializeTail(CallbackInfo ci) {
        String modId = "culturaldelights";

        for (Identifier id : Registries.SOUND_EVENT.getIds()) {
            if (modId.equals(id.getNamespace())) {
                var sound = Registries.SOUND_EVENT.get(id);
                if (sound != null) {
                    PolymerSoundEvent.registerOverlay(sound);
                }
            }
        }

        for (Identifier id : Registries.STATUS_EFFECT.getIds()) {
            if (modId.equals(id.getNamespace())) {
                var effect = Registries.STATUS_EFFECT.get(id);
                if (effect != null) {
                    PolymerMobEffect.registerOverlay(effect);
                }
            }
        }

        for (Identifier id : Registries.BLOCK_ENTITY_TYPE.getIds()) {
            if (modId.equals(id.getNamespace())) {
                var be = Registries.BLOCK_ENTITY_TYPE.get(id);
                if (be != null) {
                    PolymerBlockUtils.registerBlockEntity(be);
                }
            }
        }

        for (Identifier id : Registries.SCREEN_HANDLER.getIds()) {
            if (modId.equals(id.getNamespace())) {
                var menu = Registries.SCREEN_HANDLER.get(id);
                if (menu != null) {
                    ScreenHandlerType<?> fallback = id.getPath().contains("oven")
                            ? ScreenHandlerType.FURNACE
                            : ScreenHandlerType.GENERIC_9X3;
                    PolymerSyncedObject.setSyncedObject(Registries.SCREEN_HANDLER, menu, (t, ctx) -> fallback);
                }
            }
        }

        PolymerComponent.registerDataComponent(ModDataComponents.COOK_TIME.get());

        PolymerEntityUtils.registerType(ModBoatTypes.BEANSTALK, (type, ctx) -> EntityTypes.BAMBOO_RAFT);
        PolymerEntityUtils.registerType(ModBoatTypes.BEANSTALK_CHEST_RAFT, (type, ctx) -> EntityTypes.BAMBOO_CHEST_RAFT);

        for (Identifier id : Registries.BLOCK.getIds()) {
            if (modId.equals(id.getNamespace())) {
                Block block = Registries.BLOCK.get(id);
                if (block != null && block != Blocks.AIR) {
                    polymerifyBlock(id, block);
                }
            }
        }

        for (Identifier id : Registries.ITEM.getIds()) {
            if (modId.equals(id.getNamespace())) {
                var item = Registries.ITEM.get(id);
                if (item != null) {
                    PolymerItem.registerOverlay(item, new PolyBaseItem(item));
                }
            }
        }

        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register((group, entries) -> {
            if (isFarmersDelightTab(group)) {
                Iterable<?> rawEntries = com.baisylia.culturaldelights.item.ModItems.ITEMS.getEntries();
                for (Object obj : rawEntries) {
                    if (obj instanceof java.util.function.Supplier<?> supplier && supplier.get() instanceof net.minecraft.item.Item item) {
                        entries.add(item);
                    }
                }
            }
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack originalStack = player.getStackInHand(hand);
            Item raft = Registries.ITEM.get(Identifier.of("culturaldelights", "beanstalk_raft"));
            Item chestRaft = Registries.ITEM.get(Identifier.of("culturaldelights", "beanstalk_chest_raft"));

            Item vanillaReplacement = null;
            if (originalStack.getItem() == raft) {
                vanillaReplacement = Items.BAMBOO_RAFT;
            } else if (originalStack.getItem() == chestRaft) {
                vanillaReplacement = Items.BAMBOO_CHEST_RAFT;
            }

            if (vanillaReplacement != null) {
                ItemStack tempStack = originalStack.copyComponentsToNewStack(vanillaReplacement, originalStack.getCount());
                player.setStackInHand(hand, tempStack);

                ActionResult result = vanillaReplacement.use(world, player, hand);

                ItemStack afterUse = player.getStackInHand(hand);
                if (afterUse.isEmpty()) {
                    player.setStackInHand(hand, ItemStack.EMPTY);
                } else {
                    ItemStack restored = afterUse.copyComponentsToNewStack(originalStack.getItem(), afterUse.getCount());
                    player.setStackInHand(hand, restored);
                }

                return result;
            }

            return ActionResult.PASS;
        });
    }

    @Unique
    private static boolean isFarmersDelightTab(ItemGroup group) {
        if (group == null) return false;
        Identifier polymerId = PolymerCreativeModeTabUtils.getId(group);
        if (polymerId != null && "farmersdelight".equals(polymerId.getNamespace())) {
            return true;
        }
        if (group.getDisplayName().getContent() instanceof TranslatableTextContent translatable) {
            return translatable.getKey().contains("farmersdelight");
        }
        return false;
    }

    @Unique
    private static void polymerifyBlock(Identifier id, Block block) {
        String path = id.getPath();
        PolymerBlock overlay = null;

        if (path.endsWith("wall_hanging_sign")) {
            overlay = StateCopyFactoryBlock.HANGING_WALL_SIGN;
            CulturalDelightsPatch.LATE_INIT.add(() -> SignModel.setModel(block, CulturalDelightsPatch.id("block/" + path)));
        } else if (path.endsWith("hanging_sign")) {
            overlay = StateCopyFactoryBlock.HANGING_SIGN;
            CulturalDelightsPatch.LATE_INIT.add(() -> SignModel.setModel(block, CulturalDelightsPatch.id("block/" + path)));
        } else if (path.endsWith("wall_sign")) {
            overlay = StateCopyFactoryBlock.WALL_SIGN;
            CulturalDelightsPatch.LATE_INIT.add(() -> SignModel.setModel(block, CulturalDelightsPatch.id("block/" + path)));
        } else if (path.endsWith("sign")) {
            overlay = StateCopyFactoryBlock.SIGN;
            CulturalDelightsPatch.LATE_INIT.add(() -> SignModel.setModel(block, CulturalDelightsPatch.id("block/" + path)));
        } else if (path.equals("cheese_wheel")) {
            overlay = BaseFactoryBlock.SLAB_BOTTOM;
        } else if (block instanceof PieBlock || block instanceof EggplantFeastBlock || block instanceof RusticLoafBlock || path.equals("exotic_roll_medley")) {
            overlay = BaseFactoryBlock.CAMPFIRE;
        } else if (block instanceof WildCropBlock || block instanceof BeansBlock || block instanceof CornBlock
                || block instanceof CucumbersBlock || block instanceof EggplantsBlock || block instanceof BuddingBeansBlock
                || block instanceof MagicBeansBlock || block instanceof SaplingBlock || block instanceof AvocadoPitBlock
                || block instanceof FlowerPotBlock) {
            overlay = BaseFactoryBlock.SAPLING;
        } else if (block instanceof LeavesBlock || block instanceof BeanstalkLeafBlock) {
            overlay = StatePolymerBlock.of(block, BlockModelType.LEAVES);
        } else if (block instanceof SlabBlock) {
            overlay = SlabFactoryBlock.INSTANCE;
        } else if (block instanceof StairsBlock) {
            overlay = StateCopyFactoryBlock.STAIR;
        } else if (block instanceof DoorBlock) {
            overlay = DoorPolymerBlock.INSTANCE;
        } else if (block instanceof TrapdoorBlock) {
            overlay = TrapdoorPolymerBlock.INSTANCE;
        } else if (block instanceof FenceBlock) {
            overlay = StateCopyFactoryBlock.FENCE;
        } else if (block instanceof FenceGateBlock) {
            overlay = StateCopyFactoryBlock.FENCE_GATE;
        } else if (block instanceof PressurePlateBlock) {
            overlay = StateCopyFactoryBlock.PRESSURE_PLATE;
        } else if (block instanceof ButtonBlock) {
            overlay = StateCopyFactoryBlock.BUTTON;
        } else if (block instanceof CounterBlock) {
            overlay = StatePolymerBlock.of(block, BlockModelType.LEAVES);
        } else if (block instanceof VatBlock || block instanceof OvenBlock || block instanceof CabinetBlock) {
            overlay = StatePolymerBlock.of(block, BlockModelType.FULL_BLOCK);
        } else if (!(block instanceof BlockEntityProvider) && block.getDefaultState().isFullCube(PolymerCommonUtils.getFakeWorld(), BlockPos.ORIGIN)) {
            overlay = StatePolymerBlock.of(block, BlockModelType.FULL_BLOCK);
        }

        if (overlay == null) {
            if (block.getDefaultState().getCollisionShape(PolymerCommonUtils.getFakeWorld(), BlockPos.ORIGIN).isEmpty()) {
                overlay = BaseFactoryBlock.SAPLING;
            } else {
                overlay = BaseFactoryBlock.BARRIER;
            }
        }

        BlockStateModelManager.addBlock(id, block);
        PolymerBlock.registerOverlay(block, overlay);
        if (overlay instanceof BlockWithElementHolder blockWithElementHolder) {
            BlockWithElementHolder.registerOverlay(block, blockWithElementHolder);
        }
    }
}