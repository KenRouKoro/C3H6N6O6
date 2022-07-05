package cn.korostudio.c3h6n6o6.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Mixin(NetworkState.PacketHandler.class)
public class PacketHandlerMixin <T extends PacketListener>{
    @Shadow
    @Final
    @Mutable
    private List<Function<PacketByteBuf, ? extends Packet<T>>> packetFactories = Collections.synchronizedList(new ArrayList<>());
}
