package geometries;

import primitives.Point;

/**
 * class Triangle extends Polygon
 * the class describe polygon with 3 points
 */
public class Triangle extends Polygon{
    /**
     * constructor get 3 point of the triangle
     *
     * @param p1 first point of the triangle
     * @param p2 second point of the triangle
     * @param p3 third point of the triangle
     */
    public Triangle(Point p1, Point p2, Point p3){
        super(p1,p2,p3);
    }

    @Override
    public String toString() {
        return "Triangle{" +
                "vertices=" + _vertices +
                ", plane=" + _plane +
                '}';
    }
}
