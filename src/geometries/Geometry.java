package geometries;

import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;

/**
 * abstract class Geometry extends Intersectable
 * the class describe geometry body.
 * the geometry have material and color
 *
 */
public abstract class Geometry extends Intersectable {
    /**
     * the material of the geometry
     */
    private Material _material = new Material();
    /**
     * the color of the geometry
     */
    protected Color _emission = Color.BLACK;

    /**
     * calculate the normal from the point
     * @param p1 point to calculate the normal
     * @return normal
     */
    public abstract Vector getNormal(Point p1);

    /**
     * getter for material
     * @return material
     */
    public Material getMaterial() {
        return _material;
    }

    /**
     * setter for material
     * @param material of type Material
     * @return this
     */
    public Geometry setMaterial(Material material) {
        _material = material;
        return this;
    }

    /**
     * getter of emission
     * @return emission
     */
    public Color getEmission() {
        return _emission;
    }

    /**
     * setter of emission
     * @param emission of type Color
     * @return this
     */
    public Geometry setEmission(Color emission) {
        _emission = emission;
        return this;
    }
}
