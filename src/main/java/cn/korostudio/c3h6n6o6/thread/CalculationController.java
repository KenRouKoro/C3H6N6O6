package cn.korostudio.c3h6n6o6.thread;

import cn.korostudio.c3h6n6o6.C3H6N6O6;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.chunk.BlockEntityTickInvoker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.korostudio.c3h6n6o6.C3H6N6O6.Setting;
import static cn.korostudio.c3h6n6o6.thread.ServerUtil.LogErrorUtil;

/**
 * 核心控制器
 */
@Slf4j
public class CalculationController {
    /**
     * 线程池执行器
     */
    protected static ExecutorService executor ;
    /**
     * Tick执行中指示器
     */
    @Getter
    protected static AtomicBoolean Ticking = new AtomicBoolean();
    /**
     * 客户端指示器
     */
    @Getter
    protected static AtomicBoolean Client = new AtomicBoolean(false);
    /**
     * 线程序号
     */
    protected static AtomicInteger ThreadID = null;
    /**
     * 线程池同步用同步屏障
     */
    @Getter
    protected static Phaser phaser;
    /**
     * Tick开始时间标识，暂时没有用，为日后性能统计留
     */
    @Getter
    protected static long tickStart = 0;
    /**
     * MC服务器对象
     */
    @Getter
    protected static MinecraftServer server;

    /**
     * 初始化方法
     */
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

    /**
     * Tick开始方法
     * @param server MC服务器对象
     */
    public static void startTick(MinecraftServer server) {

        if (phaser != null) {
            log.warn("多次Tick Start?什么鬼！");
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

    /**
     * Tick结束方法，会等待线程池所有工作执行完毕
     * @param server MC服务器对象
     */
    public static void endTick(MinecraftServer server) {
        if ((Client.get())||(CalculationController.server == server)){
            phaser.arriveAndAwaitAdvance();
            Ticking.set(false);
            phaser = null;
        } else {
            log.warn("多个服务器?什么鬼！");
        }
    }

    /**
     * 执行实体Tick
     * @param entityIn 实体对象
     */
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
                    LogErrorUtil(e);
                }
            }catch(IllegalArgumentException e){
                if(Setting.getBool("logEntityException",true)) {
                    log.warn("黑索金捕捉到在实体Tick:" + Thread.currentThread().getName() + "有线程安全报错。");
                    LogErrorUtil(e);
                }
            } catch (Exception e){
                if(Setting.getBool("logEntityException",true)) {
                    String eMessage = e.getMessage();
                    log.error("黑索金捕捉到在实体Tick:" + Thread.currentThread().getName() + " 抛出异常:" + e.getClass().getName() + ":" + eMessage + "\n");
                    LogErrorUtil(e);
                }
            } finally {
                phaser.arriveAndDeregister();
            }
        });
    }

    /**
     * 执行方块实体
     * @param blockEntityTick BlockEntity对象
     */
    public static void callBlockEntityTick(BlockEntityTickInvoker blockEntityTick) {
        if (Setting.getBool("BlockEntityDisabled",false)) {
            blockEntityTick.tick();
            return;
        }
        //检查是否为容器类，容器类直接tick，不直接tick会出问题，大概是没修改ServerChunkManger的并发限制导致的，但要与C2ME兼容就改不了那里（C2ME是从Mixin又做了一次并发限制，明明已经改造的可以并发了，淦哦），QAQ
        //不过大多数能造成卡顿的Mod的实体类也不是容器类（例如线缆那种），所以影响还是不太大，虽然还是很奇怪就是了（（（
        //if(blockEntityTick instanceof Inventory){
        //    blockEntityTick.tick();
        //    return;
        //}
        //beta6：草，只用禁止玩家实体并行化就行了，什么tm玄学问题，淦！！！！！

        //判断是否处于tick中，不处于直接tick方块实体
        if(!Ticking.get()){
            blockEntityTick.tick();
            return;
        }
        phaser.register();
        executor.execute(() -> {
            try {
                blockEntityTick.tick();

            } catch (RuntimeException e) {
                if (Setting.getBool("logBlockEntityRuntimeException", false)) {
                    log.error("黑索金捕捉到在方块实体tick:" + Thread.currentThread().getName() + "发生运行时异常，异常点位于：" + blockEntityTick.getPos().toString());
                    LogErrorUtil(e);
                }
            } catch (Exception e) {
                if (Setting.getBool("logBlockEntityException", true)) {
                    log.error("黑索金捕捉到在方块实体tick:" + Thread.currentThread().getName() + "发生异常，异常点位于：" + blockEntityTick.getPos().toString());
                    LogErrorUtil(e);
                }
            } finally {
                phaser.arriveAndDeregister();
            }
        });
    }
}
