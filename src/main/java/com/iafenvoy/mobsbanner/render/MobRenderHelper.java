package com.iafenvoy.mobsbanner.render;

import com.iafenvoy.mobsbanner.component.BannerBlockComponent;
import com.iafenvoy.mobsbanner.data.DefaultMobBannerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import java.util.Stack;

@Environment(EnvType.CLIENT)
public class MobRenderHelper {
    public static final Stack<StackHolder> RENDER_STACK = new Stack<>();

    public static void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ModelPart canvas) {
        if (RENDER_STACK.isEmpty()) return;
        StackHolder holder = MobRenderHelper.RENDER_STACK.peek();
        LivingEntity living = BannerEntityCache.get(holder.component, holder.pos);
        if (living != null) {
            canvas.rotate(matrices);
            MobRenderHelper.drawEntityOnCanvas(living, matrices, vertexConsumers, light, holder.component.getTransform(), holder.isShield);
        }
        MobRenderHelper.RENDER_STACK.pop();
    }

    public static void drawEntityOnCanvas(Entity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, DefaultMobBannerData.TransformData transform, boolean isShield) {
        double width = entity.getBoundingBox().getXLength();
        double height = entity.getBoundingBox().getYLength();
        if (width > 0.6) {
            width *= 1.0 / (width / 0.6);
            height = entity.getBoundingBox().getYLength() * (width / entity.getBoundingBox().getXLength());
        }
        if (height > 2.0) {
            width *= 1.0 / (height / 2.0);
            height = entity.getBoundingBox().getYLength() * (width / entity.getBoundingBox().getXLength());
        }
        transform = transform.combineWith(DefaultMobBannerData.getTransform(entity.getType()));

        matrices.push();
        if (isShield) {
            matrices.scale(0.5F, 0.5F, 0.5F);
            matrices.translate(0, -1.25, -0.13);
        }
        matrices.translate(0, 0, -0.13);
        matrices.translate(0, 1.2 + 1.3 * height / 4, 0);
        matrices.scale(0.6f, 0.6f, 0.002f);
        matrices.scale((float) (width / entity.getBoundingBox().getXLength()), (float) (width / entity.getBoundingBox().getXLength()), (float) (width / entity.getBoundingBox().getXLength()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-30.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(225.0f + transform.yaw()));

        if (entity instanceof PlayerEntity player) {
            player.prevCapeX = player.capeX = player.getX();
            player.prevCapeY = player.capeY = player.getY();
            player.prevCapeZ = player.capeZ = player.getZ();
        }

        float scale = transform.scale();
        matrices.scale(scale, scale, scale);
        EntityRenderDispatcher renderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        renderDispatcher.setRenderShadows(false);
        renderDispatcher.render(entity, 0, 0, 0, 0, 0, matrices, vertexConsumers, light);
        renderDispatcher.setRenderShadows(true);
        matrices.pop();
    }

    public record StackHolder(BannerBlockComponent component, BlockPos pos, boolean isShield) {
    }
}
