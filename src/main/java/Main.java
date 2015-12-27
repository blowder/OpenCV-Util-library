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
                    prepareForSend(target, target);
                    System.out.println(new ImageProcessingService().isBill(target));
                } catch (Exception e) {

                }
                System.out.println("Time for file : " + name.getName() + " is " + (System.currentTimeMillis() - startTime));
            }
        }
     /*   File name = new File(root, "IMG_20151227_152023.jpg");
        File target = new File(root, "temp_" + name.getName());
        prepareForSend(name, target);*/
        //new ImageProcessingService().prepareForSend(target, target);

    }

    private static void prepareForSend(File name, File target) {
       // new ImageProcessingService().detectAndCorrectSkew(name, target);

        Mat temp = Highgui.imread(target.getAbsolutePath(), CvType.CV_8UC1);
        Imgproc.pyrDown(temp, temp);
        Imgproc.pyrDown(temp, temp);
        Imgproc.medianBlur(temp, temp, 7);
        temp = OpenCvUtils.adaptiveThreshold(temp);
        temp = OpenCvUtils.denoise(temp, new Size(5, 5));
        temp = OpenCvUtils.erode(temp);
        temp = OpenCvUtils.erode(temp);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(temp.clone(), contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        contours.sort(new Comparator<MatOfPoint>() {
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                return Double.compare(Imgproc.contourArea(o1), Imgproc.contourArea(o2));
            }
        });
        List<MatOfPoint> resultContours = new ArrayList<MatOfPoint>();
        double f1Factor = 0;
        double savedRecall = 0;
        List<Point> listOfPoints = new ArrayList<Point>();
        Rect bb = null;
        double fullArea = temp.cols() * temp.rows();
        for (MatOfPoint contour : contours) {
            double recall = (savedRecall + Imgproc.contourArea(contour)) / fullArea;
            List<Point> tempPointList = new ArrayList<Point>(contour.toList());
            tempPointList.addAll(listOfPoints);
            MatOfPoint matOfPoint = new MatOfPoint();
            matOfPoint.fromArray(tempPointList.toArray(new Point[tempPointList.size()]));
            Rect rect = Imgproc.boundingRect(matOfPoint);
            ////Rect rect = Imgproc.boundingRect(contour);

            double precision = (fullArea - rect.area()) / fullArea;
            double newF1Factor = 2 * (recall * precision) / (recall + precision);
            if (newF1Factor >= f1Factor) {
                f1Factor = newF1Factor;
                savedRecall = recall;
                listOfPoints.addAll(new ArrayList<Point>(contour.toList()));
                resultContours.add(contour);
                bb = rect;
                //Core.rectangle(temp, rect.tl(), rect.br(), new Scalar(0, 0, 0), 4);
            }
        }
        //Core.rectangle(temp, bb.tl(), bb.br(), new Scalar(0, 0, 0), 4);
        //System.out.println(Imgproc.contourArea(contours.get(0)));
        //Imgproc.drawContours(temp, contours, -1, new Scalar(255, 255, 255), 3);
        bb = new Rect(bb.x * 4, bb.y * 4, bb.width * 4, bb.height * 4);
        temp = Highgui.imread(target.getAbsolutePath(), CvType.CV_8UC1);

        Highgui.imwrite(target.getAbsolutePath(), temp.submat(bb));
    }

}
