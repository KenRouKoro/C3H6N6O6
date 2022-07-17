package cn.korostudio.c3h6n6o6.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * 对DecoderHandler的Mixin
 */
@Mixin(value = DecoderHandler.class ,priority = 10)
public class DecoderHandlerMixin  {
    /**
     * LOGGER镜像
     */
    @Final
    @Mutable
    @Shadow
    private static  Logger LOGGER;

    /**
     * 套try后可以妥善处理部分正常的异常（
     * @param instance 问MJ去
     * @param side 问MJ去
     * @param packetId 问MJ去
     * @param buf 问MJ去
     * @return 问MJ去
     */
    @Redirect(method = "decode",at = @At(value = "INVOKE",target = "Lnet/minecraft/network/NetworkState;getPacketHandler(Lnet/minecraft/network/NetworkSide;ILnet/minecraft/network/PacketByteBuf;)Lnet/minecraft/network/Packet;"))
    private @Nullable Packet<?> fixError(NetworkState instance, NetworkSide side, int packetId, PacketByteBuf buf){
        try {
            return instance.getPacketHandler(side, packetId, buf);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * 异常拦截
     * @param ctx 问MJ去
     * @param buf 问MJ去
     * @param objects 问MJ去
     * @param ci 问MJ去
     */
    @Inject(method = "decode",at = @At(value = "INVOKE",target = "Ljava/io/IOException;<init>(Ljava/lang/String;)V"),cancellable = true)
    private void protectPacket(ChannelHandlerContext ctx, ByteBuf buf, List<Object> objects, CallbackInfo ci){
        LOGGER.warn("decode故障，黑索金已拦截该报错。");
        ci.cancel();
    }


    /*
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> objects) throws Exception {
        int i = buf.readableBytes();
        if (i == 0) {
            return;
        }
        PacketByteBuf packetByteBuf = new PacketByteBuf(buf);
        int j = packetByteBuf.readVarInt();
        Packet<?> packet;
        try {
            packet = ctx.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get().getPacketHandler(this.side, j, packetByteBuf);
        }catch (IndexOutOfBoundsException e){
            return;
        }
        if (packet == null) {
            throw new IOException("Bad packet id " + j);
        }
        int k = ctx.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get().getId();
        FlightProfiler.INSTANCE.onPacketReceived(k, j, ctx.channel().remoteAddress(), i);
        if (packetByteBuf.readableBytes() > 0) {
            throw new IOException("Packet " + ctx.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get().getId() + "/" + j + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetByteBuf.readableBytes() + " bytes extra whilst reading packet " + j);
        }
        objects.add(packet);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(ClientConnection.PACKET_RECEIVED_MARKER, " IN: [{}:{}] {}", new Object[]{ctx.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get(), j, packet.getClass().getName()});
        }
    }
     */
}
