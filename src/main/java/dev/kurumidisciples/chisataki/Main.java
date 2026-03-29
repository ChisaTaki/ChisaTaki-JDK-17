package dev.kurumidisciples.chisataki;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.theokanning.openai.assistants.assistant.Assistant;
import com.theokanning.openai.service.OpenAiService;

import dev.kurumidisciples.chisataki.alerts.ErrorListener;
import dev.kurumidisciples.chisataki.commands.CommandCenter;
import dev.kurumidisciples.chisataki.games.rps.RpsInteraction;
import dev.kurumidisciples.chisataki.games.tictactoe.TTTEventHandler;
import dev.kurumidisciples.chisataki.games.tictactoe.TTTInteractionHandler;
import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.listeners.AiListenerInteraction;
import dev.kurumidisciples.chisataki.listeners.IntroductionListener;
import dev.kurumidisciples.chisataki.listeners.MemeInteraction;
import dev.kurumidisciples.chisataki.listeners.RoleMenuInteraction;
import dev.kurumidisciples.chisataki.listeners.RuleInteraction;
import dev.kurumidisciples.chisataki.listeners.ShrineDeletionInteraction;
import dev.kurumidisciples.chisataki.listeners.ShrineInteraction;
import dev.kurumidisciples.chisataki.listeners.SupportInteraction;
import dev.kurumidisciples.chisataki.listeners.WelcomeInteraction;
import dev.kurumidisciples.chisataki.modmail.ModMailInteraction;
import dev.kurumidisciples.chisataki.modmail.TicketInteraction;
import dev.kurumidisciples.chisataki.radiata.listener.RadiataImageListener;
import dev.kurumidisciples.chisataki.secretsanta.MessageUsers;
import dev.kurumidisciples.chisataki.secretsanta.SantaInteraction;
import dev.kurumidisciples.chisataki.secretsanta.time.SantaClock;
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

  private static Assistant assistant;
  private static OpenAiService aiService;

  private static OpenAIClient client;

  public static void main(String[] args) {

    
        var iFactory = org.slf4j.LoggerFactory.getILoggerFactory();
        if (!(iFactory instanceof ch.qos.logback.classic.LoggerContext ctx)) {
          throw new IllegalStateException("Not using Logback; got " + iFactory.getClass());
        }
        var tee = new ErrorListener(); // AppenderBase
        tee.setName("ErrorTee");
        tee.setContext(ctx);
        tee.start();
        var root = ctx.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        if (root.getAppender("ErrorTee") == null) root.addAppender(tee);
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
          .addEventListeners(
              new MemeInteraction(), 
              new RpsInteraction(),
              new SupportInteraction(), 
              new ShrineInteraction(),
              new ShrineDeletionInteraction(),  
              new WelcomeInteraction(),
              new RuleInteraction(), 
              new RoleMenuInteraction(),
              new ModMailInteraction(), 
              new TicketInteraction(), 
              new TTTInteractionHandler(), 
              new TTTEventHandler(),
              new AiListenerInteraction(),
              new IntroductionListener(),
              new RadiataImageListener(),
              commandCenter,
              new SantaInteraction()
          )
          .setActivity(Activity.customStatus("On Honeymoon with Takina..."))
          .build();
      jda.awaitReady(); // awaits for the cache system to build
      logger.info("Chisataki Bot successfully built and connected to JDA!");

      commandCenter.addCommands(getJDA());
      logger.info("Commands added!");
      MessageCache.setMaxSize(10000);
      logger.info("Message Cache Size: {}", MessageCache.getMaxSize());

       aiService = new OpenAiService(env.get("OPENAI_API_KEY"));
       assistant = aiService.retrieveAssistant(env.get("ASSISTANT_ID"));
       client = OpenAIOkHttpClient.builder().apiKey(env.get("OPENAI_API_KEY")).build();
      logger.info("OpenAI Service successfully built!");
      logger.info("Assistant successfully built!");
      //SantaClock.start();
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

  public static Assistant getAssistant() {
    return assistant;
  }

  public static OpenAiService getAiService() {
    return aiService;
  }

  public static OpenAIClient getAiClient(){
    return client;
  }
}
