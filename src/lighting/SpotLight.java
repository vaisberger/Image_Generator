package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class SpotLight extends PointLight {
    private Vector _direction;

    public SpotLight(Color intensity, Point position,Vector direction) {
        super(intensity, position);
        this._direction = direction.normalize();
    }

    /**
     *
     * @param p
     * @return
     */
    @Override
    public Color getIntensity(Point p) {
        Color pointLight = super.getIntensity(p);
        double dirL = Math.max(0, getL(p).dotProduct(_direction));/////////////abs
        return  pointLight.scale(dirL);
    }
}
