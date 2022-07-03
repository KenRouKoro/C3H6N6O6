package cn.korostudio.c3h6n6o6;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import cn.korostudio.c3h6n6o6.thread.CalculationController;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;

@Slf4j
public class C3H6N6O6 implements DedicatedServerModInitializer {
    public static final Setting Setting = new Setting(FileUtil.touch(System.getProperty("user.dir") + "/config/C3H6N6O6.setting"), CharsetUtil.CHARSET_UTF_8, true);
    public static Thread ServerThread;

    @Override
    public void onInitializeServer() {
        log.info("C3H6N6O6 Is Loading!");
        log.info(Thread.currentThread().getName());
        CalculationController.Init();
    }
}
