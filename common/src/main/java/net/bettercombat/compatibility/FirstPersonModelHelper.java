/*
 * Better Combat Mod
 * Copyright (C) 2024 The Better Combat Team
 * License: GNU General Public License v3.0 or later
 */

package net.bettercombat.compatibility;

import net.bettercombat.Platform;
import net.bettercombat.config.ClientConfig;

import java.lang.reflect.Field;
import java.util.function.Supplier;

public class FirstPersonModelHelper {
    private static final String FIRST_PERSON_MODEL_CLASS = "dev.tr7zw.firstperson.FirstPersonModelMod";
    private static final String CLIENT_CONFIG_HOLDER = "net.bettercombat.client.BetterCombatClient";
    private static final ClientConfig DEFAULT_CLIENT_CONFIG = new ClientConfig();
    private static Field clientConfigField;
    private static boolean clientConfigFieldResolved;

    /**
     * Creates a supplier that reflects the current ability to render first-person
     * animations. The returned supplier re-reads the client configuration every
     * time it is invoked so runtime config reloads are respected without
     * reinitializing the compatibility flags.
     */
    public static Supplier<Boolean> createFirstPersonRenderSupplier() {
        final boolean externalFirstPersonLoaded = Platform.isModLoaded("firstperson")
                || Platform.isModLoaded("firstpersonmod")
                || CompatibilityFlags.doesClassExist(FIRST_PERSON_MODEL_CLASS);

        return () -> {
            ClientConfig config = findClientConfig();
            boolean enabledInConfig = config != null
                    ? config.enableFirstPersonView
                    : DEFAULT_CLIENT_CONFIG.enableFirstPersonView;

            if (!enabledInConfig) {
                return false;
            }

            return !externalFirstPersonLoaded;
        };
    }

    private static ClientConfig findClientConfig() {
        if (!clientConfigFieldResolved) {
            clientConfigFieldResolved = true;
            try {
                Class<?> clientClass = Class.forName(CLIENT_CONFIG_HOLDER);
                clientConfigField = clientClass.getField("config");
            } catch (ClassNotFoundException | NoSuchFieldException ignored) {
                clientConfigField = null;
            } catch (Throwable ignored) {
                clientConfigField = null;
            }
        }

        if (clientConfigField == null) {
            return null;
        }

        try {
            Object value = clientConfigField.get(null);
            if (value instanceof ClientConfig clientConfig) {
                return clientConfig;
            }
        } catch (IllegalAccessException ignored) {
            return null;
        } catch (Throwable ignored) {
            return null;
        }

        return null;
    }
}
