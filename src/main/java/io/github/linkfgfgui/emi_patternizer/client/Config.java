package io.github.linkfgfgui.emi_patternizer.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.LongValue DELAY_PER_OPERATION = BUILDER.comment("Delay per click").translation("emi_patternizer.config.delay1").defineInRange("delayPerOperation", 60, 0, Long.MAX_VALUE);
    public static final ForgeConfigSpec.LongValue DELAY_ADDITIONAL_PER_PATTERN = BUILDER.comment("Additional delay after 3 clicks").translation("emi_patternizer.config.delay2").defineInRange("delayAdditionalPerPattern", 20, 0, Long.MAX_VALUE);
    public static final ForgeConfigSpec.LongValue DELAY_BEFORE_READ = BUILDER.comment("Delay before read patterns from terminal (Unit: Millisecond)").translation("emi_patternizer.config.delay3").defineInRange("delayBeforeRead", 1000, 0, Long.MAX_VALUE);
    public static final ForgeConfigSpec.BooleanValue IS_PLAY_SOUND = BUILDER.comment("Play a sound after recipe fill").translation("emi_patternizer.config.playsound").define("isPlaySound", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();
}

