package com.whipowill.bloodshed;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Random;

public class BloodBlock extends Block {

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 0.5, 16);

    public BloodBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(Properties.UP, false)
                .with(Properties.DOWN, false)
                .with(Properties.NORTH, false)
                .with(Properties.EAST, false)
                .with(Properties.SOUTH, false)
                .with(Properties.WEST, false));
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return false; // No random ticks - we use scheduled ticks
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        // Empty - no sound or slowing
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.UP, Properties.DOWN, Properties.NORTH, Properties.EAST,
                   Properties.SOUTH, Properties.WEST);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        updateConnections(state, world, pos);

        // Schedule removal after the configured time
        if (!world.isClient && world instanceof ServerWorld) {
            int fadeTicks = getFadeTicks();
            world.createAndScheduleBlockTick(pos, this, fadeTicks);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Remove the block after scheduled time - clean and simple
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    }

    private int getFadeTicks() {
        if (Bloodshed.CONFIG == null) {
            return 600; // Default 30 seconds
        }

        int baseTicks = Bloodshed.CONFIG.bloodFadeTime * 20;
        int variation = (int)(baseTicks * 0.2f); // 20% variation
        return baseTicks + (new Random().nextInt(variation * 2) - variation);
    }

    private void updateConnections(BlockState state, World world, BlockPos pos) {
        boolean up = world.getBlockState(pos.up()).isOpaqueFullCube(world, pos.up());
        boolean down = world.getBlockState(pos.down()).isOpaqueFullCube(world, pos.down());
        boolean north = world.getBlockState(pos.north()).isOpaqueFullCube(world, pos.north());
        boolean east = world.getBlockState(pos.east()).isOpaqueFullCube(world, pos.east());
        boolean south = world.getBlockState(pos.south()).isOpaqueFullCube(world, pos.south());
        boolean west = world.getBlockState(pos.west()).isOpaqueFullCube(world, pos.west());

        BlockState newState = state
                .with(Properties.UP, up)
                .with(Properties.DOWN, down)
                .with(Properties.NORTH, north)
                .with(Properties.EAST, east)
                .with(Properties.SOUTH, south)
                .with(Properties.WEST, west);

        world.setBlockState(pos, newState, 3);

        boolean isEmpty = !(up || down || north || east || south || west);
        if (isEmpty) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (world instanceof World) {
            updateConnections(state, (World) world, pos);
        }
        return state;
    }
}