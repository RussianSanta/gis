package com.russun.gis.drutils;

import com.russun.gis.MainPageController;
import com.russun.gis.TableController;
import com.russun.gis.shapes.MyLine;
import com.russun.gis.shapes.MyPolygon;
import com.russun.gis.utils.DatabaseHandler;
import com.russun.gis.utils.MyShape;
import com.russun.gis.utils.RectGeo;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.sql.SQLException;
import java.util.ArrayList;

public class DrMyPolygon {
    public static int countPointsPolygon;
    public static MyPolygon myPolygon;
    private static final ArrayList<MyPolygon> myPolygons = MyPolygon.getMyPolygons();
    private static MyLine myLine;

    public static void drawLineForPolygon(double x, double y) {
        if (countPointsPolygon == 1) {
            myPolygon = new MyPolygon(new Polygon());
            countPointsPolygon += 1;
        }
        if (countPointsPolygon == 2) {
            myLine = new MyLine(new Line());
            myLine.getLine().setStartX(x);
            myLine.getLine().setStartY(y);
            myLine.setX1rect(RectGeo.toRect(x));
            myLine.setY1rect(RectGeo.toRect(y));
            if (!(MainPageController.myPoints == null || MainPageController.myPoints.size() == 0)) {
                double[] xygeo = RectGeo.toGeo(myLine.getX1rect(), myLine.getY1rect());
                myLine.setX1geo(xygeo[0]);
                myLine.setY1geo(xygeo[1]);
                myLine.setGeo(true);
            }
            countPointsPolygon += 1;
        } else {
            myLine.getLine().setEndX(x);
            myLine.getLine().setEndY(y);
            myLine.setX2rect(RectGeo.toRect(x));
            myLine.setY2rect(RectGeo.toRect(y));
            if (myLine.isGeo()) {
                double[] xygeo = RectGeo.toGeo(myLine.getX2rect(), myLine.getY2rect());
                myLine.setX2geo(xygeo[0]);
                myLine.setY2geo(xygeo[1]);
                myLine.setLength
                        (
                                Math.sqrt
                                        (
                                                Math.pow(MainPageController.toXkm(myLine.getX1geo(), myLine.getX2geo()), 2)
                                                        +
                                                        Math.pow(MainPageController.toYkm(myLine.getY1geo(), myLine.getY2geo()), 2)
                                        )
                        );
            } else {
                myLine.setLength
                        (
                                Math.sqrt
                                        (
                                                Math.pow(Math.abs(myLine.getX2rect() - myLine.getX1rect()), 2)
                                                        +
                                                        Math.pow(Math.abs(myLine.getY2rect() - myLine.getY1rect()), 2)
                                        )
                        );
            }
            myPolygon.setPerimeter(myPolygon.getPerimeter() + myLine.getLength());
            myLine.getLine().setStrokeWidth(MainPageController.factor);
            myLine.getLine().setStroke(Color.BLUE);
            myPolygon.getMyLines().add(myLine);
            MainPageController.group.getChildren().add(myLine.getLine());
            countPointsPolygon = 0;
        }
    }

    public static void drawPolygon() {
        for (int i = 0; i < myPolygon.getMyLines().size(); i++) {
            myPolygon.getPolygon().getPoints().add(myPolygon.getMyLines().get(i).getLine().getStartX());
            myPolygon.getPolygon().getPoints().add(myPolygon.getMyLines().get(i).getLine().getStartY());
        }
        myPolygon.getPolygon().setStrokeWidth(MainPageController.factor);
        myPolygon.getPolygon().setStroke(Color.BLUE);
        myPolygon.getPolygon().setFill(Color.OLIVE);
        myPolygons.add(myPolygon);
        for (int i = 0; i < myPolygon.getMyLines().size(); i++)
            MainPageController.group.getChildren().remove(myPolygon.getMyLines().get(i).getLine());
        MainPageController.group.getChildren().add(myPolygon.getPolygon());

        TableController.myShape = new MyShape(0, "Многоугольник", 0.0, 0.0, myPolygon.getPerimeter());
        try {
            new DatabaseHandler().insertQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void redrawPolygon(boolean zoom) {
        if (myPolygons != null) {
            for (int i = 0; i < myPolygons.size(); i++) {
                MainPageController.group.getChildren().remove(myPolygons.get(i).getPolygon());
            }
            for (int i = 0; i < myPolygons.size(); i++) {
                for (int j = 0; j < myPolygons.get(i).getPolygon().getPoints().size(); j++) {
                    myPolygons.get(i).getPolygon().getPoints().set(j,
                            RectGeo.toRectForRedraw(myPolygons.get(i).getPolygon().getPoints().get(j), zoom));
                }
                myPolygons.get(i).getPolygon().setStrokeWidth(MainPageController.factor);
                MainPageController.group.getChildren().add(myPolygons.get(i).getPolygon());
            }
        }
    }
}
