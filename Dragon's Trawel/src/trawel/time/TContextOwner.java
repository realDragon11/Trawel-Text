package trawel.time;

public abstract class TContextOwner implements java.io.Serializable, CanPassTime, ReloadAble, HasTimeContext{

	protected transient TimeContext timeScope;
	@Override
	public void reload() {
		timeScope = new TimeContext(ContextType.UNBOUNDED,this);//subclasses will want to override this with their own contexts
		timeSetup();
	}
	
	/**
	 * unused but might be used later- call after you set up your timeScope in your reload function to futureproof
	 */
	public void timeSetup() {
		
	}
}
