/*
 * This file is part of Titanium
 * Copyright (C) 2018, Horizon Studio <contact@hrznstudio.com>, All rights reserved.
 */
package com.hrznstudio.titanium.core.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GetBlockModelEvent extends Event {

    private final BlockModelShapes blockModelShapes;
    private final IBlockState blockState;
    private IBakedModel blockModel;

    public GetBlockModelEvent(BlockModelShapes blockModelShapes, IBlockState blockState, IBakedModel blockModel) {
        this.blockModelShapes = blockModelShapes;
        this.blockState = blockState;
        this.blockModel = blockModel;
    }

    public BlockModelShapes getBlockModelShapes() {
        return blockModelShapes;
    }

    public IBlockState getBlockState() {
        return blockState;
    }

    public IBakedModel getBlockModel() {
        return blockModel;
    }

    public void setBlockModel(IBakedModel blockModel) {
        this.blockModel = blockModel;
    }
}
