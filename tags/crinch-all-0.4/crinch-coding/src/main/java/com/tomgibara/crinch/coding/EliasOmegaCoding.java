/*
 * Copyright 2007 Tom Gibara
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.tomgibara.crinch.coding;

import java.math.BigInteger;

import com.tomgibara.crinch.bits.BitReader;
import com.tomgibara.crinch.bits.BitStreamException;
import com.tomgibara.crinch.bits.BitVector;
import com.tomgibara.crinch.bits.BitWriter;

final public class EliasOmegaCoding extends UniversalCoding {

	// statics
	
	public static final EliasOmegaCoding instance = new EliasOmegaCoding();
	public static final ExtendedCoding extended = new ExtendedCoding(instance);
	
	private static int encodeInt0(BitWriter writer, int value) {
		if (value == 1) return 0;
        int size = 32 - Integer.numberOfLeadingZeros(value); //position of leading 1
        return encodeInt0(writer, size-1) + writer.write(value, size);
	}
	
	private static int encodeLong0(BitWriter writer, long value) {
		if (value == 1L) return 0;
        int size = 64 - Long.numberOfLeadingZeros(value); //position of leading 1
        return encodeInt0(writer, size-1) + writer.write(value, size);
	}

	private static int encodeBigInt0(BitWriter writer, BigInteger value) {
		if (value.equals(BigInteger.ONE)) return 0;
		int size = value.bitLength();
		return encodeInt0(writer, size - 1) + writer.write(value, size);
	}

	// constructors
	
	private EliasOmegaCoding() {}
	
	// coding methods
	
	@Override
    public int decodePositiveInt(BitReader reader) {
    	int value = 1;
    	while (reader.readBoolean()) {
	    	value = (1 << value) | reader.read(value);
    	}
    	return value;
    }

	@Override
    public long decodePositiveLong(BitReader reader) {
    	long value = 1;
    	while (reader.readBoolean()) {
	    	value = (1L << (int)value) | reader.readLong((int)value);
    	}
    	return value;
    }
    
	@Override
    public BigInteger decodePositiveBigInt(BitReader reader) {
    	int value = 1;
    	while (reader.readBoolean()) {
    		if (value < 32) {
    	    	value = (1 << value) | reader.read(value);
    		} else {
    			BitVector vector = new BitVector(value + 1);
    			vector.setBit(value, true);
    			reader.readBits(vector.rangeView(0, value));
    			if (reader.readBoolean()) throw new BitStreamException("value too large for BigInteger");
    			return vector.toBigInteger();
    		}
    	}
    	return BigInteger.valueOf(value & 0xffffffffL);
    }

    @Override
    int unsafeEncodePositiveInt(BitWriter writer, int value) {
    	return encodeInt0(writer, value) + writer.writeBit(0);
    }

    @Override
    int unsafeEncodePositiveLong(BitWriter writer, long value) {
    	return encodeLong0(writer, value) + writer.writeBit(0);
    }
    
    @Override
    int unsafeEncodePositiveBigInt(BitWriter writer, BigInteger value) {
    	return encodeBigInt0(writer, value) + writer.writeBit(0);
    }
    
}