package dev.kurumiDisciples.chisataki;

import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumiDisciples.chisataki.commands.CommandCenter;
import dev.kurumiDisciples.chisataki.internal.database.Database;
import dev.kurumiDisciples.chisataki.listeners.MemeInteraction;
import dev.kurumiDisciples.chisataki.listeners.RecordRolesInteraction;
import dev.kurumiDisciples.chisataki.listeners.RejoinInteraction;
import dev.kurumiDisciples.chisataki.listeners.RoleMenuInteraction;
import dev.kurumiDisciples.chisataki.listeners.RuleInteraction;
import dev.kurumiDisciples.chisataki.listeners.ShrineDeletionInteraction;
import dev.kurumiDisciples.chisataki.listeners.ShrineInteraction;
import dev.kurumiDisciples.chisataki.listeners.SupportInteraction;
import dev.kurumiDisciples.chisataki.listeners.WelcomeInteraction;
import dev.kurumiDisciples.chisataki.modmail.ModMailInteraction;
import dev.kurumiDisciples.chisataki.modmail.TicketInteraction;
import dev.kurumiDisciples.chisataki.rps.RpsInteraction;
import dev.kurumiDisciples.chisataki.tictactoe.TTTEventHandler;
import dev.kurumiDisciples.chisataki.tictactoe.TTTInteractionHandler;
import dev.kurumiDisciples.chisataki.utils.MessageCache;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {
  final static Logger logger = LoggerFactory.getLogger(Main.class);
  final static int gcSec = 3600;
  private static JDA jda;

  public static void main(String[] args) {
    // We construct a builder for a BOT account. If we wanted to use a CLIENT
    // account
    // we would use AccountType.CLIENT
    
    try {
      Dotenv env = Dotenv.configure()
        .directory("crypt/")
        .load();
        //Class.forName("com.mysql.cj.jdbc.Driver");
        DriverManager.setLoginTimeout(60);
        logger.info(String.valueOf(DriverManager.getLoginTimeout()));
      CommandCenter commandCenter = new CommandCenter();
      Database.init();
      jda = JDABuilder.createDefault(env.get("TOKEN"))
          .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
          .enableCache(CacheFlag.VOICE_STATE, CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.MEMBER_OVERRIDES,
              CacheFlag.STICKER, CacheFlag.ROLE_TAGS, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS,
              CacheFlag.SCHEDULED_EVENTS, CacheFlag.FORUM_TAGS)
          .setMemberCachePolicy(MemberCachePolicy.ALL).setChunkingFilter(ChunkingFilter.ALL)
          .addEventListeners(new MemeInteraction()).addEventListeners(new RpsInteraction())
          .addEventListeners(new SupportInteraction()).addEventListeners(new ShrineInteraction())
          .addEventListeners(new ShrineDeletionInteraction()).addEventListeners(new RecordRolesInteraction())
          .addEventListeners(new RejoinInteraction()).addEventListeners(new WelcomeInteraction())
          .addEventListeners(new RuleInteraction()).addEventListeners(new RoleMenuInteraction())
          .addEventListeners(new ModMailInteraction()).addEventListeners(new TicketInteraction(), new TTTInteractionHandler(), new TTTEventHandler())
          .addEventListeners(commandCenter)
          .setActivity(Activity.customStatus("Attending ChisaTaki Wedding"))
          .build();
      jda.awaitReady(); // awaits for the cache system to build
      logger.info("Chisataki Bot successfully built and connected to JDA!");

      commandCenter.addCommands(getJDA());
      logger.info("Commands added!");
      MessageCache.setMaxSize(2000);
      logger.info("Message Cache Size: {}", MessageCache.getMaxSize());


    }

    catch (InterruptedException e) {
      e.printStackTrace();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static JDA getJDA() {
    return jda;
  }
}
