package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.C3H6N6O6;
import cn.korostudio.c3h6n6o6.thread.CalculationController;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * 狠狠的注入MinecraftServer
 */
@Mixin(value = MinecraftServer.class,priority = 2147483647)
public abstract class MinecraftServerMixin extends ReentrantThreadExecutor<ServerTask> implements CommandOutput, AutoCloseable {
    /**
     * 获取主世界镜像
     * @return 主世界对象
     */
    @Shadow
    public abstract ServerWorld getOverworld();

    /**
     * 万  恶  之  源  镜  像
     */
    @Final
    @Shadow
    @Mutable
    private Thread serverThread;

    /**
     * 构造方法，java限制（（（
     * @param string 去问MJ！！！！
     */
    public MinecraftServerMixin(String string) {
        super(string);
    }

    /**
     * 拿服务器线程
     * @param ci 问海绵组去
     */
    @Inject(method = "runServer",at = @At("HEAD"))
    private void getServerThread(CallbackInfo ci){
        C3H6N6O6.ServerThread = Thread.currentThread();
    }

    /**
     * 拦截不是的情况~~毕竟是并发环境
     * @param minecraftServer mc服务器对象
     * @return 问MJ去
     */
    @Redirect(method = "reloadResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isOnThread()Z"))
    private boolean onServerExecutionThreadPatch(MinecraftServer minecraftServer) {
        return true;
    }

    /*
    @Inject(method = "tickWorlds", at = @At(value = "INVOKE", target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;"))
    private void preTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        CalculationController.startTick((MinecraftServer) (Object) this);
    }

    @Inject(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 1))
    private void postTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        CalculationController.endTick((MinecraftServer) (Object) this);
    }

     */

    /**
     * 调参大师.png
     * @param instance 区块管理器
     * @return 数量（解决溢出问题）
     */
    @Redirect(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;getTotalChunksLoadedCount()I"))
    private int initialChunkCountBypass(ServerChunkManager instance) {
        int loaded = this.getOverworld().getChunkManager().getLoadedChunkCount();
        return Math.min(loaded, 441); // 这玩意到底为啥溢出没人知道
    }

    /**
     * 超烦的线程判断
     * @return 是否为服务器线程运行
     */
    @Override
    public synchronized boolean isOnThread(){
        Thread getThreadThread = Thread.currentThread();
        String name = getThreadThread.getName();
        if(name.startsWith("C3H6N6O6")||name.startsWith("Worker")){
            return true;
        }
        return getThreadThread==serverThread;
    }

    /**
     * 拿服务器线程，这不比自己去判断服务器线程开心？！
     * @return 服务器线程，很明显这里改造了
     */
    @Override
    public synchronized Thread getThread() {
        Thread getThreadThread = Thread.currentThread();
        String name = getThreadThread.getName();
        if(name.startsWith("C3H6N6O6")||name.startsWith("Worker")){
            return getThreadThread;
        }
        return serverThread;
    }
}

