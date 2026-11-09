package com.vladosiwe.miragemask.registry;

import com.vladosiwe.miragemask.MirageMask;
import com.vladosiwe.miragemask.item.MirageMaskItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MirageMask.MOD_ID);

    public static final DeferredItem<MirageMaskItem> MIRAGE_MASK = ITEMS.register("mirage_mask",
            () -> new MirageMaskItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    private ModItems() {}
}
