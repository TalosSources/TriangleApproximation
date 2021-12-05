package Triangles;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static Triangles.HelperMethods.*;
import static Triangles.Main.startColor;

class Main {
    public static int WIDTH = 1000;
    public static int HEIGHT = 1000;
    public static int startColor = 0;
    public static int voidColor = 0;

    public static void main(String[] args) throws IOException, InterruptedException {

        /**
         * Setup
         */
        BufferedImage source = ImageIO.read(new File("source6.png"));
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
                new Point(0, 0),
                new Point(WIDTH - 1, 0),
                new Point(WIDTH / 2, HEIGHT - 1),
                startColor
        );
        Triangle startTriangleDown = new Triangle(
                new Point(0, HEIGHT - 1),
                new Point( WIDTH - 1, HEIGHT - 1),
                new Point( WIDTH / 2, 0),
                startColor
        );
        Triangle startTriangle1 = new Triangle(
                new Point(0,0),
                new Point(WIDTH - 1, 0),
                new Point(0, HEIGHT - 1),
                startColor
        );
        Triangle startTriangle2 = new Triangle(
                new Point(WIDTH - 1, HEIGHT - 1),
                new Point(WIDTH - 1, 1),
                new Point(1, HEIGHT - 1),
                startColor
        );
        Triangle startTriangle0 = startTriangleUp;
        List<Triangle> triangles = new ArrayList<>();
        triangles.add(startTriangle0);
        startTriangle0.color = source.getRGB((int)startTriangle0.center.x, (int)startTriangle0.center.y);
        for(Triangle t : triangles) draw(t, image);
        label.setIcon(new ImageIcon(image));
        Thread.sleep(500);
        //triangles.add(startTriangle1);
        //triangles.add(startTriangle2);

        //Thread.sleep(10000);

        for(int i = 0; i < 7; ++i) {
            //System.out.println(triangles);
            List<Triangle> newTriangles = new ArrayList<>();
            for(Triangle t : triangles){
                List<Triangle> sub = t.subdivisedIn4();
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
                //for(Triangle toDraw : sub)
                    //if(true || !toRemove.contains(toDraw))draw(toDraw, image);
                //label.setIcon(new ImageIcon(image));
                //Thread.sleep(1);
            }
            triangles = newTriangles;

            clearImage(image);
            for(Triangle t : triangles) draw(t, image);
            label.setIcon(new ImageIcon(image));
            Thread.sleep(500);
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

class Triangle {
    final Point a, b, c;
    int color;
    final Point center;

    public Triangle(Point a, Point b, Point c, int color){
        this.a = a;
        this.b = b;
        this.c = c;
        this.color = color;

        double t1 = 0.33;
        double t2 = 0.33;

        Point ab = new Point(b.x - a.x, b.y - a.y);
        Point ac = new Point(c.x - a.x, c.y - a.y);

        center = a.add(
                t1 + t2 <= 1 ?
                        ab.mult(t1).add(ac.mult(t2)) :
                        ab.mult(1 - t1).add(ac.mult(1 - t2)));
    }

    public Point center() {return center;}

    public List<Triangle> subdivisedIn2(Random RNG){
        //chose a random side in the triangle (i.e, choose the opposite point)
        int pIndex = RNG.nextInt(3);
        List<Point> points = new ArrayList<>();
        Collections.addAll(points, a, b, c);
        Point oppositePoint = points.get(pIndex);
        points.remove(oppositePoint); //the last 2 points define the side to cut
        points.sort(Comparator.comparingDouble(p -> p.x));
        int x1 = (int)points.get(0).x;
        int x2 = (int)points.get(1).x;
        int y1 = (int)points.get(0).y;
        int y2 = (int)points.get(1).y;

        double t = RNG.nextFloat() / 4 + 0.475;
        Point pCut = new Point((int) (t * (x2 - x1)) + x1, (int) (t * (y2 - y1)) + y1);

        Triangle t1 = new Triangle(points.get(0), pCut, oppositePoint, randomCloseColor(startColor, DISTANCE));
        Triangle t2 = new Triangle(points.get(1), pCut, oppositePoint, randomCloseColor(startColor, DISTANCE));

        return List.of(t1, t2);
    }

    public List<Triangle> subdivisedIn3() {
        double t1 = RNG.nextFloat();
        double t2 = RNG.nextFloat();

        Point ab = new Point(b.x - a.x, b.y - a.y);
        Point ac = new Point(c.x - a.x, c.y - a.y);

        Point center = a.add(
                t1 + t2 <= 1 ?
                        ab.mult(t1).add(ac.mult(t2)) :
                        ab.mult(1 - t1).add(ac.mult(1 - t2)));
        return List.of(
                new Triangle(a, b, center, randomCloseColor(color, DISTANCE)),
                new Triangle(a, c, center, randomCloseColor(color, DISTANCE)),
                new Triangle(b, c, center, randomCloseColor(color, DISTANCE))
        );
    }

    public List<Triangle> subdivisedIn4() {
        Point ab = b.add(a.mult(-1));
        Point ac = c.add(a.mult(-1));
        Point bc = c.add(b.mult(-1));

        Point ba = a.add(b.mult(-1));
        Point ca = a.add(c.mult(-1));
        Point cb = b.add(c.mult(-1));

        double t1 = 0.5;RNG.nextFloat();
        double t2 = 0.5;RNG.nextFloat();
        double t3 = 0.5;RNG.nextFloat();

        Point cab = ab.mult(t1).add(a);
        Point cac = ac.mult(t2).add(a);
        Point cbc = bc.mult(t3).add(b);

        Point cba = ba.mult(1 - t1).add(b);
        Point cca = ca.mult(1 - t2).add(c);
        Point ccb = cb.mult(1 - t3).add(c);

        //System.out.println("cab : " + cab + ", cba : " + cba);


        return List.of(
                new Triangle(a, cab, cac, randomCloseColor(color, DISTANCE)),
                new Triangle(b, cba, cbc, randomCloseColor(color, DISTANCE)),
                new Triangle(c, cca, ccb, randomCloseColor(color, DISTANCE)),
                new Triangle(cab, cac, cbc, randomCloseColor(color, DISTANCE))
        );
    }

    @Override
    public String toString() {
        return "Triangle{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                ", color=" + color +
                '}';
    }
}

class Point {
    final double x;
    final double y;
    //final double norm;

    public Point(double x, double y){
        this.x = x;
        this.y = y;
        //norm = Math.sqrt(x*x + y*y);
    }

    public Point add(Point other){
        return new Point(other.x + x, other.y + y);
    }

    public Point mult(double a) {
        return new Point(a * x, a * y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.x, x) == 0 &&
                Double.compare(point.y, y) == 0;
    }
}

class HelperMethods {
    public static Random RNG = new Random();
    public static int DISTANCE = 35;

    public static void safeSetRGB(BufferedImage image, int x, int y, int RGB){
        if(x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) return;
        image.setRGB(x, y, RGB);
    }

    public static void draw(Triangle t, BufferedImage image) {
        List<Point> points = new ArrayList<>();
        points.add(t.a);points.add(t.b);points.add(t.c);
        points.sort(Comparator.comparingDouble(p -> p.x)); // the point are sorted from mostLeft to mostRight
        int x1 = (int) Math.round(points.get(0).x);
        int x2 = (int) Math.round(points.get(1).x);
        int x3 = (int) Math.round(points.get(2).x);
        int y1 = (int) Math.round(points.get(0).y);
        int y2 = (int) Math.round(points.get(1).y);
        int y3 = (int) Math.round(points.get(2).y);

        //if(x1 == x2 || x1 == x3 || x2 == x3
          //  || y1 == y2 || y1 == y3 || y2 == y3) return;

        //i want to determine if point 2 is above or below the line (1, 3)
        //boolean reversed = y2 > (y3 - y1)/(x3 - x1) * (x2 - x1) + y1;
        //if(y2 > (y3 - y1)/(x3 - x1) * (x2 - x1) + y1) return;

        try{
            for(int x = x1; x <= x2; ++x) {
                int yb1 = ( (x - x1) * (y2 - y1) / (x2 - x1) ) + y1;
                int yb2 = ( (x - x1) * (y3 - y1) / (x3 - x1) ) + y1;
                int yd = Math.min(yb1, yb2);
                int yu = Math.max(yb1, yb2);
                for(int y = yd; y <= yu; ++y){
                    safeSetRGB(image, x, y, t.color);
                }
            }
        } catch(ArithmeticException ae){
            //nothing
        }

        try{
            for(int x = x2; x <= x3; ++x) {
                int yb1 = ( (x - x2) * (y3 - y2) / (x3 - x2) )+ y2; //boundaries
                int yb2 = ( (x - x1)*(y3 - y1) / (x3 - x1) ) + y1;
                int yd = Math.min(yb1, yb2);
                int yu = Math.max(yb1, yb2);
                for(int y = yd; y <= yu; ++y){
                    safeSetRGB(image, x, y, t.color);
                }
            }
        } catch(ArithmeticException ae){
            //nothing
        }

    }

    public static Triangle randomTriangle(int x1, int x2, int y1, int y2) {
        return new Triangle(
                randomPoint(x1, x2, y1, y2),
                randomPoint(x1, x2, y1, y2),
                randomPoint(x1, x2, y1, y2),
                randomColor());
    }

    public static Point randomPoint(int x1, int x2, int y1, int y2) { //random point in that rectangle
        return new Point(RNG.nextInt(x2 - x1) + x1,
                RNG.nextInt(y2 - y1) + y1);
    }

    public static int randomColor() {
        int r = RNG.nextInt(0xFF);
        int g = RNG.nextInt(0xFF);
        int b = RNG.nextInt(0xFF);

        return (r << 16) | (g << 8) | b;
    }

    public static int randomCloseColor(int color, int distance){
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;

        int nr = 0;
        int ng = 0;
        int nb = 0;

        //if we see the color space as a cube of 255 side, in the worst case 1/8 of the colors with be valid
        do {
            nr = r + RNG.nextInt(2 * distance) - distance;
            //System.out.println("r : " + r + ", nr : " + nr);
            ng = g + RNG.nextInt(2 * distance) - distance;
            nb = b + RNG.nextInt(2 * distance) - distance;
        } while (!validColor(nr, ng, nb));

        return nr << 16 | ng << 8 | nb;
    }

    public static boolean validColor(int r, int g, int b) {
        return r >= 0 && r < 0xff &&
                g >= 0 && g < 0xff &&
                b >= 0 && b < 0xff;
    }

    public static int computeColor(Triangle t, BufferedImage source) {
        int c1 = source.getRGB((int)t.a.x, (int)t.a.y);
        int c2 = source.getRGB((int)t.b.x, (int)t.b.y);
        int c3 = source.getRGB((int)t.c.x, (int)t.c.y);
        int c4 = source.getRGB((int)t.center().x, (int)t.center().y);

        int r1 = c1 >> 16 & 0xff;
        int g1 = c1 >> 8 & 0xff;
        int b1 = c1 & 0xff;

        int r2 = c2 >> 16 & 0xff;
        int g2 = c2 >> 8 & 0xff;
        int b2 = c2 & 0xff;

        int r3 = c3 >> 16 & 0xff;
        int g3 = c3 >> 8 & 0xff;
        int b3 = c3 & 0xff;

        int r4 = c4 >> 16 & 0xff;
        int g4 = c4 >> 8 & 0xff;
        int b4 = c4 & 0xff;

        int r = (r1 + r2 + r3 + r4) / 4;
        int g = (g1 + g2 + g3 + g4) / 4;
        int b = (b1 + b2 + b3 + b4) / 4;

        return (r << 16) | (g << 8) | b;
    }

    public static void clearImage(BufferedImage image) {
        for(int x = 0; x < image.getWidth(); ++x){
            for(int y = 0; y < image.getHeight(); ++y){
                image.setRGB(x, y, 0);
            }
        }
    }

    public static void pngfy(BufferedImage image) {
        for(int x = 0; x < image.getWidth(); ++x){
            for(int y = 0; y < image.getHeight(); ++y){
                if(image.getRGB(x, y) == Main.voidColor)
                image.setRGB(x, y, 0);
            }
        }
    }

    public static void shiftColorsRight(BufferedImage image) {
        for(int x = 0; x < image.getWidth(); ++x){
            for(int y = 0; y < image.getHeight(); ++y){
                int color = image.getRGB(x,y);
                int a = (color >> 24) & 0xff;

                int r = color >> 16 & 0xff;
                int g = color >> 8 & 0xff;
                int b = color & 0xff;

                //g /= 2; //synthwave trash

                color = (a << 24) | (r << 16) | (g << 8) | b;

                image.setRGB(x, y, color);
            }
        }
    }
}
