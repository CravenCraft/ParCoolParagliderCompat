package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncClientInformationMessage {
	private final ByteBuffer data = ByteBuffer.allocate(512);
	private UUID playerID = null;
	private boolean requestLimitations = false;

	public void encode(PacketBuffer packet) {
		packet.writeLong(playerID.getMostSignificantBits());
		packet.writeLong(playerID.getLeastSignificantBits());
		packet.writeBoolean(requestLimitations);
		packet.writeBytes(data);
		data.rewind();
	}

	public static SyncClientInformationMessage decode(PacketBuffer packet) {
		SyncClientInformationMessage message = new SyncClientInformationMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		message.requestLimitations = packet.readBoolean();
		while (packet.isReadable()) {
			message.data.put(packet.readByte());
		}
		message.data.flip();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player;
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				World world = Minecraft.getInstance().level;
				if (world == null) return;
				player = world.getPlayerByUUID(playerID);
				if (player == null) return;
			} else {
				ServerPlayerEntity serverPlayer = contextSupplier.get().getSender();
				player = serverPlayer;
				if (player == null) return;
				ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);
				if (requestLimitations) {
					SyncLimitationMessage.sendServerLimitation(serverPlayer);
					SyncLimitationMessage.sendIndividualLimitation(serverPlayer);
				}
			}
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			if (!player.isLocalPlayer()) {
				parkourability.getClientInfo().readFrom(data);
				data.rewind();
			}
			parkourability.getClientInfo().setSynced(true);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public void handleServer(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			ServerPlayerEntity player = contextSupplier.get().getSender();
			if (player == null) return;
			ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);

			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			if (requestLimitations) {
				SyncLimitationMessage.sendServerLimitation(player);
				SyncLimitationMessage.sendIndividualLimitation(player);
			}
			parkourability.getClientInfo().readFrom(data);
			data.rewind();
			parkourability.getClientInfo().setSynced(true);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(ClientPlayerEntity player, boolean requestSendLimitation) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		parkourability.getClientInfo().readFromLocalConfig();
		SyncClientInformationMessage message = new SyncClientInformationMessage();
		parkourability.getClientInfo().setSynced(false);
		parkourability.getClientInfo().writeTo(message.data);
		message.data.flip();
		message.playerID = player.getUUID();
		message.requestLimitations = requestSendLimitation;

		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
	}
}
