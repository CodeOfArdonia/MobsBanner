package com.iafenvoy.mobsbanner.mixin;

import com.iafenvoy.mobsbanner.banner.MobBannerHelper;
import com.iafenvoy.mobsbanner.component.BannerBlockComponent;
import com.iafenvoy.mobsbanner.component.CcaComponentHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerBlockEntity.class)
public class BannerBlockEntityMixin extends BlockEntity {
    public BannerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "readFrom(Lnet/minecraft/item/ItemStack;)V", at = @At("RETURN"))
    private void attachToBlock(ItemStack stack, CallbackInfo ci) {
        BannerBlockComponent component = BannerBlockComponent.get((BannerBlockEntity) (Object) this);
        component.readFromNbt(CcaComponentHelper.resolve(BannerBlockComponent.ID, stack));
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    private void randomData(NbtCompound nbt, CallbackInfo ci) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            BannerBlockComponent component = BannerBlockComponent.get((BannerBlockEntity) (Object) this);
            if (!component.getEntityData().isEmpty()) return;
            LivingEntity living = MobBannerHelper.createEntity(component.getType(), component.getEntityData(), serverWorld);
            if (living == null) return;
            NbtCompound compound = new NbtCompound();
            living.writeCustomDataToNbt(compound);
            MobBannerHelper.removeUnusedNbt(compound);
            component.setEntityData(compound);
        }
    }

    @ModifyReturnValue(method = "getPickStack", at = @At("RETURN"))
    private ItemStack attachToPickStack(ItemStack original) {
        NbtCompound nbt = new NbtCompound();
        BannerBlockComponent.get((BannerBlockEntity) (Object) this).writeToNbt(nbt);
        CcaComponentHelper.packetCcaNbt(original, BannerBlockComponent.ID, nbt);
        return original;
    }
}
