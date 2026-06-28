package com.hexagram2021.redstoneclock.common.menu;

import com.hexagram2021.redstoneclock.common.block.entity.RedstoneClockBlockEntity;
import com.hexagram2021.redstoneclock.common.register.RCMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;

public class RedstoneClockMenu extends AbstractContainerMenu {
	private final Container redstoneClockContainer;
	public final ContainerData redstoneClock;

	public int multiplier = 0;

	@Contract(pure = true)
	public static int toMultiplier(int log) {
		return switch (log) {
			case 1 -> 20;
			case 2 -> 200;
			case 3 -> 2000;
			case 4 -> 20000;
			case 5 -> 200000;
			default -> 2;
		};
	}

	public RedstoneClockMenu(int id) {
		this(id, new SimpleContainer(RedstoneClockBlockEntity.NUM_SLOT), new SimpleContainerData(RedstoneClockBlockEntity.NUM_DATA));
	}
	public RedstoneClockMenu(int id, Container container, ContainerData containerData) {
		super(RCMenuTypes.REDSTONE_CLOCK.get(), id);
		checkContainerSize(container, RedstoneClockBlockEntity.NUM_SLOT);
		checkContainerDataCount(containerData, RedstoneClockBlockEntity.NUM_DATA);
		this.redstoneClockContainer = container;
		this.redstoneClock = containerData;
		this.addDataSlots(containerData);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		switch(id) {
			case 0 -> this.setData(RedstoneClockBlockEntity.DATA_SIGNAL_STRENGTH, Math.min(
					15, this.redstoneClock.get(RedstoneClockBlockEntity.DATA_SIGNAL_STRENGTH) + 1
			));
			case 1 -> this.setData(RedstoneClockBlockEntity.DATA_SIGNAL_STRENGTH, Math.max(
					1, this.redstoneClock.get(RedstoneClockBlockEntity.DATA_SIGNAL_STRENGTH) - 1
			));
			case 2 -> this.setData(RedstoneClockBlockEntity.DATA_ACTIVE_INTERVAL, Math.min(
					240000, this.redstoneClock.get(RedstoneClockBlockEntity.DATA_ACTIVE_INTERVAL) + toMultiplier(this.multiplier)
			));
			case 3 -> this.setData(RedstoneClockBlockEntity.DATA_ACTIVE_INTERVAL, Math.max(
					2, this.redstoneClock.get(RedstoneClockBlockEntity.DATA_ACTIVE_INTERVAL) - toMultiplier(this.multiplier)
			));
			case 4 -> this.setData(RedstoneClockBlockEntity.DATA_IDLE_INTERVAL, Math.min(
					240000, this.redstoneClock.get(RedstoneClockBlockEntity.DATA_IDLE_INTERVAL) + toMultiplier(this.multiplier)
			));
			case 5 -> this.setData(RedstoneClockBlockEntity.DATA_IDLE_INTERVAL, Math.max(
					2, this.redstoneClock.get(RedstoneClockBlockEntity.DATA_IDLE_INTERVAL) - toMultiplier(this.multiplier)
			));
			case 6 -> this.setNextMultiplier();
			default -> {
				return false;
			}
		}
		return true;
	}

	public int getMultiplier() {
		return this.multiplier;
	}
	public void setNextMultiplier() {
		this.multiplier = (this.multiplier + 1) % RedstoneClockBlockEntity.BOUND_MULTIPLIER;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setData(int index, int value) {
		super.setData(index, value);
		this.broadcastChanges();
	}

	@Override
	public boolean stillValid(Player player) {
		return this.redstoneClockContainer.stillValid(player);
	}
}