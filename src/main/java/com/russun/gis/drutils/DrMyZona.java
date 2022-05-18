package com.russun.gis.drutils;

import com.russun.gis.MainPageController;
import com.russun.gis.TableController;
import com.russun.gis.ZonaController;
import com.russun.gis.shapes.MyZone;
import com.russun.gis.utils.DatabaseHandler;
import com.russun.gis.utils.MyShape;
import com.russun.gis.utils.RectGeo;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import java.sql.SQLException;
import java.util.ArrayList;

public class DrMyZona {
    public static int countPointsZona;

    private static MyZone myZona;

    public static void drawZona(double xgeoRadius, double geoSquare) {
        System.out.println(xgeoRadius);
        myZona = new MyZone(new Ellipse());
        double ygeoRadius = geoSquare / Math.PI / xgeoRadius;

        myZona.getEllipse().setCenterX(ZonaController.xcenter);
        myZona.getEllipse().setCenterY(ZonaController.ycenter);

        double[] xygeo = RectGeo.toGeo(RectGeo.toRect(ZonaController.xcenter), RectGeo.toRect(ZonaController.ycenter));
        myZona.setXgeoCenter(xygeo[0]);
        myZona.setYgeoCenter(xygeo[1]);

        double xgeoSubtract = MainPageController.fromXkm(xgeoRadius);
        int[] xyrect = RectGeo.fromGeo(myZona.getXgeoCenter() + xgeoSubtract, myZona.getYgeoCenter());
        double xradius = Math.abs(RectGeo.fromRect(xyrect[0]) - myZona.getEllipse().getCenterX());
        myZona.getEllipse().setRadiusX(xradius);
        myZona.setXgeoRadius(xgeoRadius);

        double ygeoSubtract = MainPageController.fromYkm(ygeoRadius);
        xyrect = RectGeo.fromGeo(myZona.getXgeoCenter(), myZona.getYgeoCenter() - ygeoSubtract);
        double yradius = Math.abs(RectGeo.fromRect(xyrect[1]) - myZona.getEllipse().getCenterY());
        myZona.getEllipse().setRadiusY(yradius);
        myZona.setYgeoRadius(ygeoRadius);

        myZona.getEllipse().setStrokeWidth(MainPageController.factor);
        myZona.getEllipse().setStroke(Color.RED);
        myZona.getEllipse().setFill(Color.ROSYBROWN);
        myZona.setGeoSquare(geoSquare);
        MyZone.getMyZones().add(myZona);
        MainPageController.group.getChildren().add(myZona.getEllipse());
        countPointsZona = 0;
        System.out.println(myZona.getEllipse().getRadiusX());

        TableController.myShape = new MyShape(0, "Зона", 0.0, myZona.getGeoSquare(), 0.0);
        try {
            new DatabaseHandler().insertQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void redrawZona(boolean zoom) {
        ArrayList<MyZone> myZonas = MyZone.getMyZones();
        if (myZonas != null) {
            for (int i = 0; i < myZonas.size(); i++)
                MainPageController.group.getChildren().remove(myZonas.get(i).getEllipse());
            for (int i = 0; i < myZonas.size(); i++) {
                myZonas.get(i).getEllipse().setCenterX(RectGeo.toRectForRedraw(myZonas.get(i).getEllipse().getCenterX(), zoom));
                myZonas.get(i).getEllipse().setCenterY(RectGeo.toRectForRedraw(myZonas.get(i).getEllipse().getCenterY(), zoom));
                myZonas.get(i).getEllipse().setRadiusX(RectGeo.toRectForRedraw(myZonas.get(i).getEllipse().getRadiusX(), zoom));
                myZonas.get(i).getEllipse().setRadiusY(RectGeo.toRectForRedraw(myZonas.get(i).getEllipse().getRadiusY(), zoom));
                myZonas.get(i).getEllipse().setStrokeWidth(MainPageController.factor);

                MainPageController.group.getChildren().add(myZonas.get(i).getEllipse());
            }
        }
    }
}
