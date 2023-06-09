package renderer;

import geometries.Intersectable.GeoPoint;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.LinkedList;
import java.util.List;

import static primitives.Double3.ZERO;
import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * class RayTracerBasic extends by abstract class RayTracer
 */
public class RayTracerBasic extends RayTracer {
    private static final double EPS = 0.1;
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * constructor
     *
     * @param scene the scene of the picture
     */
    public RayTracerBasic(Scene scene) {
        super(scene);
    }


    @Override
    public Color traceRay(Ray ray) {
        Color result = scene._background;
        List<GeoPoint> allPoints = scene.geometries.findGeoIntersection(ray);
        if (allPoints != null) {
            GeoPoint pt = ray.findClosestGeoPoint(allPoints);
            result = calcColor(pt, ray);
        }
        return result;
    }

    /**
     * calculate the color in point
     *
     * @param geoPoint in the scene
     * @return the color in the point
     */
    private Color calcColor(GeoPoint geoPoint, Ray ray) {
        if (geoPoint == null) {
            return scene._background;
        }
        return calcColor(geoPoint, ray, MAX_CALC_COLOR_LEVEL, INITIAL_K);
    }

    /**
     * calculate the color in point with using recursive function
     * @param intersection the point to calculation the color
     * @param ray from the camera
     * @param level  Maximum recursion depth.
     * @param k Scalar coefficient.
     * @return the color in the point
     */
    private Color calcColor(GeoPoint intersection, Ray ray, int level, Double3 k) {
        Color color = intersection.geometry.getEmission()
                .add(calcLocalEffects(intersection, ray, k));
        return 1 == level ? color : color.add(calcGlobalEffects(intersection, ray, level, k));
    }

    /**
     * calculates the secondary rays that occur from reflection and refraction
     * @param ray from the camera
     * @param level  Maximum recursion depth.
     * @param k  Scalar coefficient.
     * @return the color of the second ray
     */
    private Color calcSecondaryRayColor(Ray ray, int level, Double3 k) {
        GeoPoint geoPoint = findClosestIntersection(ray);
        Color color = scene._background;
        if (geoPoint != null) {
            color = calcColor(geoPoint, ray, level - 1, k);
        }
        return color;
    }

    /**
     * function that check if point shading by the light source
     * and if there is something that blocks the light
     *
     * @param light    the lightSource
     * @param l        vector of light Direction
     * @param n        vector of normal to GeoPoint
     * @param geopoint point to check if it's shading
     * @return if the geometry shaded or not
     */
    private boolean unshaded(LightSource light, Vector l, Vector n, GeoPoint geopoint) {
        Vector lightDirection = l.scale(-1); // from point to light source
        double nl = n.dotProduct(lightDirection);
        Vector epsVector;// if needed changes the vectors direction
        if (nl > 0) {
            epsVector = n.scale(EPS);
        } else
            epsVector = n.scale(-EPS);

        Point geoPoint = geopoint.point.add(epsVector);
        Ray lightRay = new Ray(geoPoint, lightDirection);
        double distance = light.getDistance(geoPoint); //distance from light to geo point
        List<GeoPoint> intersections = scene.geometries.findGeoIntersections(lightRay, distance);

        if (intersections == null)
            return true;

        intersections.removeIf(
                (item) -> {
                    return item.geometry.getMaterial().kT.lowerThan(MIN_CALC_COLOR_K);
                }
        );

        if (intersections.isEmpty())
            return true;

        else
            return false;
    }


    /**
     * function that calculate the color in a point with the local effects on the point
     *like diffusive and specular
     * @param gp   GeoPoint
     * @param ray- ray from the camera
     * @return the color on the point
     */
    private Color calcLocalEffects(GeoPoint gp, Ray ray, Double3 k) {
        Color color = Color.BLACK;
        Vector v = ray.getDir();
        Vector n = gp.geometry.getNormal(gp.point);
        double nv = alignZero(n.dotProduct(v));
        if (nv == 0)
            return color;
        Material material = gp.geometry.getMaterial();
        for (LightSource lightSource : scene.lights) {
             Vector l = lightSource.getL(gp.point);
            double nl = alignZero(n.dotProduct(l));
            if (nl * nv > 0) { // sign(nl) == sing(nv)
                Double3 ktr=transparencyBeam(gp,lightSource,n);
                //Double3 ktr = transparency(gp, lightSource, l, n);
                if (!ktr.product(k).lowerThan(MIN_CALC_COLOR_K)) {
                    Color iL = lightSource.getIntensity(gp.point).scale(ktr);
                    color = color.add(
                            iL.scale(calcDiffusive(material, nl)),
                            iL.scale(calcSpecular(material, n, l, nl, v)));
                }
            }
        }
        return color;
    }


    /**
     * Calculates the shadow on the spot and the level of shading on the point
     *
     * @param geoPoint point on the geometry
     * @param ls the light in the scene
     * @param l vector from the light
     * @param n normal to geoPoint
     * @return the level of the shadow on the geometry
     */
    private Double3 transparency(GeoPoint geoPoint, LightSource ls, Vector l, Vector n) {
        Vector lightDirection = l.scale(-1); // from point to light source
        Ray lightRay = new Ray(geoPoint.point, lightDirection, n);
        double distance = ls.getDistance(geoPoint.point);

        List<GeoPoint> intersections = scene.geometries.findGeoIntersections(lightRay, distance);

        if (intersections == null) {
            return Double3.ONE;
        }
        if (intersections.isEmpty()) {
            return Double3.ONE;
        }
        //calculate the Light intensity
        Double3 ktr = Double3.ONE;
        for (GeoPoint geoP : intersections) {
            ktr = ktr.product(geoP.geometry.getMaterial().kT);
            if (ktr.lowerThan(MIN_CALC_COLOR_K)) {
                return Double3.ZERO;
            }
        }

        return ktr;
    }

    /**
     *  calc the specular light effect on the specific point
     *
     * @param material the material of the point
     * @param n vector normal
     * @param l vector normal
     * @param nl dot-product n*l
     * @param v  vector from point of view to point
     * @return level of the scapular factor
     */
    private Double3 calcSpecular(Material material, Vector n, Vector l, double nl, Vector v) {
        // nl is the dot product among the vector from the specular light to the point and the normal vector of the point
        //nl must not be zero
        if (isZero(nl)) {
            throw new IllegalArgumentException("nl cannot be Zero for scaling the normal vector");
        }
        Vector r = l.subtract(n.scale(2 * nl));
        double vr = alignZero(v.dotProduct(r));
        if (vr >= 0) {
            return ZERO; // view from direction opposite to r vector
        }
        return material.kS.scale(Math.pow(-1d * vr, material.getShininess()));
    }

    /**
     * calculates the diffusing of the light
     * @param material the material of what in the pixel
     * @param nl distance
     * @return the level of the light in the pixel
     */
    private Double3 calcDiffusive(Material material, double nl) {
        double abs_nl = Math.abs(nl);
        return material.kD.scale(abs_nl);
    }

    /**
     * when needed calculates the rays that reflect from surface
     * @param pointGeo GeoPoint to calculate the ray
     * @param ray direction
     * @param n vector normal
     * @return reflected ray.
     */
    private Ray constructReflectedRay(Point pointGeo, Ray ray, Vector n) {
        Vector v = ray.getDir();
        double vn = v.dotProduct(n);

        if (vn == 0) {
            return null;
        }

        Vector r = v.subtract(n.scale(2 * vn));
        return new Ray(pointGeo, r, n);
    }

    /**
     * a function that calculates the global effects on the geo point
     * @param intersection GeoPoint to calculate the color
     * @param ray The ray of the point
     * @param level  Maximum recursion depth.
     * @param k  Scalar coefficient.
     * @return the color on GeoPoint intersection
     */
    private Color calcGlobalEffects(GeoPoint intersection, Ray ray, int level, Double3 k) {
        Color color = Color.BLACK;
        Vector n = intersection.geometry.getNormal(intersection.point);
        Ray reflectedRay = constructReflectedRay(intersection.point, ray, n);
        // GeoPoint reflectedPoint = findClosestIntersection(reflectedRay);

        Double3 kkr = intersection.geometry.getMaterial().kR.product(k);
        if (!kkr.lowerThan(MIN_CALC_COLOR_K)) { // check if the color it would add is Negligible
            color = color.add(calcSecondaryRayColor(reflectedRay, level, kkr)
                    .scale(intersection.geometry.getMaterial().kR));
        }

        Double3 kkt = intersection.geometry.getMaterial().kT.product(k);
        Ray refractedRay = constructRefractedRay(intersection.point, ray, n);
       // GeoPoint refractedPoint = findClosestIntersection(refractedRay);
        if (!kkt.lowerThan(MIN_CALC_COLOR_K)) { // check if the color it would add is Negligible
            color = color.add(calcSecondaryRayColor(refractedRay, level, kkt)
                    .scale(intersection.geometry.getMaterial().kT));
        }

        return color;
    }

    /**
     * returns the ray that refracts from mirrored material
     * @param point The point to calculate the effect.
     * @param ray ray normal
     * @param v vector normal
     * @return refracted ray
     */
    private Ray constructRefractedRay(Point point, Ray ray, Vector v) {
        return new Ray(point, ray.getDir(), v);
    }

    /**
     * Calculate the GeoPoint of intersection closest to the start of the ray
     *
     * @param reflectedRay rat of reflection
     * @return intersection closest to the start of the ray
     */
    private GeoPoint findClosestIntersection(Ray reflectedRay) {
        List<GeoPoint> intersections = scene.geometries.findGeoIntersection(reflectedRay);
        if (intersections == null) {
            return null;
        }
        return reflectedRay.findClosestGeoPoint(intersections);
    }


    /**
     * change of soft shadow
     * @param geoPoint The point to calculate the transparencyBeam.
     * @param lightSource the light of the scene
     * @param n vector normal
     * @return the level of the transparency
     */
    private Double3 transparencyBeam( GeoPoint geoPoint , LightSource lightSource, Vector n) {
        Double3 ktr;
        List<Vector> beamL = lightSource.circleBeam(geoPoint.point, 10);
        Double3 tempKtr = Double3.ZERO;
        for (Vector vl : beamL) {
            Point vecToPnt= new Point(vl.getX(), vl.getY(), vl.getZ());
            double lightDistance = geoPoint.point.distance(vecToPnt);
            tempKtr = tempKtr.add( transparency(geoPoint,lightSource, vl, n));
        }
        ktr = tempKtr.reduce( beamL.size());

        return ktr;
    }
}
