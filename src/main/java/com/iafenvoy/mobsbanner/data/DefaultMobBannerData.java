package com.iafenvoy.mobsbanner.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iafenvoy.mobsbanner.MobsBanner;
import com.iafenvoy.mobsbanner.banner.MobBannerHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public enum DefaultMobBannerData implements SimpleSynchronousResourceReloadListener {
    INSTANCE;
    private static final Map<EntityType<?>, DefaultDataCollection> DATA = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(MobsBanner.MOD_ID, "default_mob_banner_data");
    }

    @Override
    public void reload(ResourceManager manager) {
        DATA.clear();
        for (Map.Entry<Identifier, Resource> entry : manager.findResources("default_banner", p -> p.getPath().endsWith(".json")).entrySet()) {
            Identifier id = entry.getKey();
            Resource resource = entry.getValue();
            Identifier entityId = new Identifier(id.getNamespace(), id.getPath().replace("default_banner/", "").replace(".json", ""));
            if (!Registries.ENTITY_TYPE.containsId(entityId)) {
                MobsBanner.LOGGER.error("Cannot find entity {} in default banner data of {}", entityId, resource.getResourcePackName());
                continue;
            }
            EntityType<?> type = Registries.ENTITY_TYPE.get(entityId);
            try {
                JsonElement element = JsonParser.parseReader(new InputStreamReader(resource.getInputStream()));
                DataResult<DefaultDataCollection> d = getCodec(type).parse(JsonOps.INSTANCE, element);
                DATA.put(Registries.ENTITY_TYPE.get(entityId), d.resultOrPartial(MobsBanner.LOGGER::error).orElseThrow());
            } catch (Exception e) {
                MobsBanner.LOGGER.error("Failed to load {} from datapack {}", entityId, resource.getResourcePackName(), e);
            }
        }
        MobsBanner.LOGGER.info("Successfully load {} default banner data", DATA.size());
    }

    public static Stream<ItemStack> populateStack(EntityType<?> type) {
        if (DATA.containsKey(type))
            return DATA.get(type).data.stream().map(x -> MobBannerHelper.create(type, x.nbt, x.primary, x.secondary, x.transform));
        return Stream.of(MobBannerHelper.create(type));
    }

    public static TransformData getTransform(EntityType<?> type) {
        return Optional.ofNullable(DATA.get(type)).map(DefaultDataCollection::transform).orElse(TransformData.create());
    }

    public static Codec<DefaultDataCollection> getCodec(EntityType<?> type) {
        SpawnEggItem spawnEgg = SpawnEggItem.forEntity(type);
        int primary, secondary;
        if (spawnEgg == null) primary = secondary = -1;
        else {
            primary = spawnEgg.getColor(0);
            secondary = spawnEgg.getColor(1);
        }
        return RecordCodecBuilder.create(i1 -> i1.group(
                RecordCodecBuilder.<DefaultBannerData>create(i2 -> i2.group(
                        Codec.INT.optionalFieldOf("primary", primary).forGetter(DefaultBannerData::primary),
                        Codec.INT.optionalFieldOf("secondary", secondary).forGetter(DefaultBannerData::secondary),
                        NbtCompound.CODEC.optionalFieldOf("nbt", new NbtCompound()).forGetter(DefaultBannerData::nbt),
                        TransformData.CODEC.optionalFieldOf("transform", TransformData.create()).forGetter(DefaultBannerData::transform)
                ).apply(i2, DefaultBannerData::new)).listOf().optionalFieldOf("data", List.of(DefaultBannerData.create(primary, secondary))).forGetter(DefaultDataCollection::data),
                TransformData.CODEC.optionalFieldOf("transform", TransformData.create()).forGetter(DefaultDataCollection::transform)
        ).apply(i1, DefaultDataCollection::new));
    }

    public record DefaultDataCollection(List<DefaultBannerData> data, TransformData transform) {
    }

    public record DefaultBannerData(int primary, int secondary, NbtCompound nbt, TransformData transform) {
        public static DefaultBannerData create(int primary, int secondary) {
            return new DefaultBannerData(primary, secondary, new NbtCompound(), TransformData.create());
        }
    }

    public record TransformData(float scale, float yaw) {
        public static final Codec<TransformData> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.FLOAT.optionalFieldOf("scale", 1F).forGetter(TransformData::scale),
                Codec.FLOAT.optionalFieldOf("yaw", 0F).forGetter(TransformData::yaw)
        ).apply(i, TransformData::new));

        public static TransformData create() {
            return new TransformData(1, 0);
        }

        public TransformData combineWith(TransformData another) {
            return new TransformData(this.scale * another.scale, this.yaw + another.yaw);
        }
    }
}
