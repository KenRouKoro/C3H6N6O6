package cn.korostudio.c3h6n6o6.thread;

import cn.hutool.core.thread.ThreadUtil;
import cn.korostudio.c3h6n6o6.C3H6N6O6;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.korostudio.c3h6n6o6.C3H6N6O6.Setting;

/**
 * 核心控制器
 */
@Slf4j
public class CalculationController {
    protected static ExecutorService executor ;
    @Getter
    protected static AtomicBoolean Ticking = new AtomicBoolean();
    @Getter
    protected static AtomicBoolean Client = new AtomicBoolean(false);
    protected static AtomicInteger ThreadID = null;
    @Getter
    protected static Phaser phaser;
    @Getter
    protected static long tickStart = 0;
    @Getter
    protected static MinecraftServer server;


    static public void Init(){
        ThreadID = new AtomicInteger();
        ForkJoinPool.ForkJoinWorkerThreadFactory poolThreadFactory = p -> {
            ForkJoinWorkerThread poolThread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(p);
            poolThread.setName("C3H6N6O6-ThreadPool-ID-" + ThreadID.getAndIncrement());
            poolThread.setContextClassLoader(C3H6N6O6.class.getClassLoader());
            return poolThread;
        };
        executor = new ForkJoinPool(Math.min(0x7fff, Runtime.getRuntime().availableProcessors()), poolThreadFactory, (t, e) -> {
            log.error("在黑索金线程池中抛出异常："+e.getMessage()+" 线程："+t.getName());
            e.printStackTrace();
        }, true);
    }
    public static void startTick(MinecraftServer server) {

        if (phaser != null) {
            log.warn("多次TickStart?什么鬼！");
            log.info("Server 参数状态:"+server);
            return;
        }else if(! Client.get()){
            CalculationController.server = server;
        }
        tickStart = System.nanoTime();
        Ticking.set(true);
        phaser = new Phaser();
        phaser.register();
    }
    public static void endTick(MinecraftServer server) {
        if ((Client.get())||(CalculationController.server == server)){
            phaser.arriveAndAwaitAdvance();
            Ticking.set(false);
            phaser = null;
        } else {
            log.warn("多个服务器?什么鬼！");
        }
    }
    public static void callEntityTick(Entity entityIn) {
        if (Setting.getBool("EntityDisabled",false)){
            entityIn.tick();
            return;
        }

        //检查是否为玩家类
        if(entityIn instanceof PlayerEntity){
            entityIn.tick();
            return;
        }
        //判断是否处于tick中，不处于直接tick实体
        if(!Ticking.get()){
            entityIn.tick();
            return;
        }
        phaser.register();
        executor.execute(() -> {
            try{
                entityIn.tick();
            }catch (NullPointerException e){
                if(Setting.getBool("logEntityException",true)){
                    log.warn("黑索金捕捉到在实体Tick:" + Thread.currentThread().getName() + "有单独的空指针异常，可能是实体出了点小问题，如果有假死的实体，重启服务器，大概率能解决。");
                    e.printStackTrace();
                }
            }catch(IllegalArgumentException e){
                if(Setting.getBool("logEntityException",true)) {
                    log.warn("黑索金捕捉到在实体Tick:" + Thread.currentThread().getName() + "有线程安全报错。");
                    e.printStackTrace();
                }
            } catch (Exception e){
                if(Setting.getBool("logEntityException",true)) {
                    String eMessage = e.getMessage();
                    log.error("黑索金捕捉到在实体Tick:" + Thread.currentThread().getName() + " 抛出异常:" + e.getClass().getName() + ":" + eMessage+"\n");
                    e.printStackTrace();
                }
            } finally {
                phaser.arriveAndDeregister();
            }
        });
    }
    public static void callBlockEntityTick(BlockEntityTickInvoker tte) {
        if (Setting.getBool("BlockEntityDisabled",false)) {
            tte.tick();
            return;
        }
        //检查是否为容器类
        if(tte instanceof Inventory){
            tte.tick();
            return;
        }
        //判断是否处于tick中，不处于直接tick方块实体
        if(!Ticking.get()){
            tte.tick();
            return;
        }
        phaser.register();
        executor.execute(() -> {
            try {
                tte.tick();

            } catch (Exception e) {
                log.error("黑索金捕捉到在方块实体tick:" + Thread.currentThread().getName() + "发生异常，异常点位于：" + tte.getPos().toString());
                e.printStackTrace();
            } finally {
                phaser.arriveAndDeregister();
            }
        });
    }

    public static void sendQueuedBlockEvents(Deque<BlockEvent> d, ServerWorld sw) {
        Iterator<BlockEvent> bed = d.iterator();
        while (bed.hasNext()) {
            BlockEvent BlockEvent = bed.next();
            if (sw.processBlockEvent(BlockEvent)) {
                sw.getServer().getPlayerManager().sendToAround(null, BlockEvent.pos().getX(), BlockEvent.pos().getY(), BlockEvent.pos().getZ(), 64.0D, sw.getRegistryKey(), new BlockEventS2CPacket(BlockEvent.pos(), BlockEvent.block(), BlockEvent.type(), BlockEvent.data()));
            }
            bed.remove();
        }
    }
}
