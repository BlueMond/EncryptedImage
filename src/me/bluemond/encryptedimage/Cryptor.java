package me.bluemond.encryptedimage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/*
    Make Cryptor a parent class and create subclass of Encryptor and Decryptor

    create Pixel class

    add function and field for generating and holding array of random keys generated from base key
 */

public class Cryptor {
    private long key;
    private int[] splitKey;
    private BufferedImage image;
    private BufferedImage encryptedImage;
    private LinkedHashMap<Point, Color> encryptedPixels;

    private String extension;
    private String fileName;

    public Cryptor(long key, BufferedImage image){
        this.key = key;
        this.image = image;
        encryptedPixels = new LinkedHashMap<>();
        splitKey = new int[3];
        splitKeyToSegments();
    }

    // reverse encryptImageWith's operations IN PROGRESSS
    public String decryptImageMessage(){
        Dimension dimension = new Dimension(image.getWidth(), image.getHeight());
        String message;
        ArrayList<Point> pixelLocations = new ArrayList<>();
        Point lengthPixelLocation = getLengthPixelLocation(dimension);

        pixelLocations.add(lengthPixelLocation);
        int messageLength = decodeLengthPixel(lengthPixelLocation);
        pixelLocations.addAll(getEncryptedPixelLocations(dimension, messageLength));

        message = decodePixels(pixelLocations);

        return message;
    }

    private int decodeLengthPixel(Point lengthPixelLocation) {
    }

    public void encryptImageWith(String message) throws IOException {
        if(!hasEncryptedImage()){
            Dimension dimension = new Dimension(image.getWidth(), image.getHeight());
            ArrayList<Point> pixelLocations = new ArrayList<>();
            Point lengthPixelLocation = getLengthPixelLocation(dimension);

            pixelLocations.add(lengthPixelLocation);
            encodeLengthPixel(lengthPixelLocation);
            pixelLocations.addAll(getEncryptedPixelLocations(dimension, message.length()));

            encodePixels(message, pixelLocations);

            createNewEncryptedImage();
        }
    }

    private void createNewEncryptedImage() throws IOException {
        encryptedImage = image;
        for (Map.Entry entry : encryptedPixels.entrySet()) {
            Point point = (Point)entry.getKey();
            Color color = (Color)entry.getValue();
            encryptedImage.setRGB((int)point.getX(), (int)point.getY(), color.getRGB());
        }

        try{
            ImageIO.write(encryptedImage, "jpg", new File("encrypted2.jpg"));
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    // reverse encodePixels' operations IN PROGRESS
    private String decodePixels(ArrayList<Point> pixelLocations) {
        int[] randoms = generateRandomArray(pixelLocations.size(), splitKey[0] + splitKey[2]);
        int max = 99999;

        for (int i = 1; i < pixelLocations.size(); i++) {
            Point pixelLocation = pixelLocations.get(i);
            Color pixelColor = new Color(image.getRGB((int)pixelLocation.getX(), (int)pixelLocation.getY()));


            int character = message.toCharArray()[i-1];
            int base = 5;
            int blue = Math.abs(randoms[i] % max);
            blue = blue + character;
            int offset = blue % base;
            blue -= offset;
            int divides = 0;

            while(blue > 255){
                blue /= base;
                divides++;
            }

            int green;
            String greenString = "";
            greenString = greenString + offset + "" + divides;
            if(String.valueOf(pixelColor.getGreen()).length() == 3){
                greenString = String.valueOf(pixelColor.getGreen()).charAt(0) + greenString;
            }
            green = Integer.parseInt(greenString);

            int red = blue/2;
            if(blue % 2 != 0){
                red++;
            }
            blue /= 2;

            encryptedPixels.put(pixelLocations.get(i),
                    new Color(red, green, blue));
        }
    }

    private void encodePixels(String message, ArrayList<Point> pixelLocations) {
        int[] randoms = generateRandomArray(pixelLocations.size(), splitKey[0] + splitKey[2]);
        int max = 99999;

        for (int i = 1; i < pixelLocations.size(); i++) {
            Point pixelLocation = pixelLocations.get(i);
            Color pixelColor = new Color(image.getRGB((int)pixelLocation.getX(), (int)pixelLocation.getY()));


            int character = message.toCharArray()[i-1];
            int base = 5;
            int blue = Math.abs(randoms[i] % max);
            blue = blue + character;
            int offset = blue % base;
            blue -= offset;
            int divides = 0;

            while(blue > 255){
                blue /= base;
                divides++;
            }

            int green;
            String greenString = "";
            greenString = greenString + offset + "" + divides;
            if(String.valueOf(pixelColor.getGreen()).length() == 3){
                greenString = String.valueOf(pixelColor.getGreen()).charAt(0) + greenString;
            }
            green = Integer.parseInt(greenString);

            int red = blue/2;
            if(blue % 2 != 0){
                red++;
            }
            blue /= 2;

            encryptedPixels.put(pixelLocations.get(i),
                    new Color(red, green, blue));
        }
    }


    // move all random uses to here
    // ensures large range
    private int[] generateRandomArray(int length, long offsetKeyUsage){
        Random rnd = new Random(key + offsetKeyUsage);
        int[] randoms = new int[length];

        for(int i = 0; i < length; i++){
            int tempKey = 0;
            for(int x = 0; x < splitKey.length; x++){
                int rndIndex = Math.abs(rnd.nextInt() % splitKey.length);
                tempKey += splitKey[rndIndex];
            }
            randoms[i] = rnd.nextInt(tempKey);
        }

        return randoms;
    }

    private void splitKeyToSegments(){
        String stringKey = String.valueOf(key);
        int segmentSize = stringKey.length() / 3;
        int index = 0;

        for(int x = 0; x < splitKey.length; x++){
            index = x * segmentSize;
            String segment = stringKey.substring(index, index+(segmentSize));
            splitKey[x] = Integer.parseInt(segment);
        }


    }

    public void encodeLengthPixel(Point lengthPixelLocation){
        int[] randoms = generateRandomArray(0, splitKey[0] + splitKey[2])
        Color color = new Color(image.getRGB((int)lengthPixelLocation.getX(), (int)lengthPixelLocation.getY()));
    }

    private Point getLengthPixelLocation(Dimension dimension){
        int[] randoms = generateRandomArray(2, key-splitKey[2]);

        return new Point(Math.abs(randoms[0] % dimension.width),
                Math.abs(randoms[1] % dimension.height));
    }

    private ArrayList<Point> getEncryptedPixelLocations(Dimension dimension, int messageLength){
        Random rnd = new Random(key-(splitKey[1] * splitKey[2]));
        ArrayList<Point> pixels = new ArrayList<>();

        for(int i = 0; i < messageLength; i++){
            boolean valid = false;
            //infinite loop happens here
            while(!valid){
                int rand1 = rnd.nextInt(Math.abs((int)key) + splitKey[1] + splitKey[0]);
                int rand2 = rnd.nextInt(Math.abs((int)key) + splitKey[2] + splitKey[0]);
                Point nextPoint = new Point(rand1 % dimension.width,
                        rand2 % dimension.height);
                if(!pixels.contains(nextPoint)){
                    pixels.add(nextPoint);
                    valid = true;
                }
            }
        }


        return pixels;
    }

    public boolean hasEncryptedImage(){
        return encryptedImage != null;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
        splitKeyToSegments();
    }

    public BufferedImage getEncryptedImage() {
        return encryptedImage;
    }

    public LinkedHashMap<Point, Color> getEncryptedPixels() {
        return encryptedPixels;
    }


}
