package primitives;

import geometries.Intersectable;


import java.util.List;

import static primitives.Util.isZero;

/**
 * class for show ray
 * the ray have start point and vector
 */
public class Ray {
    /**
     * Size of moving the start of ray of the shading rays
     */
    private static final double DELTA = 0.1;

    /**
     * point of the start of the ray
     */
    Point _p0;
    /**
     * vector of the direction ray
     */
    Vector _dir;

    /**
     *constructor
     * @param p point of starting the ray
     * @param vec vector of the ray
     */
    public Ray(Point p, Vector vec){
        this._dir = vec.normalize();
        this._p0 =p;
    }

    /**
     * constructor get 2 vectors and calculate the dot product of them
     * @param point of the start of the ray
     * @param direction first vector
     * @param normal second vector
     */
    public Ray(Point point, Vector direction, Vector normal) {
        //point + normal.scale(±EPSILON)
        _dir = direction.normalize();

        double nv = normal.dotProduct(_dir);

        Vector normalEpsilon = normal.scale((nv > 0 ? DELTA  : -DELTA ));
        _p0 = point.add(normalEpsilon);
    }

    /**
     * @return string with the data of ray
     */
    @Override
    public String toString() {
        return "Ray{" +
                "p0=" + _p0 +
                ", dir=" + _dir +
                '}';
    }

    /**
     * @return P0
     */
    public Point getP0() {
        return _p0;
    }

    /**
     * @return the vector of the ray
     */
    public Vector getDir() {
        return _dir;
    }

    /**
     * calculate point with distance t from P0
     * @param t number to calculate the point
     * @return new point
     */
    public Point getPoint(double t){
        if(isZero(t)){
            return _p0;
        }
        return _p0.add(_dir.scale(t));
    }

    /**
     * find the closest Point to ray origin
     * @param points intersections point list
     * @return the closest point of the start of ray
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null || points.isEmpty() ? null
                : findClosestGeoPoint(points.stream()
                .map(p -> new Intersectable.GeoPoint(null, p)).toList()).point;

    }

    /**
     * find the closest GeoPoint to Ray origin
     * @param lst list of all intersections GeoPoint
     * @return the closest GeoPoint of the start of ray
     */
    public Intersectable.GeoPoint findClosestGeoPoint(List<Intersectable.GeoPoint> lst){

       double distance=Double.POSITIVE_INFINITY;
       Intersectable.GeoPoint p=null;
       if(lst==null)
          return null;

       for (Intersectable.GeoPoint point : lst) {
           double dCompare= point.point.distance(_p0);
           if (dCompare < distance){
               distance= dCompare;
               p=point;
            }
         }
       return p;
    }


}

