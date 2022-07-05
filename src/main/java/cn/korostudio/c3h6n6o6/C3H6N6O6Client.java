package cn.korostudio.c3h6n6o6;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ClientModInitializer;
@Slf4j
public class C3H6N6O6Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        log.warn("黑索金检测到在客户端运行，将自动禁用并行化功能！");
        C3H6N6O6.Setting.set("disabled", "true");
        C3H6N6O6.Setting.store(System.getProperty("user.dir") + "/config/C3H6N6O6.setting");
    }
}
