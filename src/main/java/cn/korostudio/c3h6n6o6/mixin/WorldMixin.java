package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.thread.CalculationController;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public class WorldMixin {

    @Redirect(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/BlockEntityTickInvoker;tick()V"))
    private void overwriteTick(BlockEntityTickInvoker blockEntityTickInvoker) {
        if(!((World)(Object)this).isClient){
            CalculationController.callBlockEntityTick(blockEntityTickInvoker);
        }else{
            blockEntityTickInvoker.tick();
        }
    }

    @Shadow
    @Final
    @Mutable
    private Thread thread;

    @Redirect(method = "getBlockEntity", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
    private Thread overwriteCurrentThread() {
        return this.thread;
    }

}
