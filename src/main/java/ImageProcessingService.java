import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.*;

/**
 * Created by sesshoumaru on 17.12.15.
 */
public class ImageProcessingService {

    public File detectAndCorrectSkew(File source, File target) {
        int lowerBorder = -45;
        int upperBorder = 45;

        Mat temp = OpenCvUtils.erode(Highgui.imread(source.getAbsolutePath()));
        temp = OpenCvUtils.adaptiveThreshold(temp);
        Mat detectedLines = OpenCvUtils.getHoughLines(temp);
        Map<Integer, Integer> angles = OpenCvUtils.calculateAnglesQuantity(detectedLines);

        Map<Integer, Integer> filteredAngles = filterAngles(lowerBorder, upperBorder, angles);
        int skewAngle = getMostPopularAngle(filteredAngles);
        if (skewAngle != 0)
            OpenCvUtils.rotate(Highgui.imread(source.getAbsolutePath()), temp, skewAngle);
        Highgui.imwrite(target.getAbsolutePath(), temp);

        return target;
    }

    private Map<Integer, Integer> filterAngles(int lowerBorder, int upperBorder, Map<Integer, Integer> angles) {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> entry : angles.entrySet())
            if (entry.getKey() > lowerBorder && entry.getKey() < upperBorder)
                result.put(entry.getKey(), entry.getValue());
        return result;
    }

    private int getMostPopularAngle(Map<Integer, Integer> angles) {
        List<Map.Entry<Integer, Integer>> result = new ArrayList<Map.Entry<Integer, Integer>>(angles.entrySet());
        result.sort(new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return Integer.compare(o2.getValue(), o1.getValue());
            }
        });
        return result.size() == 0 ? 0 : result.get(0).getKey();
    }

    int getMinRectangleArea(int cols, int rows) {
        int maxSide = Math.max(cols, rows);
        return maxSide / 30 * maxSide / 30;
    }


    public File prepareForSend(File source, File target) {
        Size blurSize = new Size(2, 2);
        int outline = 20;
        Mat temp = Highgui.imread(source.getAbsolutePath());
        int minRectangleArea = getMinRectangleArea(temp.cols(), temp.rows());

        temp = OpenCvUtils.adaptiveThreshold(temp);
        Imgproc.blur(temp, temp, blurSize);

        Highgui.imwrite(target.getAbsolutePath(), temp);

        List<Rect> rectangles = OpenCvUtils.detectLetters2(temp);
        if (rectangles.size() == 0)
            throw new RuntimeException("prepareForSend(): Text not found on " + source);

        Iterator<Rect> iterator = rectangles.iterator();
        while (iterator.hasNext())
            if (iterator.next().area() < minRectangleArea)
                iterator.remove();

        List<Point> points = new ArrayList<Point>();
        for (Rect rect : rectangles) {
            Core.rectangle(temp, rect.tl(), rect.br(), new Scalar(0, 0, 0));
            points.add(rect.br());
            points.add(rect.tl());
        }

        Point[] points2 = points.toArray(new Point[points.size()]);

        MatOfPoint matOfPoint = new MatOfPoint();
        matOfPoint.fromArray(points2);
        Rect bb = Imgproc.boundingRect(matOfPoint);

        bb = addOutline(bb, outline, temp.size());

        //Core.rectangle(temp, resultBB.tl(), resultBB.br(), new Scalar(0, 0, 0));

        Highgui.imwrite(target.getAbsolutePath(), temp.submat(bb));
        return target;
    }

    private Rect addOutline(Rect bb, int outline, Size maxBBSize) {
        Point topLeft = bb.tl();
        Point bottomRight = bb.br();
        double newTLX = (topLeft.x - outline) < 0 ? 0 : topLeft.x - outline;
        double newTLY = (topLeft.y - outline) < 0 ? 0 : topLeft.y - outline;
        double newBRX = (bottomRight.x + outline) > maxBBSize.width ? maxBBSize.width : bottomRight.x + outline;
        double newBRY = (bottomRight.y + outline) > maxBBSize.height ? maxBBSize.height : bottomRight.y + outline;
        return new Rect(new Point(newTLX, newTLY), new Point(newBRX, newBRY));
    }

    public boolean isBill(File source) {
        int threshold = 0;
        Mat temp = Highgui.imread(source.getAbsolutePath(), Imgproc.COLOR_BGR2GRAY);
        int minRectangleArea = getMinRectangleArea(temp.cols(), temp.rows());

        List<Rect> rectangles = OpenCvUtils.detectLetters2(temp);
        Iterator<Rect> iterator = rectangles.iterator();
        while (iterator.hasNext())
            if (iterator.next().area() < minRectangleArea)
                iterator.remove();

        List<Integer> xValues = new ArrayList<Integer>();
        List<Integer> yValues = new ArrayList<Integer>();
        for (Rect rect : rectangles) {
            xValues.add(rect.x);
            // xValues.add(rect.x + rect.width);
            yValues.add(rect.y);
            //yValues.add(rect.y + rect.height);
        }
        int mostPopularX = getMostPopular(xValues, threshold);
        int mostPopularY = getMostPopular(yValues, threshold);
        List<Rect> rectanglesOnHorizontalLine = new ArrayList<Rect>();
        List<Rect> rectanglesOnVerticalLine = new ArrayList<Rect>();
        for (Rect rect : rectangles) {
            if (mostPopularY - threshold <= rect.y && mostPopularY + threshold >= rect.y)
                rectanglesOnHorizontalLine.add(rect);
            if (mostPopularX - threshold <= rect.x && mostPopularX + threshold >= rect.x)
                rectanglesOnVerticalLine.add(rect);
        }

        return rectanglesOnHorizontalLine.size() > 1 && rectanglesOnVerticalLine.size() > 1;
    }

    private int getMostPopular(List<Integer> arguments, int threshold) {
        Map<Integer, Integer> argumentCounter = new HashMap<Integer, Integer>();
        for (Integer argument : arguments) {
            int counter = argumentCounter.get(argument) == null ? 1 : argumentCounter.get(argument) + 1;
            argumentCounter.put(argument, counter);
        }
        Map<Integer, Integer> newArgumentCounter = new HashMap<Integer, Integer>();
        newArgumentCounter.putAll(argumentCounter);
        for (Map.Entry<Integer, Integer> entry : argumentCounter.entrySet())
            for (int i = entry.getKey() - threshold; i <= entry.getKey() + threshold; i++)
                if (argumentCounter.get(i) != null && i != entry.getKey()) {
                    int counter = newArgumentCounter.get(entry.getKey()) == null ? entry.getValue() : newArgumentCounter.get(entry.getKey());
                    newArgumentCounter.put(entry.getKey(), counter + argumentCounter.get(i));
                }

        List<Map.Entry<Integer, Integer>> sorted = new ArrayList<Map.Entry<Integer, Integer>>(newArgumentCounter.entrySet());
        sorted.sort(new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return Integer.compare(o2.getValue(), o1.getValue());
            }
        });
        return sorted.get(0).getKey();
    }

}
