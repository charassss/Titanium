/*
 * This file is part of Titanium
 * Copyright (C) 2021, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.client.screen.addon.interfaces;

import net.minecraft.nbt.CompoundNBT;

public interface INetworkable {
    void sendMessage(int id, CompoundNBT data);
}
