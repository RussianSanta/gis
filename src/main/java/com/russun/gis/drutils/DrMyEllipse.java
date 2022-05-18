package com.russun.gis.drutils;

import com.russun.gis.MainPageController;
import com.russun.gis.TableController;
import com.russun.gis.shapes.MyEllipse;
import com.russun.gis.utils.DatabaseHandler;
import com.russun.gis.utils.MyShape;
import com.russun.gis.utils.RectGeo;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import java.sql.SQLException;
import java.util.ArrayList;

public class DrMyEllipse {
    public static int countPointsEllipse;

    private static MyEllipse myEllipse;

    public static void drawEllipse(double x, double y) {
        if (countPointsEllipse == 1) {
            myEllipse = new MyEllipse(new Ellipse());
            myEllipse.getEllipse().setCenterX(x);
            myEllipse.getEllipse().setCenterY(y);
            myEllipse.setXrectCenter(RectGeo.toRect(x));
            myEllipse.setYrectCenter(RectGeo.toRect(y));
            if (!(MainPageController.myPoints == null || MainPageController.myPoints.size() == 0)) {
                double[] xygeo = RectGeo.toGeo(myEllipse.getXrectCenter(), myEllipse.getYrectCenter());
                myEllipse.setXgeoCenter(xygeo[0]);
                myEllipse.setYgeoCenter(xygeo[1]);
                myEllipse.setGeo(true);
            }
            countPointsEllipse += 1;
        } else if (countPointsEllipse == 2) {
            double xradius = Math.abs(x - myEllipse.getEllipse().getCenterX());
            myEllipse.getEllipse().setRadiusX(xradius);
            myEllipse.setXrectRadius(RectGeo.toRect(xradius));
            if (myEllipse.isGeo()) {
                double[] xygeo = RectGeo.toGeo(RectGeo.toRect(x), RectGeo.toRect(y));
                myEllipse.setXgeoRadius(MainPageController.toXkm(myEllipse.getXgeoCenter(), xygeo[0]));
            }
            countPointsEllipse += 1;
        } else {
            ArrayList<MyEllipse> myEllipses = MyEllipse.getAllEllipses();
            double yradius = Math.abs(y - myEllipse.getEllipse().getCenterY());
            myEllipse.getEllipse().setRadiusY(yradius);
            myEllipse.setYrectRadius(RectGeo.toRect(yradius));
            myEllipse.getEllipse().setStrokeWidth(MainPageController.factor);
            myEllipse.getEllipse().setStroke(Color.BLUE);
            myEllipse.getEllipse().setFill(Color.GREEN);
            if (myEllipse.isGeo()) {
                double[] xygeo = RectGeo.toGeo(RectGeo.toRect(x), RectGeo.toRect(y));
                myEllipse.setYgeoRadius(MainPageController.toYkm(myEllipse.getYgeoCenter(), xygeo[1]));
                myEllipse.setSquare(myEllipse.getXgeoRadius() * myEllipse.getYgeoRadius() * Math.PI);
            } else
                myEllipse.setSquare(myEllipse.getXrectRadius() * myEllipse.getYrectRadius() * Math.PI);
            myEllipses.add(myEllipse);
            MainPageController.group.getChildren().add(myEllipse.getEllipse());
            countPointsEllipse = 0;

            TableController.myShape = new MyShape(0, "Элипс", 0.0, myEllipse.getSquare(), 0.0);
            try {
                new DatabaseHandler().insertQuery();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void redrawEllipse(boolean zoom) {
        ArrayList<MyEllipse> myEllipses = MyEllipse.getAllEllipses();
        if (myEllipses != null) {
            for (int i = 0; i < myEllipses.size(); i++)
                MainPageController.group.getChildren().remove(myEllipses.get(i).getEllipse());
            for (int i = 0; i < myEllipses.size(); i++) {
                myEllipses.get(i).getEllipse().setCenterX(RectGeo.toRectForRedraw(myEllipses.get(i).getEllipse().getCenterX(), zoom));
                myEllipses.get(i).getEllipse().setCenterY(RectGeo.toRectForRedraw(myEllipses.get(i).getEllipse().getCenterY(), zoom));
                myEllipses.get(i).getEllipse().setRadiusX(RectGeo.toRectForRedraw(myEllipses.get(i).getEllipse().getRadiusX(), zoom));
                myEllipses.get(i).getEllipse().setRadiusY(RectGeo.toRectForRedraw(myEllipses.get(i).getEllipse().getRadiusY(), zoom));
                myEllipses.get(i).getEllipse().setStrokeWidth(MainPageController.factor);

                MainPageController.group.getChildren().add(myEllipses.get(i).getEllipse());
            }
        }
    }


}
