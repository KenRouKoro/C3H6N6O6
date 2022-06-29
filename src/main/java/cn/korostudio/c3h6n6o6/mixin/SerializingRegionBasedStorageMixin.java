package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.fastutil.ConcurrentLongLinkedOpenHashSet;
import cn.korostudio.c3h6n6o6.fastutil.Long2ObjectConcurrentHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.world.storage.SerializingRegionBasedStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(SerializingRegionBasedStorage.class)
public abstract class SerializingRegionBasedStorageMixin<R> implements AutoCloseable {
    @Shadow @Final
    @Mutable
    private Long2ObjectMap<Optional<R>> loadedElements = new Long2ObjectConcurrentHashMap<>();

    @Shadow @Final
    @Mutable
    private LongLinkedOpenHashSet unsavedElements = new ConcurrentLongLinkedOpenHashSet();
}
