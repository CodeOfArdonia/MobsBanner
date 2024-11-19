package com.iafenvoy.mobsbanner.render;

import com.iafenvoy.mobsbanner.component.BannerBlockComponent;
import com.iafenvoy.mobsbanner.data.DefaultMobBannerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;

import java.util.Stack;

public class MobRenderHelper {
    public static final Stack<BannerBlockComponent> CURRENT_COMPONENT = new Stack<>();

    public static void drawEntityOnCanvas(Entity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, DefaultMobBannerData.TransformData transform) {
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
        matrices.translate(0, 0, -0.15);
        matrices.translate(0, 1.2 + 1.3 * height / 4, 0);
        matrices.scale(0.6f, 0.6f, 0.02f);
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
}
