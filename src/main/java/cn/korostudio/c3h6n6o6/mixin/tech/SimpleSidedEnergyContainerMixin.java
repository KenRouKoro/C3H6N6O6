package cn.korostudio.c3h6n6o6.mixin.tech;

import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import team.reborn.energy.api.base.SimpleSidedEnergyContainer;

@Mixin(SimpleSidedEnergyContainer.class)
public abstract class SimpleSidedEnergyContainerMixin extends SnapshotParticipant<Long> {
    @Shadow
    public long amount;

    @Override
    protected void readSnapshot(Long snapshot) {
        amount = snapshot==null?amount:snapshot;
    }
}
