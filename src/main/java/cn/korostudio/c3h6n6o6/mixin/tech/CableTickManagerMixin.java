package cn.korostudio.c3h6n6o6.mixin.tech;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import techreborn.blockentity.cable.CableBlockEntity;
import techreborn.blockentity.cable.CableTickManager;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;


@Mixin(CableTickManager.class)
public class CableTickManagerMixin {
    @Shadow(remap = false)
    @Final
    @Mutable
    private static List<CableBlockEntity> cableList = new CopyOnWriteArrayList<>();
    @Shadow(remap = false)
    @Final
    @Mutable
    private static List<Object> targetStorages = new CopyOnWriteArrayList<>();
    @Shadow(remap = false)
    @Final
    @Mutable
    private static Deque<CableBlockEntity> bfsQueue = new LinkedBlockingDeque<>();

}
