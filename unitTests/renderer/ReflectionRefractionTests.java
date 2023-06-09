package renderer;

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import static java.awt.Color.*;

/**
 * Tests for reflection and transparency functionality, test for partial shadows
 * (with transparency)
 *
 * @author dzilb
 */
public class ReflectionRefractionTests {
    private final Scene scene = new Scene.SceneBuilder("Test scene").build();

    /**
     * Produce a picture of a sphere lighted by a spotlight
     */
    @Test
    public void twoSpheres() {
        Camera camera = new Camera(new Point(0, 0, 1000), new Vector(0, 0, -1), new Vector(0, 1, 0)) //
                .setVPSize(150, 150)
                .setVPDistance(1000);

        scene.geometries.add( //
                new Sphere(new Point(0, 0, -50), 50d)
                        .setEmission(new Color(BLUE)) //
                        .setMaterial(new Material()
                                .setKd(0.4)
                                .setKs(0.3)
                                .setShininess(100)
                                .setKt(0.3)),
                new Sphere(new Point(0, 0, -50), 25d)
                        .setEmission(new Color(RED)) //
                        .setMaterial(new Material()
                                .setKd(0.5)
                                .setKs(0.5)
                                .setShininess(100)));
        scene.lights.add( //
                new SpotLight(new Color(1000, 600, 0), new Point(-100, -100, 500), new Vector(-1, -1, -2)) //
                        .setKl(0.0004)
                        .setKq(0.0000006));

        camera.setImageWriter(new ImageWriter("refractionTwoSpheres", 500, 500)) //
                .setRayTracer(new RayTracerBasic(scene)) //
                .renderImage() //
                .writeToImage();
    }

    /**
     * Produce a picture of a sphere lighted by a spotlight
     */
    @Test
    public void twoSpheresOnMirrors() {
        Camera camera = new Camera(new Point(0, 0, 10000), new Vector(0, 0, -1), new Vector(0, 1, 0)) //
                .setVPSize(2500, 2500)
                .setVPDistance(10000); //

        scene.setAmbientLight(new AmbientLight(new Color(WHITE), new Double3(0.1)));

        scene.geometries.add( //
                new Sphere(new Point(-950, -900, -1000), 400d)
                        .setEmission(new Color(0, 0, 100)) //
                        .setMaterial(new Material()
                                .setKd(0.25)
                                .setKs(0.25)
                                .setShininess(20)
                                .setKt(0.5)),
                new Sphere(new Point(-950, -900, -1000), 200d)
                        .setEmission(new Color(100, 20, 20)) //
                        .setMaterial(new Material()
                                .setKd(0.25)
                                .setKs(0.25)
                                .setShininess(20)),
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), new Point(670, 670, 3000)) //
                        .setEmission(new Color(20, 20, 20)) //
                        .setMaterial(new Material()
                                .setKr(1)),
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500),	new Point(-1500, -1500, -2000)) //
                        .setEmission(new Color(20, 20, 20)) //
                        .setMaterial(new Material()
                                .setKr(0.5)));

        scene.lights.add(new SpotLight(new Color(1020, 400, 400), new Point(-750, -750, -150), new Vector(-1, -1, -4)) //
                .setKl(0.00001)
                .setKq(0.000005));

        ImageWriter imageWriter = new ImageWriter("reflectionTwoSpheresMirrored", 500, 500);
        camera.setImageWriter(imageWriter) //
                .setRayTracer(new RayTracerBasic(scene)) //
                .renderImage() //
                .writeToImage();
    }

    /**
     * Produce a picture of two triangles lighted by a spotlight with a partially
     * transparent Sphere producing partial shadow
     */
    @Test
    public void trianglesTransparentSphere() {
        Camera camera = new Camera(new Point(0, 0, 1000), new Vector(0, 0, -1), new Vector(0, 1, 0)) //
                .setVPSize(200, 200)
                .setVPDistance(1000);

        scene.setAmbientLight(new AmbientLight(new Color(WHITE), new Double3(0.15)));

        scene.geometries.add( //
                new Triangle(new Point(-150, -150, -115), new Point(150, -150, -135), new Point(75, 75, -150)) //
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(60)), //
                new Triangle(new Point(-150, -150, -115), new Point(-70, 70, -140), new Point(75, 75, -150)) //
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(60)), //
                new Sphere(new Point(60, 50, -50), 30d).setEmission(new Color(BLUE)) //
                        .setMaterial(new Material().setKd(0.2).setKs(0.2).setShininess(30).setKt(0.6)));

        scene.lights.add(new SpotLight(new Color(700, 400, 400), new Point(60, 50, 0), new Vector(0, 0, -1)) //
                .setKl(4E-5).setKq(2E-7));

        ImageWriter imageWriter = new ImageWriter("refractionShadow", 600, 600);
        camera.setImageWriter(imageWriter) //
                .setRayTracer(new RayTracerBasic(scene)) //
                .renderImage()
                .writeToImage();
    }

    @Test
    public void snowMan() {

        Scene scene = new Scene.SceneBuilder("Test scene") //
                .setAmbientLight(new AmbientLight(new Color(WHITE), new Double3(0.7)))
                .setBackground(new Color(220, 255, 255))
                .build();

        Camera camera = new Camera(new Point(0, 0, 1000), new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVPSize(200, 200)
                .setVPDistance(900);


        scene.geometries.add(
                new Sphere(new Point(-40, 30, -70), 40d)//The lowest ball of the snowman
                        .setEmission(new Color(240,240,240))
                        .setMaterial(new Material()
                                .setKd(0.25)
                                .setKs(0.25)
                                .setShininess(20)
                        ),
                new Sphere(new Point(-40, 70, -60), 22d)//The middle ball of the snowman
                        .setEmission(new Color(30,30,30))
                        .setMaterial(new Material()
                                .setKd(0.25)
                                .setKs(0.25)
                                .setShininess(20)
                                .setKt(0.9)),
                new Sphere(new Point(-40, 100, -70), 15d)//The top ball of the snowman
                        .setEmission(new Color(240,240,240))
                        .setMaterial(new Material()
                                .setKd(0.25)
                                .setKs(0.25)
                                .setShininess(20)),
                new Sphere(new Point(-45, 105, -45), 3d)//The left eyes of the snowman
                        .setEmission(new Color(BLACK))
                        .setMaterial(new Material()
                                .setKd(0.25)
                                .setKs(0.25)
                                .setShininess(20)),
                new Sphere(new Point(-35, 105, -40), 3d)//The right eyes of the snowman
                        .setEmission(new Color(BLACK)) //
                        .setMaterial(new Material()
                                .setKd(0.25)
                                .setKs(0.25)
                                .setShininess(20)),
                new Triangle(new Point(-40, 97, 0), new Point(-40, 92, 0), new Point(-30,92,0)) //the carrot
                        .setEmission(new Color(ORANGE))
                        .setMaterial(new Material()
                                .setKd(0.25)
                                .setKs(0.25)
                                .setShininess(20)),
                new Plane(new Point(10,0,0),new Vector(1,0,0))
                        .setMaterial(new Material()
                                        .setKr(0.9))
        );

        scene.lights.add(new SpotLight(new Color(700, 400, 400), new Point(200, 100, -100), new Vector(-50, 0, -1)) //
                .setKl(4E-5).setKq(2E-7));


        ImageWriter imageWriter = new ImageWriter("snowMan", 600, 600);
        camera.setImageWriter(imageWriter)
                .setRayTracer(new RayTracerBasic(scene))
                .renderImage()
                .writeToImage();
    }

@Test

    public void depthOfField() {
    Scene scene = new Scene.SceneBuilder("Test scene") //
            .setAmbientLight(new AmbientLight(new Color(black), new Double3(0.7)))
            .setBackground(new Color(100, 100, 100))
            .build();

    Camera camera = new Camera(new Point(0, 0, 1000), new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVPSize(200, 200)
            .setVPDistance(900);


        scene.geometries.add(
                new Sphere(new Point(0, 0, -60), 22d)//The middle ball of the snowman
                        .setEmission(new Color(200,200,200))
                        .setMaterial(new Material()
                                .setKd(0.25)
                                .setKs(0.25)
                                .setShininess(20)),
                new Sphere(new Point(-90, 0, -500), 22d)//The top ball of the snowman
                        .setEmission(new Color(200,200,200))
                        .setMaterial(new Material()
                                .setKd(0.25)
                                .setKs(0.25)
                                .setShininess(20)));




        scene.lights.add(new SpotLight(new Color(700, 400, 400), new Point(200, 100, -100), new Vector(-50, 0, -1)) //
            .setKl(4E-5).setKq(2E-7));


    ImageWriter imageWriter = new ImageWriter("depth", 600, 600);
        camera.setImageWriter(imageWriter)
            .setRayTracer(new RayTracerBasic(scene))
            .renderImage()
                .writeToImage();
}
}