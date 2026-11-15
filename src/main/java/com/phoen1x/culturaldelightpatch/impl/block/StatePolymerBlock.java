package com.phoen1x.culturaldelightpatch.impl.block;

import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.BlockStateAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.StateModelVariant;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;

public record StatePolymerBlock(Map<BlockState, BlockState> map, PolymerBlock fallback) implements PolymerTexturedBlock {

    public static PolymerBlock of(Block block, BlockModelType type) {
        return of(block, type, null, x -> true);
    }

    public static PolymerBlock of(Block block, BlockModelType type, PolymerBlock fallback, Predicate<BlockState> canUseBlock) {
        var id = Registries.BLOCK.getId(block);
        var container = FabricLoader.getInstance().getModContainer(id.getNamespace());
        if (container.isEmpty()) return fallback;

        var optPath = container.get().findPath("assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json");
        if (optPath.isEmpty()) return fallback;

        try {
            var decoded = BlockStateAsset.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(optPath.get()))).getOrThrow().getFirst();
            var map = new IdentityHashMap<BlockState, BlockState>();
            var cache = new HashMap<List<StateModelVariant>, BlockState>();

            List<Pair<Predicate<BlockState>, List<StateModelVariant>>> list = new ArrayList<>();
            BlockStateModelManager.parseVariants(block, decoded.variants().orElseThrow(), (a, b) -> list.add(Pair.of(a::test, b)));

            for (var state : block.getStateManager().getStates()) {
                for (var pair : list) {
                    if (pair.getFirst().test(state) && canUseBlock.test(state)) {
                        map.put(state, cache.computeIfAbsent(pair.getSecond(), c -> PolymerBlockResourceUtils.requestBlock(
                                type,
                                c.stream().map(x -> new PolymerBlockModel(x.model(), x.x(), x.y(), x.uvlock(), x.weigth())).toArray(PolymerBlockModel[]::new)
                        )));
                        break;
                    }
                }
            }

            return new StatePolymerBlock(map, fallback);
        } catch (Throwable e) {
            return fallback;
        }
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        var s = map.get(blockState);
        return s != null ? s : (fallback != null ? fallback.getPolymerBlockState(blockState, packetContext) : blockState);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayerEntity player, Hand hand, ItemStack stack, ServerWorld world, BlockHitResult blockHitResult) {
        return true;
    }
}