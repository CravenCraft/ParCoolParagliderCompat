package com.alrex.parcool.common.event;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.network.ActionPermissionsMessage;
import com.alrex.parcool.common.network.DisableInfiniteStaminaMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventPlayerJoin {
	@SubscribeEvent
	public static void JoinEvent(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) entity;
			ActionPermissionsMessage.send(player);
			DisableInfiniteStaminaMessage.send(player, ParCoolConfig.CONFIG_SERVER.allowInfiniteStamina.get());
		}
	}
}
