package com.hexagram2021.redstoneclock.common.block;

import com.hexagram2021.redstoneclock.common.block.entity.RedstoneClockBlockEntity;
import com.hexagram2021.redstoneclock.common.register.RCBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class RedstoneClockBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public RedstoneClockBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(LIT, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction playerLooking = context.getNearestLookingDirection();
        return this.defaultBlockState().setValue(FACING, playerLooking.getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    public static boolean hasNeighborSignal(Level level, BlockPos pos, BlockState blockState) {
        Direction rejectFace = blockState.getValue(FACING);
        for (Direction dir : Direction.values()) {
            if (dir != rejectFace && level.hasSignal(pos.relative(dir), dir)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(blockPos);
            if (blockentity instanceof RedstoneClockBlockEntity redstoneClockBlockEntity) {
                player.openMenu(redstoneClockBlockEntity);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter level, BlockPos blockPos, Direction direction) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof RedstoneClockBlockEntity redstoneClockBlockEntity) {
            return blockState.getValue(LIT) && blockState.getValue(FACING).getOpposite() == direction
                    ? redstoneClockBlockEntity.getSignalStrength() : 0;
        }
        return 0;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter level, BlockPos blockPos, Direction direction) {
        return blockState.getSignal(level, blockPos, direction);
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos neighbor, boolean piston) {
        boolean flag = hasNeighborSignal(level, blockPos, blockState);
        if (blockState.getValue(POWERED) != flag && !level.getBlockTicks().willTickThisTick(blockPos, this)) {
            level.setBlock(blockPos, blockState.setValue(POWERED, flag), Block.UPDATE_ALL);
        }
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState old, boolean piston) {
        for (Direction direction : Direction.values()) {
            level.updateNeighborsAt(blockPos.relative(direction), this);
        }
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState old, boolean piston) {
        for (Direction direction : Direction.values()) {
            level.updateNeighborsAt(blockPos.relative(direction), this);
        }
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        if (itemStack.hasCustomHoverName()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof RedstoneClockBlockEntity redstoneClockBlockEntity) {
                redstoneClockBlockEntity.setCustomName(itemStack.getHoverName());
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, LIT);
    }

    @Override
    public RedstoneClockBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RedstoneClockBlockEntity(blockPos, blockState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, RCBlockEntities.REDSTONE_CLOCK.get(), RedstoneClockBlockEntity::serverTick);
    }
}