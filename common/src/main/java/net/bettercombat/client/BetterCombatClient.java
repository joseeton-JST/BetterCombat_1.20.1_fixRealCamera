package net.bettercombat.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.bettercombat.BetterCombat;
import net.bettercombat.Platform;
import net.bettercombat.PlatformClient;
import net.bettercombat.client.animation.AnimationRegistry;
import net.bettercombat.config.ClientConfig;
import net.bettercombat.config.ClientConfigWrapper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.ActionResult;

@Environment(EnvType.CLIENT)
public class BetterCombatClient implements ClientModInitializer {
    public static boolean ENABLED = false;
    public static ClientConfig config;
    @Override
    public void onInitializeClient() {
        AutoConfig.register(ClientConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        var holder = AutoConfig.getConfigHolder(ClientConfigWrapper.class);
        // Intuitive way to load a config :)
        config = holder.getConfig().client;
        if (normalizeAttackMovementLock(config)) {
            holder.save();
        }
        holder.registerLoadListener((manager, data) -> {
            config = data.client;
            if (normalizeAttackMovementLock(config)) {
                manager.save();
            }
            return ActionResult.SUCCESS;
        });
        holder.registerSaveListener((manager, data) -> {
            normalizeAttackMovementLock(data.client);
            config = data.client;
            return ActionResult.SUCCESS;
        });

        ClientNetwork.initializeHandlers();
        WeaponAttributeTooltip.initialize();
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            var resourceManager = MinecraftClient.getInstance().getResourceManager();
            AnimationRegistry.load(resourceManager);
        });
        PlatformClient.registerKeyBindings(BetterCombatKeybindings.all);

        if (Platform.Fabric) { // forge renames this method
            ModelPredicateProviderRegistry.register(new Identifier(BetterCombat.MODID, "loaded"), (stack, world, entity, seed) -> {
                return 1.0F;
            });
        }
    }

    private static boolean normalizeAttackMovementLock(ClientConfig config) {
        if (config == null) {
            return false;
        }
        boolean changed = false;
        if (config.attackMovementLockSpeedPercent < 0) {
            config.attackMovementLockSpeedPercent = 0;
            changed = true;
        } else if (config.attackMovementLockSpeedPercent > 100) {
            config.attackMovementLockSpeedPercent = 100;
            changed = true;
        }
        if (!config.attackMovementLockSpeedPercentInitialized) {
            if (config.attackMovementLockSpeedPercent == 100) {
                config.attackMovementLockSpeedPercent = 0;
            }
            config.attackMovementLockSpeedPercentInitialized = true;
            changed = true;
        }
        return changed;
    }
}
