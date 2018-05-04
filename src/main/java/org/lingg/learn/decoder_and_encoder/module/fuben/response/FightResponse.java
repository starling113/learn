package org.lingg.learn.decoder_and_encoder.module.fuben.response;

import org.lingg.learn.decoder_and_encoder.serial.Serializer;

public class FightResponse extends Serializer {

	/**
	 * 获取金币
	 */
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
