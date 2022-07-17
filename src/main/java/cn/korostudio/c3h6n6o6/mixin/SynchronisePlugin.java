package cn.korostudio.c3h6n6o6.mixin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.*;

/**
 * 项目究竟核心玄学类，一个Mixin的插件
 */
public class SynchronisePlugin implements IMixinConfigPlugin {
    /**
     * logger
     */
    private static final Logger syncLogger = LogManager.getLogger();
    /**
     * 列表类集
     */
    private final TreeSet<String> syncAllSet = new TreeSet<>();

    /**
     * 加入列表.png
     * @param mixinPackage The mixin root package from the config
     */
    @Override
    public void onLoad(String mixinPackage) {
        syncAllSet.add("cn.korostudio.c3h6n6o6.mixin.SynchronicityFixer");
    }
    /**
     * 没用到（（（
     * @return returnnull 还要写注释？？？？？？
     */
    @Override
    public String getRefMapperConfig() {
        return null;
    }

    /**
     * 没用到（（（
     * @return 问海绵组去
     */
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }
    /**
     * 没用到（（（
     */
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    /**
     * 没用到（（（
     * @return return null 还要写注释？？？？？？
     */
    @Override
    public List<String> getMixins() {
        return null;
    }
    /**
     * 没用到（（（
     */
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    /**
     * 最玄学的地方！去看源码罢
     * @param targetClassName Transformed name of the target class
     * @param targetClass Target class tree
     * @param mixinClassName Name of the mixin class
     * @param mixinInfo Information about this mixin
     */
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (syncAllSet.contains(mixinClassName)) {
            int posFilter = Opcodes.ACC_PUBLIC;
            int negFilter = Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_NATIVE | Opcodes.ACC_ABSTRACT | Opcodes.ACC_BRIDGE;
            for (MethodNode method : targetClass.methods) {
                if ((method.access & posFilter) == posFilter
                        && (method.access & negFilter) == 0
                        && !method.name.equals("<init>")) {
                    method.access |= Opcodes.ACC_SYNCHRONIZED;
                    syncLogger.info("正在为类"+ targetClassName+ "的" +method.name + "()方法设置同步关键字");
                }
            }

        }

    }
}
