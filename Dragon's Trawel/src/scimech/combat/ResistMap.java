package scimech.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResistMap {
	private Map<DamageTypes,SubHolder> dtMap = new HashMap<DamageTypes,SubHolder>();
	private Map<DamageMods,SubHolder> dmMap = new HashMap<DamageMods,SubHolder>();
	
	public boolean isSub = false;
	public List<ResistMap> subMaps = new ArrayList<ResistMap>();
	
	public class SubHolder{
		public float systemDamageMult;
		public float hpDamageMult;
		
		public SubHolder(float systemDamageMult,float hpDamageMult) {
			this.systemDamageMult = systemDamageMult;
			this.hpDamageMult = hpDamageMult;
		}
	}
	
	public void put(DamageTypes dt, float systemDamageMult,float hpDamageMult) {
		dtMap.put(dt, new SubHolder(systemDamageMult,hpDamageMult));
	}
	
	public void put(DamageMods dm, float systemDamageMult,float hpDamageMult) {
		dmMap.put(dm, new SubHolder(systemDamageMult,hpDamageMult));
	}
	
	public SubHolder calcMult(DamageTypes dt,DamageMods dm) {
		SubHolder a = dtMap.get(dt);
		SubHolder b = dmMap.get(dm);
		if (a == null) {
			a = new SubHolder(1f,1f);;
		}
		if (b == null) {
			b = new SubHolder(1f,1f);;
		}
		SubHolder c;
		//if (isSub) {
			c = new SubHolder(1f,1f);
		//}else {
			float totalA = 1,totalB =1;
			for (ResistMap rm: subMaps) {
				SubHolder sh = rm.calcMult(dt, dm);
				totalA *=sh.systemDamageMult;
				totalB *=sh.hpDamageMult;
			}
			c = new SubHolder(totalA,totalB);
		//}
		return new SubHolder(a.systemDamageMult*b.systemDamageMult*c.systemDamageMult,a.hpDamageMult*b.hpDamageMult*c.hpDamageMult);
	}
}
