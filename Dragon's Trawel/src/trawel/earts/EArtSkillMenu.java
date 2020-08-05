package trawel.earts;

import java.util.ArrayList;
import java.util.List;

import trawel.MenuGenerator;
import trawel.MenuItem;
import trawel.MenuLine;
import trawel.MenuSelect;
import trawel.Player;
import trawel.Skill;
import trawel.extra;

public abstract class EArtSkillMenu extends MenuSelect{
	
	EArt art;
	public EArtSkillMenu(EArt ea) {
		art = ea;
	}

	public static EArtSkillMenu construct(EArt ea) {
		switch (ea) {
		case ARCANIST:
			return new EArtSkillMenu(ea) {

				@Override
				public String title() {
					return "Arcanist";
				}

				@Override
				public boolean go() {
					extra.menuGo(new MenuGenerator() {
						@Override
						public List<MenuItem> gen() {
							List<MenuItem> list = new ArrayList<MenuItem>();
							list.add(new MenuLine() {

								@Override
								public String title() {
									return "You have " + Player.player.getPerson().getSkillPoints() + " skillpoint"+ (Player.player.getPerson().getSkillPoints() == 1 ? "" : "s") +".";
								}});
							list.add(new MenuLine() {
								@Override
								public String title() {
									return "You have " + extra.format(Player.player.eaBox.aSpellPower) + " Arcane Spell Power.";
								}});
							if (Player.player.getPerson().getSkillPoints() > 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "increase power";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().setSkillPoints((Player.player.getPerson().getSkillPoints()-1));
										Player.player.eaBox.aSpellPower+=1;
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "learn new spell";
									}

									@Override
									public boolean go() {
										extra.menuGo(new MenuGenerator() {

											@Override
											public List<MenuItem> gen() {
												List<MenuItem> list2 = new ArrayList<MenuItem>();
												for (ASpell s: ASpell.values()) {
													if (!Player.player.eaBox.aSpells.contains(s)) {
														list2.add(new ASpellLearner(s));
													}
												}
												list2.add(new MenuSelect() {

													@Override
													public String title() {
														return "back";
													}

													@Override
													public boolean go() {
														return true;
													}});
												return list2;
											}});
										return false;
									}});
							}
							if (Player.player.eaBox.aSpells.size() > 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "swap first spell";
									}

									@Override
									public boolean go() {
										
											extra.menuGo(new MenuGenerator() {
												
												@Override
												public List<MenuItem> gen() {
													List<MenuItem> list2 = new ArrayList<MenuItem>();
													for (ASpell a: Player.player.eaBox.aSpells) {
														list2.add(new ASpellChooser(a,1));
													}
													
													return list2;
												}});
										
										return false;
									}});
								
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "swap second spell";
									}

									@Override
									public boolean go() {
										
											extra.menuGo(new MenuGenerator() {
												
												@Override
												public List<MenuItem> gen() {
													List<MenuItem> list2 = new ArrayList<MenuItem>();
													for (ASpell a: Player.player.eaBox.aSpells) {
														list2.add(new ASpellChooser(a,2));
													}
													
													return list2;
												}});
										
										return false;
									}});
								
								}
							
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return "back";
								}

								@Override
								public boolean go() {
									return true;
								}});
							return list;
						}
						
					});
					return false;
				}
				
			};
		case EXECUTIONER:
			return new EArtSkillMenu(ea) {

				@Override
				public String title() {
					return "Executioner";
				}

				@Override
				public boolean go() {
					extra.menuGo(new MenuGenerator() {
						@Override
						public List<MenuItem> gen() {
							List<MenuItem> list = new ArrayList<MenuItem>();
							list.add(new MenuLine() {

								@Override
								public String title() {
									return "You have " + Player.player.getPerson().getSkillPoints() + " skillpoint"+ (Player.player.getPerson().getSkillPoints() == 1 ? "" : "s") +".";
								}});
							list.add(new MenuLine() {
								@Override
								public String title() {
									return "You have " +  extra.format(Player.player.eaBox.getExeExe()) + " Execute Power and " + Player.player.eaBox.exeTrainLevel + " training level.";
								}});
							if (Player.player.getPerson().getSkillPoints() > 0) {
								if (Player.player.eaBox.exeTrainLevel == 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "basic training";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().setSkillPoints((Player.player.getPerson().getSkillPoints()-1));
										Player.player.getPerson().addSkill(Skill.BLOODTHIRSTY);
										Player.player.getPerson().addSkill(Skill.DSTRIKE);
										Player.player.getPerson().addSkill(Skill.KILLHEAL);
										Player.player.eaBox.exeTrainLevel = 1;
										return false;
									}});
							}
							}
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return "back";
								}

								@Override
								public boolean go() {
									return true;
								}});
							return list;
						}
						
					});
					return false;
				}
				
			};
		case BERSERKER:
			return new EArtSkillMenu(ea) {

				@Override
				public String title() {
					return "Berserker";
				}

				@Override
				public boolean go() {
					extra.menuGo(new MenuGenerator() {
						@Override
						public List<MenuItem> gen() {
							List<MenuItem> list = new ArrayList<MenuItem>();
							list.add(new MenuLine() {

								@Override
								public String title() {
									return "You have " + Player.player.getPerson().getSkillPoints() + " skillpoint"+ (Player.player.getPerson().getSkillPoints() == 1 ? "" : "s") +".";
								}});
							list.add(new MenuLine() {
								@Override
								public String title() {
									return "You have " + extra.format(Player.player.eaBox.berTrainLevel) + " training level.";
								}});
							if (Player.player.getPerson().getSkillPoints() > 0) {
								if (Player.player.eaBox.berTrainLevel == 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "basic training";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().setSkillPoints((Player.player.getPerson().getSkillPoints()-1));
										Player.player.getPerson().addSkill(Skill.BLOODTHIRSTY);
										Player.player.getPerson().addSkill(Skill.BLITZ);
										Player.player.getPerson().addSkill(Skill.BERSERKER);
										Player.player.getPerson().addSkill(Skill.BONUSATTACK_BERSERKER);
										Player.player.eaBox.berTrainLevel = 1;
										return false;
									}});
							}else {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "hand-to-hand training";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().setSkillPoints((Player.player.getPerson().getSkillPoints()-1));
										Player.player.eaBox.berTrainLevel += 1;
										if (Player.player.eaBox.berTrainLevel%5 == 0) {
											Player.player.eaBox.berTrainLevel += 1;
										}
										return false;
									}});
							}
								
							}
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return "back";
								}

								@Override
								public boolean go() {
									return true;
								}});
							return list;
						}
						
					});
					return false;
				}
				
			};
		case HUNTER:
			return new EArtSkillMenu(ea) {

				@Override
				public String title() {
					return "Hunter";
				}

				@Override
				public boolean go() {
					extra.menuGo(new MenuGenerator() {
						@Override
						public List<MenuItem> gen() {
							List<MenuItem> list = new ArrayList<MenuItem>();
							list.add(new MenuLine() {

								@Override
								public String title() {
									return "You have " + Player.player.getPerson().getSkillPoints() + " skillpoint"+ (Player.player.getPerson().getSkillPoints() == 1 ? "" : "s") +".";
								}});
							list.add(new MenuLine() {
								@Override
								public String title() {
									return "You have " + Player.player.eaBox.huntTrainLevel + " training level.";
								}});
							if (Player.player.getPerson().getSkillPoints() > 0) {
								if (Player.player.eaBox.huntTrainLevel == 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "basic training";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().setSkillPoints((Player.player.getPerson().getSkillPoints()-1));
										Player.bag.dbMax++;
										Player.player.eaBox.huntTrainLevel = 1;
										return false;
									}});
							}else {
								if (Player.player.eaBox.huntTrainLevel == 1) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "novice training";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().setSkillPoints((Player.player.getPerson().getSkillPoints()-1));
										Player.bag.dbMax++;
										Player.player.eaBox.huntTrainLevel = 2;
										return false;
									}});
								}
							}
								
							}
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return "back";
								}

								@Override
								public boolean go() {
									return true;
								}});
							return list;
						}
						
					});
					return false;
				}
				
			};
		case DRUNK:
			return new EArtSkillMenu(ea) {

				@Override
				public String title() {
					return "Drunk";
				}

				@Override
				public boolean go() {
					extra.menuGo(new MenuGenerator() {
						@Override
						public List<MenuItem> gen() {
							List<MenuItem> list = new ArrayList<MenuItem>();
							list.add(new MenuLine() {

								@Override
								public String title() {
									return "You have " + Player.player.getPerson().getSkillPoints() + " skillpoint"+ (Player.player.getPerson().getSkillPoints() == 1 ? "" : "s") +".";
								}});
							list.add(new MenuLine() {
								@Override
								public String title() {
									return "You have " + Player.player.eaBox.drunkTrainLevel + " training level.";
								}});
							if (Player.player.getPerson().getSkillPoints() > 0) {
								if (Player.player.eaBox.drunkTrainLevel == 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "basic training";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().setSkillPoints((Player.player.getPerson().getSkillPoints()-1));
										Player.player.getPerson().addSkill(Skill.BEER_BELLY);
										Player.player.getPerson().addSkill(Skill.BEER_LOVER);
										Player.player.eaBox.drunkTrainLevel = 1;
										return false;
									}});
							}else {
								if (Player.player.eaBox.drunkTrainLevel > 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "kung-fu training";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().setSkillPoints((Player.player.getPerson().getSkillPoints()-1));
										Player.player.eaBox.drunkTrainLevel += 1;
										if (Player.player.eaBox.drunkTrainLevel%5 == 0) {
											Player.player.eaBox.drunkTrainLevel += 1;
										}
										return false;
									}});
								}
							}
								
							}
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return "back";
								}

								@Override
								public boolean go() {
									return true;
								}});
							return list;
						}
						
					});
					return false;
				}
				
			};
		}
		throw new RuntimeException("EArt not found to construct");
	}
}
