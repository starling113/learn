package org.lingg.learn.pattern.observer;

public interface Watched
{
	public void addWatcher(Watcher watcher);
	
	public void removeWatcher(Watcher watcher);
	
	public void notifyWatcher(String str);
}
