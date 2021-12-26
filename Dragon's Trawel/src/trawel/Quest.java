package trawel;

import java.io.Serializable;

public interface Quest extends Serializable {

	public String name();
	
	public String desc();
	
	public void fail();
	
	public void complete();
	
	public void take();
	
	public void questTrigger(String trigger, int num);
	
	public BasicSideQuest reactionQuest();
	
}
