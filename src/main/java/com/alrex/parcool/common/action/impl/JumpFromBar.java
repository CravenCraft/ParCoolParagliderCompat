package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.JumpFromBarAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.EntityUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;

import java.nio.ByteBuffer;

public class JumpFromBar extends Action {
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		HangDown hangDown = parkourability.get(HangDown.class);
		return hangDown.isDoing()
				&& hangDown.getDoingTick() > 2
				&& parkourability.getActionInfo().can(JumpFromBar.class)
				&& KeyRecorder.keyJumpState.isPressed();
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		return getDoingTick() < 2;
	}

	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		EntityUtil.addVelocity(player, player.getLookAngle().multiply(1, 0, 1).normalize().scale(player.getBbWidth() * 0.75));
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1f, 0.6f);
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new JumpFromBarAnimator());
	}

	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new JumpFromBarAnimator());
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}
}
