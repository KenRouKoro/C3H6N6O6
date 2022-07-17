package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.fastutil.ConcurrentShortHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.chunk.light.LightingProvider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MCMTCE那边的并发改造代码（
 */
@Mixin(ChunkHolder.class)
public class ChunkHolderMixin {
    /**
     * blockUpdatesBySection镜像
     */
    @Mutable
    @Shadow
    @Final
    private ShortSet[] blockUpdatesBySection;

    /**
     * 实例化时修改blockUpdatesBySection为并发兼容的数据结构
     * @param pos 问MJ去
     * @param level 问MJ去
     * @param world 问MJ去
     * @param lightingProvider 问MJ去
     * @param levelUpdateListener 问MJ去
     * @param playersWatchingChunkProvider 问MJ去
     * @param ci 问海绵组去
     */
    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ChunkHolder;blockUpdatesBySection:[Lit/unimi/dsi/fastutil/shorts/ShortSet;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void overwriteShortSet(ChunkPos pos, int level, HeightLimitView world, LightingProvider lightingProvider, ChunkHolder.LevelUpdateListener levelUpdateListener, ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider, CallbackInfo ci) {
        this.blockUpdatesBySection = new ConcurrentShortHashSet[world.countVerticalSections()];
    }
    /**
     * 更新时修改blockUpdatesBySection为并发兼容的数据结构
     * @param array 问MJ去
     * @param index 问MJ去
     * @param value 问MJ去
     */
    @Redirect(method = "markForBlockUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ChunkHolder;blockUpdatesBySection:[Lit/unimi/dsi/fastutil/shorts/ShortSet;", args = "array=set"))
    private void setBlockUpdatesBySection(ShortSet[] array, int index, ShortSet value) {
        array[index] = new ConcurrentShortHashSet();
    }
}
