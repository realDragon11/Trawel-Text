package trawel.earts;

import derg.menus.MenuSelect;

public abstract class EArtSkillMenu extends MenuSelect{
	
	EArt art;
	public EArtSkillMenu(EArt ea) {
		art = ea;
	}
/*
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
									return "A combat magic class focused on swapping out spell slots. Study arcane lore at libraries.";
								}});
							list.add(new PlayerSkillpointsLine());
							list.add(new MenuLine() {
								@Override
								public String title() {
									return "You have " + extra.format(Player.player.eaBox.aSpellPower) + " Arcane Spell Power.";
								}});
							if (Player.player.getPerson().getSkillPoints() > 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "increase power (1sp)";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
										Player.player.eaBox.aSpellPower+=1;
										Player.player.eaBox.arcTrainLevel++;
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "learn new spell (1sp)";
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
												list2.add(new MenuBack());
												return list2;
											}});
										return false;
									}});
							}
							if (Player.player.eaBox.aSpells.size() > 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "select first spell";
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
										return "select second spell";
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
							
							list.add(new MenuBack());
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
									return "A martial class based around dealing the final blow. Complete kill quests to increase in power.";
								}});
							list.add(new PlayerSkillpointsLine());
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
										return "basic training (1sp)";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
										Player.player.getPerson().addSkill(Skill.BLOODTHIRSTY);
										Player.player.getPerson().addSkill(Skill.DSTRIKE);
										Player.player.getPerson().addSkill(Skill.KILLHEAL);
										Player.player.eaBox.exeTrainLevel = 1;
										return false;
									}});
							}
							}
							list.add(new MenuBack());
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
									return "A martial class that cannot examine, and instead strikes lightning-quick. Also adept at hand-to-hand.";
								}});
							list.add(new PlayerSkillpointsLine());
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
										return "basic training (1sp)";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
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
										return "hand-to-hand training (1sp)";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
										Player.player.eaBox.berTrainLevel += 1;
										if (Player.player.eaBox.berTrainLevel%5 == 0) {
											Player.player.eaBox.berTrainLevel += 1;
										}
										return false;
									}});
							}
								
							}
							list.add(new MenuBack());
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
									return "A utility class that can carry more drawbanes.";
								}});
							list.add(new PlayerSkillpointsLine());
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
										return "basic training (1sp)";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
										Player.bag.dbMax++;
										Player.player.eaBox.huntTrainLevel = 1;
										return false;
									}});
							}else {
								if (Player.player.eaBox.huntTrainLevel == 1) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "novice training (1sp)";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
										Player.bag.dbMax++;
										Player.player.getPerson().addSkill(Skill.HPSENSE);
										Player.player.eaBox.huntTrainLevel = 2;
										return false;
									}});
								}
							}
								
							}
							list.add(new MenuBack());
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
									return "A martial class that has more health and can use kung-fu.";
								}});
							list.add(new PlayerSkillpointsLine());
							list.add(new PlayerSkillpointsLine());
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
										return "basic training (1sp)";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
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
										return "kung-fu training (1sp) [+martial arts damage]";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
										Player.player.eaBox.drunkTrainLevel += 1;
										if (Player.player.eaBox.drunkTrainLevel%5 == 0) {
											Player.player.eaBox.drunkTrainLevel += 1;
										}
										return false;
									}});
								}
								if (!Player.player.getPerson().hasEnduranceTraining) {
									list.add(new MenuSelect() {

										@Override
										public String title() {
											return "advanced endurance training (1sp) [x2 effectiveness of endurance training]";
										}

										@Override
										public boolean go() {
											Player.player.getPerson().useSkillPoint();
											Player.player.getPerson().hasEnduranceTraining = true;
											Player.player.getPerson().edrLevel++;
											return false;
										}});
								}
								if (!Player.player.getPerson().hasSkill(Skill.SPUNCH)) {
									list.add(new MenuSelect() {

										@Override
										public String title() {
											return "sucker punch (1sp) [increase delay on hit]";
										}

										@Override
										public boolean go() {
											Player.player.getPerson().useSkillPoint();
											Player.player.getPerson().addSkill(Skill.SPUNCH);
											return false;
										}});
								}
							}
								
							}
							list.add(new MenuBack());
							return list;
						}
						
					});
					return false;
				}
				
			};
		case WITCH:
			return new EArtSkillMenu(ea) {

				@Override
				public String title() {
					return "Witch";
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
									return "A utility class that is adept with curses, alchemy, and healing magic.";
								}});
							list.add(new PlayerSkillpointsLine());
							list.add(new MenuLine() {
								@Override
								public String title() {
									return "You have " + Player.player.eaBox.witchTrainLevel + " training level.";
								}});
							if (Player.player.getPerson().getSkillPoints() > 0) {
								if (Player.player.eaBox.witchTrainLevel == 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "basic training (1sp)";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
										Player.player.getPerson().addSkill(Skill.CURSE_MAGE);
										Player.player.getPerson().addSkill(Skill.LIFE_MAGE);
										Player.player.getPerson().addSkill(Skill.MONEY_MAGE);
										Player.player.eaBox.witchTrainLevel = 1;
										return false;
									}});
							}else {
								if (Player.player.eaBox.witchTrainLevel > 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "witch training (1sp)";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
										Player.player.eaBox.witchTrainLevel += 1;
										if (Player.player.eaBox.witchTrainLevel%5 == 0) {
											Player.player.eaBox.witchTrainLevel += 1;
										}
										return false;
									}});
								}
							}
								
							}
							list.add(new MenuBack());
							return list;
						}
						
					});
					return false;
				}
				
			};
		case BLOODMAGE:
			return new EArtSkillMenu(ea) {

				@Override
				public String title() {
					return "Bloodmage";
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
									return "A magic class that uses blood.";
								}});
							list.add(new PlayerSkillpointsLine());
							list.add(new MenuLine() {
								@Override
								public String title() {
									return "You have " + Player.player.eaBox.bloodTrainLevel + " training level.";
								}});
							if (Player.player.getPerson().getSkillPoints() > 0) {
								if (Player.player.eaBox.bloodTrainLevel == 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "basic training (1sp)";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
										Player.player.getPerson().addSkill(Skill.BLOODTHIRSTY);
										Player.player.eaBox.bloodTrainLevel = 1;
										return false;
									}});
							}else {
								if (Player.player.eaBox.bloodTrainLevel > 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "bloodmage training (1sp)";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
										Player.player.eaBox.bloodTrainLevel += 1;
										if (Player.player.eaBox.bloodTrainLevel%5 == 0) {
											Player.player.eaBox.bloodTrainLevel += 1;
										}
										return false;
									}});
								}
							}
								
							}
							list.add(new MenuBack());
							return list;
						}
						
					});
					return false;
				}
				
			};
		case DEFENDER:
			return new EArtSkillMenu(ea) {

				@Override
				public String title() {
					return "Defender";
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
									return "A martial class with a shield or dagger.";
								}});
							list.add(new PlayerSkillpointsLine());
							list.add(new MenuLine() {
								@Override
								public String title() {
									return "You have " + Player.player.eaBox.defTrainLevel + " training level.";
								}});
							if (Player.player.getPerson().getSkillPoints() > 0) {
								if (Player.player.eaBox.defTrainLevel == 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "basic training (1sp)";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
										Player.player.getPerson().addSkill(Skill.ARMORHEART);
										Player.player.getPerson().addSkill(Skill.TA_NAILS);
										Player.player.getPerson().addSkill(Skill.COUNTER);
										Player.player.eaBox.defTrainLevel = 1;
										return false;
									}});
							}else {
								if (Player.player.eaBox.defTrainLevel > 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "defender training (1sp)";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().useSkillPoint();
										Player.player.eaBox.defTrainLevel += 1;
										if (Player.player.eaBox.defTrainLevel%5 == 0) {
											Player.player.eaBox.defTrainLevel += 1;
										}
										return false;
									}});
								}
							}
								if (!Player.player.getPerson().hasSkill(Skill.PARRY) && !Player.player.getPerson().hasSkill(Skill.SHIELD)) {
									list.add(new MenuSelect() {

										@Override
										public String title() {
											return "buy a shield (1sp)";
										}

										@Override
										public boolean go() {
											Player.player.getPerson().useSkillPoint();
											Player.player.eaBox.defTrainLevel += 1;
											Player.player.getPerson().addSkill(Skill.SHIELD);
											Player.player.getPerson().addSkill(Skill.ARMORSPEED);
											return false;
										}});
									list.add(new MenuSelect() {

										@Override
										public String title() {
											return "buy a dagger (1sp)";
										}

										@Override
										public boolean go() {
											Player.player.getPerson().useSkillPoint();
											Player.player.eaBox.defTrainLevel += 1;
											Player.player.getPerson().addSkill(Skill.PARRY);
											Player.player.getPerson().addSkill(Skill.SPEEDDODGE);
											
											return false;
										}});
								}
							}
							list.add(new MenuBack());
							return list;
						}
						
					});
					return false;
				}
				
			};
		}
		throw new RuntimeException("EArt not found to construct");
	}*/
}
