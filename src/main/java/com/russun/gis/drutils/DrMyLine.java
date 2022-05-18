package com.russun.gis.drutils;

import com.russun.gis.MainPageController;
import com.russun.gis.TableController;
import com.russun.gis.shapes.MyLine;
import com.russun.gis.utils.DatabaseHandler;
import com.russun.gis.utils.MyShape;
import com.russun.gis.utils.RectGeo;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.sql.SQLException;
import java.util.ArrayList;

public class DrMyLine {
    public static int countPointsLine;

    private static MyLine myLine;

    public static void drawLine(double x, double y) {
        if (countPointsLine == 1) {
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
            countPointsLine += 1;
        } else {
            ArrayList<MyLine> myLines = MyLine.getMyLines();
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
            myLine.getLine().setStrokeWidth(MainPageController.factor);
            myLine.getLine().setStroke(Color.ORANGE);
            myLines.add(myLine);
            MainPageController.group.getChildren().add(myLine.getLine());
            countPointsLine = 0;

            TableController.myShape = new MyShape(0, "Линия", myLine.getLength(), 0.0, 0.0);
            try {
                new DatabaseHandler().insertQuery();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void redrawLine(boolean zoom) {
        ArrayList<MyLine> myLines = MyLine.getMyLines();
        if (myLines != null) {
            for (int i = 0; i < myLines.size(); i++)
                MainPageController.group.getChildren().remove(myLines.get(i).getLine());
            for (int i = 0; i < myLines.size(); i++) {
                myLines.get(i).getLine().setStartX(RectGeo.toRectForRedraw(myLines.get(i).getLine().getStartX(), zoom));
                myLines.get(i).getLine().setStartY(RectGeo.toRectForRedraw(myLines.get(i).getLine().getStartY(), zoom));
                myLines.get(i).getLine().setEndX(RectGeo.toRectForRedraw(myLines.get(i).getLine().getEndX(), zoom));
                myLines.get(i).getLine().setEndY(RectGeo.toRectForRedraw(myLines.get(i).getLine().getEndY(), zoom));
                myLines.get(i).getLine().setStrokeWidth(MainPageController.factor);

                MainPageController.group.getChildren().add(myLines.get(i).getLine());
            }
        }
    }
}
