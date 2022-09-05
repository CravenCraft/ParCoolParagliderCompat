package com.alrex.parcool.proxy;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.gui.ParCoolGuideScreen;
import com.alrex.parcool.client.hud.HUDHost;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.client.hud.impl.StaminaHUDController;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.simple.SimpleChannel;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
	@Override
	public void registerMessages(SimpleChannel instance) {
		instance.registerMessage(
				0,
				ResetFallDistanceMessage.class,
				ResetFallDistanceMessage::encode,
				ResetFallDistanceMessage::decode,
				ResetFallDistanceMessage::handle
		);
		instance.registerMessage(
				1,
				SetActionPossibilityMessage.class,
				SetActionPossibilityMessage::encode,
				SetActionPossibilityMessage::decode,
				SetActionPossibilityMessage::handle
		);
		instance.registerMessage(
				2,
				ShowActionPossibilityMessage.class,
				ShowActionPossibilityMessage::encode,
				ShowActionPossibilityMessage::decode,
				ShowActionPossibilityMessage::handle
		);
		instance.registerMessage(
				3,
				StartBreakfallMessage.class,
				StartBreakfallMessage::encode,
				StartBreakfallMessage::decode,
				StartBreakfallMessage::handleClient
		);
		instance.registerMessage(
				10,
				SyncStaminaMessage.class,
				SyncStaminaMessage::encode,
				SyncStaminaMessage::decode,
				SyncStaminaMessage::handleClient
		);
		instance.registerMessage(
				12,
				ActionPermissionsMessage.class,
				ActionPermissionsMessage::encode,
				ActionPermissionsMessage::decode,
				ActionPermissionsMessage::handle
		);
		instance.registerMessage(
				14,
				StartVaultMessage.class,
				StartVaultMessage::encode,
				StartVaultMessage::decode,
				StartVaultMessage::handleClient
		);
		instance.registerMessage(
				15,
				SyncActionStateMessage.class,
				SyncActionStateMessage::encode,
				SyncActionStateMessage::decode,
				SyncActionStateMessage::handleClient
		);
	}

	@Override
	public void showParCoolGuideScreen(Player playerIn) {
		if (playerIn.level.isClientSide) {
			Minecraft.getInstance().setScreen(new ParCoolGuideScreen());
		}
	}

	@Override
	public void registerModBus(IEventBus bus) {
		super.registerModBus(bus);
		bus.register(KeyBindings.class);
		bus.register(HUDHost.getInstance());
	}

	@Override
	public void setup() {
		super.setup();
		HUDHost.getInstance().getHuds().add(
				new StaminaHUDController(
						new Position(
								ParCoolConfig.CONFIG_CLIENT.alignHorizontalStaminaHUD.get(),
								ParCoolConfig.CONFIG_CLIENT.alignVerticalStaminaHUD.get(),
								ParCoolConfig.CONFIG_CLIENT.marginHorizontalStaminaHUD.get(),
								ParCoolConfig.CONFIG_CLIENT.marginVerticalStaminaHUD.get()
						)
				));
	}
}
