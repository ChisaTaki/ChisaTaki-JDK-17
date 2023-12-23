package dev.kurumidisciples.chisataki.rps;

public enum RpsChoice {
	ROCK("U+1FAA8"),
	PAPER("U+1F4C4"),
	SCISSORS("U+2702");
	
	private String unicode;
	
	private RpsChoice(String unicode) {
		this.unicode = unicode;
	}
	
	public String getUnicode() {
		return this.unicode;
	}

	@Override
	public String toString() {
		return this.name().substring(0, 1) + this.name().substring(1).toLowerCase();
	}

	public static RpsChoice getStrongAgainst(RpsChoice choice) {
		if (RpsChoice.ROCK.equals(choice)) {
			return RpsChoice.PAPER;
		} else if (RpsChoice.PAPER.equals(choice)) {
			return RpsChoice.SCISSORS;
		} else if (RpsChoice.SCISSORS.equals(choice)) {
			return RpsChoice.ROCK;
		}
		
		return null;
	}
	
	public static RpsChoice getWeakAgainst(RpsChoice choice) {
		if (RpsChoice.ROCK.equals(choice)) {
			return RpsChoice.SCISSORS;
		} else if (RpsChoice.PAPER.equals(choice)) {
			return RpsChoice.ROCK;
		} else if (RpsChoice.SCISSORS.equals(choice)) {
			return RpsChoice.PAPER;
		}
		
		return null;
	}
}