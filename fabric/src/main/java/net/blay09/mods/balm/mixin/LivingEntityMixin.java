package net.blay09.mods.balm.mixin;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.LivingDamageEvent;
import net.blay09.mods.balm.api.event.LivingFallEvent;
import net.blay09.mods.balm.api.event.LivingHealEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    private static final ThreadLocal<LivingFallEvent> currentFallEvent = new ThreadLocal<>();

    @Inject(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", at = @At("HEAD"))
    private void actuallyHurt(DamageSource damageSource, float damageAmount, CallbackInfo callbackInfo) {
        Balm.getEvents().fireEvent(new LivingDamageEvent((LivingEntity) (Object) this, damageSource, damageAmount));
    }

    @Inject(method = "causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("HEAD"), cancellable = true)
    private void causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> callbackInfo) {
        LivingFallEvent event = new LivingFallEvent((LivingEntity) (Object) this);
        Balm.getEvents().fireEvent(event);
        if (event.isCanceled()) {
            callbackInfo.setReturnValue(false);
        }
        currentFallEvent.set(event);
    }

    @ModifyVariable(method = "causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("STORE"), ordinal = 0)
    private int modifyFallDamage(int damage) {
        LivingFallEvent event = currentFallEvent.get();
        float effectiveDamage = damage;
        if (event != null && event.getFallDamageOverride() != null) {
            effectiveDamage = event.getFallDamageOverride();
        }
        return (int) effectiveDamage;
    }

    @ModifyVariable(method = "heal(F)V", at = @At("HEAD"), argsOnly = true)
    private float modifyHealing(float heal) {
        LivingEntity entity = (LivingEntity) (Object) this;
        LivingHealEvent event = new LivingHealEvent(entity, heal);
        return event.isCanceled() ? 0f : heal;
    }

}
