/*
 * This file is part of Titanium
 * Copyright (C) 2023, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.client.screen.addon;

import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IAsset;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.IEnergyStorage;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class EnergyBarScreenAddon extends BasicScreenAddon {

    private final IEnergyStorage handler;
    private IAsset background;

    public EnergyBarScreenAddon(int posX, int posY, IEnergyStorage handler) {
        super(posX, posY);
        this.handler = handler;
    }

    public static IAsset drawBackground(MatrixStack stack, Screen screen, IAssetProvider provider, int handlerPosX, int handlerPosY, int guiX, int guiY) {
        IAsset background = IAssetProvider.getAsset(provider, AssetTypes.ENERGY_BACKGROUND);
        Point offset = background.getOffset();
        Rectangle area = background.getArea();
        AssetUtil.drawAsset(stack, screen, background, guiX + handlerPosX + offset.x, guiY + handlerPosY + offset.y);
        return background;
    }

    public static void drawForeground(MatrixStack stack, Screen screen, IAssetProvider provider, int handlerPosX, int handlerPosY, int guiX, int guiY, double stored, double capacity) {
        IAsset asset = IAssetProvider.getAsset(provider, AssetTypes.ENERGY_BAR);
        Point offset = asset.getOffset();
        Rectangle area = asset.getArea();
        screen.getMinecraft().getTextureManager().bindTexture(asset.getResourceLocation());
        int powerOffset = (int) ((stored / Math.max(capacity, 1)) * area.height);
        screen.blit(stack, handlerPosX + offset.x, handlerPosY + offset.y + area.height - powerOffset, area.x, area.y + (area.height - powerOffset), area.width, powerOffset);
    }

    public static List<ITextComponent> getTooltip(int stored, int capacity) {
        return Arrays.asList(new StringTextComponent(TextFormatting.GOLD + "Power:"), new StringTextComponent(new DecimalFormat().format(stored) + TextFormatting.GOLD + "/" + TextFormatting.WHITE + new DecimalFormat().format(capacity) + TextFormatting.DARK_AQUA + " FE"));
    }

    @Override
    public int getXSize() {
        return background != null ? background.getArea().width : 0;
    }

    @Override
    public int getYSize() {
        return background != null ? background.getArea().height : 0;
    }

    @Override
    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        background = drawBackground(stack, screen, provider, getPosX(), getPosY(), guiX, guiY);
    }

    @Override
    public void drawForegroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY) {
        drawForeground(stack, screen, provider, getPosX(), getPosY(), guiX, guiY, handler.getEnergyStored(), handler.getMaxEnergyStored());
    }

    @Override
    public List<ITextComponent> getTooltipLines() {
        return getTooltip(handler.getEnergyStored(), handler.getMaxEnergyStored());
    }
}
