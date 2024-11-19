package com.iafenvoy.mobsbanner.mixin;

import com.iafenvoy.mobsbanner.component.BannerBlockComponent;
import com.iafenvoy.mobsbanner.cursed.DummyClientPlayerEntity;
import com.iafenvoy.mobsbanner.render.MobRenderHelper;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(BannerBlockEntityRenderer.class)
public class BannerBlockEntityRendererMixin {
    @Inject(method = "render(Lnet/minecraft/block/entity/BannerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("HEAD"))
    private void onStartRender(BannerBlockEntity bannerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        MobRenderHelper.CURRENT_COMPONENT.push(BannerBlockComponent.get(bannerBlockEntity));
    }

    @Inject(method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLjava/util/List;Z)V", at = @At("RETURN"))
    private static void onRenderCanvas(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ModelPart canvas, SpriteIdentifier baseSprite, boolean isBanner, List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns, boolean glint, CallbackInfo ci) {
        if (MobRenderHelper.CURRENT_COMPONENT.isEmpty()) return;
        BannerBlockComponent component = MobRenderHelper.CURRENT_COMPONENT.peek();
        if (component.getType() != null) {
            LivingEntity living = null;
            if (component.getType() == EntityType.PLAYER) {
                NbtCompound nbt = component.getEntityData();
                living = DummyClientPlayerEntity.get(nbt.containsUuid("player_uuid") ? nbt.getUuid("player_uuid") : null, nbt.getString("player_name"));
            } else if (component.getType().create(MinecraftClient.getInstance().world) instanceof LivingEntity livingEntity) {
                try {
                    livingEntity.readCustomDataFromNbt(component.getEntityData());
                } catch (Exception ignored) {
                }
                living = livingEntity;
            }
            if (living != null) {
                canvas.rotate(matrices);
                MobRenderHelper.drawEntityOnCanvas(living, matrices, vertexConsumers, light, component.getTransform());
            }
        }
        MobRenderHelper.CURRENT_COMPONENT.pop();
    }
}