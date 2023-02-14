package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.FlippingAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import net.minecraft.entity.player.PlayerEntity;

import java.nio.ByteBuffer;

public class Flipping extends Action {

	public enum FlippingDirection {
		Front, Back;

		public int getCode() {
			switch (this) {
				case Front:
					return 0;
				case Back:
					return 1;
			}
			return -1;
		}

		public static FlippingDirection getFromCode(int code) {
			switch (code) {
				case 0:
					return Front;
				case 1:
					return Back;
			}
			return null;
		}
	}

	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startInfo) {
		FlippingDirection fDirection;
		if (KeyBindings.getKeyBack().isDown()) {
			fDirection = FlippingDirection.Back;
		} else {
			fDirection = FlippingDirection.Front;
		}
		startInfo.putInt(fDirection.getCode());
		return (parkourability.getPermission().canFlipping()
				&& !stamina.isExhausted()
				&& parkourability.get(AdditionalProperties.class).getNotLandingTick() <= 1
				&& KeyBindings.getKeyRight().isDown()
				&& KeyRecorder.keyRight.getTickKeyDown() < 3
				&& KeyBindings.getKeyLeft().isDown()
				&& KeyRecorder.keyLeft.getTickKeyDown() < 3
		);
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return !player.isOnGround() || getDoingTick() <= 2;
	}

	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startData) {
		player.jumpFromGround();
		stamina.consume(parkourability.getActionInfo().getStaminaConsumptionFlipping(), player);
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new FlippingAnimator(
					FlippingDirection.getFromCode(startData.getInt())
			));
		}
	}

	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new FlippingAnimator(
					FlippingDirection.getFromCode(startData.getInt())
			));
		}
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
	}
}
