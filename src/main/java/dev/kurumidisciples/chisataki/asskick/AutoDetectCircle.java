package dev.kurumidisciples.chisataki.asskick;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class AutoDetectCircle {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AutoDetectCircle.class);

    public static List<Position> detect(BufferedImage image) {
        List<Position> positions = new ArrayList<>();

        int splitX = 232;

        // Process the left side of the image independently
        logger.debug("Processing left side...");
        List<Position> leftPositions = detectTransparentArea(image, 0, splitX, "Left Side");

        // Process the right side of the image independently
        logger.debug("Processing right side...");
        List<Position> rightPositions = detectTransparentArea(image, splitX, image.getWidth(), "Right Side");

        // Ensure both sides have detected areas by retrying if necessary
        if (leftPositions.isEmpty()) {
            logger.debug("Retrying left side detection with modified parameters...");
            leftPositions = retryDetection(image, 0, splitX, "Left Side");
        }

        if (rightPositions.isEmpty()) {
            logger.debug("Retrying right side detection with modified parameters...");
            rightPositions = retryDetection(image, splitX, image.getWidth(), "Right Side");
        }

        // After retries, handle the case where still no transparent area is detected
        if (!leftPositions.isEmpty()) {
            positions.add(leftPositions.get(0));
        } else {
            System.err.println("Failed to detect transparent area in the left side after retry.");
        }

        if (!rightPositions.isEmpty()) {
            positions.add(rightPositions.get(0));
        } else {
            System.err.println("Failed to detect transparent area in the right side after retry.");
        }

        return positions;
    }

    private static List<Position> detectTransparentArea(BufferedImage image, int startX, int endX, String label) {
        List<Position> positions = new ArrayList<>();
        int minX = endX;
        int minY = image.getHeight();
        int maxX = startX;
        int maxY = 0;

        // Iterate over the specified part of the image
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = startX; x < endX; x++) {
                int pixelColor = image.getRGB(x, y);

                // Check if the pixel is transparent
                if ((pixelColor >> 24) == 0x00) {
                    // Update the bounds for this part
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }

        // Log detected bounds for debugging
        logger.debug(label + " detected bounds:");
        logger.debug("minX: " + minX + ", minY: " + minY);
        logger.debug("maxX: " + maxX + ", maxY: " + maxY);

        // Check if any transparent area was found
        if (minX >= endX || maxX <= startX || minY == image.getHeight() || maxY == 0) {
            logger.debug(label + ": No transparent area detected.");
            return positions;
        }

        // Calculate the circle's properties
        int diameter = Math.max(maxX - minX, maxY - minY);
        int centerX = (minX + maxX) / 2;
        int centerY = (minY + maxY) / 2;
        int circleX = centerX - diameter / 2;
        int circleY = centerY - diameter / 2;

        // Ensure the coordinates are within valid bounds
        int imagePosX = Math.max(0, circleX);
        int imagePosY = Math.max(0, circleY);

        // Correct the final y-position
        if (imagePosY < minY) {
            imagePosY = minY;
        }

        // Log final calculated position
        logger.debug(label + " final position to place image: (" + imagePosX + ", " + imagePosY + ")");

        // Add the position to the list
        positions.add(new Position(imagePosX, imagePosY));

        return positions;
    }

    private static List<Position> retryDetection(BufferedImage image, int startX, int endX, String label) {
        logger.debug(label + ": Adjusting parameters for retry...");

        // Expand the search area slightly or adjust sensitivity
        int expandedStartX = Math.max(0, startX - 5);  // Expand search area to the left
        int expandedEndX = Math.min(image.getWidth(), endX + 5);  // Expand search area to the right

        // You can also consider making semi-transparent pixels fully transparent in this retry
        // Here's an example where we slightly relax the transparency threshold
        List<Position> positions = new ArrayList<>();
        int minX = expandedEndX;
        int minY = image.getHeight();
        int maxX = expandedStartX;
        int maxY = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = expandedStartX; x < expandedEndX; x++) {
                int pixelColor = image.getRGB(x, y);

                // Modify detection criteria: consider pixels with some transparency
                if ((pixelColor >> 24) < 0x80) {  // Consider pixels with any transparency
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }

        logger.debug(label + " Retry detected bounds:");
        logger.debug("minX: " + minX + ", minY: " + minY);
        logger.debug("maxX: " + maxX + ", maxY: " + maxY);

        if (minX >= expandedEndX || maxX <= expandedStartX || minY == image.getHeight() || maxY == 0) {
            logger.debug(label + ": No transparent area detected during retry.");
            return positions;
        }

        int diameter = Math.max(maxX - minX, maxY - minY);
        int centerX = (minX + maxX) / 2;
        int centerY = (minY + maxY) / 2;
        int circleX = centerX - diameter / 2;
        int circleY = centerY - diameter / 2;

        int imagePosX = Math.max(0, circleX);
        int imagePosY = Math.max(0, circleY);

        if (imagePosY < minY) {
            imagePosY = minY;
        }

        logger.debug(label + " Retry final position to place image: (" + imagePosX + ", " + imagePosY + ")");

        positions.add(new Position(imagePosX, imagePosY));

        return positions;
    }
}
