package io.github.linkfgfgui.emi_patternizer.client.mixin;

import appeng.client.gui.me.interfaceterminal.InterfaceRecord;
import appeng.client.gui.me.interfaceterminal.InterfaceTerminalScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(value = InterfaceTerminalScreen.class, remap = false)
public interface InterfaceTerminalScreenAccessor {
    @Accessor("byId")
    HashMap<Long, InterfaceRecord> getById();
}
