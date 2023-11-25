package dev.kurumidisciples.chisataki.listeners;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import dev.kurumidisciples.chisataki.utils.AnimatedGifEncoder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

public class WelcomeInteraction extends ListenerAdapter {

	private static final int THREAD_POOL_SIZE = 10;

	private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	SlashCommandInteractionEvent event;

	private static Font font = null;
	private static Random random = new Random();

	public void onGuildMemberJoin(GuildMemberJoinEvent event){
		threadPool.execute(() -> {
			if (!event.getMember().getUser().isBot()) {
				int guildSize = event.getGuild().getMembers().size();
				String channelId = ChannelEnum.WELCOME.getId();
				
				try {
					event.getGuild().getTextChannelById(channelId).sendMessage("Hello " + event.getMember().getAsMention() + "!").setEmbeds(buildEmbed(guildSize))
					.setFiles(FileUpload.fromData(createWelcomeGif(event.getMember()), "welcome.gif")).queue();
				} catch (Exception e) {
					e.printStackTrace();
					event.getGuild().getTextChannelById(channelId).sendMessage("Hello " + event.getMember().getAsMention() + "!").setEmbeds(createEmbedFailure(guildSize)).queue();
				}
			}
		});
	}

	public static MessageEmbed buildEmbed(int guildSize) {
		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle("Welcome to the Church of ChisaTaki!");
		builder.setDescription("Read <#1010080963927232573> and pick roles in <#1024037775743406111>. Enjoy your stay as you worship ChisaTaki~");
		builder.setImage("attachment://welcome.gif");
		builder.setFooter("Worshipper Count: " + guildSize);
		builder.setColor(new Color(254,57,168));

		return builder.build();
	}

	private static MessageEmbed createEmbedFailure(int guildSize) {
		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle("Welcome to the Church of ChisaTaki!");
		builder.setDescription("Read <#1010080963927232573> and pick roles in <#1024037775743406111>. Enjoy your stay as you worship ChisaTaki~");
		builder.setImage("https://media.discordapp.net/attachments/1011966579610755102/1015326004828446770/tenor_1.gif");
		builder.setFooter("Worshipper Count: " + guildSize);
		builder.setColor(new Color(254,57,168));

		return builder.build();

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

	public static BufferedImage overlayImages(BufferedImage baseImage, BufferedImage topImage, int x, int y) {
		Graphics2D g2d = null;
		try {
			g2d = baseImage.createGraphics();
			g2d.drawImage(topImage, x, y, null);
		} finally {
			if (g2d != null) {
				g2d.dispose();
			}
		}
		return baseImage;
	}


	public static BufferedImage writeTextOnImage(BufferedImage image, String text, int x, int y, int fontSize) {
		Graphics2D g2 = image.createGraphics();
		try{
			if (font == null){
				font = Font.createFont(Font.TRUETYPE_FONT, new File("data/font/YuseiMagic-Regular.ttf"));
			}
		}
		catch (Exception e){
			System.out.println("Unable to load font");
			font = new Font("Arial", Font.PLAIN, fontSize);
		}
		g2.setFont(font.deriveFont((float) fontSize));
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

	public static ArrayList<BufferedImage> gifToBufferedImages(String filePath, int numberOfFrames, int frameWidth) throws Exception {
		ArrayList<BufferedImage> frames = new ArrayList<>();
		BufferedImage gifImage = ImageIO.read(new File(filePath));
		frameWidth = frameWidth / numberOfFrames;
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

	public static InputStream createWelcomeGif(Member member) throws Exception {
		ArrayList<BufferedImage> bufferedImages = new ArrayList<>();
		 int chance = random.nextInt(100);
		 if (chance == 0) {
			 bufferedImages = gifToBufferedImages("data/images/random.png", 30, 14940);
		 } else {
			 bufferedImages = gifToBufferedImages("data/images/outline.png", 93, 46314);
		 }

		ArrayList<BufferedImage> generateFrames = new ArrayList<>(bufferedImages.size());
		for (BufferedImage img : bufferedImages) {
			generateFrames.add(modifyFrame(img, member));
		}

		InputStream welcomeGif = createGifEncoder(generateFrames);
		System.gc();

		return welcomeGif;
	}

	private static BufferedImage modifyFrame(BufferedImage frame, Member member) throws Exception{
		return writeTextOnImage(overlayImages(
				frame,
				makeCircleImage(ImageIO.read(new URL(member.getUser().getAvatarUrl()))), 
				185,25), "Welcome " + member.getUser().getName() + "!",  25, 255, 30);
	}

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