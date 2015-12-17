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
        File source = new File("/home/sesshoumaru/bilet.jpg");
        File target = new File("/home/sesshoumaru/target.png");

        long startTime = System.currentTimeMillis();


        /*Mat temp = OpenCvUtils.erode(Highgui.imread(source.getAbsolutePath()));
        Highgui.imwrite(target.getAbsolutePath(), temp);

        temp = OpenCvUtils.adaptiveThreshold(temp);
        Highgui.imwrite(target.getAbsolutePath(), temp);

        Mat lines = OpenCvUtils.getHoughLines(temp);

        Map<Integer, Integer> angles = OpenCvUtils.calculateAnglesQuantity(lines);

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

        OpenCvUtils.rotate(Highgui.imread(source.getAbsolutePath()), temp, sortedAngles.get(sortedAngles.size() - 1).getKey());
        Highgui.imwrite(target.getAbsolutePath(), temp);*/
        new ImageProcessingService().detectAndCorrectSkew(source,target);
        new ImageProcessingService().prepareForSend(target,target);
       /* Mat temp = Highgui.imread(target.getAbsolutePath());

        temp = OpenCvUtils.adaptiveThreshold(temp);
        Highgui.imwrite(target.getAbsolutePath(), temp);

        Imgproc.blur(temp, temp, new Size(2, 2));
        List<Rect> rectangles = OpenCvUtils.detectLetters(temp);

        List<Rect> filteredRectangles = new ArrayList<Rect>();
        int minRectangleArea = 0 * 15;
        for (Rect rect : rectangles) {
            if (rect.area() > minRectangleArea)
                filteredRectangles.add(rect);
        }
        List<Point> points = new ArrayList<Point>();
        for (Rect rect : filteredRectangles) {
            Core.rectangle(temp, rect.tl(), rect.br(), new Scalar(0, 0, 0));
            points.add(rect.br());
            points.add(rect.tl());
        }
        Highgui.imwrite(target.getAbsolutePath(), temp);
        MatOfPoint matOfPoint = new MatOfPoint();
        Point[] points2 = points.toArray(new Point[points.size()]);
        matOfPoint.fromArray(points2);
        Rect bb = Imgproc.boundingRect(matOfPoint);
        int outline = 0;
        Rect resultBB = new Rect(new Point(bb.tl().x - outline, bb.tl().y - outline), new Point(bb.br().x + outline, bb.br().y + outline));
        //Core.rectangle(temp, resultBB.tl(), resultBB.br(), new Scalar(0, 0, 0));

        Highgui.imwrite(target.getAbsolutePath(), temp.submat(resultBB));*/
        System.out.println("Time is: " + (System.currentTimeMillis() - startTime));

    }

}
