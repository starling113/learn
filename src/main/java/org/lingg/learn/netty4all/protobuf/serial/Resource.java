package org.lingg.learn.netty4all.protobuf.serial;

import org.lingg.learn.netty4all.protobuf.serial.core.Serializer;

public class Resource extends Serializer {
	
	private int gold;
	

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	@Override
	protected void read() {
		this.gold = readInt();
	}

	@Override
	protected void write() {
		writeInt(gold);
	}

}
