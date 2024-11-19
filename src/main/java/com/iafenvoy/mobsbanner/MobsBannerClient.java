package com.iafenvoy.mobsbanner;

import com.iafenvoy.mobsbanner.data.DefaultMobBannerData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class MobsBannerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(DefaultMobBannerData.INSTANCE);
    }
}