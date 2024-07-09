package rtrawel.village;

import java.util.Arrays;
import java.util.List;

import rtrawel.EventFlag;
import rtrawel.battle.Party;
import rtrawel.unit.RPlayer;
import trawel.core.Input;
import trawel.core.Print;

public class RecruitSpot implements Content{

	public String name;
	public String pname;
	public Village village;
	public int wanders;
	public List<String> paths;
	public String evF;
	public RecruitSpot(String n, String pn, List<String> paths,Village v, String evF) {
		pname = pn;
		name = n;
		village = v;
		this.paths = paths;
		this.evF = evF;
	}
	
	public RecruitSpot(String n, String pn,Village v, String evF, String...strings) {
		pname = pn;
		name = n;
		village = v;
		this.paths = Arrays.asList(strings);
		this.evF = evF;
	}
	
	@Override
	public boolean go() {
		Print.println("What class do you want " + pname + " to be?");
		for (int i = 0; i < paths.size();i++) {
			Print.println((i+1) +" " + paths.get(i));
		}
		RPlayer p = new RPlayer(pname,paths.get(Input.inInt(paths.size())-1));
		village.conts.remove(this);
		EventFlag.eventFlag.setEF(evF, 1);
		Party.party.list.add(p);
		p.refresh();
		return false;
		
	}

	@Override
	public String name() {
		return name ;
	}

}
