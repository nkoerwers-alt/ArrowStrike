package com.arrowstrike;

import java.util.function.Function;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ArrowStrike implements ModInitializer {
    public static final String MOD_ID = "arrowstrike";

    public static Item ARROW_STAB;
    public static Item ARROW_FIELD;

    @Override
    public void onInitialize() {
        ARROW_STAB = register("arrow_stab", ArrowStabItem::new);
        ARROW_FIELD = register("arrow_field", ArrowFieldItem::new);
    }

    private static Item register(String name, Function<Item.Settings, Item> factory) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
        Item.Settings settings = new Item.Settings().registryKey(key).maxDamage(1);
        return Registry.register(Registries.ITEM, key, factory.apply(settings));
    }
}
// TODO: implement ArrowStrike
