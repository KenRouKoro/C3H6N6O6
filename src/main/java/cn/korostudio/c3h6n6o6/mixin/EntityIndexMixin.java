package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.fastutil.ConcurrentCollections;
import cn.korostudio.c3h6n6o6.fastutil.Int2ObjectConcurrentHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.EntityIndex;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

/**
 * 一般路过改造类
 * @param <T> 啊吧啊吧
 */
@Mixin(EntityIndex.class)
public abstract class EntityIndexMixin<T extends EntityLike> {
    /**
     * idToEntity镜像
     */
    @Shadow
    @Final
    @Mutable
    private Int2ObjectMap<T> idToEntity;
    /**
     * 改造~改造~~
     */
    @Shadow
    @Final
    @Mutable
    private Map<UUID, T> uuidToEntity = ConcurrentCollections.newHashMap();
    /**
     * 改造~改造~~
     */
    @Inject(method = "<init>",at = @At("TAIL"))
    private void replaceConVars(CallbackInfo ci)
    {
        idToEntity = new Int2ObjectConcurrentHashMap<>();
    }

}
