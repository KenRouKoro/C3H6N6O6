package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.thread.CalculationController;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public class WorldMixin {

    @Redirect(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/BlockEntityTickInvoker;tick()V"))
    private void overwriteTick(BlockEntityTickInvoker blockEntityTickInvoker) {
        CalculationController.callBlockEntityTick(blockEntityTickInvoker, (World) (Object) this);
    }
}
