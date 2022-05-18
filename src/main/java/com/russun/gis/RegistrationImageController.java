package com.russun.gis;

import com.russun.gis.utils.MyPoint;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static com.russun.gis.MainPageController.FACTOR;

public class RegistrationImageController {
    public static Stage registrationImageStage;
    public static File file;
    public static MyPoint myPoint;
    private static double baseImgHeight;
    private static double baseImgWidth;
    private MyPoint selectedMyPoint;
    private Image image;
    private double widthImg;
    private double heightImg;
    private double factor = 1.0;
    @FXML
    private ImageView scaleMapImageView;
    @FXML
    private TableView<MyPoint> pointsTableView;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Group mainGroup;
    private Group group;

    public void initialize() throws MalformedURLException {
        initImage();
        group = new Group();
        mainGroup.getChildren().add(group);
        MainPageController.myPoints = FXCollections.observableArrayList();
        initTableColumns();
        pointsTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MyPoint>() {
            @Override
            public void changed(ObservableValue<? extends MyPoint> observableValue, MyPoint oldPoint, MyPoint newPoint) {
                selectedMyPoint = newPoint;
            }
        });
        EventHandler<MouseEvent> eventHandler = new ImplEventHandlerMouse();
        scaleMapImageView.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
    }

    @FXML
    private void onClickDefaultBtn() {
        myPoint = new MyPoint("Точка 1", 55.715874, 49.189384,
                190, 188);
        MainPageController.myPoints.add(myPoint);

        myPoint = new MyPoint("Точка 2", 55.848740, 49.189384,
                471, 188);
        MainPageController.myPoints.add(myPoint);

        myPoint = new MyPoint("Точка 3", 55.718960, 49.151299,
                388, 747);
        MainPageController.myPoints.add(myPoint);

        myPoint = new MyPoint("Точка 4", 55.715874, 49.071148,
                190, 762);
        MainPageController.myPoints.add(myPoint);

        pointsTableView.setItems(MainPageController.myPoints);
        myPoint = null;
    }

    @FXML
    private void onClickDeleteBtn() {
        if (selectedMyPoint != null) {
            MainPageController.myPoints.remove(selectedMyPoint);
            pointsTableView.setItems(MainPageController.myPoints);
        }
    }

    @FXML
    private void onClickCancelBtn() {
        MainPageController.myPoints = null;
        registrationImageStage.close();
    }

    @FXML
    private void onClickOkBtn() {
        if (MainPageController.myPoints.size() == 4) {
            MyPoint.factorToGeoXTop =
                    (MainPageController.myPoints.get(1).getXgeo() - MainPageController.myPoints.get(0).getXgeo())
                            /
                            (MainPageController.myPoints.get(1).getXrect() - MainPageController.myPoints.get(0).getXrect());

            MyPoint.factorToGeoXBottom =
                    (MainPageController.myPoints.get(2).getXgeo() - MainPageController.myPoints.get(3).getXgeo())
                            /
                            (MainPageController.myPoints.get(2).getXrect() - MainPageController.myPoints.get(3).getXrect());

            MyPoint.factorToGeoYLeft =
                    (MainPageController.myPoints.get(0).getYgeo() - MainPageController.myPoints.get(3).getYgeo())
                            /
                            (MainPageController.myPoints.get(3).getYrect() - MainPageController.myPoints.get(0).getYrect());

            MyPoint.factorToGeoYRight =
                    (MainPageController.myPoints.get(1).getYgeo() - MainPageController.myPoints.get(2).getYgeo())
                            /
                            (MainPageController.myPoints.get(2).getYrect() - MainPageController.myPoints.get(1).getYrect());
            registrationImageStage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ГИС");
            alert.setHeaderText("Необходимо 4 точки");
            alert.showAndWait();
        }

    }

    @FXML
    private void onClickZoomInBtn() {
        widthImg *= FACTOR;
        heightImg *= FACTOR;
        scaleMapImageView.setFitWidth(widthImg);
        scaleMapImageView.setFitHeight(heightImg);

        factor *= FACTOR;
    }

    @FXML
    private void onClickZoomOutBtn() {
        if (widthImg > baseImgWidth) {
            widthImg /= FACTOR;
            heightImg /= FACTOR;
            scaleMapImageView.setFitWidth(widthImg);
            scaleMapImageView.setFitHeight(heightImg);
            factor /= FACTOR;

            if (widthImg < baseImgWidth) {
                widthImg = baseImgWidth;
                heightImg = baseImgHeight;
                scaleMapImageView.setFitWidth(baseImgWidth);
                scaleMapImageView.setFitHeight(baseImgHeight);
                factor = 1;
            }
        }
    }

    private void initImage() throws MalformedURLException {
        image = new Image(file.toURI().toURL().toString());
        widthImg = image.getWidth();
        heightImg = image.getHeight();
        //Подстраивание ширины избражения под окно
        double koef = widthImg / 598;
        widthImg = 598;
        heightImg = heightImg / koef;
        scaleMapImageView.setImage(image);
        scaleMapImageView.setFitWidth(widthImg);
        scaleMapImageView.setFitHeight(heightImg);
        baseImgHeight = heightImg;
        baseImgWidth = widthImg;
    }

    private void initTableColumns() {
        TableColumn<MyPoint, String> nameColumn = new TableColumn<>("Название");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pointsTableView.getColumns().add(nameColumn);

        TableColumn<MyPoint, Double> xColumn = new TableColumn<>("X");
        xColumn.setCellValueFactory(new PropertyValueFactory<>("xgeo"));
        pointsTableView.getColumns().add(xColumn);

        TableColumn<MyPoint, Double> yColumn = new TableColumn<>("Y");
        yColumn.setCellValueFactory(new PropertyValueFactory<>("ygeo"));
        pointsTableView.getColumns().add(yColumn);
    }

    private class ImplEventHandlerMouse implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (MainPageController.myPoints.size() == 4) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("ГИС");
                alert.setHeaderText("Необходимо только 4 точки.\nВы можете удалить существующие, если хотите добавить новые");
                alert.showAndWait();
                return;
            }
            myPoint = new MyPoint("Точка", 0.0, 0.0,
                    (int) Math.round(mouseEvent.getX() / factor), (int) Math.round(mouseEvent.getY() / factor));
            startAddingBreakpointStage();
            if (myPoint != null) {
                MainPageController.myPoints.add(myPoint);
                pointsTableView.setItems(MainPageController.myPoints);
                myPoint = null;
            }
        }

        private void startAddingBreakpointStage() {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("adding_breakpoint_view.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = new Stage();
            stage.setTitle("Добавить контрольную точку");
            stage.setScene(scene);

            AddingBreakpointController.addingBreakpointStage = stage;

            stage.showAndWait();
        }
    }
}

