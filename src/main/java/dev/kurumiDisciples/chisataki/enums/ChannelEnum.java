package dev.kurumiDisciples.chisataki.enums;

public enum ChannelEnum {
	BOT_CHANNEL("1018915242027266129"),
	BOT_HOUSE("1010078629344051202"),
	CHISATAKI("1018915242027266129"),
	CHISATO_SHRINE("1013939451979911289"),
	ROLES("1024037775743406111"),
	RULES("1010080963927232573"),
	SERVER_LOGS("1010094270033711175"),
	TAKINA_SHRINE("1013939540420997262"),
	TICKET_LOGS("1063866445861245128"),
	TRANSCRIPT_LOGS("1063866322020212787"),
	WELCOME("1010096738381611029");

	private String id;

	private ChannelEnum(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public String getAsMention() {
        return "<#" + this.id + '>';
    }
	
	public static boolean areSlashCommandsAllowed(String channelId) {
		return CHISATAKI.id.equals(channelId) || BOT_HOUSE.id.equals(channelId);
	}
}
