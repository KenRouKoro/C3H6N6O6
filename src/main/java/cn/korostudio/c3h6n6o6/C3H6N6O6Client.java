package cn.korostudio.c3h6n6o6;

import cn.korostudio.c3h6n6o6.thread.CalculationController;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ClientModInitializer;
/**
 * 看啥看，Client入口类
 */
@Slf4j
public class C3H6N6O6Client implements ClientModInitializer {
    /**
     * 入      口
     */
    @Override
    public void onInitializeClient() {
        log.info("黑索金检测到在客户端运行，将使用客户端策略！");
        CalculationController.getClient().set(true);
    }
}
