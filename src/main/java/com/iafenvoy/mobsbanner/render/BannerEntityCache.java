package com.iafenvoy.mobsbanner.render;

import com.iafenvoy.mobsbanner.banner.MobBannerHelper;
import com.iafenvoy.mobsbanner.component.BannerBlockComponent;
import com.iafenvoy.mobsbanner.cursed.DummyClientPlayerEntity;
import com.iafenvoy.mobsbanner.data.DefaultMobBannerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class BannerEntityCache {
    private static final List<Cache> CACHE = new LinkedList<>();

    @Nullable
    public static LivingEntity get(BannerBlockComponent component, BlockPos pos) {
        boolean fulfilled = !component.getEntityData().isEmpty();
        if (component.getType() == null) return null;
        if (fulfilled) {
            Optional<Cache> cache = CACHE.stream().filter(x -> x.match(component)).findFirst();
            if (cache.isPresent()) return cache.get().living;
        }
        LivingEntity living = createEntity(component.getType(), component.getEntityData(), MinecraftClient.getInstance().world);
        if (living == null) return null;
        if (fulfilled)
            CACHE.add(new Cache(component.getType(), component.getEntityData(), component.getTransform(), pos, living));
        return living;
    }

    @Nullable
    public static LivingEntity createEntity(EntityType<?> type, NbtCompound nbt, World world) {
        if (type == EntityType.PLAYER)
            return DummyClientPlayerEntity.get(nbt.containsUuid("player_uuid") ? nbt.getUuid("player_uuid") : null, nbt.getString("player_name"));
        return MobBannerHelper.createEntity(type, nbt, world);
    }

    private record Cache(EntityType<?> type, NbtCompound nbt, DefaultMobBannerData.TransformData transform,
                         BlockPos pos, LivingEntity living) {
        public boolean match(BannerBlockComponent component) {
            return this.type == component.getType() && this.nbt.equals(component.getEntityData()) && this.transform.equals(component.getTransform());
        }
    }
}
