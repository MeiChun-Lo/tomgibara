/*
 * Copyright 2011 Tom Gibara
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
package com.tomgibara.crinch.record.compact;

import java.util.List;

import com.tomgibara.crinch.coding.CodedWriter;
import com.tomgibara.crinch.record.ColumnStats;
import com.tomgibara.crinch.record.LinearRecord;
import com.tomgibara.crinch.record.ProcessContext;
import com.tomgibara.crinch.record.RecordStats;
import com.tomgibara.crinch.record.def.ColumnType;
import com.tomgibara.crinch.record.def.RecordDefinition;

public class RecordCompactor {

	//TODO could trim arrays rather than retain startIndex
	private final ColumnType[] types;
	private final ColumnCompactor[] compactors;
	private final int startIndex;
	
	public RecordCompactor(ProcessContext context, RecordDefinition recordDef, int startIndex) {
		if (recordDef == null) recordDef = context.getRecordDef();
		if (recordDef == null) throw new IllegalArgumentException("context has no record definition");
		RecordStats stats = context.getRecordStats();
		if (stats == null) throw new IllegalArgumentException("context has no record stats");
		stats = stats.adaptFor(recordDef);
		if (startIndex < 0) throw new IllegalArgumentException("negative startIndex");
		if (startIndex > stats.getColumnStats().size()) throw new IllegalArgumentException("invalid startIndex");
		
		List<ColumnType> types = recordDef.getTypes();
		ColumnCompactor[] compactors = new ColumnCompactor[types.size()];
		List<ColumnStats> list = stats.getColumnStats();
		for (int i = 0; i < compactors.length; i++) {
			compactors[i] = new ColumnCompactor(list.get(i));
		}
		
		this.types = (ColumnType[]) types.toArray(new ColumnType[types.size()]);
		this.compactors = compactors;
		this.startIndex = startIndex;
	}

	public ColumnStats getColumnStats(int index) {
		if (index < 0) throw new IllegalArgumentException("negative index");
		if (index >= compactors.length) throw new IllegalArgumentException("invalid index");
		return compactors[index].getStats();
	}
	
	public int compact(CodedWriter writer, LinearRecord record) {
		int c = 0;
		for (int i = startIndex; i < compactors.length; i++) {
			ColumnCompactor compactor = compactors[i];
			switch (types[i]) {
			case BOOLEAN_PRIMITIVE:
			case BOOLEAN_WRAPPER:
			{
				boolean value = record.nextBoolean();
				boolean isNull = record.wasNull();
				c += compactor.encodeNull(writer, isNull);
				if (!isNull) c += compactor.encodeBoolean(writer, value);
				break;
			}
			case BYTE_PRIMITIVE:
			case BYTE_WRAPPER:
			{
				byte value = record.nextByte();
				boolean isNull = record.wasNull();
				c += compactor.encodeNull(writer, isNull);
				if (!isNull) c += compactor.encodeInt(writer, value);
				break;
			}
			case SHORT_PRIMITIVE:
			case SHORT_WRAPPER:
			{
				short value = record.nextShort();
				boolean isNull = record.wasNull();
				c += compactor.encodeNull(writer, isNull);
				if (!isNull) c += compactor.encodeInt(writer, value);
				break;
			}
			case INT_PRIMITIVE:
			case INT_WRAPPER:
			{
				int value = record.nextInt();
				boolean isNull = record.wasNull();
				c += compactor.encodeNull(writer, isNull);
				if (!isNull) c += compactor.encodeInt(writer, value);
				break;
			}
			case LONG_PRIMITIVE:
			case LONG_WRAPPER:
			{
				long value = record.nextLong();
				boolean isNull = record.wasNull();
				c += compactor.encodeNull(writer, isNull);
				if (!isNull) c += compactor.encodeLong(writer, value);
				break;
			}
			case FLOAT_PRIMITIVE:
			case FLOAT_WRAPPER:
			{
				float value = record.nextFloat();
				boolean isNull = record.wasNull();
				c += compactor.encodeNull(writer, isNull);
				if (!isNull) c += compactor.encodeFloat(writer, value);
				break;
			}
			case DOUBLE_PRIMITIVE:
			case DOUBLE_WRAPPER:
			{
				double value = record.nextDouble();
				boolean isNull = record.wasNull();
				c += compactor.encodeNull(writer, isNull);
				if (!isNull) c += compactor.encodeDouble(writer, value);
				break;
			}
			case CHAR_PRIMITIVE:
			case CHAR_WRAPPER:
			{
				char value = record.nextChar();
				boolean isNull = record.wasNull();
				c += compactor.encodeNull(writer, isNull);
				if (!isNull) c += compactor.encodeChar(writer, value);
				break;
			}
			case STRING_OBJECT:
			{
				String value = record.nextString();
				boolean isNull = record.wasNull();
				c += compactor.encodeNull(writer, isNull);
				if (!isNull) c += compactor.encodeString(writer, value);
				break;
			}
				default: throw new IllegalStateException("Unsupported type");
			}
		}
		return c;
	}
	
}
