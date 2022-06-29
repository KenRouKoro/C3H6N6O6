package cn.korostudio.c3h6n6o6.mixin;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SharedConstants.class)
public abstract class SharedConstantsMixin {
    @Shadow
    public static boolean isDevelopment = false;
}
