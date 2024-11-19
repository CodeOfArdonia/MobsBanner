package com.iafenvoy.mobsbanner.mixin;

import com.iafenvoy.mobsbanner.MobsBanner;
import com.iafenvoy.mobsbanner.banner.MobBannerHelper;
import com.mojang.authlib.GameProfile;
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

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    public abstract GameProfile getGameProfile();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;emitGameEvent(Lnet/minecraft/world/event/GameEvent;)V"))
    private void handleDrop(DamageSource source, float amount, CallbackInfo ci) {
        if (this.isDead() && this.random.nextDouble() < this.getWorld().getGameRules().get(MobsBanner.DROP_CHANCE).get()) {
            NbtCompound compound = new NbtCompound();
            this.writeCustomDataToNbt(compound);
            compound.putUuid("player_uuid", this.getGameProfile().getId());
            compound.putString("player_name", this.getGameProfile().getName());
            this.dropStack(MobBannerHelper.create(this.getType(), compound));
        }
    }
}
