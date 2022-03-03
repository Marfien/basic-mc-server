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

import io.netty.util.ByteProcessor;

public class VarIntByteDecoder implements ByteProcessor {

  private int readVarInt;
  private int bytesRead;
  private DecodeResult result = DecodeResult.TOO_SHORT;

  @Override
  public boolean process(byte k) {
    this.readVarInt |= (k & 0x7F) << this.bytesRead++ * 7;
    if (this.bytesRead > 3) {
      this.result = DecodeResult.TOO_BIG;
      return false;
    }
    if ((k & 0x80) != 128) {
      this.result = DecodeResult.SUCCESS;
      return false;
    }
    return true;
  }

  public int readVarInt() {
    return readVarInt;
  }

  public int bytesRead() {
    return bytesRead;
  }

  public DecodeResult result() {
    return result;
  }

  public enum DecodeResult {
    SUCCESS,
    TOO_SHORT,
    TOO_BIG
  }
}