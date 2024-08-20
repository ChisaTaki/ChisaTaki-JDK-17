package dev.kurumidisciples.chisataki.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImageUtils {

    private static Font font = null;
    

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

	public static BufferedImage shrinkImage(BufferedImage image, int maxWidth, int maxHeight){
		int originalWidth = image.getWidth();
		int originalHeight = image.getHeight();
		
		// Calculate the scaling factor to maintain aspect ratio
		double widthRatio = (double) maxWidth / originalWidth;
		double heightRatio = (double) maxHeight / originalHeight;
		double scalingFactor = Math.min(widthRatio, heightRatio);
		
		// Calculate new dimensions
		int newWidth = (int) (originalWidth * scalingFactor);
		int newHeight = (int) (originalHeight * scalingFactor);
		
		// Create a new buffered image with the new dimensions
		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
		
		// Draw the resized image
		Graphics2D g2d = resizedImage.createGraphics();
		g2d.drawImage(image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
		g2d.dispose();
		
		return resizedImage;
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

    public static ArrayList<BufferedImage> gifToBufferedImages(String filePath, int numberOfFrames, int frameWidth, int frameHeight) throws Exception {
		ArrayList<BufferedImage> frames = new ArrayList<>();
		BufferedImage gifImage = ImageIO.read(new File(filePath));
		frameWidth = frameWidth / numberOfFrames;
		for (int i = 0; i < numberOfFrames; i++) {
			int x = i * frameWidth;
			int width = Math.min(frameWidth, gifImage.getWidth() - x);
			if (width <= 0) {
				System.out.println("width is 0");
				break;
			}
			BufferedImage frame = gifImage.getSubimage(x, 0, width, frameHeight);
			System.out.println("adding frame " + i);
			frames.add(frame);
		}
		return frames;
	}

    public static InputStream createGifEncoder(ArrayList<BufferedImage> imageList, int delay) throws Exception {
		//  File gifFile = new File("data/images/test.gif");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		AnimatedGifEncoder encoder = new AnimatedGifEncoder();
		encoder.start(baos);
		encoder.setRepeat(0);
		encoder.colorDepth = 16;
		encoder.setDelay(delay);
		for (BufferedImage image : imageList) {
			//System.out.println("adding image");
			encoder.addFrame(image);
		}
		encoder.finish();
		baos.close();
		return new ByteArrayInputStream(baos.toByteArray());
	}

    public static BufferedImage readImageFile(String filePath) throws Exception {
        return ImageIO.read(new File(filePath
        ));
    }

	public static void writeInputStreamToFile(InputStream stream, String filePath) {
		try (OutputStream outputStream = new FileOutputStream(filePath)) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = stream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage retrieveImageFromUrl(String url) throws Exception {
		return ImageIO.read(new URL(url));
	}

	
}
