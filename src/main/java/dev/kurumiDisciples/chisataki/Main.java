package dev.kurumidisciples.chisataki;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.commands.CommandCenter;
import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.listeners.MemeInteraction;
import dev.kurumidisciples.chisataki.listeners.RecordRolesInteraction;
import dev.kurumidisciples.chisataki.listeners.RejoinInteraction;
import dev.kurumidisciples.chisataki.listeners.RoleMenuInteraction;
import dev.kurumidisciples.chisataki.listeners.RuleInteraction;
import dev.kurumidisciples.chisataki.listeners.ShrineDeletionInteraction;
import dev.kurumidisciples.chisataki.listeners.ShrineInteraction;
import dev.kurumidisciples.chisataki.listeners.SupportInteraction;
import dev.kurumidisciples.chisataki.listeners.WelcomeInteraction;
import dev.kurumidisciples.chisataki.modmail.ModMailInteraction;
import dev.kurumidisciples.chisataki.modmail.TicketInteraction;
import dev.kurumidisciples.chisataki.rps.RpsInteraction;
import dev.kurumidisciples.chisataki.secretsanta.SantaInteraction;
import dev.kurumidisciples.chisataki.secretsanta.time.SantaClock;
import dev.kurumidisciples.chisataki.tictactoe.TTTEventHandler;
import dev.kurumidisciples.chisataki.tictactoe.TTTInteractionHandler;
import dev.kurumidisciples.chisataki.utils.MessageCache;
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
      CommandCenter commandCenter = new CommandCenter();
      Database.start();
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
          .addEventListeners(new RuleInteraction()).addEventListeners(new RoleMenuInteraction()).addEventListeners(new SantaInteraction())
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
      SantaClock.start();

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
