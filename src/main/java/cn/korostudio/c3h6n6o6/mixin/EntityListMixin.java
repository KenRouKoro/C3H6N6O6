package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.fastutil.Int2ObjectConcurrentHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.world.EntityList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityList.class)
public abstract class EntityListMixin {
    @Shadow
    private Int2ObjectMap<Entity> entities = new Int2ObjectConcurrentHashMap<>();

    @Shadow
    private Int2ObjectMap<Entity> temp = new Int2ObjectConcurrentHashMap<>();
}
