/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class FastCollinearPoints {
    // finds all line segments containing 4 points
    private LineSegment[] lineSegments;
    private int lineSegmentsCurrentIndex;

    public FastCollinearPoints(Point[] points) {
        // lets covert corner cases, and throw IllegalArgumentException if needed
        if (points == null) throw new IllegalArgumentException();
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException();
        }
        Point[] copy = copyArray(points);
        // sorting the copy, based on compareTo method in Point class
        Arrays.sort(copy, Point::compareTo);
        for (int i = 0; i < copy.length - 1; i++) {
            if (copy[i].compareTo(copy[i + 1]) == 0) throw new IllegalArgumentException();
        }
        this.lineSegments = new LineSegment[points.length];
        this.lineSegmentsCurrentIndex = 0;
        double[] slopes = new double[points.length];
        Point[] endPoints = new Point[points.length];
        // finds all line segments containing 4 points
        for (int i = 0; i < copy.length - 3; i++) {
            Point p = copy[i];
            Point[] pointsToConsider = copyFromIndex(copy, i + 1);
            Point[] sorted = sortedBySlope(pointsToConsider, p);
            double[] slopesSorted = slopeArray(sorted, p);
            int j = 0, counter = 0;
            while (j < slopesSorted.length - 1) {
                if (slopesSorted[j] == slopesSorted[j + 1]) {
                    while (j < slopesSorted.length - 1 && slopesSorted[j] == slopesSorted[j + 1]) {
                        counter++;
                        j++;
                    }
                }
                if (counter >= 2) {
                    Point startPoint = sorted[j - counter];
                    Point endPoint = sorted[j];
                    double slope = p.slopeTo(endPoint);
                    if (p.compareTo(startPoint) < 0 && !checkIfLineExists(slope, endPoint, slopes,
                                                                          endPoints)) {
                        if (shouldResize()) {
                            slopes = resizeDoubleArray(slopes);
                            endPoints = resizePointArray(endPoints);
                            this.lineSegments = resizeLineSegmentsArray(this.lineSegments);
                        }
                        slopes[lineSegmentsCurrentIndex] = slope;
                        endPoints[lineSegmentsCurrentIndex] = endPoint;
                        this.lineSegments[lineSegmentsCurrentIndex++] = new LineSegment(p,
                                                                                        endPoint);
                    }
                }
                counter = 0;
                j++;
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return this.lineSegmentsCurrentIndex;
    }

    // the line segments
    public LineSegment[] segments() {
        return Arrays.copyOf(this.lineSegments, this.lineSegmentsCurrentIndex);
    }

    private boolean checkIfLineExists(double slope, Point endPoint, double[] slopes,
                                      Point[] endPoints) {
        for (int i = lineSegmentsCurrentIndex; i > 0; i--) {
            if (slope == slopes[i] && endPoint == endPoints[i]) {
                return true;
            }
        }
        return false;
    }

    private Point[] copyArray(Point[] points) {
        Point[] copy = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            copy[i] = points[i];
        }
        return copy;
    }

    private Point[] copyFromIndex(Point[] points, int index) {
        Point[] copy = new Point[points.length - index];
        for (int i = index; i < points.length; i++) {
            copy[i - index] = points[i];
        }
        return copy;
    }

    private Point[] sortedBySlope(Point[] points, Point p) {
        return Arrays.stream(points).sorted(p.slopeOrder())
                     .filter(point -> p.slopeTo(point) != Double.NEGATIVE_INFINITY)
                     .toArray(Point[]::new);
    }

    private double[] slopeArray(Point[] points, Point p) {
        return Arrays.stream(points).mapToDouble(p::slopeTo)
                     .filter(slope -> slope != Double.NEGATIVE_INFINITY).toArray();
    }

    private boolean shouldResize() {
        return lineSegmentsCurrentIndex == lineSegments.length;
    }

    private LineSegment[] resizeLineSegmentsArray(LineSegment[] originLineSegments) {
        return Arrays.copyOf(originLineSegments, originLineSegments.length * 2);
    }

    private double[] resizeDoubleArray(double[] originArray) {
        return Arrays.copyOf(originArray, originArray.length * 2);
    }

    private Point[] resizePointArray(Point[] originArray) {
        return Arrays.copyOf(originArray, originArray.length * 2);
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        StdOut.println("number of lines: " + collinear.numberOfSegments());
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
