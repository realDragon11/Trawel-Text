package rtrawel.unit;

import java.util.HashMap;

public class ActionFactory {
	private static HashMap<String,Action> data = new HashMap<String, Action>();
	public static void init() {
		data.put("attack",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				caster.getWeapon();
			}

			@Override
			public double getWeight() {
				return 4;
			}

			@Override
			public boolean canCast(RUnit caster) {
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}});
	}
	
	public static Action getActionByName(String str) {
		return data.get(str);
	}
}
