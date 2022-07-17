package cn.korostudio.c3h6n6o6.mixin;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 一个玄学Mixin位置
 */
@Mixin(SharedConstants.class)
public abstract class SharedConstantsMixin {
    /**
     * 开发版开关（
     */
    @Shadow
    public static boolean isDevelopment = false;
}
