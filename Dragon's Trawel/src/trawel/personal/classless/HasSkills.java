package trawel.personal.classless;

import java.util.Iterator;

public interface HasSkills {

	public Iterator<Skill> getSkills();

	public static class SkillJoiner implements Iterator<Skill>{
		private Iterator<Skill> i1;
		private Iterator<Skill> i2;
		private Iterator<Skill> i3;
		private byte current;
		public SkillJoiner(Iterator<Skill> i1,Iterator<Skill> i2,Iterator<Skill> i3) {
			current = 0;
			this.i1 = i1;
			this.i2 = i2;
			this.i3 = i3;
		}

		@Override
		public boolean hasNext() {
			switch (current) {
			case 0:
				if (i1.hasNext()) {
					return true;
				}else {
					current = 1;
					return hasNext();
				}
			case 1:
				if (i2.hasNext()) {
					return true;
				}else {
					current = 2;
					return hasNext();
				}
			case 2:
				if (i3.hasNext()) {
					return true;
				}else {
					current = 3;
					return hasNext();
				}

			default:
				return false;
			}
		}

		@Override
		public Skill next() {
			switch (current) {
			case 0:
				if (i1.hasNext()) {
					return i1.next();
				}
				current = 1;
				return next();
			case 1:
				if (i2.hasNext()) {
					return i2.next();
				}
				current = 2;
				return next();
			case 2:
				if (i3.hasNext()) {
					return i3.next();
				}
				current = 3;
				return next();
			default:
				return null;
			}
		}

	}
}
