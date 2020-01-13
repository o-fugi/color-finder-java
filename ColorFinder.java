import javafx.scene.control.Slider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.lang.NullPointerException;

public class ColorFinder extends Application
{
    private Stage primary;
    private TextField hex;
    private Slider sliderRed;
    private Slider sliderBlue;
    private Slider sliderGreen;
    private VBox display;
    Scene scene;

    public ColorFinder()
    {
        hex = new TextField("#000000");
        hex.setEditable(true);
        hex.setOnAction( new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                try {
                    Color hexColor = validate(hex.getText());
                    hex.setStyle("-fx-control-inner-background:#FFFFFF;");
                    sliderRed.adjustValue(hexColor.getRed() * 255);
                    sliderGreen.adjustValue(hexColor.getGreen() * 255);
                    sliderBlue.adjustValue(hexColor.getBlue() * 255);
                }
                catch(NullPointerException ex) {
                    hex.setStyle("-fx-control-inner-background:#FA8072;");
                };
            }
        });
        sliderRed = new Slider(0, 255, 0);
        sliderGreen = new Slider(0, 255, 0);
        sliderBlue = new Slider(0, 255, 0);
    }

    @Override
    public void start(Stage primary)
    {
        this.primary = primary;

        BorderPane bp = new BorderPane();
        HBox words = initLabels();

        HBox sliders = new HBox();
        sliders.getChildren().addAll(sliderRed, sliderGreen, sliderBlue, hex);

        VBox topPart = new VBox();
        topPart.getChildren().addAll(words, sliders);
        bp.setTop(topPart);

        display = new VBox();
        bp.setCenter(display);

        scene = new Scene(bp, 700,500);

        Thread thread = initThread();
        thread.setDaemon(true);
        thread.start();

        primary.setTitle("ColorFinder");
        primary.setScene(scene);
        primary.show();
    }

    private HBox initLabels()
    {
        HBox words = new HBox();
        Label redLabel = new Label("red");
        redLabel.setTextFill(Color.RED);
        Label greenLabel = new Label("green");
        greenLabel.setTextFill(Color.GREEN);
        Label blueLabel = new Label("blue");
        redLabel.setTextFill(Color.BLUE);
        Label hexLabel = new Label("hex code");
        words.getChildren().addAll(redLabel, greenLabel, blueLabel, hexLabel);
        return words;
    }

    private Thread initThread()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable updater = new Runnable() {
                    @Override
                    public void run() {
                        String hexValue = String.format("#%02x%02x%02x", (int) sliderRed.getValue(), (int) sliderGreen.getValue(), (int) sliderBlue.getValue());
                        Node fo = scene.getFocusOwner();
                        if (!(fo instanceof TextField)) {
                            hex.setText(hexValue);
                        }
                        display.setStyle("-fx-background-color:" + hexValue + ";");
                        // sliderRed.adjustValue(3);
                        //int red = sliderRed.getValue();
                    }
                };
                while (true) {
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException ex) {
                    }
                    // UI update is run on the Application thread
                    Platform.runLater(updater);
                }
            }
        });
        return thread;
    }

    private Color validate(String input)
    {
        try {
            Color returnColor = Color.valueOf(input);
            return returnColor;
        }
        catch(IllegalArgumentException e) {
            return null;
        }
    }
}
