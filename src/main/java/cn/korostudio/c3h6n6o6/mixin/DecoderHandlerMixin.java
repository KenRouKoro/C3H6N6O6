package cn.korostudio.c3h6n6o6.mixin;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.minecraft.network.*;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.util.List;

@Mixin(DecoderHandler.class)
public class DecoderHandlerMixin extends ByteToMessageDecoder {
    @Final
    @Mutable
    @Shadow
    private static  Logger LOGGER;
    @Final
    @Mutable
    @Shadow
    private  NetworkSide side;
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
}
