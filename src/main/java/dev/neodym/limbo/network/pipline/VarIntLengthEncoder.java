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
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;

@ChannelHandler.Sharable
@RequiredArgsConstructor
public class VarIntLengthEncoder extends MessageToByteEncoder<ByteBuf> {

  protected void encode(ChannelHandlerContext ctx, ByteBuf byteBuf, ByteBuf byteBuf2) {
    int readableBytes = byteBuf.readableBytes();
    int varIntSize = LimboByteBuf.varIntSize(readableBytes);
    if (varIntSize > 3) throw new IllegalArgumentException("unable to fit %d into 3".formatted(readableBytes));

    final LimboByteBuf buf = LimboByteBuf.create(byteBuf2);
    buf.ensureWritable(varIntSize + readableBytes);
    buf.write(readableBytes);
    buf.writeBytes(byteBuf, byteBuf.readerIndex(), readableBytes);
  }
}
