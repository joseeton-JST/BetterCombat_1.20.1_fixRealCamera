package net.bettercombat.compatibility;

import java.util.function.Supplier;

import net.bettercombat.Platform;

public class CompatibilityFlags {   


    public static boolean firstPersonRender() {
        return false;
    }
    public static Supplier<Boolean> firstPersonRender = () -> { return false; };
    public static boolean usePehkui = false;

    public static void initialize() {
        firstPersonRender = FirstPersonModelHelper.isDisabled();
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
