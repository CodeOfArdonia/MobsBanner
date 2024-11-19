package com.iafenvoy.mobsbanner.component;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class CcaComponentHelper {
    public static void packetCcaNbt(ItemStack stack, Identifier id, NbtCompound compound) {
        NbtCompound ccaNbt = new NbtCompound();
        ccaNbt.put(id.toString(), compound);
        stack.getOrCreateSubNbt("BlockEntityTag").put("cardinal_components", ccaNbt);
    }

    public static NbtCompound resolve(Identifier id, ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return new NbtCompound();
        return nbt.getCompound("BlockEntityTag").getCompound("cardinal_components").getCompound(id.toString());
    }
}
