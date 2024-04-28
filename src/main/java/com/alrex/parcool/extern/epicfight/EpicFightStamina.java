package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.api.Effects;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.stamina.Stamina;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class EpicFightStamina implements IStamina {
    private final Player player;
    private float consumeBuffer = 0;
    private final Stamina parcoolStamina;

    public EpicFightStamina(Player player) {
        this.player = player;
        parcoolStamina = new Stamina(player);
    }

    @Override
    public int getActualMaxStamina() {
        if (EpicFightManager.isBattleMode(player)) {
            PlayerPatch<?> patch = EpicFightManager.getPlayerPatch(player);
            if (patch == null) return 0;
            return (int) patch.getMaxStamina();
        } else {
            return parcoolStamina.getActualMaxStamina();
        }
    }

    @Override
    public int get() {
        if (EpicFightManager.isBattleMode(player)) {
            PlayerPatch<?> patch = EpicFightManager.getPlayerPatch(player);
            if (patch == null) return 0;
            return (int) patch.getStamina();
        } else {
            return parcoolStamina.get();
        }
    }

    @Override
    public int getOldValue() {
        if (EpicFightManager.isBattleMode(player)) {
            return get();
        } else {
            return parcoolStamina.getOldValue();
        }
    }

    @Override
    public void consume(int value) {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        if (isExhausted()
                || parkourability.getActionInfo().isStaminaInfinite(player.isSpectator() || player.isCreative())
                || player.hasEffect(Effects.INEXHAUSTIBLE.get())
        ) return;
        if (EpicFightManager.isBattleMode(player)) {
            consumeBuffer += value / 15f;
        } else {
            parcoolStamina.consume(value);
        }
    }

    @Override
    public void recover(int value) {
        if (!EpicFightManager.isBattleMode(player)) {
            parcoolStamina.recover(value);
        }
    }

    @Override
    public boolean isExhausted() {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return false;
        if (parkourability.getActionInfo().isStaminaInfinite(player.isSpectator() || player.isCreative())
                || player.hasEffect(Effects.INEXHAUSTIBLE.get())
        ) return false;
        if (!EpicFightManager.isBattleMode(player)) {
            return parcoolStamina.isExhausted();
        }
        return false;
    }

    @Override
    public void setExhaustion(boolean value) {
        if (!EpicFightManager.isBattleMode(player)) {
            parcoolStamina.setExhaustion(value);
        }
    }

    @Override
    public void tick() {
        if (!EpicFightManager.isBattleMode(player)) {
            parcoolStamina.tick();
        }
    }

    @Override
    public void set(int value) {
        if (!EpicFightManager.isBattleMode(player)) {
            parcoolStamina.set(value);
        }
    }

    @Override
    public boolean wantToConsumeOnServer() {
        return EpicFightManager.isBattleMode(player) && consumeBuffer != 0f;
    }

    @Override
    public int getRequestedValueConsumedOnServer() {
        int neededValue = (int) (consumeBuffer * 10000f);
        consumeBuffer = 0f;
        return neededValue;
    }

    public static void consumeOnServer(ServerPlayer player, int value) {
        PlayerPatch<?> patch = EpicFightManager.getPlayerPatch(player);
        if (patch == null) return;
        patch.setStamina(patch.getStamina() - value / 10000f);
    }
}