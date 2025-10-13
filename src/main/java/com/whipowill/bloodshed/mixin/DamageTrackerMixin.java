package com.whipowill.bloodshed.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.whipowill.bloodshed.BloodEntity;
import com.whipowill.bloodshed.Bloodshed;

import java.util.Random;

@Mixin(DamageTracker.class)
public abstract class DamageTrackerMixin {

    @Shadow @Final private LivingEntity entity;

    // Use the exact signature from the working version: 3 parameters
    @Inject(at = @At("HEAD"), method = "onDamage")
    public void onDamage(DamageSource source, float originalHealth, float damage, CallbackInfo ci) {
        // Direct CONFIG checks
        if (Bloodshed.CONFIG == null || !Bloodshed.CONFIG.enabled) return;

        // Skip if entity shouldn't bleed
        if (!shouldBleed()) return;

        boolean isMoreOrLess = damage > 2;
        if (!isMoreOrLess) return;

        if (entity.world == null || entity.isInvulnerableTo(source)) return;

        if (source.getSource() != null) {
            spreadBloodDirectly(damage, source.getSource().getEyePos());
            return;
        }

        if (source.getAttacker() != null) {
            spreadBloodDirectly(damage, source.getAttacker().getEyePos());
            return;
        }
        spreadBlood(damage);
    }

    private boolean shouldBleed() {
        Identifier entityId = Registry.ENTITY_TYPE.getId(entity.getType());
        return Bloodshed.CONFIG.shouldEntityBleed(entityId.toString());
    }

    public void spreadBloodDirectly(float amount, Vec3d pos) {
        Vec3d dirPos = this.entity.getEyePos().relativize(pos).normalize();
        dirPos = dirPos.multiply(-0.5);
        int count = calculateBloodCount(amount);
        Random r = new Random();

        for (int i = 0; i <= count; i++) {
            Vec3d localPos = dirPos;
            localPos = localPos.add(
                (r.nextFloat() - 0.5) * 0.25,
                (r.nextFloat() - 0.5) * 0.25,
                (r.nextFloat() - 0.5) * 0.25
            );
            localPos = localPos.multiply((r.nextFloat() * count * 0.5));

            if (!entity.world.isClient) {
                spawnBloodEntity(localPos);
            }
        }
    }

    public void spreadBlood(float amount) {
        int count = calculateBloodCount(amount);
        Random r = new Random();

        for (int i = 0; i <= count; i++) {
            Vec3d localPos = new Vec3d(
                r.nextFloat() - 0.5,
                r.nextFloat() - 0.5,
                r.nextFloat() - 0.5
            );
            localPos = localPos.multiply((r.nextFloat() * count * 0.5));

            if (!entity.world.isClient) {
                spawnBloodEntity(localPos);
            }
        }
    }

    private void spawnBloodEntity(Vec3d velocity) {
        BloodEntity bloodEntity = Bloodshed.BLOOD_ENTITY.create(this.entity.world);
        if (bloodEntity != null) {
            bloodEntity.setPosition(this.entity.getPos().add(0, this.entity.getHeight() * 0.8, 0));
            bloodEntity.setVelocity(velocity);
            this.entity.getWorld().spawnEntity(bloodEntity);
        }
    }

    private int calculateBloodCount(float damage) {
        int baseCount = Math.min((int) damage, 16);
        return (int) (baseCount * Bloodshed.CONFIG.goreValue);
    }
}