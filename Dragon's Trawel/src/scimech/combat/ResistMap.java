package scimech.combat;

import java.util.HashMap;
import java.util.Map;

public class ResistMap {
	private Map<DamageTypes,SubHolder> dtMap = new HashMap<DamageTypes,SubHolder>();
	private Map<DamageMods,SubHolder> dmMap = new HashMap<DamageMods,SubHolder>();
	
	
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
		return new SubHolder(a.systemDamageMult*b.systemDamageMult,a.hpDamageMult*b.hpDamageMult);
	}
}
