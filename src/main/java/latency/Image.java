package latency;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Image {

    public static final String SOURCE_FILE = "many-flowers.jpg";
    public static final String DESTINATION_FILE = "many-flowers-out.jpg";

    public static void main(String[] args) throws IOException {
        String file = Objects.requireNonNull(Image.class.getClassLoader().getResource(SOURCE_FILE)).getFile();

        File inputFile = new File(file);

        System.out.println(inputFile.getAbsolutePath());
        BufferedImage originalImage = ImageIO.read(inputFile);
        BufferedImage outputImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);


        long startTime = System.currentTimeMillis();
//        recolorSingleThreaded(originalImage, outputImage);
        recolorMultithreaded(originalImage, outputImage, 3);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;


        File outputFile = new File("target/" + DESTINATION_FILE);
        ImageIO.write(outputImage, "jpg", outputFile);

        System.out.println(String.valueOf(duration));

    }

    public static void recolorMultithreaded(BufferedImage originalImage, BufferedImage recoloredImage, int numberOfThreads) {
        List<Thread> threads = new ArrayList<>();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight() / numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadMultiplier = i;
            Thread thread = new Thread(() -> {
                int leftCorner = 0;
                int topCorner = height * threadMultiplier;
                recolorImage(originalImage, recoloredImage, leftCorner, topCorner, width, height);
            });
            threads.add(thread);
        }

        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void recolorSingleThreaded(BufferedImage originalImage, BufferedImage recoloredImage) {
        recolorImage(originalImage, recoloredImage, 0, 0,
                originalImage.getWidth(), originalImage.getHeight());
    }

    public static void recolorImage(BufferedImage originalImage, BufferedImage recoloredImage, int leftCorner, int topCorner,
                                    int width, int height) {
        for (int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); x++) {
            for (int y = topCorner; y < topCorner + height && y < originalImage.getHeight(); y++) {
                recolorPixel(originalImage, recoloredImage, x, y);
            }
        }
    }

    public static void recolorPixel(BufferedImage originalImage, BufferedImage outputImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);
        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;
        if (isShadeOfGray(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newBlue = blue;
            newGreen = green;
        }

        int newRgb = createRgbFromColors(newRed, newGreen, newBlue);
        setRgb(outputImage, x, y, newRgb);
    }

    public static void setRgb(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }

    public static int createRgbFromColors(int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;
        return rgb;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        return (rgb & 0x000000FF);
    }
}
