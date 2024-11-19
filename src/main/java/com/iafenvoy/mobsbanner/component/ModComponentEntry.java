package com.iafenvoy.mobsbanner.component;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import net.minecraft.block.entity.BannerBlockEntity;

public class ModComponentEntry implements BlockComponentInitializer {
    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
        registry.registerFor(BannerBlockEntity.class, BannerBlockComponent.KEY, blockEntity -> new BannerBlockComponent());
    }
}
