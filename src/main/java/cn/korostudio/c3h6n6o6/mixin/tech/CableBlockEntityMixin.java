package cn.korostudio.c3h6n6o6.mixin.tech;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techreborn.blockentity.cable.CableBlockEntity;
import techreborn.blockentity.cable.CableTickManager;
import techreborn.blockentity.cable.OfferedEnergyStorage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
/**
 * 这里重写了Tech线缆运行逻辑，将线缆运算全部放到独立线程运行，但在极多线缆的情况下可能会出现滞后，不过还未完全验证，等到有bug再修（
 **/
@Mixin(CableBlockEntity.class)
public class CableBlockEntityMixin extends BlockEntity implements BlockEntityTicker<CableBlockEntity> {
    /**
     * 那会吾还不会用@Slf4j
     */
    private static Logger LOGGER = LoggerFactory.getLogger("C3H6N6O6CWTechMixin");
    /**
     * 一个有界阻塞队列，用于缓存线缆实体。
     */
    private static LinkedBlockingQueue<CableBlockEntity> SynchronizationQueue  = new LinkedBlockingQueue<CableBlockEntity>(2000);
    private static Thread SynchronizationThread;
    static{
        SynchronizationThread = new Thread(() -> {
            for(;;){
                try {
                    CableTickManager.handleCableTick(SynchronizationQueue.take());
                } catch (InterruptedException e) {
                    LOGGER.warn("C3H6N6O6对Tech兼容层同步线程捕捉到异常："+e.getClass().getName()+":"+e.getMessage());
                }
            }
        });
        SynchronizationThread.setName("C3H6N6O6-For-Tech-SynchronizationThread");
        SynchronizationThread.start();
    }

    /**
     * 兼容。。兼容。。。
     */
    @Shadow(remap = false)
    List<CableBlockEntity.CableTarget> targets;
    /**
     * 兼容。。兼容。。。
     */
    public CableBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * 兼容。。兼容。。。
     */
    @Redirect(remap = false,method = "appendTargets", at = @At(value = "FIELD", target = "Ltechreborn/blockentity/cable/CableBlockEntity;targets:Ljava/util/List;", opcode = Opcodes.PUTFIELD))
    private void synList(CableBlockEntity instance, List<CableBlockEntity.CableTarget> value) {
        targets = Collections.synchronizedList(value);
    }
    /**
     * 兼容。。兼容。。。
     */
    @Inject(remap = false,method = "appendTargets",at = @At(value = "INVOKE", target ="Ljava/util/List;iterator()Ljava/util/Iterator;"),cancellable = true)
    private void setNullCheck(List<OfferedEnergyStorage> targetStorages, CallbackInfo ci){
        if(targets==null){
            ci.cancel();
        }
    }
    /**
     * 兼容。。兼容。。。
     */
    @Override
    public void tick(World world, BlockPos pos, BlockState state, CableBlockEntity blockEntity2) {
        if (world != null && !world.isClient) {
            SynchronizationQueue.add((CableBlockEntity) ((Object) this));
        }
    }



}
