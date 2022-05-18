package com.russun.gis;

import com.russun.gis.drawers.*;
import com.russun.gis.utils.DatabaseHandler;
import com.russun.gis.utils.MyPoint;
import com.russun.gis.utils.RectGeo;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class MainPageController {
    public static final double KM_PER_DEGREE_LATITUDE = 111.3;
    public static double FACTOR = 1.13;
    public static ObservableList<MyPoint> myPoints;

    public static double km_per_degree_longitude;
    public static Group group;
    public static double widthImg;
    public static double heightImg;
    public static double factor = 1.0;
    private static Stage primaryStage;
    private static double baseImgHeight;
    private static double baseImgWidth;
    private Image image;
    @FXML
    private ImageView mapImageView;
    @FXML
    private Group mainGroup;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private HBox imageContactButtonContainer;
    @FXML
    private VBox helloText;
    @FXML
    private MenuItem showFiguresBtn;

    public MainPageController() {
        try {
            new DatabaseHandler().cleanQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static double toXkm(double x1, double x2) {
        return Math.abs(x2 - x1) * km_per_degree_longitude;
    }

    public static double toYkm(double y1, double y2) {
        return Math.abs(y2 - y1) * KM_PER_DEGREE_LATITUDE;
    }

    public static double fromXkm(double xgeoRadius) {
        return xgeoRadius / km_per_degree_longitude;
    }

    public static double fromYkm(double ygeoRadius) {
        return ygeoRadius / KM_PER_DEGREE_LATITUDE;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        MainPageController.primaryStage = primaryStage;
    }

    @FXML
    private void onClickMenuFileItemOpen() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            group = new Group();

            image = new Image(file.toURI().toURL().toString());
            widthImg = image.getWidth();
            heightImg = image.getHeight();
            //Подстраивание ширины избражения под окно
            double koef = widthImg / (scrollPane.getWidth() - 16);
            widthImg = scrollPane.getWidth() - 16;
            heightImg = heightImg / koef;

            baseImgHeight = heightImg;
            baseImgWidth = widthImg;

            mapImageView.setImage(image);
            mapImageView.setFitWidth(widthImg);
            mapImageView.setFitHeight(heightImg);
            imageContactButtonContainer.setVisible(true);
            helloText.setVisible(false);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("ГИС");
            alert.setHeaderText("Зарегистрировать изображение?");
            alert.setContentText("Необходимо для работы с географическими координатами");
            ButtonType yes = new ButtonType("Да");
            ButtonType no = new ButtonType("Нет");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(yes, no);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == yes) {
                RegistrationImageController.file = file;
                startRegistrationImageStage();
                if (!(myPoints == null || myPoints.size() == 0)) {
                    double midLatitude =
                            myPoints.get(3).getYgeo()
                                    +
                                    ((myPoints.get(0).getYgeo() - myPoints.get(3).getYgeo()) / 2);
                    km_per_degree_longitude = KM_PER_DEGREE_LATITUDE * Math.cos(midLatitude / 180 * Math.PI);
                }
            }
            mainGroup.getChildren().add(group);
            EventHandler<MouseEvent> eventHandler = new ImplEventHandlerMouse();
            mapImageView.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
        }
    }

    @FXML
    private void onClickMenuDataItemTable() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("table_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Таблица");
        stage.setScene(scene);

        TableController.tableStage = stage;

        stage.showAndWait();
    }

    @FXML
    private void onClickMenuDataItemSql() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sql_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Пользовательский SQL запрос");
        stage.setScene(scene);

        SqlController.sqlStage = stage;

        stage.showAndWait();

    }

    @FXML
    private void onClickZoomInBtn() {
        widthImg *= FACTOR;
        heightImg *= FACTOR;
        mapImageView.setFitWidth(widthImg);
        mapImageView.setFitHeight(heightImg);

        factor *= FACTOR;

        redraw(true);
    }

    @FXML
    private void onClickZoomOutBtn() {
        if (widthImg > baseImgWidth) {
            widthImg /= FACTOR;
            heightImg /= FACTOR;
            mapImageView.setFitWidth(widthImg);
            mapImageView.setFitHeight(heightImg);
            factor /= FACTOR;

            if (widthImg < baseImgWidth) {
                widthImg = baseImgWidth;
                heightImg = baseImgHeight;
                mapImageView.setFitWidth(baseImgWidth);
                mapImageView.setFitHeight(baseImgHeight);
                factor = 1;
            }

            redraw(false);
        }
    }

    @FXML
    private void onClickDefaultBtn() {
        if (widthImg > baseImgWidth) {
            widthImg = baseImgWidth;
            heightImg = baseImgHeight;
            mapImageView.setFitWidth(baseImgWidth);
            mapImageView.setFitHeight(baseImgHeight);
            FACTOR = factor;
            factor = 1;
            redraw(false);
            FACTOR = 1.13;
        }
    }

    @FXML
    private void onClickLineBtn() {
        DrMyLine.countPointsLine += 1;
    }

    @FXML
    private void onClickEllipseBtn() {
        DrMyEllipse.countPointsEllipse += 1;
    }

    @FXML
    private void onClickRectangleBtn() {
        DrMyRectangle.countPointsRectangle += 1;
    }

    @FXML
    private void onClickPolygonBtn() {
        DrMyPolygon.countPointsPolygon += 1;
    }

    @FXML
    private void onClickZonaBtn() {
        DrMyZona.countPointsZona += 1;
    }

    @FXML
    private void onClickShowFiguresBtn() {
        if (showFiguresBtn.getText().equals("Скрыть")) {
            group.setVisible(false);
            showFiguresBtn.setText("Показать");
        } else {
            group.setVisible(true);
            showFiguresBtn.setText("Скрыть");
        }
    }

    private void startRegistrationImageStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("registration_image_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Регистрация изображения");
        stage.setScene(scene);

        RegistrationImageController.registrationImageStage = stage;

        stage.showAndWait();
    }

    private void startZonaStage(double x, double y) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("zona_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Зона заражения");
        stage.setScene(scene);

        ZonaController.zonaStage = stage;
        ZonaController.xcenter = x;
        ZonaController.ycenter = y;

        stage.showAndWait();
    }

    private void redraw(boolean zoom) {
        DrMyLine.redrawLine(zoom);
        DrMyEllipse.redrawEllipse(zoom);
        DrMyRectangle.redrawRectangle(zoom);
        DrMyPolygon.redrawPolygon(zoom);
        DrMyZona.redrawZona(zoom);
    }

    private class ImplEventHandlerMouse implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();

            if (DrMyLine.countPointsLine != 0)
                DrMyLine.drawLine(x, y);
            else if (DrMyEllipse.countPointsEllipse != 0)
                DrMyEllipse.drawEllipse(x, y);
            else if (DrMyRectangle.countPointsRectangle != 0)
                DrMyRectangle.drawRectangle(x, y);
            else if (DrMyPolygon.countPointsPolygon != 0) {
                DrMyPolygon.drawLineForPolygon(x, y);
                if (DrMyPolygon.countPointsPolygon == 0) {
                    if (Math.abs(DrMyPolygon.myPolygon
                            .getMyLines().get(0).getLine().getStartX() - x) <= 2
                            && Math.abs(DrMyPolygon.myPolygon
                            .getMyLines().get(0).getLine().getStartY() - y) <= 2) {
                        DrMyPolygon.drawPolygon();
                        DrMyPolygon.countPointsPolygon = 0;
                    } else {
                        DrMyPolygon.countPointsPolygon += 2;
                        DrMyPolygon.drawLineForPolygon(x, y);
                    }
                }
            } else if (DrMyZona.countPointsZona != 0) {
                try {
                    startZonaStage(x, y);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                RectGeo.determineCoordsOfPoint(mouseEvent);
        }
    }
}