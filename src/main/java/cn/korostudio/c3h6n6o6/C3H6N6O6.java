package cn.korostudio.c3h6n6o6;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import cn.korostudio.c3h6n6o6.thread.CalculationController;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;

/**
 * 看啥看，Main入口类
 */
@Slf4j
public class C3H6N6O6 implements ModInitializer {
    /**
     * 核心配置文件对象，变成小懒蛋力
     */
    public static final Setting Setting = new Setting(FileUtil.touch(System.getProperty("user.dir") + "/config/C3H6N6O6.setting"), CharsetUtil.CHARSET_UTF_8, true);
    /**
     * 服务器线程，这玩意原来还有点用，现在没用
     */
    public static Thread ServerThread;

    /**
     * 入     口
     */
    @Override
    public void onInitialize() {
        log.info("C3H6N6O6 is loading!");
        Setting.autoLoad(true);
        log.info("C3H6N6O6 is being initialised.");
        CalculationController.Init();
        log.info("Initialisation of C3H6N6O6 is complete.");
    }
}
