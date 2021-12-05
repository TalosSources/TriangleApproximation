package Triangles;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Triangles.HelperMethods.*;
import static Triangles.HelperMethods.draw;

public class Main {
    public static int WIDTH = 1000;
    public static int HEIGHT = 1000;
    public static int startColor = 0;
    public static int voidColor = 0;

    public static void main(String[] args) throws IOException, InterruptedException {

        /**
         * Setup
         */
        BufferedImage source = ImageIO.read(new File("res/source2.png"));
        WIDTH = source.getWidth();
        HEIGHT = source.getHeight();
        voidColor = source.getRGB(10, 10);
        System.out.println("voidColor : " + voidColor);
        shiftColorsRight(source);
        //pngfy(source);

        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        JLabel label = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        /**
         * individual triangle
         */
        /*Point p1 = new Point(300, 300);
        Point p2 = new Point(600, 300);
        Point p3 = new Point(601, 600);

        Triangle t1 = new Triangle(p1, p2, p3, 0xFF0000);
        t1.draw(image);
        label.setIcon(new ImageIcon(image));*/



        /**
         * multiple random triangles with time interval
         */
        /*for(int i = 0; i < 100; ++i){
            draw(randomTriangle(0, WIDTH, 0, HEIGHT), image);
            label.setIcon(new ImageIcon(image));
            Thread.sleep(300
           );
        }*/

        /**
         * subdivision in 2
         */
        startColor = randomColor();
        Triangle startTriangleUp = new Triangle(
                new Triangles.Point(0, 0),
                new Triangles.Point(WIDTH - 1, 0),
                new Triangles.Point(WIDTH / 2, HEIGHT - 1),
                startColor
        );
        Triangle startTriangleDown = new Triangle(
                new Triangles.Point(0, HEIGHT - 1),
                new Triangles.Point( WIDTH - 1, HEIGHT - 1),
                new Triangles.Point( WIDTH / 2, 0),
                startColor
        );
        Triangle startTriangle1 = new Triangle(
                new Triangles.Point(0,0),
                new Triangles.Point(WIDTH - 1, 0),
                new Triangles.Point(0, HEIGHT - 1),
                startColor
        );
        Triangle startTriangle2 = new Triangle(
                new Triangles.Point(WIDTH - 1, HEIGHT - 1),
                new Triangles.Point(WIDTH - 1, 1),
                new Triangles.Point(1, HEIGHT - 1),
                startColor
        );
        Triangle startTriangle0 = startTriangleUp;
        java.util.List<Triangle> triangles = new ArrayList<>();
        triangles.add(startTriangle0);
        startTriangle0.color = source.getRGB((int)startTriangle0.center.x, (int)startTriangle0.center.y);
        for(Triangle t : triangles) draw(t, image);
        label.setIcon(new ImageIcon(image));
        //Thread.sleep(500);
        //triangles.add(startTriangle1);
        //triangles.add(startTriangle2);

        //Thread.sleep(10000);

        for(int i = 0; i < 7; ++i) {
            //System.out.println(triangles);
            java.util.List<Triangle> newTriangles = new ArrayList<>();
            for(Triangle t : triangles){
                java.util.List<Triangle> sub = t.subdivisedIn4();
                newTriangles.addAll(sub);
                List<Triangle> toRemove = new ArrayList<>();
                for(Triangle tt : sub) {
                    int removeColor = computeColor(tt, source);
                    //System.out.println("tt : " + tt + ", removeColor : " + removeColor);
                    tt.color = source.getRGB((int)tt.center.x, (int)tt.center.y);
                    if(removeColor == voidColor && i > 3) {
                        System.out.println("removed " + tt);
                        newTriangles.remove(tt);
                        toRemove.add(tt);
                    }
                }
                for(Triangle toDraw : sub)
					if(!toRemove.contains(toDraw))draw(toDraw, image);
                label.setIcon(new ImageIcon(image));
                if(toRemove.isEmpty()) Thread.sleep(6);
            }
            triangles = newTriangles;

            //clearImage(image);
            //for(Triangle t : triangles) draw(t, image);
            //label.setIcon(new ImageIcon(image));
            //Thread.sleep(500);
        }
        Thread.sleep(2000);

        clearImage(image);
        int j = 0;
        for(Triangle toDraw : triangles){
            ++j;
            draw(toDraw, image);
            label.setIcon(new ImageIcon(image));
            if(j%1 == 0)Thread.sleep(1);
        }

        /**
         * Result
         */
        ImageIO.write(image, "png", new File("triangle.png"));

    }

}
