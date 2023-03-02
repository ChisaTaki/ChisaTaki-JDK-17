package dev.kurumiDisciples.chisataki.audio.listeners;

import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.Map;
import java.util.HashMap;

import dev.kurumiDisciples.chisataki.audio.*;

public class MusicInteraction extends ListenerAdapter {

  private static SlashCommandInteractionEvent slashEvent;
  private static AudioPlayerManager playerManager;
  private static Map<Long, GuildMusicManager> musicManagers;

  private static Guild chisaTakiGuild;

 public MusicInteraction() {
    musicManagers = new HashMap<>();

    playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    AudioSourceManagers.registerLocalSource(playerManager);
  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    Thread musicThread = new Thread(){
      public void run(){
        if (event.getName().equals("start-music")){
          slashEvent = event;
          event.deferReply(true).queue();
          chisaTakiGuild = event.getGuild();
          try {
          VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();

          
          loadAndPlay("https://cdn.discordapp.com/attachments/1017955351280230440/1078420189462409317/Lycoris_Recoil_Original_Soundtrack_Vol._1.mp3", voiceChannel);
          }
          catch (NullPointerException e){
            event.getHook().editOriginal("You are not in a voice channel right now.").queue();
          }
        }
        else if (event.getName().equals("stop-music")){
          event.deferReply(true).queue();
          
          Guild guild = event.getGuild();

          guild.getAudioManager().closeAudioConnection();
          event.getHook().editOriginal("Audio Connection closed").queue();
        }
      }
    };
    musicThread.start();
  }

  private static void loadAndPlay(final String trackUrl, VoiceChannel voiceChannel) {
    GuildMusicManager musicManager = getGuildAudioPlayer(voiceChannel.getGuild());

    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        try{
         slashEvent.getHook().editOriginal("Adding to queue " + track.getInfo().title).queue();
        }
        catch (NullPointerException e){
          slashEvent.getHook().editOriginal("Added").queue();
        }
        catch (Exception e){
          System.out.println("Repeat loaded");
        }

        play(voiceChannel.getGuild(), musicManager, track, voiceChannel);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null) {
          firstTrack = playlist.getTracks().get(0);
        }

        slashEvent.getHook().editOriginal("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

        play(voiceChannel.getGuild(), musicManager, firstTrack, voiceChannel);
      }

      @Override
      public void noMatches() {
        slashEvent.getHook().editOriginal("Nothing found by " + trackUrl).queue();
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        slashEvent.getHook().editOriginal("Could not play: " + exception.getMessage()).queue();
      }
    });
  }

  private static void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, VoiceChannel voiceChannel) {
    connectToFirstVoiceChannel(guild.getAudioManager(), voiceChannel);

    musicManager.getTrackScheduler().queue(track);
  }
  
  private static void skipTrack() {
    GuildMusicManager musicManager = getGuildAudioPlayer(slashEvent.getGuild());
    musicManager.scheduler.nextTrack();

    slashEvent.getHook().editOriginal("Skipped to next track.").queue();
  }

  private static void connectToFirstVoiceChannel(AudioManager audioManager, VoiceChannel voiceChannel) {
    if (!audioManager.isConnected()) {     
        audioManager.openAudioConnection(voiceChannel);
    }
  }

  private static synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
    long guildId = Long.parseLong(guild.getId());
    GuildMusicManager musicManager = musicManagers.get(guildId);

    if (musicManager == null) {
      musicManager = new GuildMusicManager(playerManager);
      musicManagers.put(guildId, musicManager);
    }

    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

    return musicManager;
  }

  public static void repeatTrack(){
    try{
    VoiceChannel currentlyConnected = chisaTakiGuild.getAudioManager().getConnectedChannel().asVoiceChannel();

    loadAndPlay("https://cdn.discordapp.com/attachments/1017955351280230440/1078420189462409317/Lycoris_Recoil_Original_Soundtrack_Vol._1.mp3", currentlyConnected);
    }
    catch (NullPointerException e){
      System.out.println("Could not repeat track");
    }
  }
}