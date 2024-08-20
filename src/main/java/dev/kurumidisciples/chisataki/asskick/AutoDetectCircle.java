package dev.kurumidisciples.chisataki.asskick;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoDetectCircle {

    private static final Logger logger = LoggerFactory.getLogger(AutoDetectCircle.class);

    public static List<Position> detect(BufferedImage image) {
        List<Position> positions = new ArrayList<>();

        int midX = image.getWidth() / 2;

        // Process the left half of the image
        logger.info("Processing left half...");
        positions.addAll(detectTransparentArea(image, 0, midX, "Left Half"));

        // Process the right half of the image
        logger.info("Processing right half...");
        positions.addAll(detectTransparentArea(image, midX, image.getWidth(), "Right Half"));

        return positions;
    }

    /**
 * Detects the transparent area in the specified portion of the image and calculates
 * the top-left position of a circle that fits within the detected transparent area.
 *
 * <p>The process can be described mathematically as follows:</p>
 *
 * <ul>
 *   <li>Let <code>W</code> be the width of the image, and <code>H</code> be the height.</li>
 *   <li>Let <code>S<sub>x</sub></code> be the starting x-coordinate and <code>E<sub>x</sub></code> be the ending x-coordinate
 *       of the image portion being processed.</li>
 *   <li>Define <code>T(x, y)</code> as a function that returns 1 if the pixel at position <code>(x, y)</code> is transparent,
 *       and 0 otherwise.</li>
 * </ul>
 *
 * <p>Steps:</p>
 *
 * <ol>
 *   <li>Determine the bounding box of transparent pixels:
 *   <pre>
 *   minX = min { x | Sx ≤ x &lt; Ex and ∃ y ∈ [0, H) such that T(x, y) = 1 }
 *   maxX = max { x | Sx ≤ x &lt; Ex and ∃ y ∈ [0, H) such that T(x, y) = 1 }
 *   minY = min { y | 0 ≤ y &lt; H and ∃ x ∈ [Sx, Ex) such that T(x, y) = 1 }
 *   maxY = max { y | 0 ≤ y &lt; H and ∃ x ∈ [Sx, Ex) such that T(x, y) = 1 }
 *   </pre>
 *   </li>
 *
 *   <li>Check if any transparent area was found:
 *   <pre>
 *   If (minX ≥ Ex) or (maxX ≤ Sx) or (minY = H) or (maxY = 0), then there is no transparent area detected.
 *   </pre>
 *   </li>
 *
 *   <li>Calculate the diameter and center of the bounding box:
 *   <pre>
 *   D = max(maxX - minX, maxY - minY)
 *   Cx = (minX + maxX) / 2
 *   Cy = (minY + maxY) / 2
 *   </pre>
 *   </li>
 *
 *   <li>Calculate the circle's top-left position:
 *   <pre>
 *   circleX = Cx - D / 2
 *   circleY = Cy - D / 2
 *   </pre>
 *   </li>
 *
 *   <li>Ensure the coordinates are within valid bounds:
 *   <pre>
 *   finalX = max(0, circleX)
 *   finalY = max(0, circleY)
 *   finalY = max(finalY, minY) // Adjust y-position to ensure it's not above minY
 *   </pre>
 *   </li>
 * </ol>
 *
 * @param image The image in which to detect the transparent area.
 * @param startX The starting x-coordinate for the portion of the image to be processed.
 * @param endX The ending x-coordinate for the portion of the image to be processed.
 * @param label A label for logging purposes.
 * @return A list of positions where the circles should be placed.
 */
    private static List<Position> detectTransparentArea(BufferedImage image, int startX, int endX, String label) {
        List<Position> positions = new ArrayList<>();
        int minX = endX;
        int minY = image.getHeight();
        int maxX = startX;
        int maxY = 0;

        // Iterate over the specified half of the image
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = startX; x < endX; x++) {
                int pixelColor = image.getRGB(x, y);

                // Check if the pixel is transparent
                if ((pixelColor >> 24) == 0x00) {
                    // Update the bounds for this half
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }

        // Log detected bounds for debugging
        logger.info(label + " minX: " + minX + " minY: " + minY + " maxX: " + maxX + " maxY: " + maxY);

        // Check if any transparent area was found
        if (minX >= endX || maxX <= startX || minY == image.getHeight() || maxY == 0) {
            logger.info(label + " no transparent area found");
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
        logger.info("Final " + label + " circleX: " + imagePosX + " circleY: " + imagePosY);

        // Add the position to the list
        positions.add(new Position(imagePosX, imagePosY));

        return positions;
    }
}
