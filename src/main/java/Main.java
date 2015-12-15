import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.*;
import java.util.*;

/**
 * Created by sesshoumaru on 8/22/15.
 */
public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String... args) throws IOException {
        File source = new File("/home/sesshoumaru/bilet_skew.jpg");
        File target = new File("/home/sesshoumaru/target.png");


        Mat temp = new Mat();
        OpenCvUtils.erode(Highgui.imread(source.getAbsolutePath()), temp);
        Highgui.imwrite(target.getAbsolutePath(), temp);
        OpenCvUtils.adaptiveThreshold(target, target);

        temp = Highgui.imread(target.getAbsolutePath());
        Map<Integer, Integer> angles = OpenCvUtils.detectMostPossibleRotationAngle(temp);
        List<Map.Entry<Integer, Integer>> sortedAngles = new ArrayList<Map.Entry<Integer, Integer>>(angles.entrySet());
        Iterator<Map.Entry<Integer, Integer>> iterator = sortedAngles.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            if (entry.getKey() > 45 || entry.getKey() < -45)
                iterator.remove();
        }
        sortedAngles.sort(new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return Integer.compare(o1.getValue(), o2.getValue());
            }
        });
        OpenCvUtils.rotate(temp, temp, sortedAngles.get(sortedAngles.size() - 1).getKey());

        Highgui.imwrite(target.getAbsolutePath(), temp);
    }

}
