package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.fastutil.Long2ByteConcurrentHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.world.ChunkPosDistanceLevelPropagator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkTicketManager.DistanceFromNearestPlayerTracker.class)
public abstract class DistanceFromNearestPlayerTrackerMixin extends ChunkPosDistanceLevelPropagator {
    @Shadow @Final @Mutable protected final Long2ByteMap distanceFromNearestPlayer = new Long2ByteConcurrentHashMap();

    protected DistanceFromNearestPlayerTrackerMixin(int i, int j, int k) {
        super(i, j, k);
    }
}
