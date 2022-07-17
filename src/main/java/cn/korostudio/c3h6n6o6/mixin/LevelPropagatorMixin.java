package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.fastutil.ConcurrentLongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.world.chunk.light.LevelPropagator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
/**
 * 一般路过改造Mixin
 */
@Mixin(LevelPropagator.class)
public abstract class LevelPropagatorMixin {
    /**
     * 调参大师.png
     * @param hashSets 问MJ去
     * @param index 问MJ去
     * @param hashSet 问MJ去
     * @param levelCount 问MJ去
     * @param expectedLevelSize 问MJ去
     * @param expectedTotalSize 问MJ去
     */
    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/light/LevelPropagator;pendingIdUpdatesByLevel:[Lit/unimi/dsi/fastutil/longs/LongLinkedOpenHashSet;", args = "array=set"))
    private void overwritePendingIdUpdatesByLevel(LongLinkedOpenHashSet[] hashSets, int index, LongLinkedOpenHashSet hashSet, int levelCount, final int expectedLevelSize, final int expectedTotalSize) {
        hashSets[index] = new ConcurrentLongLinkedOpenHashSet(expectedLevelSize, 0.5f);
    }
}
