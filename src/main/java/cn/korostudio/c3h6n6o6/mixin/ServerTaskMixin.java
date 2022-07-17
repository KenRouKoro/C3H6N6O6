package cn.korostudio.c3h6n6o6.mixin;

import net.minecraft.server.ServerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 干死不处理异常的MJ！
 */
@Mixin(ServerTask.class)
public class ServerTaskMixin implements Runnable{
    /**
     * 直接就是一个拿来用
     */
    @Shadow
    @Final
    @Mutable
    private  Runnable runnable;

    /**
     * 没想到罢.png
     * 好孩子要好好的处理异常哦，不能像MJ顶层不处理（恼
     * 当然，该抛还得抛
     */
    @Override
    public void run() {
        try{
            runnable.run();
        }catch (Exception e){
            Logger logger = LoggerFactory.getLogger(ServerTask.class);
            logger.error("黑索金捕捉到在服务器任务（ServerTask）执行中抛出异常："+e.getClass().getName()+":"+e.getMessage());
        }
    }
}
