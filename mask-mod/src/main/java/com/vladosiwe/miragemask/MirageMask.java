package com.vladosiwe.miragemask;

import com.vladosiwe.miragemask.disguise.DisguiseManager;
import com.vladosiwe.miragemask.network.DisguisePayload;
import com.vladosiwe.miragemask.registry.ModItems;
import com.vladosiwe.miragemask.registry.ModParticles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(MirageMask.MOD_ID)
public class MirageMask {
    public static final String MOD_ID = "miragemask";
    public static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    public MirageMask(IEventBus modBus) {
        ModItems.ITEMS.register(modBus);
        ModParticles.PARTICLES.register(modBus);
        modBus.addListener(MirageMask::registerPayloads);
        modBus.addListener(MirageMask::creativeTab);
        NeoForge.EVENT_BUS.register(DisguiseManager.class);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("2")
                .playToClient(DisguisePayload.TYPE, DisguisePayload.STREAM_CODEC, DisguisePayload::handle)
                .playToServer(com.vladosiwe.miragemask.network.MaskActionPayload.TYPE,
                        com.vladosiwe.miragemask.network.MaskActionPayload.STREAM_CODEC,
                        com.vladosiwe.miragemask.network.MaskActionPayload::handle);
    }

    private static void creativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES || event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.MIRAGE_MASK.get());
        }
    }
}
