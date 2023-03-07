package dev.kurumiDisciples.chisataki.enums;

public enum ChannelEnum {
	BOT_HOUSE("1010078629344051202"),
	CHISATAKI("1018915242027266129"),
	CHISATO_SHRINE("1013939451979911289"),
	TAKINA_SHRINE("1013939540420997262"),
	TRANSCRIPT_LOGS("1063866322020212787"),
	TICKET_LOGS("1063866445861245128"),
	BOT_CHANNEL("1018915242027266129");

	private String id;

	private ChannelEnum(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public long getIdLong(){
		return Long.parseLong(this.id);
	}
	
	public static boolean areCommandsAllowed(String channelId) {
		return CHISATAKI.id.equals(channelId) || BOT_HOUSE.id.equals(channelId);
	}
}
