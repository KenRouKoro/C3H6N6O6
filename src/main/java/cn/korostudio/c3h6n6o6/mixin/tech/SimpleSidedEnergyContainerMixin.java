package cn.korostudio.c3h6n6o6.mixin.tech;

import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import team.reborn.energy.api.base.SimpleSidedEnergyContainer;

/**
 * 这一个类可能会突然暴毙，不过吾不检查这个Mixin是否成功，随他了。
 */
@Mixin(SimpleSidedEnergyContainer.class)
public abstract class SimpleSidedEnergyContainerMixin extends SnapshotParticipant<Long> {
    /**
     * amount镜像
     */
    @Shadow
    public long amount;

    /**
     * 加一层空判断，可以解决异常（智将
     * @param snapshot 源数据
     */
    @Override
    protected void readSnapshot(Long snapshot) {
        amount = snapshot==null?amount:snapshot;
    }
}
