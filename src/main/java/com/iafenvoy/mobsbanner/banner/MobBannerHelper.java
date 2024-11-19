package com.iafenvoy.mobsbanner.banner;

import com.iafenvoy.mobsbanner.component.BannerBlockComponent;
import com.iafenvoy.mobsbanner.component.CcaComponentHelper;
import com.iafenvoy.mobsbanner.data.DefaultMobBannerData;
import com.iafenvoy.mobsbanner.util.DyeColorUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class MobBannerHelper {
    public static Item getBannerItem(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_BANNER;
            case ORANGE -> Items.ORANGE_BANNER;
            case MAGENTA -> Items.MAGENTA_BANNER;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_BANNER;
            case YELLOW -> Items.YELLOW_BANNER;
            case LIME -> Items.LIME_BANNER;
            case PINK -> Items.PINK_BANNER;
            case GRAY -> Items.GRAY_BANNER;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_BANNER;
            case CYAN -> Items.CYAN_BANNER;
            case PURPLE -> Items.PURPLE_BANNER;
            case BLUE -> Items.BLUE_BANNER;
            case BROWN -> Items.BROWN_BANNER;
            case GREEN -> Items.GREEN_BANNER;
            case RED -> Items.RED_BANNER;
            case BLACK -> Items.BLACK_BANNER;
        };
    }

    public static ItemStack create(EntityType<?> type) {
        return create(type, new NbtCompound());
    }

    public static ItemStack create(EntityType<?> type, NbtCompound entityData) {
        SpawnEggItem spawnEgg = SpawnEggItem.forEntity(type);
        if (spawnEgg == null) {
            if (type == EntityType.PLAYER && !entityData.isEmpty())
                return create(Registries.ENTITY_TYPE.getId(type), Items.WHITE_BANNER, type, entityData, DefaultMobBannerData.TransformData.create());
            return ItemStack.EMPTY;
        }
        DyeColor primaryColor = DyeColorUtil.getDyeColorByColor(spawnEgg.getColor(0));
        DyeColor secondaryColor = DyeColorUtil.getDyeColorByColor(spawnEgg.getColor(1));
        if (primaryColor == secondaryColor) {
            Pair<DyeColor, DyeColor> pair = DyeColorUtil.getColorPair(primaryColor);
            primaryColor = pair.getFirst();
            secondaryColor = pair.getSecond();
        }
        return create(type, entityData, primaryColor, secondaryColor, DefaultMobBannerData.TransformData.create());
    }

    public static ItemStack create(EntityType<?> type, NbtCompound entityData, int primaryColor, int secondaryColor, DefaultMobBannerData.TransformData transform) {
        return create(type, entityData, DyeColorUtil.getDyeColorByColor(primaryColor), DyeColorUtil.getDyeColorByColor(secondaryColor), transform);
    }

    public static ItemStack create(EntityType<?> type, NbtCompound entityData, DyeColor primaryColor, DyeColor secondaryColor, DefaultMobBannerData.TransformData transform) {
        entityData.remove("Health");
        entityData.remove("DeathTime");
        entityData.remove("Attributes");
        entityData.remove("Inventory");
        return create(Registries.ENTITY_TYPE.getId(type), getBannerItem(secondaryColor), type, entityData, transform,
                new Pair<>(BannerPatterns.TRIANGLES_TOP, primaryColor),
                new Pair<>(BannerPatterns.TRIANGLES_BOTTOM, primaryColor),
                new Pair<>(BannerPatterns.BORDER, primaryColor),
                new Pair<>(BannerPatterns.STRIPE_CENTER, secondaryColor));
    }

    @SafeVarargs
    public static ItemStack create(Identifier id, Item baseItem, EntityType<?> type, NbtCompound entityData, DefaultMobBannerData.TransformData transform, Pair<RegistryKey<BannerPattern>, DyeColor>... patterns) {
        BannerPattern.Patterns p = new BannerPattern.Patterns();
        for (Pair<RegistryKey<BannerPattern>, DyeColor> pattern : patterns)
            p.add(pattern.getFirst(), pattern.getSecond());

        NbtCompound mobNbt = new NbtCompound();
        BannerBlockComponent component = new BannerBlockComponent();
        component.setType(type);
        component.setEntityData(entityData);
        component.setTransform(transform);
        component.writeToNbt(mobNbt);

        ItemStack stack = new ItemStack(baseItem);
        stack.getOrCreateSubNbt("BlockEntityTag").put("Patterns", p.toNbt());
        CcaComponentHelper.packetCcaNbt(stack, BannerBlockComponent.ID, mobNbt);
        stack.setCustomName(Text.translatable("item.mobs_banner.banner", Text.translatable(id.toTranslationKey("entity"))).setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE).withItalic(false)));
        stack.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);
        return stack;
    }
}
