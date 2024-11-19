package com.iafenvoy.mobsbanner.mixin;

import com.iafenvoy.mobsbanner.MobsBanner;
import com.iafenvoy.mobsbanner.banner.MobBannerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract boolean isDead();

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;emitGameEvent(Lnet/minecraft/world/event/GameEvent;)V"))
    private void handleDrop(DamageSource source, float amount, CallbackInfo ci) {
        if (this.isDead() && source.getAttacker() instanceof PlayerEntity && this.random.nextDouble() < this.getWorld().getGameRules().get(MobsBanner.DROP_CHANCE).get()) {
            NbtCompound compound = new NbtCompound();
            this.writeCustomDataToNbt(compound);
            this.dropStack(MobBannerHelper.create(this.getType(), compound));
        }
    }
}
