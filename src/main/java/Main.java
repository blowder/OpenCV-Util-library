import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by sesshoumaru on 8/22/15.
 */
public class Main {
    static {
        System.out.println("Trying to load library " + Core.NATIVE_LIBRARY_NAME);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String... args) throws IOException {
        File root = new File("/home/sesshoumaru/bills/");
     /*    for (File name : root.listFiles()) {
            if (name.isFile() && !name.getName().startsWith("temp_")) {
                long startTime = System.currentTimeMillis();
                File target = new File(root, "temp_" + name.getName());
                try {
                    new ImageProcessingService().detectAndCorrectSkew(name, target);
                    new ImageProcessingService().prepareForSend(target, target);
                    System.out.println(new ImageProcessingService().isBill(target));
                } catch (Exception e) {

                }
                System.out.println("Time for file : " + name.getName() + " is " + (System.currentTimeMillis() - startTime));
            }
        }*/

        File name = new File(root, "собака-песочница-2654844.jpeg");
        File target = new File(root, "temp_" + name.getName());

        Circle[] circles = new Circle[4];
        circles[0] = new Circle(110, 195, 5);
        circles[1] = new Circle(390, 200, 5);
        circles[2] = new Circle(500, 490, 5);
        circles[3] = new Circle(40, 500, 5);

        Size size = new Size(300, 300);

        new ImageProcessingService().cutOutRectangle(name, target, circles, size);
    }
}
