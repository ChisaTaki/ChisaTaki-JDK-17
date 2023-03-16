package dev.kurumiDisciples.chisataki.utils;


public class HTMLUtils{

  public static String getTranscriptHTML(){
    return "<Server-Info>\n" +
        "Server: test (724450679028056084)\n" +
        "Channel: closed-0014 (1080717629708320828)\n" +
        "Messages: 11\n" +
        "Attachments Saved: 0\n" +
        "Attachments Skipped: 0 (due maximum file size limits.)\n" +
        "\n" +
        "<User-Info>\n" +
        "4 - Ticket Tool#4843 (557628352828014614)\n" +
        "3 - HickoryMax#6161 (414141961600106516)\n" +
        "3 - FerrisNya#7967 (654324045025771540)\n" +
        "1 - poetiquette#2493 (185512885080817664)\n" +
        "\n" +
        "<Base-Transcript>\n" +
        "<script src=\"https://tickettool.xyz/transcript/transcript.bundle.min.obv.js\"></script><script type=\"text/javascript\">let channel = \"%s\";let server = \"%s\";let messages = \"%s\";window.Convert(messages, channel, server)</script>";
  }
}