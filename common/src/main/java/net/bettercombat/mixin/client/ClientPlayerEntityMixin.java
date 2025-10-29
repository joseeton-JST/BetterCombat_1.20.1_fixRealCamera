package net.bettercombat.mixin.client;

import net.bettercombat.BetterCombat;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.utils.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(ZF)V", shift = At.Shift.AFTER))
    private void tickMovement_ModifyInput(CallbackInfo ci) {
        var config = BetterCombat.config;
        var clientPlayer = (ClientPlayerEntity)((Object)this);
        var multiplier = Math.min(Math.max(config.movement_speed_while_attacking, 0.0), 1.0);
        var clientConfig = BetterCombatClient.config;
        if (clientConfig != null && clientConfig.isAttackMovementLockEnabled) {
            float clientMultiplier = Float.NaN;
            if (clientConfig.attackMovementLockUsesAttributes) {
                var attributes = WeaponRegistry.getAttributes(clientPlayer.getMainHandStack());
                if (attributes != null) {
                    var rawValue = attributes.movementSpeedWhileAttackingRaw();
                    if (rawValue != null) {
                        clientMultiplier = (float) Math.min(Math.max(rawValue, 0.0D), 1.0D);
                    }
                }
            }
            if (Float.isNaN(clientMultiplier)) {
                clientMultiplier = Math.min(Math.max(clientConfig.attackMovementLockSpeedPercent / 100.0F, 0.0F), 1.0F);
            }
            multiplier = Math.min(multiplier, clientMultiplier);
        }
//        System.out.println("Multiplier " + multiplier);
        if (multiplier == 1) {
            return;
        }
        if (clientPlayer.hasVehicle() && !config.movement_speed_effected_while_mounting) {
            return;
        }
        var client = (MinecraftClient_BetterCombat) MinecraftClient.getInstance();
        var swingProgress = client.getSwingProgress();
        if (swingProgress < 0.98) {
            if (config.movement_speed_applied_smoothly) {
                double p2 = 0;
                if (swingProgress <= 0.5) {
                    p2 = MathHelper.easeOutCubic(swingProgress * 2);
                } else {
                    p2 = MathHelper.easeOutCubic(1 - ((swingProgress - 0.5) * 2));
                }
                multiplier = (float) ( 1.0 - (1.0 - multiplier) * p2 );
//                var chart = "-".repeat((int)(100.0 * multiplier)) + "x";
//                System.out.println("Movement speed multiplier: " + String.format("%.4f", multiplier) + ">" + chart);
            }
            clientPlayer.input.movementForward *= multiplier;
            clientPlayer.input.movementSideways *= multiplier;
        }
    }
}
