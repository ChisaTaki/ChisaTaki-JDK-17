package dev.kurumidisciples.chisataki.listeners;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import dev.kurumidisciples.chisataki.utils.AnimatedGifEncoder;
import dev.kurumidisciples.chisataki.utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.filedisplay.FileDisplay;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

@SuppressWarnings("null")
public class WelcomeInteraction extends ListenerAdapter {

	private static final int THREAD_POOL_SIZE = 10;

	private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	private static final Logger logger = LoggerFactory.getLogger(WelcomeInteraction.class);

	private static final Font DEFAULT_FONT;
	private static Random RANDOM = new Random();

	static {
        Font tempFont;
        try {
            tempFont = Font.createFont(Font.TRUETYPE_FONT, new File("data/font/YuseiMagic-Regular.ttf"));
        } catch (Exception e) {
            logger.error("Unable to load custom font for WelcomeInteraction, using Arial instead.", e);
            tempFont = new Font("Arial", Font.PLAIN, 30);
        }
        DEFAULT_FONT = tempFont;
    }

    @Override
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event){
		threadPool.execute(() -> {
			if (!event.getMember().getUser().isBot()) {
				int guildSize = event.getGuild().getMembers().size();
				
				try {
					event.getGuild().getTextChannelById(ChannelEnum.WELCOME.getId()).sendMessageComponents(
						getWelcomeContainer(event.getMember(), event.getGuild().getMembers().size())
					).useComponentsV2().queue();
				} catch (IOException e) {
					logger.error("Failed to create welcome message for " + event.getMember().getUser().getName(), e);
					try {
                        event.getGuild().getTextChannelById(ChannelEnum.WELCOME.getId()).sendMessageComponents(
                            createFailureContainer(event.getMember(), guildSize)
                        ).useComponentsV2().queue();
                    } catch (IOException e1) {
                       logger.error("Failed to create fallback welcome message for " + event.getMember().getUser().getName(), e1);
                    }
				}
			}
		});
	}

	public static Container getWelcomeContainer(Member member, int guildSize) throws IOException {
		return Container.of(
			TextDisplay.of("## Welcome to the Church of ChisaTaki!"),
			TextDisplay.of(
				"Read <#1010080963927232573> and pick roles in <#1024037775743406111>. Enjoy your stay as you worship ChisaTaki~"
			),
            MediaGallery.of(MediaGalleryItem.fromFile(FileUpload.fromData(createWelcomeGif(member), "welcome.gif"))),
			TextDisplay.of("-# Worshipper Count: " + guildSize)
		).withAccentColor(ColorUtils.PURPLE);
	}

	@Deprecated
	public static MessageEmbed buildEmbed(int guildSize) {
		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle("Welcome to the Church of ChisaTaki!");
		builder.setDescription("Read <#1010080963927232573> and pick roles in <#1024037775743406111>. Enjoy your stay as you worship ChisaTaki~");
		builder.setImage("attachment://welcome.gif");
		builder.setFooter("Worshipper Count: " + guildSize);
		builder.setColor(new Color(254,57,168));

		return builder.build();
	}

    private static Container createFailureContainer(Member member, int guildSize) throws IOException {
        return Container.of(
            TextDisplay.of("## Welcome to the Church of ChisaTaki!"),
            TextDisplay.of("Read <#1010080963927232573> and pick roles in <#1024037775743406111>. Enjoy your stay as you worship ChisaTaki~"),
            MediaGallery.of(MediaGalleryItem.fromFile(FileUpload.fromData(createWelcomeGif(member), "welcome.gif"))),
            TextDisplay.of("-# Worshipper Count: " + guildSize)
        ).withAccentColor(ColorUtils.PURPLE);
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

	  public static BufferedImage makeCircleImage(BufferedImage image) {
        int diameter = Math.min(image.getWidth(), image.getHeight());
        BufferedImage output = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = output.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setClip(new Ellipse2D.Double(0, 0, diameter, diameter));
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return output;
    }

    public static BufferedImage overlayImages(BufferedImage baseImage, BufferedImage topImage, int x, int y) {
        Graphics2D g2d = baseImage.createGraphics();
        g2d.drawImage(topImage, x, y, null);
        g2d.dispose();
        return baseImage;
    }

    public static BufferedImage writeTextOnImage(BufferedImage image, String text, int x, int y, int fontSize) {
        Graphics2D g2 = image.createGraphics();
        g2.setFont(DEFAULT_FONT.deriveFont((float) fontSize));
        g2.setColor(Color.WHITE);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString(text, x, y);
        g2.dispose();
        return image;
    }

    public static ArrayList<BufferedImage> gifToBufferedImages(String filePath, int numberOfFrames, int fullWidth) throws IOException {
        ArrayList<BufferedImage> frames = new ArrayList<>(numberOfFrames);
        BufferedImage gifImage = ImageIO.read(new File(filePath));
        int frameWidth = fullWidth / numberOfFrames;
        int frameHeight = gifImage.getHeight();

        for (int i = 0; i < numberOfFrames; i++) {
            int x = i * frameWidth;
            int width = Math.min(frameWidth, gifImage.getWidth() - x);
            if (width <= 0) break;
            BufferedImage frame = gifImage.getSubimage(x, 0, width, frameHeight);
            frames.add(frame);
        }

        return frames;
    }

    public static InputStream createWelcomeGif(Member member) throws IOException {
        ArrayList<BufferedImage> bufferedImages;
        int chance = RANDOM.nextInt(100);
        if (chance == 0) {
            bufferedImages = gifToBufferedImages("data/images/random.png", 30, 14940);
        } else {
            bufferedImages = gifToBufferedImages("data/images/outline.png", 93, 46314);
        }

        // Cache avatar image and welcome text
        BufferedImage avatar = makeCircleImage(ImageIO.read(new URL(member.getUser().getAvatarUrl())));
        String welcomeText = "Welcome " + member.getUser().getName() + "!";

        ArrayList<BufferedImage> generateFrames = new ArrayList<>(bufferedImages.size());
        for (BufferedImage frame : bufferedImages) {
            generateFrames.add(modifyFrame(frame, avatar, welcomeText));
        }

        return encodeGif(generateFrames);
    }

    private static BufferedImage modifyFrame(BufferedImage frame, BufferedImage avatar, String text) {
        overlayImages(frame, avatar, 185, 25);
        writeTextOnImage(frame, text, 25, 255, 30);
        return frame;
    }

    public static InputStream encodeGif(ArrayList<BufferedImage> imageList) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(baos);
        encoder.setRepeat(0);
        encoder.setDelay(40); // ~25fps
        for (BufferedImage image : imageList) {
            encoder.addFrame(image);
        }
        encoder.finish();
        baos.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
class ImageData {
	public BufferedImage image;
	public int frameWidth;
	public int numberOfFrames;

	public ImageData(BufferedImage image, int frameWidth, int numberOfFrames) {
		this.image = image;
		this.frameWidth = frameWidth;
		this.numberOfFrames = numberOfFrames;
	}


}
