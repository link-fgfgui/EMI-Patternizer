package io.github.linkfgfgui.emi_patternizer.mixin;

import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternEncodingTermMenu.class)
public class PatternEncodingTermMenuMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructorReturn(CallbackInfo ci) {
        PatternEncodingTermMenu menu = (PatternEncodingTermMenu)(Object) this;
        ((PatternEncodingTermMenuAccessor) menu).getBlankPatternSlot();

    }
}
