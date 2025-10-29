/*
 *
 * Better Combat Mod
 * Copyright (C) 2024 The Better Combat Team
 * License: GNU General Public License v3.0 or later
 */

package net.bettercombat.forge;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

public class PlatformClientImpl {
    public static void registerKeyBindings(List<KeyBinding> keyBindings) {
        // Do nothing, Forge asks us to register
    }
    public static void onEmptyLeftClick(PlayerEntity player) {
        MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.LeftClickEmpty(player));
    }
}
