package cn.korostudio.c3h6n6o6.mixin.c2me;

import cn.korostudio.c3h6n6o6.thread.CalculationController;
import com.ishland.c2me.fixes.worldgen.threading_issues.common.CheckedThreadLocalRandom;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(value = CheckedThreadLocalRandom.class)
public class CheckedThreadLocalRandomMixin {
    @Shadow
    @Final
    @Mutable
    private Supplier<Thread> owner;

    @Redirect(method = "method_43156", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
    private Thread fixSetSeedThread() {
        Thread th = this.owner != null ? this.owner.get() : null;
        return CalculationController.getServer().isOnThread() ? th : Thread.currentThread();
    }

    @Redirect(method = "method_43156", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
    private Thread fixNextThread() {
        Thread th = this.owner != null ? this.owner.get() : null;
        return CalculationController.getServer().isOnThread() ? th : Thread.currentThread();
    }

}
