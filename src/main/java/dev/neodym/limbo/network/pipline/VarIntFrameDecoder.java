/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.neodym.limbo.network.pipline;

import dev.neodym.limbo.network.LimboByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class VarIntFrameDecoder extends ByteToMessageDecoder {

  private final byte[] lenBuf = new byte[3];

  @Override
  protected void decode(final @NotNull ChannelHandlerContext ctx, final @NotNull ByteBuf byteBuf, final @NotNull List<Object> out) {
    // Paper start - if channel is not active just discard the packet
    if (!ctx.channel().isActive()) {
      byteBuf.skipBytes(byteBuf.readableBytes());
      return;
    }
    // Paper end
    byteBuf.markReaderIndex();
    // Paper start - reuse temporary length buffer
    byte[] abyte = lenBuf;
    java.util.Arrays.fill(abyte, (byte) 0);
    // Paper end
    byte[] bs = new byte[3];

    for(int i = 0; i < bs.length; ++i) {
      if (!byteBuf.isReadable()) {
        byteBuf.resetReaderIndex();
        return;
      }

      bs[i] = byteBuf.readByte();
      if (bs[i] >= 0) {
        LimboByteBuf friendlyByteBuf = LimboByteBuf.create(Unpooled.wrappedBuffer(bs));

        try {
          int j = friendlyByteBuf.read(int.class);
          if (byteBuf.readableBytes() >= j) {
            out.add(byteBuf.readBytes(j));
            return;
          }

          byteBuf.resetReaderIndex();
        } finally {
          friendlyByteBuf.release();
        }

        return;
      }
    }

    throw new CorruptedFrameException("length wider than 21-bit");
  }
}