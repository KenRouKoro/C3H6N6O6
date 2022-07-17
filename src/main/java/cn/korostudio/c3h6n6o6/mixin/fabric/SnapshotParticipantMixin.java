package cn.korostudio.c3h6n6o6.mixin.fabric;


import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 咋FabricAPI也喜欢用Arraylist啊（撅望
 * @param <T> 为什么模板也要写JavaDOC啊（恼
 */
@Mixin(SnapshotParticipant.class)
public class SnapshotParticipantMixin<T> {
    /**
     * 爷直接给汝改喽！
     */
    @Shadow
    @Final
    @Mutable
    private List<T> snapshots = new CopyOnWriteArrayList<>();
}
