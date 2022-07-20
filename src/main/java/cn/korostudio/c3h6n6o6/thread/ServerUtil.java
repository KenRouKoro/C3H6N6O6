package cn.korostudio.c3h6n6o6.thread;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;

import java.util.Deque;
import java.util.Iterator;
@Slf4j
public class ServerUtil {
    /**
     * 别问吾，这玩意是MCMT的产物，用来妥善分发的，虽然证实没啥大用（
     * @param d 双向队列（方块事件）
     * @param sw 服务器世界对象
     */
    public synchronized static void sendQueuedBlockEvents(Deque<BlockEvent> d, ServerWorld sw) {

        Iterator<BlockEvent> bed = d.iterator();
        while (bed.hasNext()) {
            BlockEvent BlockEvent = bed.next();
            if (sw.processBlockEvent(BlockEvent)) {
                sw.getServer().getPlayerManager().sendToAround(null, BlockEvent.pos().getX(), BlockEvent.pos().getY(), BlockEvent.pos().getZ(), 64.0D, sw.getRegistryKey(), new BlockEventS2CPacket(BlockEvent.pos(), BlockEvent.block(), BlockEvent.type(), BlockEvent.data()));
            }
            bed.remove();
        }
    }

    /**
     * 异常堆栈打印助手
     *
     * @param ex 异常对象
     */
    public static void LogErrorUtil(Exception ex) {
        log.error("黑索金异常捕捉:", ex);
    }
}
