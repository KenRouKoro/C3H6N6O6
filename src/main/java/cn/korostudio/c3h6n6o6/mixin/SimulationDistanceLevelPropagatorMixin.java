package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.fastutil.Long2ByteConcurrentHashMap;
import cn.korostudio.c3h6n6o6.fastutil.Long2ObjectOpenConcurrentHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.util.collection.SortedArraySet;
import net.minecraft.world.ChunkPosDistanceLevelPropagator;
import net.minecraft.world.SimulationDistanceLevelPropagator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 一般路过改造Mixin
 */
@Mixin(SimulationDistanceLevelPropagator.class)
public abstract class SimulationDistanceLevelPropagatorMixin  {
    /**
     * 改造~改造~~
     */
    @Shadow
    @Final
    @Mutable
    protected Long2ByteMap levels = new Long2ByteConcurrentHashMap();
    /**
     * 改造~改造~~
     */
    @Shadow
    @Final
    @Mutable
    private Long2ObjectOpenHashMap<SortedArraySet<ChunkTicket<?>>> tickets = new Long2ObjectOpenConcurrentHashMap<>();
}
