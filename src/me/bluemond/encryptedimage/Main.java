package me.bluemond.encryptedimage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class Main {

    //problem with repetitive key producing infinite loop in finding pixel locations

    public static void main(String[] args) throws IOException {
        BufferedImage image = null;
        String path = "";
        Cryptor cryptor;

        while(image == null) {
            //retrieve file path from user
            path = promptForPath();

            //retrieve image
            image = getImageFromPath(path);
        }


        //retrieve key from user
        long key = promptForKey();
        //retrieve message from user
        String message = promptForMessage();

        cryptor = new Cryptor(key, image);
        cryptor.encryptImageWith(message);
        System.out.println("Encryption finihsed");

        /*
        image.setRGB(10, 10, new Color(255,255,255).getRGB());
        ImageIO.write(image, "png", new File("lifemc2.png"));
         */

    }

    //make prompt class with base prompt functions for necessary data types and more defined functions
    //that utilize the base functions
    public static int promptForOperation(){
        Scanner scanner = new Scanner(System.in);
        int operation;

        System.out.println("Enter number for option: ");
        System.out.println("0 - encrypt image");
        System.out.println("1 - decrypt image");
        operation = Integer.parseInt(scanner.nextLine());

        return operation;
    }


    public static String promptForMessage(){
        Scanner scanner = new Scanner(System.in);
        String message;

        System.out.print("Enter a message to encrypt: ");
        message = scanner.nextLine();

        return message;
    }


    private static String promptForPath() {
        Scanner scanner = new Scanner(System.in);
        String path;

        System.out.print("Enter a file path: ");
        path = scanner.nextLine();

        return path;
    }

    // make it that you can enter 0s and varying ranges of digits and it just converts the key
    private static long promptForKey() {
        Scanner scanner = new Scanner(System.in);
        long key = 0;
        boolean valid = false;

        while(!valid) {
            try{
                System.out.print("Enter a number-based encryption key (of 15 digits with no zeros): ");
                key = Long.parseLong(scanner.nextLine());
                if(String.valueOf(key).length() != 15){
                    System.out.println("Key has to be 15 digits long!");
                }else if(key < 0){
                    System.out.println("Key must be a positive number!");
                }else if(String.valueOf(key).contains("0")) {
                    System.out.println("Key must not contain zeros!");
                }else{
                    valid = true;
                }
            }catch(NumberFormatException e){
                System.out.println("You need to enter a whole number!");
            }
        }

        return key;
    }


    private static LinkedHashMap<Point, Color> getMapFromImage(BufferedImage image) {
        LinkedHashMap<Point, Color> pixels = new LinkedHashMap<>();

        if(image != null){
            for(int y = 0; y < image.getHeight(); y++){
                for(int x = 0; x < image.getWidth(); x++){
                    Point point = new Point(x, y);
                    Color color = new Color(image.getRGB(x, y), true);
                    pixels.put(point, color);
                }
            }
        }

        return pixels;
    }


    public static BufferedImage getImageFromPath(String path){
        File file = new File(path);
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("File not found!");
        }

        return image;
    }
}
