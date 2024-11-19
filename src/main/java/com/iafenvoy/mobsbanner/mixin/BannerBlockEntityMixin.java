package com.iafenvoy.mobsbanner.mixin;

import com.iafenvoy.mobsbanner.component.BannerBlockComponent;
import com.iafenvoy.mobsbanner.component.CcaComponentHelper;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerBlockEntity.class)
public class BannerBlockEntityMixin {
    @Inject(method = "readFrom(Lnet/minecraft/item/ItemStack;)V", at = @At("RETURN"))
    private void attachMobBanner(ItemStack stack, CallbackInfo ci) {
        BannerBlockComponent.get((BannerBlockEntity) (Object) this).readFromNbt(CcaComponentHelper.resolve(BannerBlockComponent.ID, stack));
    }
}
