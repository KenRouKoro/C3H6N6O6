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

@Mixin(value = MinecraftServer.class,priority = 2147483647)
public abstract class MinecraftServerMixin extends ReentrantThreadExecutor<ServerTask> implements CommandOutput, AutoCloseable {
    @Shadow
    public abstract ServerWorld getOverworld();
    @Final
    @Shadow
    @Mutable
    private Thread serverThread;

    public MinecraftServerMixin(String string) {
        super(string);
    }

    @Inject(method = "runServer",at = @At("HEAD"))
    private void getServerThread(CallbackInfo ci){
        C3H6N6O6.ServerThread = Thread.currentThread();
    }

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

    @Redirect(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;getTotalChunksLoadedCount()I"))
    private int initialChunkCountBypass(ServerChunkManager instance) {
        int loaded = this.getOverworld().getChunkManager().getLoadedChunkCount();
        return Math.min(loaded, 441); // Maybe because multi loading caused overflow
    }

    @Override
    public synchronized boolean isOnThread(){
        Thread getThreadThread = Thread.currentThread();
        String name = getThreadThread.getName();
        if(name.startsWith("C3H6N6O6")||name.startsWith("Worker")){
            return true;
        }
        return getThreadThread==serverThread;
    }

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

