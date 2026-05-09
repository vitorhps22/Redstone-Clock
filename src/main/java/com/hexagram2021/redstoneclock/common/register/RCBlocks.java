package com.hexagram2021.redstoneclock.common.register;

import com.hexagram2021.redstoneclock.common.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.redstoneclock.RedstoneClock.MODID;

public final class RCBlocks {
	private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final RegistryObject<RedstoneClockBlock> REDSTONE_CLOCK = REGISTER.register("redstone_clock", () -> new RedstoneClockBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F)
                    .lightLevel(blockState -> blockState.getValue(RedstoneClockBlock.POWERED) ? 7 : 0)
                    .sound(SoundType.METAL).pushReaction(PushReaction.BLOCK)
                    .isValidSpawn((BlockState blockState, BlockGetter level, BlockPos blockPos, EntityType<?> entityType) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
    ));
	public static final RegistryObject<PulseDividerBlock> PULSE_DIVIDER = REGISTER.register("pulse_divider", () -> new PulseDividerBlock(
			BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F)
					.sound(SoundType.METAL).pushReaction(PushReaction.BLOCK)
					.isValidSpawn((BlockState blockState, BlockGetter level, BlockPos blockPos, EntityType<?> entityType) -> false)
	));

	private RCBlocks() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
