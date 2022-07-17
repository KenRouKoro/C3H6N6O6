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

/**
 * World.class ,吾进来了~！
 */
@Mixin(World.class)
public class WorldMixin {
    /**
     * 拦截方块实体Tick
     * @param blockEntityTickInvoker 方块实体对象，嘿嘿嘿......
     */
    @Redirect(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/BlockEntityTickInvoker;tick()V"))
    private void overwriteTick(BlockEntityTickInvoker blockEntityTickInvoker) {
        //高效判断是否处于客户端.png
        //之前判断类的吾简直就是个傻逼
        if(!((World)(Object)this).isClient){
            CalculationController.callBlockEntityTick(blockEntityTickInvoker);
        }else{
            blockEntityTickInvoker.tick();
        }
    }

    /**
     * 服务器线程，tm MJ为啥不从MinecraftServer拿线程啊（恼 ，这样整的吾很烦，日哦。吾又不能控制个字段获取，尤其是在MIXIN环境下（巨恼
     */
    @Shadow
    @Final
    @Mutable
    private Thread thread;

    /**
     * 喜欢直接用字段是吧（恼
     * @return 爷给汝修成服务器线程！
     */
    @Redirect(method = "getBlockEntity", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
    private Thread overwriteCurrentThread() {
        return this.thread;
    }

}
