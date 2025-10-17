/*
 * Better Combat Mod
 * Copyright (C) 2024 The Better Combat Team
 * License: GNU General Public License v3.0 or later
 */

package net.bettercombat.compatibility;

import java.util.function.Supplier;

import net.bettercombat.Platform;

public class CompatibilityFlags {


    public static boolean firstPersonRender() {
        return firstPersonRenderSupplier.get();
    }
    private static Supplier<Boolean> firstPersonRenderSupplier = () -> false;
    public static boolean usePehkui = false;

    public static void initialize() {
        firstPersonRenderSupplier = FirstPersonModelHelper.isDisabled();
        if (Platform.isModLoaded("pehkui")) {
            usePehkui = true;
            PehkuiHelper.load();
        }
    }

    /**
     * Checks if a class exists or not
     * @param name
     * @return
     */
    public static boolean doesClassExist(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        } catch (Throwable t) {
            // Por seguridad, cualquier otro error devolvemos false y no rompemos la carga
            return false;
        }
    }
}
