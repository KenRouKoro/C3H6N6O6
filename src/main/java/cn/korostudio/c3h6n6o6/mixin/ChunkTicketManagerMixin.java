package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.fastutil.ConcurrentCollections;
import cn.korostudio.c3h6n6o6.fastutil.ConcurrentLongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(ChunkTicketManager.class)
public abstract class ChunkTicketManagerMixin {

    @Shadow
    @Final
    @Mutable
    Set<ChunkHolder> chunkHolders = ConcurrentCollections.newHashSet();

    @Shadow
    @Final
    @Mutable
    LongSet chunkPositions = new ConcurrentLongLinkedOpenHashSet();
}
