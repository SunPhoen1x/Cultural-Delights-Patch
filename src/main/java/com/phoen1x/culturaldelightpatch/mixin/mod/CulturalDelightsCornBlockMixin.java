package com.phoen1x.culturaldelightpatch.mixin.mod;

import com.baisylia.culturaldelights.block.custom.CornBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CornBlock.class)
public abstract class CulturalDelightsCornBlockMixin extends CropBlock {
    protected CulturalDelightsCornBlockMixin(Settings settings) {
        super(settings);
    }

    @Overwrite
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        int height = state.get(CornBlock.HEIGHT);
        int age = state.get(CornBlock.AGE);
        int maxHeight = CornBlock.getExpectedMaxHeight(age);

        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        } else if (direction == Direction.UP && height < maxHeight && (neighborState.getBlock() != this || neighborState.get(CornBlock.HEIGHT) != height + 1)) {
            return Blocks.AIR.getDefaultState();
        }

        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }
}