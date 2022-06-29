package cn.korostudio.c3h6n6o6.mixin.fabric;

import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(SnapshotParticipant.class)
public class SnapshotParticipantMixin<T> {
    @Shadow
    @Final
    @Mutable
    private List<T> snapshots = new CopyOnWriteArrayList<>();
}
