package cn.korostudio.c3h6n6o6.thread;

import cn.hutool.core.thread.ThreadUtil;
import cn.korostudio.c3h6n6o6.C3H6N6O6;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
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
@Slf4j
public class CalculationController {
    protected static ExecutorService executor ;
    @Getter
    protected static AtomicBoolean Ticking = new AtomicBoolean();
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
            log.warn("多个服务器?什么鬼！");
        } else {
            tickStart = System.nanoTime();
            Ticking.set(true);
            phaser = new Phaser();
            phaser.register();
            CalculationController.server = server;
        }
    }
    public static void endTick(MinecraftServer server) {
        if (CalculationController.server != server) {
            log.warn("多个服务器?什么鬼！");
        } else {
            phaser.arriveAndAwaitAdvance();
            Ticking.set(false);
            phaser = null;
        }
    }
    public static void callEntityTick(Entity entityIn, ServerWorld serverworld) {
        if (Setting.getBool("disabled",false)){
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
    public static void callBlockEntityTick(BlockEntityTickInvoker tte, World world) {
        if (Setting.getBool("disabled",false)) {
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
}
