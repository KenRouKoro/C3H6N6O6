package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.fastutil.ConcurrentCollections;
import net.minecraft.util.collection.TypeFilterableList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.AbstractCollection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collector;

@Mixin(TypeFilterableList.class)
public abstract class TypeFilterableListMixin<T> extends AbstractCollection<T> {
    /**
     * 改造~改造~~
     */
    @Shadow
    @Final
    @Mutable
    private Map<Class<?>, List<T>> elementsByType = new ConcurrentHashMap<>();
    /**
     * 改造~改造~~
     */
    @Shadow
    @Final
    @Mutable
    private List<T> allElements = new CopyOnWriteArrayList<>();

    /**
     * 这样就能并发了捏
     * @param collector 问mj去
     * @return 问mj去
     * @param <T> 怎么这玩意要写注释？？？
     */
    @ModifyArg(method = "method_15217", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;"))
    private <T> Collector<T, ?, List<T>> overwriteCollectToList(Collector<T, ?, List<T>> collector) {
        return ConcurrentCollections.toList();
    }
}