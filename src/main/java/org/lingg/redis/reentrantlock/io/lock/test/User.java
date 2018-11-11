package org.lingg.redis.reentrantlock.io.lock.test;

import org.lingg.redis.reentrantlock.io.lock.redis.util.LockInfo;

import java.util.Set;

class User {
	private String name;

	private Set<LockInfo> infos;

	public static final String DEFAULT_NAME = "name";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static String getDefaultName() {
		return DEFAULT_NAME;
	}

	public Set<LockInfo> getInfos() {
		return infos;
	}

	public void setInfos(Set<LockInfo> infos) {
		this.infos = infos;
	}

}
