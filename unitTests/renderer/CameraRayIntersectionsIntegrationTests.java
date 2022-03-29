package renderer;

import org.junit.jupiter.api.Test;

import geometries.*;
import primitives.*;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.*;

class CameraRayIntersectionsIntegrationTests {

    /**
     *      * Test helper function to count the intersections and compare with expected value
     * @param cam
     * @param geo
     * @param expected
     */
    private void assertCountIntersections(Camera cam, Intersectable geo, int expected) {   }
    /**
     *
     */
    @Test
    public void cameraRaySphereIntegration() {
        // TC01: Small Sphere 2 points
        //assertCountIntersections

        // TC02: Big Sphere 18 points
        // TC03: Medium Sphere 10 points
        // TC04: Inside Sphere 9 points
        // TC05: Beyond Sphere 0 points

    }

    @Test
    public void cameraRayPlaneIntegration() {

        // TC01: Plane against camera 9 points

        // TC02: Plane with small angle 9 points

        // TC03: Plane parallel to lower rays 6 points

        // TC04: Beyond Plane 0 points
    }

    /**
     *
     */
    @Test
    public void cameraRayTriangleIntegration() {

        // TC01: Small triangle 1 point

        // TC02: Medium triangle 2 points
    }

}
