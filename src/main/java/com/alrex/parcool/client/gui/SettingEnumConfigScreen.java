package com.alrex.parcool.client.gui;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.SyncClientInformationMessage;
import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class SettingEnumConfigScreen extends ParCoolSettingScreen {
    private final EnumConfigSet<?>[] enumConfigList = new EnumConfigSet[]{
            new EnumConfigSet<>(ParCoolConfig.Client.AlignHorizontalStaminaHUD),
            new EnumConfigSet<>(ParCoolConfig.Client.AlignVerticalStaminaHUD),
            new EnumConfigSet<>(ParCoolConfig.Client.FastRunControl),
            new EnumConfigSet<>(ParCoolConfig.Client.CrawlControl),
            new EnumConfigSet<>(ParCoolConfig.Client.FlipControl),
            new EnumConfigSet<>(ParCoolConfig.Client.VaultAnimationMode),
            new EnumConfigSet<>(ParCoolConfig.Client.GUIColorTheme),
            new EnumConfigSet<>(ParCoolConfig.Client.StaminaHUDType),
    };
    private final Button[] enumConfigButtons = new Button[enumConfigList.length];

    public SettingEnumConfigScreen(Component titleIn, ActionInfo info, ColorTheme theme) {
        super(titleIn, info, theme);
        currentScreen = 2;
        for (int i = 0; i < enumConfigList.length; i++) {
            int index = i;
            enumConfigButtons[index] = new Button(0, 0, 0, 0, Component.literal(enumConfigList[index].get().toString()), it -> {
                enumConfigList[index].next();
                it.setMessage(Component.literal(enumConfigList[index].get().toString()));
            });
        }
    }

    @Override
    protected boolean isDownScrollable() {
        return topIndex + viewableItemCount < enumConfigButtons.length;
    }

    @Override
    protected void renderContents(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int topOffset, int bottomOffset) {
        final int offsetX = 40;
        int contentWidth = width - offsetX * 2;
        int contentHeight = height - topOffset - bottomOffset;
        viewableItemCount = contentHeight / Checkbox_Item_Height;
        for (Button configButton : enumConfigButtons) {
            configButton.setWidth(0);
        }
        for (int i = 0; i < viewableItemCount && i + topIndex < enumConfigList.length; i++) {
            Button button = enumConfigButtons[i + topIndex];
            int buttonWidth = contentWidth / 3;
            button.x = width - offsetX - buttonWidth;
            button.y = topOffset + Checkbox_Item_Height * i;
            button.setWidth(buttonWidth);
            button.setHeight(20);
            button.render(poseStack, mouseX, mouseY, partialTick);
            List<String> path = enumConfigList[i + topIndex].configInstance.getPath();
            drawString(poseStack, font, path.get(path.size() - 1), offsetX + 6, button.y + 1 + (button.getHeight() - font.lineHeight) / 2, color.getText());
            fill(poseStack, offsetX, button.y + button.getHeight(), width - offsetX, button.y + button.getHeight() + 1, color.getSubSeparator());
        }
        fill(poseStack, width - offsetX, topOffset, width - offsetX - 1, topOffset + contentHeight, color.getSeparator());
        fill(poseStack, offsetX, topOffset, offsetX + 1, topOffset + contentHeight, color.getSeparator());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int type) {
        for (Button button : enumConfigButtons) {
            if (button.mouseClicked(mouseX, mouseY, type)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, type);
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void save() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
        SyncClientInformationMessage.sync(player, true);
    }

    private static class EnumConfigSet<T extends Enum<T>> {
        final ForgeConfigSpec.EnumValue<T> configInstance;
        final T[] values;

        public EnumConfigSet(ForgeConfigSpec.EnumValue<T> configInstance) {
            this.configInstance = configInstance;
            values = configInstance.get().getDeclaringClass().getEnumConstants();
        }

        public void next() {
            int index = (configInstance.get().ordinal() + 1) % values.length;
            configInstance.set(values[index]);
        }

        public T get() {
            return configInstance.get();
        }
    }
}
