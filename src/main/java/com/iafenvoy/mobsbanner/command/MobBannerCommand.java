package com.iafenvoy.mobsbanner.command;

import com.iafenvoy.mobsbanner.banner.MobBannerHelper;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class MobBannerCommand {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> dispatcher.register(CommandManager.literal("mob_banner")
                .then(CommandManager.argument("players", EntityArgumentType.players())
                        .then(CommandManager.argument("entity_type", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENTITY_TYPE))
                                .then(CommandManager.argument("base_item", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ITEM))
                                        .then(CommandManager.argument("player_name", StringArgumentType.string())
                                                .then(CommandManager.argument("player_uuid", UuidArgumentType.uuid())
                                                        .executes(ctx -> {
                                                            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");
                                                            EntityType<?> target = RegistryEntryArgumentType.getEntityType(ctx, "entity_type").value();
                                                            Item item = RegistryEntryArgumentType.getRegistryEntry(ctx, "base_item", RegistryKeys.ITEM).value();
                                                            String name = StringArgumentType.getString(ctx, "player_name");
                                                            UUID uuid = UuidArgumentType.getUuid(ctx, "player_uuid");
                                                            return processBanner(players, target, item, name, uuid);
                                                        })
                                                ).executes(ctx -> {
                                                    Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");
                                                    EntityType<?> target = RegistryEntryArgumentType.getEntityType(ctx, "entity_type").value();
                                                    Item item = RegistryEntryArgumentType.getRegistryEntry(ctx, "base_item", RegistryKeys.ITEM).value();
                                                    String name = StringArgumentType.getString(ctx, "player_name");
                                                    return processBanner(players, target, item, name, null);
                                                }))
                                ).executes(ctx -> {
                                    Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");
                                    EntityType<?> target = RegistryEntryArgumentType.getEntityType(ctx, "entity_type").value();
                                    return processBanner(players, target, null, null, null);
                                })))));
    }

    public static int processBanner(Collection<ServerPlayerEntity> players, EntityType<?> target, @Nullable Item baseItem, @Nullable String playerName, @Nullable UUID playerUuid) {
        NbtCompound nbt = new NbtCompound();
        if (playerName != null) nbt.putString("player_name", playerName);
        if (playerUuid != null) nbt.putUuid("player_uuid", playerUuid);
        ItemStack stack = MobBannerHelper.create(target, nbt);
        if (baseItem != null) {
            ItemStack newStack = new ItemStack(baseItem);
            newStack.setNbt(stack.getNbt());
            stack = newStack;
        }
        for (ServerPlayerEntity player : players)
            player.giveItemStack(stack.copy());
        return players.size();
    }
}
