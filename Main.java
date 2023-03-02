import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import net.dv8tion.jda.api.requests.*;
import net.dv8tion.jda.api.utils.*;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.entities.Guild;

import javax.security.auth.login.LoginException;

import dev.kurumiDisciples.chisataki.listeners.*;
import dev.kurumiDisciples.chisataki.audio.*;
import dev.kurumiDisciples.chisataki.audio.listeners.*;

public class Main {
  final static Logger logger = LoggerFactory.getLogger(Main.class);
  final static int gcSec = 3600;
  private static JDA jda;
  
  public static void main(String[] args) {
    // We construct a builder for a BOT account. If we wanted to use a CLIENT
    // account
    // we would use AccountType.CLIENT

    try {
      jda = JDABuilder.createDefault("MTA3MDA3NDk5MTY1MzE2NzE0NA.G1QKcY.XHfp2jt38TuuLgRhKewLgO3ecJMP56zT30xo74")
          .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
          .enableCache(CacheFlag.VOICE_STATE, CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.MEMBER_OVERRIDES,
              CacheFlag.STICKER, CacheFlag.ROLE_TAGS, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS,
              CacheFlag.SCHEDULED_EVENTS, CacheFlag.FORUM_TAGS)
          .setMemberCachePolicy(MemberCachePolicy.ALL).setChunkingFilter(ChunkingFilter.ALL)
          .addEventListeners(new MemeInteraction()).addEventListeners(new RockPaperInteraction())
          .addEventListeners(new EmbedInteraction()).addEventListeners(new SupportInteraction())
          .addEventListeners(new ShrineInteraction()).addEventListeners(new ShrineDeletionInteraction())
          .addEventListeners(new BotDevContext()).addEventListeners(new GifInteraction())
          .addEventListeners(new RecordRolesInteraction()).addEventListeners(new RejoinInteraction())
          .addEventListeners(new WelcomeInteraction()).addEventListeners(new IgnoreInteraction())
          .addEventListeners(new RuleInteraction()).addEventListeners(new RoleMenuInteraction())
          .addEventListeners(new Debugger()).addEventListeners(new ModMailInteraction())
        .addEventListeners(new TicketInteraction()).addEventListeners(new MusicInteraction())
          .setActivity(Activity.of(ActivityType.WATCHING, "ChisaTaki's Wedding", "https://chisatakicopium.com"))
          .build();
      jda.awaitReady(); // awaits for the cache system to build 
      logger.info("Chisataki Bot successfully built and connected to JDA!");
      
      CommandCenter.addCommands(getJDA());
      logger.info("Commands added!");
      MessageCache.setMaxSize(2000);
      logger.info("Message Cache Size: {}", MessageCache.getMaxSize());


      
    }

    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static JDA getJDA() {
    return jda;
  }
}
