package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public interface IStamina {
	@Nullable
	public static IStamina get(Player player) {
		LazyOptional<IStamina> optional = player.getCapability(Capabilities.STAMINA_CAPABILITY);
		return optional.orElse(null);
	}

	public int getMaxStamina();

	public int getActualMaxStamina();

	public void setMaxStamina(int value);

	public int get();

	public int getOldValue();

	public void consume(int value);

	public void recover(int value);

	public boolean isExhausted();

	public void setExhaustion(boolean value);

	public void tick();

	public void set(int value);
}
