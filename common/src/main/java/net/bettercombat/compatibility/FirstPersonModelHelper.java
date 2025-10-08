package net.bettercombat.compatibility;

import java.util.function.Supplier;
public class FirstPersonModelHelper {
    public static Supplier<Boolean> isDisabled() {
        return (() -> {
            return true;
        });
    }
}
