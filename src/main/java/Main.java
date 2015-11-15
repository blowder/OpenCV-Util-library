import org.opencv.core.*;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Created by sesshoumaru on 8/22/15.
 */
public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String... args) throws IOException {
        File source = new File("/home/sesshoumaru/bilet.jpg");
        File target = new File("/home/sesshoumaru/target.png");

        Mat temp = Highgui.imread(source.getAbsolutePath());
        //looking for white rectangle
        Set<Rect> rects = OpenCvUtils.findSquares(temp);
        //crop main image to this rectangle
        Mat ROI = OpenCvUtils.cropByRect(temp, rects.iterator().next());
        //look for text on crop rectangle
        System.out.println("Image contain text: " + OpenCvUtils.ifContainText(ROI));

        //writing crop result
        Highgui.imwrite(target.getAbsolutePath(), ROI);

        //draw rectangle on result
        //Core.rectangle(temp,rect.br(),rect.tl(),new Scalar(0,0,255));
    }
}
