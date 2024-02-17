package com.benjaminiserman.hungerpersistence;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Arrays;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HungerPersistenceConfig {
	public interface Item<T> {
		T get();

		void set(T value);

		String getPath();

		@Nullable
		ForgeConfigSpec.ConfigValue<T> getInternalInstance();

		void register(ForgeConfigSpec.Builder builder);

		void writeToBuffer(ByteBuffer buffer);

		T readFromBuffer(ByteBuffer buffer);
	}

	public enum ConfigGroup {
		Hunger, Saturation, Thirst
	}

	public static class Server {
		public enum Integers implements Item<Integer> {
			MinimumHungerOnRespawn(
					ConfigGroup.Hunger, "If a player's hunger on death is lower than this value, then it will be set to this value on respawn.\n"
		+ "Default Minecraft sets your hunger to 20 on respawn (full hunger).\n"
		+ "Note: 1 hunger unit = 1/2 hunger bar, so, for example, a value of 4 here means respawning with 2 hunger bars.",
					"minimum_hunger_on_respawn", 10, 0, Integer.MAX_VALUE
			),
			MaximumHungerOnRespawn(
				ConfigGroup.Hunger, "If a player's hunger on death is higher than this value, then it will be set to this value on respawn.\n"
		+ "Set this equal to " + MinimumHungerOnRespawn.getPath() + " to always have the player respawn with that exact hunger value.\n"
		+ "Note: 1 hunger unit = 1/2 hunger bar, and 20 hunger units is 'full'.\n",
					"maximum_hunger_on_respawn", 20, 0, Integer.MAX_VALUE
			);
			public final ConfigGroup Group;
			@Nullable
			public final String Comment;
			public final String Path;
			public final int DefaultValue;
			public final int Min;
			public final int Max;
			@Nullable
			private ForgeConfigSpec.IntValue configInstance = null;

			Integers(
					ConfigGroup group,
					@Nullable String comment,
					String path,
					int defaultValue,
					int min,
					int max
			) {
				Group = group;
				Comment = comment;
				Path = path;
				DefaultValue = defaultValue;
				Min = min;
				Max = max;
			}

			@Override
			public String getPath() {
				return Path;
			}

			public void register(ForgeConfigSpec.Builder builder) {
				if (Comment != null) {
					builder.comment(Comment);
				}
				configInstance = builder.defineInRange(Path, DefaultValue, Min, Max);
			}

			@Override
			public Integer get() {
				if (configInstance == null) return DefaultValue;
				return configInstance.get();
			}

			@Override
			public void set(Integer value) {
				if (configInstance != null) {
					configInstance.set(value);
				}
			}

			public ForgeConfigSpec.IntValue getInternalInstance() {
				return configInstance;
			}

			@Override
			public void writeToBuffer(ByteBuffer buffer) {
				buffer.putInt(get());
			}

			@Override
			public Integer readFromBuffer(ByteBuffer buffer) {
				return buffer.getInt();
			}
		}

		public enum Doubles implements Item<Double> {
			MinimumSaturationOnRespawn(
					ConfigGroup.Saturation, "If a player's saturation on death is lower than this value, then it will be set to this value on respawn.\n"
		+ "Default Minecraft sets your saturation to 5.0 on respawn.\n"
		+ "Note: 20 saturation is 'full', and saturation can never be higher than the player's current hunger level",
					"minimum_saturation_on_respawn", 0, 0, 20
			),
			MaximiumSaturationOnRespawn(
					ConfigGroup.Saturation, "If a player's saturation on death is higher than this value, then it will be set to this value on respawn.\n"
		+ "Set this to 0 to always have the player respawn with no saturation.\n"
		+ "Note: 20 saturation is 'full', and saturation can never be higher than the player's current hunger level.\n",
					"maximum_saturation_on_respawn", 20, 0, 20
			);
			public final ConfigGroup Group;
			@Nullable
			public final String Comment;
			public final String Path;
			public final double DefaultValue;
			public final double Min;
			public final double Max;
			@Nullable
			private ForgeConfigSpec.DoubleValue configInstance = null;

			Doubles(
					ConfigGroup group,
					@Nullable String comment,
					String path,
					double defaultValue,
					double min,
					double max
			) {
				Group = group;
				Comment = comment;
				Path = path;
				DefaultValue = defaultValue;
				Min = min;
				Max = max;
			}

			@Override
			public String getPath() {
				return Path;
			}

			public void register(ForgeConfigSpec.Builder builder) {
				if (Comment != null) {
					builder.comment(Comment);
				}
				configInstance = builder.defineInRange(Path, DefaultValue, Min, Max);
			}

			@Override
			public void writeToBuffer(ByteBuffer buffer) {
				buffer.putDouble(get());
			}

			@Override
			public Double readFromBuffer(ByteBuffer buffer) {
				return buffer.getDouble();
			}

			@Override
			public Double get() {
				if (configInstance == null) return DefaultValue;
				return configInstance.get();
			}

			@Override
			public void set(Double value) {
				if (configInstance != null) {
					configInstance.set(value);
				}
			}

			@OnlyIn(Dist.CLIENT)
			@Nullable
			public ForgeConfigSpec.DoubleValue getInternalInstance() {
				return configInstance;
			}
		}

		public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
		public static final ForgeConfigSpec BUILT_CONFIG;

		private static void register(ForgeConfigSpec.Builder builder, ConfigGroup group) {
			Arrays.stream(Server.Integers.values()).filter(x -> x.Group == group).forEach(x -> x.register(builder));
			Arrays.stream(Server.Doubles.values()).filter(x -> x.Group == group).forEach(x -> x.register(builder));
		}

		static {
			ForgeConfigSpec.Builder builder = BUILDER;
			builder.push("Hunger");
			{
				register(builder, ConfigGroup.Hunger);
			}
			builder.pop();
			builder.push("Saturation");
			{
				register(builder, ConfigGroup.Saturation);
			}
			builder.pop();
			BUILT_CONFIG = builder.build();
		}
	}
}
