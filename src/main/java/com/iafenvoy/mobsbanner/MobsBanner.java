package com.iafenvoy.mobsbanner;

import com.iafenvoy.mobsbanner.banner.MobBannerHelper;
import com.iafenvoy.mobsbanner.command.MobBannerCommand;
import com.iafenvoy.mobsbanner.data.DefaultMobBannerData;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;

public class MobsBanner implements ModInitializer {
    public static final String MOD_ID = "mobs_banner";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ItemGroup MAIN = Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "main"), FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.mobs_banner.creative_tab"))
            .icon(() -> MobBannerHelper.create(EntityType.PIG))
            .entries((ctx, entries) -> Registries.ENTITY_TYPE.stream().filter(x -> x != EntityType.PLAYER).flatMap(DefaultMobBannerData::populateStack).filter(x -> !x.isEmpty()).forEach(entries::add))
            .build());
    public static final GameRules.Key<DoubleRule> DROP_CHANCE = GameRuleRegistry.register(new Identifier(MOD_ID, "drop_chance").toString(), GameRules.Category.DROPS, GameRuleFactory.createDoubleRule(0.1, 0, 1));

    @Override
    public void onInitialize() {
        MobBannerCommand.init();
    }
}