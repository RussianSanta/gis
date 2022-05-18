package com.russun.gis.drutils;

import com.russun.gis.MainPageController;
import com.russun.gis.TableController;
import com.russun.gis.shapes.MyRectangle;
import com.russun.gis.utils.DatabaseHandler;
import com.russun.gis.utils.MyShape;
import com.russun.gis.utils.RectGeo;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.sql.SQLException;
import java.util.ArrayList;

public class DrMyRectangle {
    public static int countPointsRectangle;

    private static MyRectangle myRectangle;

    public static void drawRectangle(double x, double y) {
        if (countPointsRectangle == 1) {
            myRectangle = new MyRectangle(new Rectangle());
            myRectangle.getRectangle().setX(x);
            myRectangle.getRectangle().setY(y);
            myRectangle.setXrectLeftTop(RectGeo.toRect(x));
            myRectangle.setYrectLeftTop(RectGeo.toRect(y));
            if (!(MainPageController.myPoints == null || MainPageController.myPoints.size() == 0)) {
                double[] xygeo = RectGeo.toGeo(myRectangle.getXrectLeftTop(), myRectangle.getYrectLeftTop());
                myRectangle.setXgeoLeftTop(xygeo[0]);
                myRectangle.setYgeoLeftTop(xygeo[1]);
                myRectangle.setGeo(true);
            }
            countPointsRectangle += 1;
        } else {
            ArrayList<MyRectangle> myRectangles = MyRectangle.getMyRectangles();
            double width = Math.abs(x - myRectangle.getRectangle().getX());
            double height = Math.abs(y - myRectangle.getRectangle().getY());
            myRectangle.getRectangle().setWidth(width);
            myRectangle.getRectangle().setHeight(height);
            myRectangle.setRectWidth(RectGeo.toRect(width));
            myRectangle.setRectHeight(RectGeo.toRect(height));
            if (myRectangle.isGeo()) {
                double[] xygeo = RectGeo.toGeo(RectGeo.toRect(x), RectGeo.toRect(y));
                myRectangle.setGeoWidth(MainPageController.toXkm(myRectangle.getXgeoLeftTop(), xygeo[0]));
                myRectangle.setGeoHeight(MainPageController.toYkm(myRectangle.getYgeoLeftTop(), xygeo[1]));
                myRectangle.setSquare(myRectangle.getGeoWidth() * myRectangle.getGeoHeight());
                myRectangle.setPerimeter(myRectangle.getGeoWidth() * 2 + myRectangle.getGeoHeight() * 2);
            } else {
                myRectangle.setSquare(myRectangle.getRectWidth() * myRectangle.getRectHeight());
                myRectangle.setPerimeter(myRectangle.getRectWidth() * 2 + myRectangle.getRectHeight() * 2);
            }
            myRectangle.getRectangle().setStrokeWidth(MainPageController.factor);
            myRectangle.getRectangle().setStroke(Color.RED);
            myRectangle.getRectangle().setFill(Color.YELLOW);
            myRectangles.add(myRectangle);
            MainPageController.group.getChildren().add(myRectangle.getRectangle());
            countPointsRectangle = 0;

            TableController.myShape = new MyShape(0, "Прямоугольник", 0.0, myRectangle.getSquare(), myRectangle.getPerimeter());
            try {
                new DatabaseHandler().insertQuery();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void redrawRectangle(boolean zoom) {
        ArrayList<MyRectangle> myRectangles = MyRectangle.getMyRectangles();
        if (myRectangles != null) {
            for (int i = 0; i < myRectangles.size(); i++)
                MainPageController.group.getChildren().remove(myRectangles.get(i).getRectangle());
            for (int i = 0; i < myRectangles.size(); i++) {
                myRectangles.get(i).getRectangle().setX(RectGeo.toRectForRedraw(myRectangles.get(i).getRectangle().getX(), zoom));
                myRectangles.get(i).getRectangle().setY(RectGeo.toRectForRedraw(myRectangles.get(i).getRectangle().getY(), zoom));
                myRectangles.get(i).getRectangle().setWidth(RectGeo.toRectForRedraw(myRectangles.get(i).getRectangle().getWidth(), zoom));
                myRectangles.get(i).getRectangle().setHeight(RectGeo.toRectForRedraw(myRectangles.get(i).getRectangle().getHeight(), zoom));
                myRectangles.get(i).getRectangle().setStrokeWidth(MainPageController.factor);

                MainPageController.group.getChildren().add(myRectangles.get(i).getRectangle());
            }
        }
    }
}
