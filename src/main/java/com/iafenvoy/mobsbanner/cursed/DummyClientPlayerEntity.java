package com.iafenvoy.mobsbanner.cursed;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

//Code from CICADA under MIT license
public class DummyClientPlayerEntity extends ClientPlayerEntity {
    private static final Map<UUID, DummyClientPlayerEntity> FAKE_PLAYERS = new HashMap<>();
    private Identifier skinIdentifier = null, capeIdentifier = null;
    private String model = null;
    public Function<EquipmentSlot, ItemStack> equippedStackSupplier = slot -> ItemStack.EMPTY;

    @Nullable
    public static DummyClientPlayerEntity get(UUID uuid, String name) {
        if (uuid == null && name.isBlank()) return null;
        if (!FAKE_PLAYERS.containsKey(uuid))
            FAKE_PLAYERS.put(uuid, new DummyClientPlayerEntity(new GameProfile(uuid, name)));
        return FAKE_PLAYERS.get(uuid);
    }

    private DummyClientPlayerEntity(GameProfile profile) {
        super(MinecraftClient.getInstance(), DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance(), null, null, false, false);
        this.setUuid(UUID.randomUUID());
        MinecraftClient.getInstance().getSkinProvider().loadSkin(profile, (type, identifier, texture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                this.skinIdentifier = identifier;
                this.model = texture.getMetadata("model");
                if (this.model == null)
                    this.model = "default";
            }
            if (type == MinecraftProfileTexture.Type.CAPE)
                this.capeIdentifier = identifier;
        }, true);
    }

    @Override
    public boolean isPartVisible(PlayerModelPart modelPart) {
        return true;
    }

    @Override
    public boolean hasSkinTexture() {
        return true;
    }

    @Override
    public Identifier getSkinTexture() {
        return this.skinIdentifier == null ? DefaultSkinHelper.getTexture(this.getUuid()) : this.skinIdentifier;
    }

    @Override
    public boolean canRenderCapeTexture() {
        return true;
    }

    @Override
    public @Nullable Identifier getCapeTexture() {
        return this.capeIdentifier;
    }

    @Override
    public String getModel() {
        return this.model == null ? DefaultSkinHelper.getModel(this.getUuid()) : this.model;
    }

    @Nullable
    @Override
    protected PlayerListEntry getPlayerListEntry() {
        return null;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return true;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return this.equippedStackSupplier.apply(slot);
    }

    @Override
    public Text getName() {
        return super.getName();
    }
}
