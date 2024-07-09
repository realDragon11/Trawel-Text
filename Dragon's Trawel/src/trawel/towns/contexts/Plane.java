package trawel.towns.contexts;
import java.util.ArrayList;
import java.util.List;

import trawel.personal.people.Player;
import trawel.threads.BlockTaskManager;
import trawel.threads.FollowUp.FollowType;
import trawel.threads.ThreadData;
import trawel.time.ContextLevel;
import trawel.time.ContextType;
import trawel.time.TContextOwner;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public class Plane extends TContextOwner{
	private static final long serialVersionUID = 1L;
	private List<World> worlds = new ArrayList<World>();
	private Player player;
	
	public void addWorld(World w) {
		worlds.add(w);	
	}
	
	public List<World> worlds() {
		return worlds;
	}
	
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player p) {
		player = p;
	}

	public List<Town> getTowns() {
		List<Town> list = new ArrayList<Town>();
		for(World w: worlds) {
			for(Island i: w.getIslands()) {
				list.addAll(i.getTowns());
			}
		}
		
		return list;
	}
	/**
	 * unused for Planes, since they are top level
	 */
	@Deprecated
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		return null;
	}
	
	public void advanceTime(double time) {
		timepassive(time);
		for (World t: worlds) {
			//need to set world in thread data now
			ThreadData.getThreadData().world = t;
			if (t == Player.player.getWorld()) {
				timeScope.localEvents(t.contextTime(time,timeScope,true));
			}else {
				timeScope.localEvents(t.contextTime(time,timeScope,ContextType.BACKGROUND,false));
			}
		}
		ThreadData.getThreadData().world = Player.getPlayerWorld();
	}
	
	/**
	 * we do not save time debt, so we resolve it before saves
	 * @param time
	 */
	public void resolveTimeDebt(double time) {
		timepassive(time);
		for (World t: worlds) {
			timeScope.localEvents(t.contextTime(time,timeScope,true));
		}
	}
	
	private void timepassive(double time) {
		if (time == 0) {
			return;
		}
		/*
		if (Player.hasSkill(Skill.MONEY_MAGE)) {
			Player.bag.addGold((int) (Player.player.getPerson().getMageLevel()*time));
		}*/
	}
	
	@Override
	public void reload() {
		trawel.threads.BlockTaskManager.setup();//we set up threads here
		timeScope = new TimeContext(ContextType.UNBOUNDED,this);
		timeSetup();
		for(World w: worlds) {
			w.reload();
		}
	}
	
	@Override
	public void prepareSave() {
		super.prepareSave();
		for(World w: worlds) {
			w.prepareSave();
		}
	}

	public int passiveTasks(BlockTaskManager handler) {
		int tasks = 0;
		for(World w: worlds) {
			if (w.hasDebt()) {
				assert Player.player.getWorld() != w;
				handler.execute(trawel.threads.FollowUp.createTask(w,FollowType.WORLD_TIME));
				tasks++;
			}
		}
		return tasks;
	}
	
	@Override
	public List<TimeEvent> consumeEvents(List<TimeEvent> list) {
		// TODO Auto-generated method stub
		//FIXME plane just discards events rn
		list.clear();
		return list;
	}
	@Override
	public ContextLevel contextLevel() {
		return ContextLevel.PLANE;
	}
}
