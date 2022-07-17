package cn.korostudio.c3h6n6o6.mixin.ae2;

import appeng.core.AppEng;
import appeng.util.Platform;
import cn.korostudio.c3h6n6o6.thread.CalculationController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 这里使得AE2项目组编写的验证是否为服务主线程的逻辑正常化。
 * Here the logic written by the AE2 project team to verify that it is the main thread of the service is normalised.
 */
@Mixin(Platform.class)
public class PlatformMixin {
    /**
     * 别和吾说什么非原创代码，MCMTCE也是吾的项目（怒视
     * @param cir 这玩意去问海绵组去
     */
    @Inject( method = "isServer",at = @At("HEAD"),cancellable = true,remap = false)
    private static void isServerEX(CallbackInfoReturnable<Boolean> cir){
        try {
            var currentServer = AppEng.instance().getCurrentServer();
            //cir.setReturnValue(currentServer != null && ParallelProcessor.serverExecutionThreadPatch(currentServer));
            //cir.setReturnValue(currentServer != null && (ParallelProcessor.serverExecutionThreadPatch(currentServer)||(Thread.currentThread().getName().equalsIgnoreCase("Server thread"))));
            cir.setReturnValue(currentServer != null && (currentServer.getThread()==Thread.currentThread()));
            //System.out.println((currentServer != null && ParallelProcessor.serverExecutionThreadPatch(currentServer)) + "  test  "+Thread.currentThread().getName());
            cir.cancel();
        } catch (NullPointerException npe) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }


}
