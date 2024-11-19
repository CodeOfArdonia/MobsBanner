package com.iafenvoy.mobsbanner.component;

import com.iafenvoy.mobsbanner.MobsBanner;
import com.iafenvoy.mobsbanner.data.DefaultMobBannerData;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class BannerBlockComponent implements AutoSyncedComponent, ComponentV3 {
    public static final Identifier ID = new Identifier(MobsBanner.MOD_ID, "banner_block");
    public static final ComponentKey<BannerBlockComponent> KEY = ComponentRegistry.getOrCreate(ID, BannerBlockComponent.class);
    private EntityType<?> type;
    private NbtCompound entityData = new NbtCompound();
    private DefaultMobBannerData.TransformData transform = DefaultMobBannerData.TransformData.create();

    @Override
    public void readFromNbt(NbtCompound nbt) {
        try {
            Identifier id = new Identifier(nbt.getString("type"));
            if (Registries.ENTITY_TYPE.containsId(id)) this.setType(Registries.ENTITY_TYPE.get(id));
            else this.setType(null);
        } catch (Exception ignored) {
            this.setType(null);
        }
        this.setEntityData(nbt.getCompound("nbt"));
        this.setTransform(DefaultMobBannerData.TransformData.CODEC.parse(NbtOps.INSTANCE, nbt.get("transform")).resultOrPartial(MobsBanner.LOGGER::error).orElse(DefaultMobBannerData.TransformData.create()));
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        if (this.getType() != null)
            nbt.putString("type", Registries.ENTITY_TYPE.getId(this.getType()).toString());
        nbt.put("nbt", this.getEntityData());
        nbt.put("transform", DefaultMobBannerData.TransformData.CODEC.encodeStart(NbtOps.INSTANCE, this.getTransform()).getOrThrow(true, MobsBanner.LOGGER::error));
    }

    @Nullable
    public EntityType<?> getType() {
        return this.type;
    }

    public NbtCompound getEntityData() {
        return this.entityData;
    }

    public void setType(EntityType<?> type) {
        this.type = type;
    }

    public void setEntityData(NbtCompound entityData) {
        this.entityData = entityData;
    }

    public DefaultMobBannerData.TransformData getTransform() {
        return this.transform;
    }

    public void setTransform(DefaultMobBannerData.TransformData transform) {
        this.transform = transform;
    }

    public static BannerBlockComponent get(BannerBlockEntity blockEntity) {
        return KEY.get(blockEntity);
    }
}
