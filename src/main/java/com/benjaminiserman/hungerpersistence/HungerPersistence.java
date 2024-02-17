package com.benjaminiserman.hungerpersistence;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HungerPersistence.MOD_ID)
public class HungerPersistence
{
    public static final String MOD_ID = "hungerpersistence";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public HungerPersistence()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, HungerPersistenceConfig.Server.BUILT_CONFIG);
    }

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event)
	{
		if (!event.isWasDeath()) 
		{
			return;
		}

		event.getPlayer().getFoodData().setSaturation(event.getOriginal().getFoodData().getSaturationLevel());
		event.getPlayer().getFoodData().setFoodLevel(event.getOriginal().getFoodData().getFoodLevel());

		var foodStats = event.getPlayer().getFoodData();
		if (foodStats.getFoodLevel() < HungerPersistenceConfig.Server.Integers.MinimumHungerOnRespawn.get())
		{
			foodStats.setFoodLevel(HungerPersistenceConfig.Server.Integers.MinimumHungerOnRespawn.get());
		}

		if (foodStats.getFoodLevel() > HungerPersistenceConfig.Server.Integers.MaximumHungerOnRespawn.get()) 
		{
			foodStats.setFoodLevel(HungerPersistenceConfig.Server.Integers.MaximumHungerOnRespawn.get());
		}

		if (foodStats.getSaturationLevel() < HungerPersistenceConfig.Server.Doubles.MinimumSaturationOnRespawn.get())
		{
			foodStats.setSaturation(HungerPersistenceConfig.Server.Doubles.MinimumSaturationOnRespawn.get().floatValue());
		}
		
		if (foodStats.getSaturationLevel() > HungerPersistenceConfig.Server.Doubles.MaximiumSaturationOnRespawn.get())
		{
			foodStats.setSaturation(HungerPersistenceConfig.Server.Doubles.MaximiumSaturationOnRespawn.get().floatValue());
		}
	}
}
