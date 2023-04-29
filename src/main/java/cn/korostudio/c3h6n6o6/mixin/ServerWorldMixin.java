package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.fastutil.ConcurrentCollections;
import cn.korostudio.c3h6n6o6.thread.CalculationController;
import cn.korostudio.c3h6n6o6.thread.ServerUtil;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * 被撅的最狠的一个类，谁叫MJ优化差！
 */
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin  {
    /**
     * 缓存
     */
    ConcurrentLinkedDeque<BlockEvent> syncedBlockEventCLinkedQueue = new ConcurrentLinkedDeque<>();
    @Shadow private List<GameEvent.Message> queuedEvents;
    @Shadow protected abstract void processEvents(List<GameEvent.Message> events);

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/world/level/ServerWorldProperties;Lnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/world/dimension/DimensionOptions;Lnet/minecraft/server/WorldGenerationProgressListener;ZJLjava/util/List;Z)V")
    public void overrideConstructorAssigners(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, CallbackInfo ci) {
        this.queuedEvents = ConcurrentCollections.newLinkedList();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;processEventQueue()V"))
    private void overwriteProcessEventQueue(ServerWorld instance) {
        if (!this.queuedEvents.isEmpty()) {
            List<GameEvent.Message> list = this.queuedEvents;
            this.queuedEvents = ConcurrentCollections.newLinkedList();
            this.processEvents(list);
        }
    }


    /**
     * 拦截实体Tick
     * @param entity 实体对象
     */
    @Redirect(method = "tickEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V"))
    private void overwriteEntityTicking(Entity entity) {
        CalculationController.callEntityTick(entity);
    }

    /**
     * Tick开始（和MCMT不一样，吾把线程池Tick范围固定在实体tick内
     * @param shouldKeepTicking 问MJ去
     * @param ci 问海绵组去
     */
    @Inject(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/EntityList;forEach(Ljava/util/function/Consumer;)V"))
    private void tickStart(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
       CalculationController.startTick(((ServerWorld)(Object)this).getServer());
    }

    /**
     * Tick结束
     * @param shouldKeepTicking 问MJ去
     * @param ci 问海绵组去
     */
    @Inject(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V", shift = At.Shift.AFTER))
    private void tickEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        CalculationController.endTick(((ServerWorld)(Object)this).getServer());
    }

    /**
     * 啊吧啊吧
     */
    @Redirect(method = "processSyncedBlockEvents", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;addAll(Ljava/util/Collection;)Z"))
    private boolean overwriteQueueAddAll(ObjectLinkedOpenHashSet<BlockEvent> instance, Collection<? extends BlockEvent> c) {
        return syncedBlockEventCLinkedQueue.addAll(c);
    }
    /**
     * 啊吧啊吧
     */
    @Redirect(method = "addSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;add(Ljava/lang/Object;)Z"))
    private boolean overwriteQueueAdd(ObjectLinkedOpenHashSet<BlockEvent> objectLinkedOpenHashSet, Object object) {
        return syncedBlockEventCLinkedQueue.add((BlockEvent) object);
    }
    /**
     * 啊吧啊吧
     */
    @Redirect(method = "clearUpdatesInArea", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;removeIf(Ljava/util/function/Predicate;)Z"))
    private boolean overwriteQueueRemoveIf(ObjectLinkedOpenHashSet<BlockEvent> objectLinkedOpenHashSet, Predicate<BlockEvent> filter) {
        return syncedBlockEventCLinkedQueue.removeIf(filter);
    }
    /**
     * 啊吧啊吧
     */
    @Redirect(method = "processSyncedBlockEvents", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;isEmpty()Z"))
    private boolean overwriteEmptyCheck(ObjectLinkedOpenHashSet<BlockEvent> objectLinkedOpenHashSet) {
        return syncedBlockEventCLinkedQueue.isEmpty();
    }
    /**
     * 啊吧啊吧
     */
    @Redirect(method = "processSyncedBlockEvents", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;removeFirst()Ljava/lang/Object;"))
    private Object overwriteQueueRemoveFirst(ObjectLinkedOpenHashSet<BlockEvent> objectLinkedOpenHashSet) {
        BlockEvent blockEvent = syncedBlockEventCLinkedQueue.removeFirst();
        ServerUtil.sendQueuedBlockEvents(syncedBlockEventCLinkedQueue, (ServerWorld) (Object) this);
        return blockEvent;
    }

    /**
     * duringListenerUpdate镜像
     */
    @Shadow
    volatile boolean duringListenerUpdate;

    /**
     * 锁~~~
     */
    private final ReentrantLock lock = new ReentrantLock();


    /**
     * 给不听话的非线程安全updateListeners上锁~~~
     * @param pos 问MJ去
     * @param oldState 问MJ去
     * @param newState 问MJ去
     * @param flags 问MJ去
     * @param ci 问海绵组去
     */
    @Inject(method = "updateListeners",at = @At("HEAD"))
    private void lockUpdateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci){
        lock.lock();
    }
    /**
     * 给不听话的非线程安全updateListeners解锁~~~
     * @param pos 问MJ去
     * @param oldState 问MJ去
     * @param newState 问MJ去
     * @param flags 问MJ去
     * @param ci 问海绵组去
     */
    @Inject(method = "updateListeners",at = @At("RETURN"))
    private void unlockUpdateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci){
        lock.unlock();
    }
    /**
     * 给不听话的而且还抛异常的非线程安全updateListeners解锁~~~
     * @param pos 问MJ去
     * @param oldState 问MJ去
     * @param newState 问MJ去
     * @param flags 问MJ去
     * @param ci 问海绵组去
     */
    @Inject(method = "updateListeners",at = @At(value = "INVOKE",target = "Lnet/minecraft/util/Util;error(Ljava/lang/String;Ljava/lang/Throwable;)V"),cancellable = true)
    private void unlockInError(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci){
        duringListenerUpdate = false;
        Logger logger = LoggerFactory.getLogger(ServerWorld.class);
        logger.error("updateListeners递归调用辣！！！");
        lock.unlock();
        ci.cancel();
    }

}
