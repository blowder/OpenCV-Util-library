import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Set;

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
        Mat lines = new Mat();
        int threshold = 150;
        int minLineSize = 100;
        int lineGap = 50;

        OpenCvUtils.adaptiveThreshold(source, target);

        Mat temp = Highgui.imread(target.getAbsolutePath());
        Core.bitwise_not(temp, temp);
        Imgproc.cvtColor(temp, temp, Imgproc.COLOR_BGR2GRAY);
        Imgproc.HoughLinesP(temp, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);

        double angle = 0;
        for (int x = 0; x < lines.cols(); x++) {
            double[] vec = lines.get(0, x);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            Core.line(temp, start, end, new Scalar(255, 0, 0), 3);

            angle += Math.atan2(vec[3] - vec[1], vec[2] - vec[0]);
        }
        angle /= lines.cols();


        double degreceAngle = angle * 180 / Math.PI;
        int len = Math.max(temp.cols(), temp.rows());
        Mat rotationMat = Imgproc.getRotationMatrix2D(new Point(len / 2, len / 2), -degreceAngle, 1);
        Imgproc.warpAffine(temp, temp, rotationMat, new Size(len, len));

        // cv::Point2f pt(len/2., len/2.);
        //  cv::Mat r = cv::getRotationMatrix2D(pt, angle, 1.0);

        //cv::warpAffine(src, dst, r, cv::Size(len, len));


        Highgui.imwrite(target.getAbsolutePath(), temp);

        //show image
     /*   BufferedImage bufImage = null;
        try {
            InputStream in = new FileInputStream(target);
            bufImage = ImageIO.read(in);
            JFrame frame = new JFrame();

            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)).);
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //writing crop result
        //Highgui.imwrite(target.getAbsolutePath(), ROI);

        //draw rectangle on result
        //Core.rectangle(temp,rect.br(),rect.tl(),new Scalar(0,0,255));
    }
}
