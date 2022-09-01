package cn.korostudio.c3h6n6o6.mixin.c2me;

import cn.korostudio.c3h6n6o6.thread.CalculationController;
import com.ishland.c2me.fixes.worldgen.threading_issues.common.CheckedThreadLocalRandom;
import net.minecraft.world.gen.random.SimpleRandom;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ConcurrentModificationException;
import java.util.function.Supplier;

@Mixin(value = CheckedThreadLocalRandom.class)
public class CheckedThreadLocalRandomMixin extends SimpleRandom {

    @Shadow
    @Final
    @Mutable
    private Supplier<Thread> owner;

    public CheckedThreadLocalRandomMixin(long seed) {
        super(seed);
    }

    @Override
    public void setSeed(long l) {
        //return CalculationController.getServer().isOnThread() ? th : Thread.currentThread();
        Thread owner = this.owner != null ? this.owner.get() : null;
        if (owner != null && (CalculationController.getServer().isOnThread() ? owner : Thread.currentThread()) != owner)
            throw new ConcurrentModificationException();
        super.setSeed(l);
    }

    @Override
    public int next(int bits) {
        Thread owner = this.owner != null ? this.owner.get() : null;
        if (owner != null && (CalculationController.getServer().isOnThread() ? owner : Thread.currentThread()) != owner)
            throw new ConcurrentModificationException();
        return super.next(bits);
    }

    /*
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
    */

}
