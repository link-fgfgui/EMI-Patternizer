package io.github.linkfgfgui.emi_patternizer;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.LongValue DELAY_PER_OPERATION = BUILDER.comment("Delay per click").translation("emi_patternizer.config.delay1").defineInRange("delayPerOperation", 60, 0, Long.MAX_VALUE);
    public static final ModConfigSpec.LongValue DELAY_ADDITIONAL_PER_PATTERN = BUILDER.comment("Additional delay after 3 clicks").translation("emi_patternizer.config.delay2").defineInRange("delayAdditionalPerPattern", 20, 0, Long.MAX_VALUE);
    public static final ModConfigSpec.LongValue DELAY_BEFORE_READ = BUILDER.comment("Delay before read patterns from terminal (Unit: Millisecond)").translation("emi_patternizer.config.delay3").defineInRange("delayBeforeRead", 1000, 0, Long.MAX_VALUE);
    public static final ModConfigSpec.BooleanValue IS_PLAY_SOUND = BUILDER.comment("Play a sound after recipe fill").translation("emi_patternizer.config.playsound").define("isPlaySound", true);
    public static final ModConfigSpec.BooleanValue IS_SIMULATE_CLICK = BUILDER.comment("Whether to simulate a encode button click").translation("emi_patternizer.config.simulate").define("isSimulateClick", false);
    public static final ModConfigSpec.BooleanValue IS_SHOW_LOAD_MESSAGE = BUILDER.comment("Whether to show the loaded message").translation("emi_patternizer.config.loaded").define("isShowLoaded", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
