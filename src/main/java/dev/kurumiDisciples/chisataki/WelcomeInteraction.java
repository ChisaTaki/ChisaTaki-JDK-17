package dev.kurumiDisciples.chisataki;
/* Java Color */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

public class WelcomeInteraction extends ListenerAdapter {

  final static String WELCOME_CHANNEL = "1010096738381611029";

  SlashCommandInteractionEvent event;

  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    Thread testCommand = new Thread(){
      public void run(){
        if (event.getName().equals("test-image")){
          event.deferReply().queue();
          try{
            
          event.getHook().editOriginal("Hello " + event.getMember().getAsMention() + "!").setEmbeds(createEmbed(event))
            .setFiles(FileUpload.fromData(createGifForWelcome(event.getMember()), "welcome.gif")).queue(); 
            
        }
          catch (Exception e){
            event.getHook().editOriginal("Generation Failed. See console").queue();
            e.printStackTrace();
          }
        }
      }
    };
    testCommand.start();
  }
  public void onGuildMemberJoin(GuildMemberJoinEvent event){
    Thread welcomeThread = new Thread(){
      public void run(){
        if (!event.getMember().getUser().isBot()){
          try{
          event.getGuild().getTextChannelById(WELCOME_CHANNEL).sendMessage("Hello " + event.getMember().getAsMention() + "!").setEmbeds(createEmbed(event))
            .setFiles(FileUpload.fromData(createGifForWelcome(event.getMember()), "welcome.gif")).queue();
        }
          catch (Exception e){
            e.printStackTrace();
          event.getGuild().getTextChannelById(WELCOME_CHANNEL).sendMessage("Hello " + event.getMember().getAsMention() + "!").setEmbeds(createEmbedFailure(event)).queue();
          }

        if (event.getUser().getId().equals("249721338044874753")){
          pingLemons(event);
        }
      }
      }
    };
    welcomeThread.setName("Welcome-Thread");
    welcomeThread.start();
  }


  private static MessageEmbed createEmbed(GuildMemberJoinEvent event){
    EmbedBuilder builder = new EmbedBuilder();

    builder.setTitle("Welcome to the Church of ChisaTaki!");
    builder.setDescription("Read <#1010080963927232573> and pick roles in <#1024037775743406111>. Enjoy your stay as you worship ChisaTaki~");
    builder.setImage("attachment://welcome.gif");
    builder.setFooter("Worshipper Count: " + String.valueOf(event.getGuild().getMembers().size()));
    builder.setColor(new Color(254,57,168));
    
    return builder.build();
  }

  private static MessageEmbed createEmbedFailure(GuildMemberJoinEvent event){
    EmbedBuilder builder = new EmbedBuilder();

    builder.setTitle("Welcome to the Church of ChisaTaki!");
    builder.setDescription("Read <#1010080963927232573> and pick roles in <#1024037775743406111>. Enjoy your stay as you worship ChisaTaki~");
    builder.setImage("https://media.discordapp.net/attachments/1011966579610755102/1015326004828446770/tenor_1.gif");
    builder.setFooter("Worshipper Count: " + String.valueOf(event.getGuild().getMembers().size()));
    builder.setColor(new Color(254,57,168));
    
    return builder.build();
    
  }


  private static MessageEmbed createEmbed(SlashCommandInteractionEvent event){
    EmbedBuilder builder = new EmbedBuilder();

    builder.setTitle("Welcome to the Church of ChisaTaki!");
    builder.setDescription("Read <#1010080963927232573> and pick roles in <#1024037775743406111>. Enjoy your stay as you worship ChisaTaki~");
    builder.setImage("attachment://welcome.gif");
    builder.setFooter("Worshipper Count: " + String.valueOf(event.getGuild().getMembers().size()));
    builder.setColor(new Color(254,57,168));
    
    return builder.build();
  }

  private static void pingLemons(GuildMemberJoinEvent event){
    event.getGuild().getTextChannelById("1010078629344051202").sendMessage("<@263352590534836224> JustW has rejoined the server!").queue();
  }

  private static BufferedImage makeCircleImage(BufferedImage image) {
        int diameter = Math.min(image.getWidth(), image.getHeight());
        BufferedImage output = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = output.createGraphics();
        g2d.setClip(new Ellipse2D.Double(0, 0, diameter, diameter));
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return output;
    }

  private static BufferedImage getImageFromAvatar(String avatarUrl) {
    try{
    return ImageIO.read(new URL(avatarUrl));
    }
    catch (Exception e){
      return null;
    }
  }

  public static BufferedImage overlayImages(BufferedImage baseImage, BufferedImage topImage, int x, int y) {
    Graphics2D g2d = baseImage.createGraphics();
    g2d.drawImage(topImage, x, y, null);
    g2d.dispose();
    return baseImage;
}

  
  public static BufferedImage writeTextOnImage(BufferedImage image, String text, int x, int y, int fontSize) {
    Graphics2D g2 = image.createGraphics();
    try{
    g2.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("data/font/YuseiMagic-Regular.ttf")).deriveFont(Float.valueOf(fontSize)));
    }
    catch (Exception e){
      System.out.println("Unable to load font");
      g2.setFont(new Font("Arial", Font.PLAIN, fontSize));
    }
    
    g2.setColor(Color.WHITE);
    g2.drawString(text, x, y);
    g2.dispose();
    return image;
}

  public static InputStream bufferedImageToInputStream(BufferedImage image, String formatName) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ImageIO.write(image, formatName, outputStream);
    InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
    return inputStream;
}


  private static BufferedImage getBaseImage(){
    BufferedImage baseImage = null;
    try{
      baseImage = ImageIO.read(new File("data/images/testImage.png"));
    }
    catch (Exception e){
      e.printStackTrace();
    }
    return baseImage;
  }

  private static InputStream generateImage(Member member) throws Exception{
    return bufferedImageToInputStream(
                
      writeTextOnImage(overlayImages(
                             getBaseImage(),
                  makeCircleImage(ImageIO.read(new URL(member.getUser().getAvatarUrl()))),
                  185,25)
              , "Welcome " + member.getUser().getName() + "!", 50, 255, 25)
      ,"png");
  }


public static ArrayList<BufferedImage> gifToBufferedImages(String filePath) throws Exception {
    ArrayList<BufferedImage> frames = new ArrayList<>();
    BufferedImage gifImage = ImageIO.read(new File(filePath));
    System.out.println(gifImage.getWidth() + " " + gifImage.getHeight());
    int numberOfFrames = 93;
    int frameWidth = 46314 / numberOfFrames;
    int frameHeight = 290;
    for (int i = 0; i < numberOfFrames; i++) {
          int x = i * frameWidth;
        int width = Math.min(frameWidth, gifImage.getWidth() - x);
        if (width <= 0) {
            break;
        }
        BufferedImage frame = gifImage.getSubimage(x, 0, width, frameHeight);
      //System.out.println("adding frame " + i);
        frames.add(frame);
    }
    return frames;
}


  private static InputStream createGifForWelcome(Member member) throws Exception{

    ArrayList<BufferedImage> bufferedImages = gifToBufferedImages("data/images/outline.png");
    ArrayList<BufferedImage> generateFrames = new ArrayList<>();
    for (int i = 0; i < bufferedImages.size(); i++) {
      generateFrames.add(modifyFrame(bufferedImages.get(i), member));
    }

    return createGifEncoder(generateFrames);
  }

  private static BufferedImage modifyFrame(BufferedImage frame, Member member) throws Exception{
    return writeTextOnImage(overlayImages(
                             frame,
                  makeCircleImage(ImageIO.read(new URL(member.getUser().getAvatarUrl()))), 
                  185,25), "Welcome " + member.getUser().getName() + "!",  25, 255, 30);
  }


  public static BufferedImage createGifImage(ArrayList<BufferedImage> frames, int delay) {
    BufferedImage gifImage = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
        ImageIO.write(frames.get(0), "gif", baos);
        baos.flush();

        for (int i = 1; i < frames.size(); i++) {
            ImageIO.write(frames.get(i), "gif", baos);
        }

        baos.close();
        byte[] bytes = baos.toByteArray();
        gifImage = ImageIO.read(new ByteArrayInputStream(bytes));
    } catch (IOException e) {
        e.printStackTrace();
    }

    return gifImage;
}
  /*
public static InputStream createGifEncoder(ArrayList<BufferedImage> frames, int delay) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    // Get the first frame and create an ImageWriter to write the output GIF
    BufferedImage firstFrame = frames.get(0);
    Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("gif");
    ImageWriter writer = writers.next();

    // Set the output stream and the output parameters
    ImageOutputStream ios = new MemoryCacheImageOutputStream(baos);
    writer.setOutput(ios);

    // Create the write parameters for the GIF format and set the delay time
  
    writer.prepareWriteSequence(null);
    // Set the metadata for the output image
    IIOMetadata metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(firstFrame), null);
    IIOMetadataNode root = new IIOMetadataNode("javax_imageio_gif_image_1.0");
    IIOMetadataNode graphicsControlExtension = new IIOMetadataNode("GraphicControlExtension");
    graphicsControlExtension.setAttribute("delayTime", Integer.toString(delay / 10)); // Convert to centiseconds
    graphicsControlExtension.setAttribute("disposalMethod", "none");
    graphicsControlExtension.setAttribute("userInputFlag", "FALSE");
    graphicsControlExtension.setAttribute("transparentColorFlag", "TRUE");
    graphicsControlExtension.setAttribute("transparentColorIndex", "0");
  
    root.appendChild(graphicsControlExtension);
    metadata.mergeTree("javax_imageio_gif_image_1.0", root);

    try {
        // Write the first frame and the rest of the frames with their delay times
        writer.writeToSequence(new IIOImage(firstFrame, null, metadata), null);
        for (int i = 1; i < frames.size(); i++) {
            BufferedImage frame = frames.get(i);
            writer.writeToSequence(new IIOImage(frame, null, metadata), null);
        }

        writer.endWriteSequence();
        ios.close();
    } finally {
        writer.dispose();
    }

    return new ByteArrayInputStream(baos.toByteArray());
}
*/

  public static InputStream createGifEncoder(ArrayList<BufferedImage> imageList) throws Exception {
  //  File gifFile = new File("data/images/test.gif");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    AnimatedGifEncoder encoder = new AnimatedGifEncoder();
    encoder.start(baos);
     encoder.setRepeat(0);
    encoder.setDelay(40);
    for (BufferedImage image : imageList) {
      //System.out.println("adding image");
      encoder.addFrame(image);
    }
    encoder.finish();
    baos.close();
    return new ByteArrayInputStream(baos.toByteArray());
}
  
}