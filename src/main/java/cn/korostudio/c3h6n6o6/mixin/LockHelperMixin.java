package cn.korostudio.c3h6n6o6.mixin;

import net.minecraft.util.thread.LockHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.Semaphore;
/**
 * 一般路过改造Mixin
 */
@Mixin(LockHelper.class)
public abstract class LockHelperMixin<T> {
    /**
     * 玄学改造.png
     */
    @Shadow
    @Final
    @Mutable
    private Semaphore semaphore = new Semaphore(255);
}
