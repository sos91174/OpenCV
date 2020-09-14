import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import static org.opencv.imgcodecs.Imgcodecs.imread;

import java.util.Arrays;

/**
 *
 * @Title: Opencv 圖片人臉識別 相機鏡頭人臉識別 影片人臉識別
 * @Description: OpenCV-4.1.1 测试文件
 * @date: 2019年8月19日 17:17:48
 * @version: V-1.0.0
 *
 */
public class FaceVideo {

    // 初始化人臉探測
    static CascadeClassifier faceDetector;

    static int i=0;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        faceDetector = new CascadeClassifier("D:\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
    }

    public static void main(String[] args) {
        // 1- 從攝影機識別人臉 識別成功保存圖片到電腦內
        getVideoFromCamera();


        // 2- 從電腦的影片中識別人臉
        //getVideoFromFile();

        // 3- 選取圖片執行人臉辨識，識別成功保存人臉照片到電腦
        //face();

        // 4- 比對2張照片的相似度（越接近1越相似）
//        String basePicPath = "D:\\Pictures\\";
//        double compareHist = compare_image(basePicPath + "77.jpg", basePicPath + "face.png");
//        System.out.println(compareHist);
//        if (compareHist > 0.72) {
//            System.out.println("Ture");
//        } else {
//            System.out.println("False");
//        }
    }




    /**
     * OpenCV-4.1.1 從鏡頭讀取
     * @return: void
     * @date: 2019年8月19日 17:20:13
     */
    public static void getVideoFromCamera() {
        //1 如果要從鏡頭讀取影片 則要在 VideoCapture 的構造方法內寫0
        VideoCapture capture=new VideoCapture(0);
        Mat video=new Mat();
        int index=0;
        if (capture.isOpened()) {
            while(i<15) {// 匹配成功3次退出
                capture.read(video);
                HighGui.imshow("進行人臉識別中", getFace(video));
                index=HighGui.waitKey(500);
                if (index==27) {
                    capture.release();
                    break;
                }
            }
        }else{
            System.out.println("相機未開啟");
        }
        try {
            capture.release();
            Thread.sleep(1000);
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * OpenCV-4.1.1 從影片中讀取
     * @return: void
     * @date: 2019年8月19日 17:20:20
     */
    public static void getVideoFromFile() {
        VideoCapture capture=new VideoCapture();
        capture.open("C:\\Users\\Administrator\\Desktop\\1.avi");//1 讀取影片的路徑

        if(!capture.isOpened()){
            System.out.println("讀取影片失敗！");
            return;
        }
        Mat video=new Mat();
        int index=0;
        while(capture.isOpened()) {
            capture.read(video);//2 影片寫入 Mat video 中
            HighGui.imshow("影片識別人臉中", getFace(video));//3 顯示圖像
            index=HighGui.waitKey(100);//4 獲取鍵盤輸入
            if(index==27) {//5 如果是 Esc 則退出
                capture.release();
                return;
            }
        }
    }

    /**
     * OpenCV-4.1.1 人臉識別
     * @date: 2019年8月19日 17:19:36
     * @param image 待處理Mat照片(影片中的某一個)
     * @return 處理後的照片

     */
    public static Mat getFace(Mat image) {
        // 1 讀取OpenCV自帶的人臉識別特徵XML文件(faceDetector)
//        CascadeClassifier facebook=new CascadeClassifier("D:\\Sofeware\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
        // 2  特徵匹配
        MatOfRect face = new MatOfRect();
        // 3 特徵匹配
        faceDetector.detectMultiScale(image, face);
        Rect[] rects=face.toArray();
        System.out.println("匹配到 "+rects.length+" 個人臉");
        if(rects != null && rects.length >= 1) {

            // 4 為每張識別到的人臉畫一個圈
            for (int i = 0; i < rects.length; i++) {
                Imgproc.rectangle(image, new Point(rects[i].x, rects[i].y), new Point(rects[i].x + rects[i].width, rects[i].y + rects[i].height), new Scalar(0, 255, 0));
                Imgproc.putText(image, "Human", new Point(rects[i].x, rects[i].y), Imgproc.FONT_HERSHEY_SCRIPT_SIMPLEX, 1.0, new Scalar(0, 255, 0), 1, Imgproc.LINE_AA, false);
                //Mat dst=image.clone();
                //Imgproc.resize(image, image, new Size(300,300));
            }
            i++;
            if(i==3) {// 獲取匹配成功第10次的照片
                Imgcodecs.imwrite("D:\\Pictures\\" + "face.png", image);
            }
        }
        return image;
    }



    /**
     * OpenCV-4.1.1 照片人臉識別
     * @return: void
     * @date: 2019年5月7日12:16:55
     */
    public static void face() {
        // 1 讀取OpenCV自帶的人臉識别特徵XML文件
        //OpenCV 圖像識別庫一般位於 opencv\sources\data 下面
//        CascadeClassifier facebook=new CascadeClassifier("D:\\Sofeware\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
        // 2 讀取測試照片
        String imgPath = "D:\\Pictures\\99.jpg";
        Mat image=Imgcodecs.imread(imgPath);
        if(image.empty()){
            System.out.println("image 内容不存在！");
            return;
        }
        // 3 特徵匹配
        MatOfRect face = new MatOfRect();
        faceDetector.detectMultiScale(image, face);
        // 4 匹配 Rect 矩陣 數組
        Rect[] rects=face.toArray();
        System.out.println("匹配到 "+rects.length+" 個人臉");
        // 5 為每張識別到的人臉畫一個圈
        int i =1 ;
        for (Rect rect : face.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0), 3);
            imageCut(imgPath, "D:\\Pictures\\"+i+".jpg", rect.x, rect.y, rect.width, rect.height);// 進行圖片剪切
            i++;
        }
        // 6 展示圖片
        HighGui.imshow("人臉識別", image);
        HighGui.waitKey(0);
    }

    /**
     * 裁剪人臉
     * @param imagePath
     * @param outFile
     * @param posX
     * @param posY
     * @param width
     * @param height
     */
    public static void imageCut(String imagePath, String outFile, int posX, int posY, int width, int height) {
        // 原始圖像
        Mat image = Imgcodecs.imread(imagePath);
        // 截取的區域：參數,座標X,座標Y,截圖寬度,截圖長度
        Rect rect = new Rect(posX, posY, width, height);
        // 兩句效果一樣
        Mat sub = image.submat(rect); // Mat sub = new Mat(image,rect);
        Mat mat = new Mat();
        Size size = new Size(width, height);
        Imgproc.resize(sub, mat, size);// 將人臉進行截圖並保存
        Imgcodecs.imwrite(outFile, mat);
        System.out.println(String.format("圖片裁切成功，裁切後圖片文件為： %s", outFile));

    }

    /**
     * 人臉比對
     * @param img_1
     * @param img_2
     * @return
     */
    public static double compare_image(String img_1, String img_2) {
        Mat mat_1 = conv_Mat(img_1);
        Mat mat_2 = conv_Mat(img_2);
        Mat hist_1 = new Mat();
        Mat hist_2 = new Mat();

        //顏色範圍
        MatOfFloat ranges = new MatOfFloat(0f, 256f);
        //直方圖大小， 越大匹配越精確 (越慢)
        MatOfInt histSize = new MatOfInt(1000);

        Imgproc.calcHist(Arrays.asList(mat_1), new MatOfInt(0), new Mat(), hist_1, histSize, ranges);
        Imgproc.calcHist(Arrays.asList(mat_2), new MatOfInt(0), new Mat(), hist_2, histSize, ranges);

        // CORREL 相關係數
        double res = Imgproc.compareHist(hist_1, hist_2, Imgproc.CV_COMP_CORREL);
        return res;
    }

    /**
     * 灰度化人臉
     * @param img
     * @return
     */
    public static Mat conv_Mat(String img) {
        Mat image0 = Imgcodecs.imread(img);

        Mat image1 = new Mat();
        // 灰度化
        Imgproc.cvtColor(image0, image1, Imgproc.COLOR_BGR2GRAY);
        // 探測人臉
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image1, faceDetections);
        // rect中人臉圖片的範圍
        for (Rect rect : faceDetections.toArray()) {
            Mat face = new Mat(image1, rect);
            return face;
        }
        return null;
    }

    /**
     * OpenCV-4.1.1 將相機拍的影片寫入電腦
     * @return: void
     * @date: 2019年8月19日 17:20:48
     */
    public static void writeVideo() {
        //1 如果要從相機獲取影片 則要在 VideoCapture 的構造方法寫 0
        VideoCapture capture=new VideoCapture(0);
        Mat video=new Mat();
        int index=0;
        Size size=new Size(capture.get(Videoio.CAP_PROP_FRAME_WIDTH),capture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
        VideoWriter writer=new VideoWriter("D:/a.mp4",VideoWriter.fourcc('D', 'I', 'V', 'X'), 15.0,size, true);
        while(capture.isOpened()) {
            capture.read(video);//2 相機拍的影片寫入 Mat video 中
            writer.write(video);
            HighGui.imshow("從相機獲取影片", video);//3 顯示圖像
            index=HighGui.waitKey(100);//4 獲取鍵盤輸入
            if(index==27) {//5 如果是 Esc 則退出
                capture.release();
                writer.release();
                return;
            }
        }
    }

}