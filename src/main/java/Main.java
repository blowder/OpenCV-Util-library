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

        for (File name : root.listFiles()) {
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
        }
/*        File name = new File(root, "bilet_skew.jpg");
        File target = new File(root, "temp_" + name.getName());
        new ImageProcessingService().detectAndCorrectSkew(name, target);
        new ImageProcessingService().prepareForSend(target, target);*/

    }

}
