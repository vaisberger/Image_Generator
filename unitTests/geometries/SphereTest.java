package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Sphere}
 */
class SphereTest {

    /**
     * Test method for {@link geometries.Sphere#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
     Sphere sphere= new Sphere(new Point(1,0,0),1);
     assertEquals(new Vector(1,0,0),sphere.getNormal(new Point(2,0,0)));
    }

    /**
     * Test method for{@link geometries.Sphere#findIntersections(Ray)}
     */
    @Test
    void testFindIntersections() {
        Sphere sphere = new Sphere( new Point(1, 0, 0),1d);

        // ============ Equivalence Partitions Tests ==============
        // TC_1: Ray's line is outside the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(1, 1, 0))),
                "Ray's line out of sphere");
        // TC_2: Ray starts before and crosses the sphere (2 points)
        Point p1 = new Point(0.0651530771650466, 0.355051025721682, 0);
        Point p2 = new Point(1.53484692283495, 0.844948974278318, 0);
        List<Point> result = sphere.findIntersections(new Ray(new Point(-1, 0, 0),
                new Vector(3, 1, 0)));
        assertEquals(2, result.size(), "Wrong number of points");
        if (result.get(0).getX() > result.get(1).getX())
            result = List.of(result.get(1), result.get(0));
        assertEquals(List.of(p1, p2), result, "Ray crosses sphere");
        // TC_3: Ray starts inside the sphere (1 point)
        result= sphere.findIntersections(new Ray(new Point(0.5, 0.5, 0), new Vector(3, 1, 0)));
              assertEquals(List.of(p2),result,"Ray starts inside the sphere");
        // TC_4: Ray starts after the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(3,4,0),new Vector(3,1,0))));

        // =============== Boundary Values Tests ==================

        // **** Group: Ray's line crosses the sphere (but not the center)
        // TC11: Ray starts at sphere and goes inside (1 points)
       assertEquals(List.of(new Point(1,1,0)),
                 sphere.findIntersections(new Ray(new Point(0.5,0.5,0),new Vector(0.5,.5,0))),"Ray from inside the sphere");
        // TC12: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1,1,0),new Vector(2,3,0))),"Ray from outside the sphere");

        // **** Group: Ray's line goes through the center
        // TC13: Ray starts before the sphere (2 points)
       assertEquals(List.of(new Point(2,0,0),new Point(0,0,0)),
                sphere.findIntersections(new Ray(new Point(-1,0,0),new Vector(1,0,0))),"Line through O, ray crosses sphere");
        // TC14: Ray starts at sphere and goes inside (1 points)
        assertEquals(List.of(new Point(2,0,0)),
                sphere.findIntersections(new Ray(new Point(0,0,0),new Vector(1,0,0))),"Line through O, ray from and crosses sphere");
        // TC15: Ray starts inside (1 points)
        assertEquals(List.of(new Point(2,0,0)),
                sphere.findIntersections(new Ray(new Point(0.5,0,0),new Vector(1,0,0))),"Line through O, ray from inside sphere");
        // TC16: Ray starts at the center (1 points)
        assertEquals(List.of(new Point(2,0,0)),
                sphere.findIntersections(new Ray(new Point(1,0,0),new Vector(2,0,0))),"Line through O, ray from O");
        // TC17: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(2,0,0),new Vector(3,0,0))),"Line through O, ray from sphere outside");
        // TC18: Ray starts after sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(3,0,0),new Vector(4,0,0))),"Line through O, ray outside sphere");

        // **** Group: Ray's line is tangent to the sphere (all tests 0 points)
        // TC19: Ray starts before the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(0,-1,0),new Vector(0,1,0))),"Tangent line, ray before sphere");
        // TC20: Ray starts at the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(0,0,0),new Vector(0,1,0))),"Tangent line, ray at sphere");
        // TC21: Ray starts after the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(0,1,0),new Vector(0,2,0))),"Tangent line, ray after sphere");

        // **** Group: Special cases
        // TC19: Ray's line is outside, ray is orthogonal to ray start to sphere's center line
        assertNull(sphere.findIntersections(new Ray(new Point(-1,0,0),new Vector(-1,1,0))),"Ray orthogonal to ray head -> O line");

    }
}