package dev.kurumiDisciples.chisataki.notifications;

import dev.kurumiDisciples.javadex.api.listeners.ListenerImpl;
import dev.kurumiDisciples.javadex.api.entities.enums.TranslatedLanguage;
import dev.kurumiDisciples.javadex.api.events.NewChapterEvent;

import dev.kurumiDisciples.chisataki.Main;

import java.util.concurrent.*;

public class MangaNotification extends ListenerImpl {

  private static final ExecutorService executor = Executors.newCachedThreadPool();

  @Override
  public void onNewChapterEvent(NewChapterEvent event){
    executor.execute(() -> {
      if (event.getChapter().getTranslatedLanguage().equals(TranslatedLanguage.ENGLISH)) {
        Main.getJDA()
          .getGuildById("1010078628761055234").getTextChannelById("1012045253638889493")
          .sendMessage("<@&1013809402547011616> New Manga Chapter Available! https://www.mangadex.org/chapter/" + event.getChapter().getId()).queue();
      }
    });
  }
}