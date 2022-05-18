package com.russun.gis.shapes;

import com.russun.gis.MainPageController;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Ellipse;

import java.util.ArrayList;
import java.util.Optional;

public class MyZone {
    private static final ArrayList<MyZone> myZones = new ArrayList<>();

    private Ellipse ellipse;

    private double xgeoCenter;
    private double ygeoCenter;
    private double xgeoRadius;
    private double ygeoRadius;

    private double geoSquare;

    public MyZone(Ellipse ellipse) {
        this.ellipse = ellipse;
        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("ГИС");
                alert.setHeaderText("Информация о зоне заражения в географической системе координат");
                alert.setContentText("Центральный x = " + xgeoCenter + "; Центральный y = " + ygeoCenter
                        + "\nГлубина зоны заражения = " + xgeoRadius + " км");
                ButtonType ok = new ButtonType("ОК");
                ButtonType delete = new ButtonType("Удалить");
                alert.getButtonTypes().clear();
                alert.getButtonTypes().addAll(ok, delete);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == delete) remove();
            }
        };
        this.ellipse.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
    }

    public static ArrayList<MyZone> getMyZones() {
        return myZones;
    }

    public void remove() {
        MainPageController.group.getChildren().remove(ellipse);
        myZones.remove(MyZone.this);
    }

    public Ellipse getEllipse() {
        return ellipse;
    }

    public void setEllipse(Ellipse ellipse) {
        this.ellipse = ellipse;
    }

    public double getXgeoCenter() {
        return xgeoCenter;
    }

    public void setXgeoCenter(double xgeoCenter) {
        this.xgeoCenter = xgeoCenter;
    }

    public double getYgeoCenter() {
        return ygeoCenter;
    }

    public void setYgeoCenter(double ygeoCenter) {
        this.ygeoCenter = ygeoCenter;
    }

    public double getXgeoRadius() {
        return xgeoRadius;
    }

    public void setXgeoRadius(double xgeoRadius) {
        this.xgeoRadius = xgeoRadius;
    }

    public double getYgeoRadius() {
        return ygeoRadius;
    }

    public void setYgeoRadius(double ygeoRadius) {
        this.ygeoRadius = ygeoRadius;
    }

    public double getGeoSquare() {
        return geoSquare;
    }

    public void setGeoSquare(double geoSquare) {
        this.geoSquare = geoSquare;
    }
}
