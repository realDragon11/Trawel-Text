package rtrawel.items;

import rtrawel.unit.RUnit;

public abstract class OnHit {

	public final static OnHit empty = new OnHit() {

		@Override
		public void go(RUnit caster, RUnit u) {}};
	
	public abstract void go(RUnit caster, RUnit u);
}
