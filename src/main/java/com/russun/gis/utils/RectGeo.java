package com.russun.gis.utils;

import com.russun.gis.MainPageController;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;

public class RectGeo {
    public static void determineCoordsOfPoint(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ГИС");
        int xrect = toRect(mouseEvent.getX());
        int yrect = toRect(mouseEvent.getY());
        if (MainPageController.myPoints == null || MainPageController.myPoints.size() == 0) {
            alert.setHeaderText("Координаты точки в прямоугольной системе координат");
            alert.setContentText("x = " + xrect + "; y = " + yrect);
        } else {
            double[] xygeo = toGeo(xrect, yrect);
            double xgeo = xygeo[0];
            double ygeo = xygeo[1];
            alert.setHeaderText("Координаты точки в географической системе координат");
            alert.setContentText("x = " + xgeo + "; y = " + ygeo);
        }
        alert.showAndWait();
    }

    public static int toRect(double n) {
        return (int) Math.round(n / MainPageController.factor);
    }

    public static double[] toGeo(int xrect, int yrect) {
        double xgeo;
        double ygeo;

        if (xrect < MainPageController.widthImg / 2 && yrect < MainPageController.heightImg / 2) {
            xgeo = MainPageController.myPoints.get(0).getXgeo() + ((xrect - MainPageController.myPoints.get(0).getXrect()) * MyPoint.factorToGeoXTop);
            ygeo = MainPageController.myPoints.get(0).getYgeo() - ((yrect - MainPageController.myPoints.get(0).getYrect()) * MyPoint.factorToGeoYLeft);
        } else if (xrect > MainPageController.widthImg / 2 && yrect < MainPageController.heightImg / 2) {
            xgeo = MainPageController.myPoints.get(0).getXgeo() + ((xrect - MainPageController.myPoints.get(0).getXrect()) * MyPoint.factorToGeoXTop);
            ygeo = MainPageController.myPoints.get(0).getYgeo() - ((yrect - MainPageController.myPoints.get(1).getYrect()) * MyPoint.factorToGeoYRight);
        } else if (xrect > MainPageController.widthImg / 2 && yrect > MainPageController.heightImg / 2) {
            xgeo = MainPageController.myPoints.get(0).getXgeo() + ((xrect - MainPageController.myPoints.get(3).getXrect()) * MyPoint.factorToGeoXBottom);
            ygeo = MainPageController.myPoints.get(0).getYgeo() - ((yrect - MainPageController.myPoints.get(1).getYrect()) * MyPoint.factorToGeoYRight);
        } else {
            xgeo = MainPageController.myPoints.get(0).getXgeo() + ((xrect - MainPageController.myPoints.get(3).getXrect()) * MyPoint.factorToGeoXBottom);
            ygeo = MainPageController.myPoints.get(0).getYgeo() - ((yrect - MainPageController.myPoints.get(0).getYrect()) * MyPoint.factorToGeoYLeft);
        }

        return new double[]{xgeo, ygeo};
    }

    public static double toRectForRedraw(double n, boolean zoom) {
        if (zoom)
            return n * MainPageController.FACTOR;
        else
            return n / MainPageController.FACTOR;
    }

    public static int[] fromGeo(double xgeo, double ygeo) {
        int xrect;
        int yrect;

        if (xgeo < (MainPageController.myPoints.get(0).getXgeo()
                + (MainPageController.myPoints.get(1).getXgeo() - MainPageController.myPoints.get(0).getXgeo()) / 2)
                && ygeo > (MainPageController.myPoints.get(0).getYgeo()
                - (MainPageController.myPoints.get(0).getYgeo() - MainPageController.myPoints.get(3).getYgeo()) / 2)) {
            xrect = (int) Math.round(MainPageController.myPoints.get(0).getXrect() + ((xgeo - MainPageController.myPoints.get(0).getXgeo()) / MyPoint.factorToGeoXTop));
            yrect = (int) Math.round(MainPageController.myPoints.get(0).getYrect() + ((MainPageController.myPoints.get(0).getYgeo() - ygeo) / MyPoint.factorToGeoYLeft));
        } else if (xgeo > (MainPageController.myPoints.get(0).getXgeo()
                + (MainPageController.myPoints.get(1).getXgeo() - MainPageController.myPoints.get(0).getXgeo()) / 2)
                && ygeo > (MainPageController.myPoints.get(0).getYgeo()
                - (MainPageController.myPoints.get(0).getYgeo() - MainPageController.myPoints.get(3).getYgeo()) / 2)) {
            xrect = (int) Math.round(MainPageController.myPoints.get(0).getXrect() + ((xgeo - MainPageController.myPoints.get(0).getXgeo()) / MyPoint.factorToGeoXTop));
            yrect = (int) Math.round(MainPageController.myPoints.get(0).getYrect() + ((MainPageController.myPoints.get(1).getYgeo() - ygeo) / MyPoint.factorToGeoYRight));
        } else if (xgeo > (MainPageController.myPoints.get(0).getXgeo()
                + (MainPageController.myPoints.get(1).getXgeo() - MainPageController.myPoints.get(0).getXgeo()) / 2)
                && ygeo < (MainPageController.myPoints.get(1).getYgeo()
                - (MainPageController.myPoints.get(1).getYgeo() - MainPageController.myPoints.get(2).getYgeo()) / 2)) {
            xrect = (int) Math.round(MainPageController.myPoints.get(0).getXrect() + ((xgeo - MainPageController.myPoints.get(3).getXgeo()) / MyPoint.factorToGeoXBottom));
            yrect = (int) Math.round(MainPageController.myPoints.get(0).getYrect() + ((MainPageController.myPoints.get(1).getYgeo() - ygeo) / MyPoint.factorToGeoYRight));
        } else {
            xrect = (int) Math.round(MainPageController.myPoints.get(0).getXrect() + ((xgeo - MainPageController.myPoints.get(3).getXgeo()) / MyPoint.factorToGeoXBottom));
            yrect = (int) Math.round(MainPageController.myPoints.get(0).getYrect() + ((MainPageController.myPoints.get(0).getYgeo() - ygeo) / MyPoint.factorToGeoYLeft));
        }

        return new int[]{xrect, yrect};
    }

    public static double fromRect(int n) {
        return n * MainPageController.factor;
    }
}
