package dev.kurumidisciples.chisataki.enums;

@Deprecated
public enum FilePathEnum {
	CHINANAGO("data/chisato.json"),
	CHISATOHEART("data/chisatoHeart.json"),
	SAKANA("data/takina.json");

	private String filePath;

	private FilePathEnum(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return this.filePath;
	}
}
