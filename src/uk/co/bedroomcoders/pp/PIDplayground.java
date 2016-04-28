package uk.co.bedroomcoders.pp;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.animation.Timeline;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.animation.Animation;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DoubleStringConverter;
import javafx.scene.input.KeyCode;

public class PIDplayground extends Application implements EventHandler<KeyEvent> {

    private LineChart<Number, Number> chart;
    private NumberAxis xAxis; // only the xAxis need modifying to animate it
    private XYChart.Series<Number, Number> targetSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> pidSeries = new XYChart.Series<>();
    private Timeline animation;
    private double sequence = 0;
    private double y = 10;
    private double CP;

    private double target=0;
    private double pidVal;

    private final int MAX_DATA_POINTS = 128, MAX = 100, MIN = 0;

    private PID pid = new PID();

    private TextField Pinput = new TextField();
    private TextField Iinput = new TextField();
    private TextField Dinput = new TextField();

    public PIDplayground() {

        animation = new Timeline();
        animation.getKeyFrames()
        .add(new KeyFrame(Duration.millis(100),
                          (ActionEvent actionEvent) -> animate()));
        animation.setCycleCount(Animation.INDEFINITE);
    }

    private void animate() {
        target = Math.sin( ( (sequence/8.0) ) - ( Math.cos(sequence/12.0) ) ) * (CP *.8 )+ CP;
        pidVal += pid.value(target, pidVal, .1);
        targetSeries.getData().add(new XYChart.Data<Number, Number>(++sequence, target ));
        pidSeries.getData().add(new XYChart.Data<Number, Number>(sequence, pidVal ));

        // "scroll" axis
        if (sequence > MAX_DATA_POINTS - 1) {
            xAxis.setLowerBound(xAxis.getLowerBound() + 1);
            xAxis.setUpperBound(xAxis.getUpperBound() + 1);
        }

        // "scroll" data
        if (sequence > MAX_DATA_POINTS) {
            targetSeries.getData().remove(0);
            pidSeries.getData().remove(0);
        }
    }

    public Parent createContent() {

        xAxis = new NumberAxis(0, MAX_DATA_POINTS + 1, 3);
        final NumberAxis yAxis = new NumberAxis(MIN - 1, MAX + 1, 1);
        chart = new LineChart<>(xAxis, yAxis);

        chart.setAnimated(false);
        chart.setLegendVisible(false);
        chart.setTitle("PID and Target");
        xAxis.setLabel("X Axis (time)");
        yAxis.setLabel("Y Axis (value)");
        xAxis.setForceZeroInRange(false);

        targetSeries.setName("Target");
        pidSeries.setName("Pid");

        targetSeries.getData()
        .add(new XYChart.Data<Number, Number>(++sequence, y, 0));
        pidSeries.getData()
        .add(new XYChart.Data<Number, Number>(sequence, y, 0));

        chart.getData().add(targetSeries);
        chart.getData().add(pidSeries);

        VBox vbox = new VBox();
        vbox.setSpacing(8);

        vbox.getChildren().add(chart);
        vbox.getChildren().add(Pinput);
        vbox.getChildren().add(Iinput);
        vbox.getChildren().add(Dinput);

        VBox.setVgrow(chart, Priority.ALWAYS);

        return vbox;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        CP = (MAX-MIN)/2.0;

        Pinput.setText(pid.Kp.toString());
        Iinput.setText(pid.Ki.toString());
        Dinput.setText(pid.Kd.toString());

        final TextFormatter<Double> Pformatter = new TextFormatter<>(new DoubleStringConverter());
        final TextFormatter<Double> Iformatter = new TextFormatter<>(new DoubleStringConverter());
        final TextFormatter<Double> Dformatter = new TextFormatter<>(new DoubleStringConverter());
        Pinput.setTextFormatter(Pformatter);
        Iinput.setTextFormatter(Iformatter);
        Dinput.setTextFormatter(Dformatter);
        Pinput.setOnKeyPressed(this);
        Iinput.setOnKeyPressed(this);
        Dinput.setOnKeyPressed(this);

        Pinput.setText(pid.Kp.toString());
        Iinput.setText(pid.Ki.toString());
        Dinput.setText(pid.Kd.toString());

        primaryStage.setScene(new Scene(createContent(),800,600));
        
        primaryStage.setTitle("PID playground");
        primaryStage.show();
        animation.play();
    }


    public void handle(KeyEvent ev) {
        String c = ev.getText();

        try {
            if (ev.getCode()==KeyCode.ENTER) {
                if (ev.getSource()==Pinput) pid.Kp = Double.parseDouble(Pinput.getText());
                if (ev.getSource()==Iinput) pid.Ki = Double.parseDouble(Iinput.getText());
                if (ev.getSource()==Dinput) pid.Kd = Double.parseDouble(Dinput.getText());
            }
        } catch (Exception e) {
            // TODO!
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
