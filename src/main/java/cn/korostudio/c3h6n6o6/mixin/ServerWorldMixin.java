package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.fastutil.ConcurrentCollections;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements StructureWorldAccess {

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
    }

    ConcurrentLinkedDeque<BlockEvent> syncedBlockEventCLinkedQueue = new ConcurrentLinkedDeque<BlockEvent>();

    @Shadow
    @Final
    @Mutable
    Set<MobEntity> loadedMobs = ConcurrentCollections.newHashSet();

    @Shadow
    @Final
    @Mutable
    private  ObjectLinkedOpenHashSet<BlockEvent> syncedBlockEventQueue = new ObjectLinkedOpenHashSet<>();

    @Redirect(method = "tickEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V"))
    private void overwriteEntityTicking(Entity entity) {
        ParallelProcessor.callEntityTick(entity, (ServerWorld) (Object) this);
    }

    @Redirect(method = "addSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;add(Ljava/lang/Object;)Z"))
    private boolean overwriteQueueAdd(ObjectLinkedOpenHashSet<BlockEvent> objectLinkedOpenHashSet, Object object) {
        return syncedBlockEventCLinkedQueue.add((BlockEvent) object);
    }

    @Redirect(method = "clearUpdatesInArea", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;removeIf(Ljava/util/function/Predicate;)Z"))
    private boolean overwriteQueueRemoveIf(ObjectLinkedOpenHashSet<BlockEvent> objectLinkedOpenHashSet, Predicate<BlockEvent> filter) {
        return syncedBlockEventCLinkedQueue.removeIf(filter);
    }

    @Redirect(method = "processSyncedBlockEvents", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;isEmpty()Z"))
    private boolean overwriteEmptyCheck(ObjectLinkedOpenHashSet<BlockEvent> objectLinkedOpenHashSet) {
        return syncedBlockEventCLinkedQueue.isEmpty();
    }

    @Redirect(method = "processSyncedBlockEvents", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;removeFirst()Ljava/lang/Object;"))
    private Object overwriteQueueRemoveFirst(ObjectLinkedOpenHashSet<BlockEvent> objectLinkedOpenHashSet) {
        BlockEvent blockEvent = syncedBlockEventCLinkedQueue.removeFirst();
        ParallelProcessor.sendQueuedBlockEvents(syncedBlockEventCLinkedQueue, (ServerWorld) (Object) this);
        return blockEvent;
    }

    @Redirect(method = "processSyncedBlockEvents", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;addAll(Ljava/util/Collection;)Z"))
    private boolean overwriteQueueAddAll(ObjectLinkedOpenHashSet<BlockEvent> instance, Collection<? extends BlockEvent> c) {
        return syncedBlockEventCLinkedQueue.addAll(c);
    }

    @Redirect(method = "updateListeners",at = @At(value = "FIELD",target = "Lnet/minecraft/server/world/ServerWorld;duringListenerUpdate:Z",opcode = Opcodes.PUTFIELD))
    private void skipSendBlockUpdatedCheck(ServerWorld instance, boolean value)
    {

    }
}
