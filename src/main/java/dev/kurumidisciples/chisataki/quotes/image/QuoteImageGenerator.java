package dev.kurumidisciples.chisataki.quotes.image;

import dev.kurumidisciples.chisataki.quotes.QuoteSettings;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Message;

public class QuoteImageGenerator {

    private static Logger logger = LoggerFactory.getLogger(QuoteImageGenerator.class);
    
     public static BufferedImage generateQuoteImage(QuoteSettings settings, Message message) {
          final int width = settings.getWidth();
          final int height = settings.getHeight();

          BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

          BufferedImage overlayImage; // read from avatarUrl
          try {
               InputStream in = message.getMember().getEffectiveAvatar().download().get();
            overlayImage = ImageIO.read(in);
          } catch (IOException | ExecutionException | InterruptedException e) {
               try {
                overlayImage = ImageIO.read(new File("data/image/default-avatar.jpeg"));
               } catch (IOException e1) {
                logger.error("Failed to read default avatar", e1);
                return null;
               }
          }

          ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
          ColorConvertOp op = new ColorConvertOp(cs, null);
          overlayImage = op.filter(overlayImage, null);

          Graphics2D graphics = image.createGraphics();

          graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

          graphics.drawImage(overlayImage.getScaledInstance(width / 2, height, Image.SCALE_SMOOTH), 0, 0, width / 2, height, null); // Draw the avatar

          // Define the start and end points of the gradient
          Point2D start = new Point2D.Float(0, 0); // top left corner
          Point2D end = new Point2D.Float(width, height); // bottom right corner

          // Define fixed distribution points for the gradient
          float[] dist = {
              0.0f, // Start with transparent
              0.30f, // Start of semi-transparent transition
              0.40f, // End of semi-transparent transition
              1.0f // Fully opaque
          };

          // Adjust the colors array to match the distribution
          Color[] colors = {
              new Color(0, 0, 0, 0), // Transparent color
              new Color(0, 0, 0, 100), // Semi-transparent black
              new Color(0, 0, 0, 255), // Opaque black
              new Color(0, 0, 0, 255) // Fully black at 100%
          };

          // Create the gradient paint
          LinearGradientPaint gradientPaint = new LinearGradientPaint(start, end, dist, colors);

          graphics.setPaint(gradientPaint);
          graphics.fillRect(0, 0, width, height);

          int rightMargin = 50; // Distance from the right edge

          graphics.setColor(Color.WHITE);

           // Define text attributes
           int maxWidth = (int) (width * 0.5); // Maximum width for text to occupy
           int startX = (int) (width * 0.46); // Starting X to ensure text is more to the right
           int startY = height / 2; // Adjust starting Y as necessary
   
           // Text content
           String quote = message.getContentStripped();
           String author = "- " + message.getAuthor().getName() + ", " + Calendar.getInstance().get(Calendar.YEAR);
   
           // Draw text
           drawWrappedText(graphics, quote, startX, startY, maxWidth, new Font("Serif", Font.BOLD, 28));
           // Adjust Y position for the author, assuming some space between quote and author
           startY += graphics.getFontMetrics().getHeight() * (countLines(quote, maxWidth, graphics.getFontMetrics()) + 1);
           drawWrappedText(graphics, author, startX, startY, maxWidth, new Font("Serif", Font.ITALIC, 24));
   
           graphics.dispose(); // Clean up the graphics context

          return image;
     }

    private static void drawWrappedText(Graphics2D g2d, String text, int startX, int startY, int maxWidth, Font font) {
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineHeight = fm.getHeight();

        for (String word : words) {
            // Calculate the width of the next word alone
            int nextWordWidth = fm.stringWidth(word);
            
            // Include space in the width calculation if the line is not empty
            int lineWidthWithNextWord = fm.stringWidth(line + (line.length() > 0 ? " " : "") + word);
            
            // If the current line plus the next word is too wide, then wrap.
            // Also, if the next word alone is wider than maxWidth, it should be put on a new line.
            if (lineWidthWithNextWord > maxWidth) {
                g2d.drawString(line.toString(), startX, startY);
                line = new StringBuilder(word); // Start the new line with the current word
                startY += lineHeight; // Move to the next line
            } else {
                line.append((line.length() > 0 ? " " : "")).append(word);
            }
        }
        // Draw any remaining text that didn't exceed maxWidth.
        if (!line.toString().isEmpty()) {
            g2d.drawString(line.toString(), startX, startY);
        }
    }

    // Utility method to estimate the number of lines needed for the given text
    private static int countLines(String text, int maxWidth, FontMetrics fm) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lines = 1; // Start with one line

        for (String word : words) {
            if (fm.stringWidth(line + word + " ") <= maxWidth) {
                line.append(word).append(" ");
            } else {
                lines++; // Increment for a new line
                line = new StringBuilder(word).append(" ");
            }
        }

        return lines;
    }
}
