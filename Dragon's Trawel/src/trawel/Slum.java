package trawel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import trawel.Feature.QRType;

public class Slum extends Feature implements QuestBoardLocation{

	private boolean removable;
	public SuperPerson crimeLord;
	private double timePassed = 0;
	private int wins = 0;
	private Town town;
	
	private boolean canQuest = true;
	
	public ArrayList<Quest> sideQuests = new ArrayList<Quest>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public QRType getQRType() {
		return QRType.SLUM;
	}

	public Slum(Town t, String name,boolean removable) {
		town = t;
		this.name = name;
		tutorialText = "Slums house crime lords.";
		color = Color.RED;
		this.removable = removable;
	}
	
	@Override
	public void init() {
		try {
			while (sideQuests.size() < 3) {
				generateSideQuest();
			}
			}catch (Exception e) {
				canQuest = false;
			}
	}
	
	private void generateSideQuest() {
		if (sideQuests.size() >= 3) {
			sideQuests.remove(extra.randList(sideQuests));
		}
		BasicSideQuest bsq = BasicSideQuest.getRandomSideQuest(town,this);
		if (bsq != null) {
		sideQuests.add(bsq);
		}
	}
	
	@Override
	public void go() {
		Networking.setArea("dungeon");
		Slum sl = this;
		int removecost = town.getTier()*1000;
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {
					
					@Override
					public String title() {
						return "hang around";
					}
	
					@Override
					public boolean go() {
						backroom();
						return false;
					}
				});
				if (crimeLord != null) { 
					mList.add(new MenuSelect() {
						
						@Override
						public String title() {
							return "attack crime lord";
						}
		
						@Override
						public boolean go() {
							killCrime();
							return false;
						}
					});
				}
				
				if (crimeLord == null && removable) {
					
					mList.add(new MenuSelect() {
						
						@Override
						public String title() {
							return "pay to remove slum ("+removecost+" gold)";
						}
		
						@Override
						public boolean go() {
							if (Player.bag.getGold() > removecost) {
								extra.println("You pay for the reform programs.");
								Player.bag.modGold(-removecost);
								town.enqueneRemove(sl);
								return true;
							}else {
								extra.println("You can't afford to uplift the slum out of poverty.");
							}
							return false;
						}
					});
				}
				mList.add(new MenuSelect() {
	
					@Override
					public String title() {
						return "leave";
					}
	
					@Override
					public boolean go() {
						return true;
					}
				});
				return mList;
			}});
	}

	@Override
	public void passTime(double time) {
		timePassed-=time;
		if (timePassed < 0) {
			if (canQuest) {this.generateSideQuest();}
			if (crimeLord == null){
				crimeLord = extra.randList(town.getOccupants());
				town.getOccupants().remove(crimeLord);
				timePassed = 24;
			}else {
				SuperPerson sp = extra.randList(town.getOccupants());
				if (((Agent)sp).getPerson().getLevel() > ((Agent)crimeLord).getPerson().getLevel() && extra.chanceIn(1,3)) {
					town.getOccupants().add(crimeLord);
					crimeLord = sp;
					town.getOccupants().remove(sp);
					timePassed = 24;
				}else {
					((Agent)crimeLord).getPerson().getBag().modGold(100*town.getTier());
					
				}
			}
			
		}

	}
	
	private void backroom() {
		Slum sl = this;
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				
				for (Quest q: sideQuests) {
					mList.add(new QBMenuItem(q,sl));
				}
				for (QuestR qr: qrList) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "back";
					}

					@Override
					public boolean go() {
						return true;
					}
				});
				return mList;
			}});
		
	}
	
	private void killCrime() {
		Person p = ((Agent)crimeLord).getPerson();
		extra.println(extra.inlineColor(extra.colorMix(Color.RED,Color.WHITE,.5f))+"Attack " + p.getName() + "?");
		if (extra.yesNo()) {
			Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
			if (winner == Player.player.getPerson()) {
				extra.println("You kill the crime lord!");
				crimeLord = null;
			}else {
				extra.println("The crime lord kills you.");
			}
		}
		
	}

	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}

}
