package FiSy;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

class animation extends Transition {

    private final Interpolator WEB_EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
    static Timeline timeline = new Timeline();
    public static Timeline timeline2 = new Timeline();
    private final Node node;
    private boolean oldCache = false;
    private CacheHint oldCacheHint = CacheHint.DEFAULT;
    private final boolean useCache = true;
    private final double xIni;
    private final DoubleProperty x = new SimpleDoubleProperty();

    private static Interpolator animInte = new Interpolator() {
        private double val(double t, double sx, double ex, double maxVal) {
            double v = (t - sx) * (ex - t);
            double max = (ex - sx) / 2;
            return maxVal * v / (max * max);
        }

        @Override
        protected double curve(double t) {
            double x;
            if (t <= 0.37) {
                x = val(t, -0.37, 0.37, 1);
            } else if (t <= 0.73) {
                x = val(t, 0.37, 0.73, 0.25);
            } else if (t <= 0.91) {
                x = val(t, 0.73, 0.91, 0.08);
            } else {
                x = val(t, 0.91, 1, 0.03);
            }
            return 1 - x;
        }

    };

    private static Interpolator animInte2 = new Interpolator() {
        @Override
        protected double curve(double t) {
            double s = 0.075D;
            return Math.pow(2.0D,-10.0D * t) * Math.sin((t - s) * 6.283185307179586D / 0.3D)+1.0D;
        }
    };
    public static boolean SlideDown(Class clas, Pane mainPane, String FXML){return Slide(clas,mainPane,FXML,0);}
    public static boolean SlideRight(Class clas, Pane mainPane, String FXML){return Slide(clas,mainPane,FXML,1);}
    public static boolean SlideUp(Class clas, Pane mainPane, String FXML){return Slide(clas,mainPane,FXML,2);}
    public static boolean SlideLeft(Class clas, Pane mainPane, String FXML){return Slide(clas,mainPane,FXML, 3);}

    private static boolean Slide(Class clas, Pane mainPane,String FXML, int type){
    if (timeline2.getStatus() == Status.RUNNING) return false;
    int milis = 700;
        timeline2 = new Timeline();
        Pane pane2;
        final boolean[] sizeTrue = {true,true};
        final int[] i = {0};

        WritableValue<Double> writableWidth = new WritableValue<Double>() {
            @Override
            public Double getValue() {
                if (sizeTrue[0]) {
                    sizeTrue[0] = false;
                    return mainPane.getScene().getWindow().getWidth();
                } else return null;
            }

            @Override
            public void setValue(Double value) {
                if (sizeTrue[0]){
                    sizeTrue[0]=false;
                    mainPane.getScene().getWindow().setWidth(value);
        }}};

        WritableValue<Double> writableHeight = new WritableValue<Double>() {
            @Override
            public Double getValue() {
                if (sizeTrue[1]) {
                    sizeTrue[1] = false;
                    return mainPane.getScene().getWindow().getHeight();
                } else return null;
            }

            @Override
            public void setValue(Double value) {
                if (sizeTrue[1]){
                    sizeTrue[1]=false;
                    mainPane.getScene().getWindow().setHeight(value);
                }}
        };


        try {
            pane2 = FXMLLoader.load(clas.getResource(FXML));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        EventHandler finish = (EventHandler<ActionEvent>) t -> {
            mainPane.getScene().getWindow().setHeight(pane2.getPrefHeight()+getTrueWindowHeightBorder(pane2)+((Pane) mainPane.getChildren().get(0)).getHeight());
            mainPane.getScene().getWindow().setWidth(pane2.getPrefWidth()+getTrueWindowWidthBorder(pane2));
            ((Pane)mainPane.getChildren().get(1)).getChildren().remove(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 2);

        };
        KeyFrame key1 = null;
        Interpolator interpolator = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
        Interpolator interpolator2 = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);

        switch (type){
            case 0:{
                ((Pane)mainPane.getChildren().get(1)).getChildren().addAll(pane2);
                pane2.setOpacity(0);
                pane2.layoutYProperty().setValue(0+pane2.getPrefHeight());
                key1 = new KeyFrame(Duration.millis(milis),
                        //new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 2).layoutYProperty(), ((Pane)mainPane.getChildren().get(1)).getPrefWidth(),  interpolator),
                        new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 1).layoutYProperty(), 0, interpolator),
                        new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 2).opacityProperty(), 0, interpolator),
                        new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 1).opacityProperty(), 1, interpolator),
                        new KeyValue(writableWidth,pane2.getPrefWidth()+getTrueWindowWidthBorder(pane2),interpolator2),
                        new KeyValue(writableHeight,pane2.getPrefHeight()+getTrueWindowHeightBorder(pane2)+((Pane) mainPane.getChildren().get(0)).getHeight(),interpolator2));
                        //new KeyValue(((Pane)mainPane.getChildren().get(0)).prefWidthProperty(),pane2.getPrefWidth()+getTrueWindowWidthBorder(pane2),interpolator2));
            }break;

            case 1:{
                ((Pane)mainPane.getChildren().get(1)).getChildren().addAll(pane2);
                pane2.setOpacity(0);
                pane2.layoutXProperty().setValue(0-pane2.getPrefWidth());
                    key1 = new KeyFrame(Duration.millis(milis),
                    new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 2).layoutXProperty(), ((Pane)mainPane.getChildren().get(1)).getPrefWidth(),  interpolator),
                    new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 1).layoutXProperty(), 0, interpolator),
                            new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 2).opacityProperty(), 0,  interpolator),
                            new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 1).opacityProperty(), 1, interpolator),
                    new KeyValue(writableWidth,pane2.getPrefWidth()+getTrueWindowWidthBorder(pane2),interpolator2),
                    new KeyValue(writableHeight,pane2.getPrefHeight()+getTrueWindowHeightBorder(pane2)+((Pane) mainPane.getChildren().get(0)).getHeight(),interpolator2));
                    //new KeyValue(((Pane)mainPane.getChildren().get(0)).prefWidthProperty(),pane2.getPrefWidth()+getTrueWindowWidthBorder(pane2),interpolator2));
        }break;

            case 2:{
                ((Pane)mainPane.getChildren().get(1)).getChildren().addAll(pane2);
                //pane2.layoutXProperty().setValue(((Pane)mainPane.getChildren().get(1)).getPrefWidth());
                pane2.setOpacity(0);
                key1 = new KeyFrame(Duration.millis(milis),
                        new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 2).layoutYProperty(), 0+((Pane)mainPane.getChildren().get(1)).getPrefWidth(),interpolator),
                        //new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 1).layoutXProperty(), 0,interpolator),
                        new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 2).opacityProperty(), 0,  interpolator),
                        new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 1).opacityProperty(), 1, interpolator),
                        new KeyValue(writableWidth,pane2.getPrefWidth()+getTrueWindowWidthBorder(pane2),interpolator2),
                        new KeyValue(writableHeight,pane2.getPrefHeight()+getTrueWindowHeightBorder(pane2)+((Pane) mainPane.getChildren().get(0)).getHeight(),interpolator2));
                //  new KeyValue(((Pane)mainPane.getChildren().get(0)).prefWidthProperty(),pane2.getPrefWidth()+getTrueWindowWidthBorder(pane2),interpolator2));
            }break;
            case 3:
         {
             ((Pane)mainPane.getChildren().get(1)).getChildren().addAll(pane2);
             pane2.layoutXProperty().setValue(((Pane)mainPane.getChildren().get(1)).getPrefWidth());
             pane2.setOpacity(0);
            key1 = new KeyFrame(Duration.millis(milis),
                    new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 2).layoutXProperty(), 0-((Pane)mainPane.getChildren().get(1)).getPrefWidth(),interpolator),
                    new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 1).layoutXProperty(), 0,interpolator),
                    new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 2).opacityProperty(), 0,  interpolator),
                    new KeyValue(((Pane)mainPane.getChildren().get(1)).getChildren().get(((Pane)mainPane.getChildren().get(1)).getChildren().size() - 1).opacityProperty(), 1, interpolator),
                    new KeyValue(writableWidth,pane2.getPrefWidth()+getTrueWindowWidthBorder(pane2),interpolator2),
                    new KeyValue(writableHeight,pane2.getPrefHeight()+getTrueWindowHeightBorder(pane2)+((Pane) mainPane.getChildren().get(0)).getHeight(),interpolator2));
                   // new KeyValue(((Pane)mainPane.getChildren().get(0)).prefWidthProperty(),pane2.getPrefWidth()+getTrueWindowWidthBorder(pane2),interpolator2));
        }break;

        }
        timeline2.setOnFinished(finish);
        timeline2.getKeyFrames().add(key1);
        timeline2.play();

        Timer animTimer = new Timer();
        animTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                i[0] = i[0] +1;
                //System.out.println(i[0]);
                sizeTrue[0]=true;
                sizeTrue[1]=true;
                if (timeline2.getStatus() == Status.STOPPED){
                    animTimer.cancel();
                    animTimer.purge();
                }
            }

        }, 0,16);

        //motherPane.getScene().getWindow().setHeight(1000);
        return true;
    }

    public static void toogleNode(Node node, double opacity){toogleNode(node, opacity, true);}
    public static void toogleNode(Node node, double opacity, boolean disable){
            if (opacity != 1 && disable){
                node.setDisable(true);
                node.setOpacity(node.getOpacity()); }
            Timeline timeline1 = new Timeline();
            KeyFrame key = new KeyFrame(Duration.millis(300), new KeyValue(node.opacityProperty(), opacity, Interpolator.SPLINE(0.25, 0.1, 0.25, 1)));
            EventHandler finish = (EventHandler<ActionEvent>) t -> {
                if (opacity == 1) node.setDisable(false);
            };
            timeline1.getKeyFrames().add(key);
            timeline1.setOnFinished(finish);
            timeline1.play();
    }

    public static double getTrueWindowWidthBorder(Node node){ return node.getScene().getWindow().getWidth()-node.getScene().getWidth(); }
    public static double getTrueWindowHeightBorder(Node node){ return node.getScene().getWindow().getHeight()-node.getScene().getHeight(); }

    public animation(final Node node, EventHandler<ActionEvent> event) {
        this.node = node;
        statusProperty().addListener((ov, t, newStatus) -> {
            switch (newStatus) {
                case RUNNING:
                    starting();
                    break;
                default:
                    stopping();
                    break;
            }
        });
        this.timeline = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(x, 0, WEB_EASE)),
                new KeyFrame(Duration.millis(100), new KeyValue(x, -10, WEB_EASE)),
                new KeyFrame(Duration.millis(200), new KeyValue(x, 10, WEB_EASE)),
                new KeyFrame(Duration.millis(300), new KeyValue(x, -10, WEB_EASE)),
                new KeyFrame(Duration.millis(400), new KeyValue(x, 10, WEB_EASE)),
                new KeyFrame(Duration.millis(500), new KeyValue(x, -10, WEB_EASE)),
                new KeyFrame(Duration.millis(600), new KeyValue(x, 10, WEB_EASE)),
                new KeyFrame(Duration.millis(700), new KeyValue(x, -10, WEB_EASE)),
                new KeyFrame(Duration.millis(800), new KeyValue(x, 10, WEB_EASE)),
                new KeyFrame(Duration.millis(900), new KeyValue(x, -10, WEB_EASE)),
                new KeyFrame(Duration.millis(1000), new KeyValue(x, 0, WEB_EASE))
        );
        xIni = node.getScene().getWindow().getX();
        x.addListener((ob, n, n1) -> (node.getScene().getWindow()).setX(xIni + n1.doubleValue()));
        setCycleDuration(Duration.seconds(1));
        setDelay(Duration.seconds(0.2));
        setOnFinished(event);
    }

    protected final void starting() {
        if (useCache) {
            oldCache = node.isCache();
            oldCacheHint = node.getCacheHint();
            node.setCache(true);
            node.setCacheHint(CacheHint.SPEED);
        }
    }

    protected final void stopping() {
        if (useCache) {
            node.setCache(oldCache);
            node.setCacheHint(oldCacheHint);
        }
    }

    @Override
    protected void interpolate(double d) {
        timeline.playFrom(Duration.seconds(d));
        timeline.stop();
    }

    static void fadeShake(Node node){
        FadeTransition ft = new FadeTransition(Duration.millis(100), node);
        ft.setFromValue(1.0);
        ft.setToValue(0);
        ft.setCycleCount(4);
        ft.setAutoReverse(true);
        ft.play();
    }
    }