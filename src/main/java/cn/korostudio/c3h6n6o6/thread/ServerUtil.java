package cn.korostudio.c3h6n6o6.thread;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;

import java.util.Deque;
import java.util.Iterator;
@Slf4j
public class ServerUtil {
    public synchronized static void sendQueuedBlockEvents(Deque<BlockEvent> d, ServerWorld sw) {

        Iterator<BlockEvent> bed = d.iterator();
        while (bed.hasNext()) {
            BlockEvent BlockEvent = bed.next();
            if (sw.processBlockEvent(BlockEvent)) {
                sw.getServer().getPlayerManager().sendToAround(null, BlockEvent.pos().getX(), BlockEvent.pos().getY(), BlockEvent.pos().getZ(), 64.0D, sw.getRegistryKey(), new BlockEventS2CPacket(BlockEvent.pos(), BlockEvent.block(), BlockEvent.type(), BlockEvent.data()));
            }
            if (!CalculationController.getTicking().get()) {
                log.warn("阻止tick以外的更新.");
            }
            bed.remove();
        }
    }
}
