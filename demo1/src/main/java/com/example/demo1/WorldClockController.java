package com.example.demo1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldClockController {

    public AnchorPane mainPane;
    public Text clockLabel;
    public Label errlb;
    public Text clockLabel1;


    @FXML
    private TextField timeZoneField;

    @FXML
    private VBox bottomBox;

    @FXML
    private Button addButton;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @FXML
    private void initialize() {
        // Bắt đầu đồng hồ chính khi controller được khởi tạo
        startMainClock();

        // Thêm sự kiện cho nút addButton để thêm đồng hồ mới
        addButton.setOnAction(event -> addClock());
    }

    private void startMainClock() {
        executorService.execute(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
                    Platform.runLater(() -> {
                        // Hiển thị thời gian của đồng hồ chính trên clockLabel

                        clockLabel.setText("\n"+ now);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addClock() {
        String timeZoneId = timeZoneField.getText();
        if (!isValidTimeZone(timeZoneId)) {
            // Nếu múi giờ không hợp lệ, thông báo lỗi và không tạo đồng hồ mới
            errlb.setText("Múi Giờ Không Hợp Lệ");
            return;
        }

        BorderPane newClockPane = new BorderPane();
        Label newClockLabel = new Label();
        newClockPane.setCenter(newClockLabel);

        // Tạo cửa sổ mới để hiển thị đồng hồ mới
        Scene newScene = new Scene(newClockPane, 200, 100);
        Stage newStage = new Stage();
        newStage.setScene(newScene);
        newStage.setTitle("Đồng hồ " + timeZoneId);
        
        newStage.show();

        // Khởi tạo và chạy thread cho đồng hồ mới
        Thread newClockThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    LocalTime now = LocalTime.now(ZoneId.of(timeZoneId)).truncatedTo(ChronoUnit.SECONDS);
                    Platform.runLater(() -> newClockLabel.setText("Đồng hồ " + timeZoneId + ": " + now.toString()));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (DateTimeException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        newClockThread.setDaemon(true);
        newClockThread.start();
    }

    // Phương thức kiểm tra xem múi giờ có hợp lệ hay không
    private boolean isValidTimeZone(String timeZoneId) {
        try {
            ZoneId.of(timeZoneId);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

}
