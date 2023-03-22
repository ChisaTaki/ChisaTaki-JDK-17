package dev.kurumiDisciples.chisataki.utils;


public class HTMLUtils{

  public static String getTranscriptHTML(){
    return "<Server-Info>\n" +
        "Server: empty ()\n" +
        "Channel: empty ()\n" +
        "Messages: 0\n" +
        "Attachments Saved: 0\n" +
        "Attachments Skipped: 0 (due maximum file size limits.)\n" +
        "\n" +
        "<User-Info>\n" +
        "\n" +
        "<Base-Transcript>\n" +
        "<script src=\"https://tickettool.xyz/transcript/transcript.bundle.min.obv.js\"></script><script type=\"text/javascript\">let channel = \"%s\";let server = \"%s\";let messages = \"%s\";window.Convert(messages, channel, server)</script>";
  }
}