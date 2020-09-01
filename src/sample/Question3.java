package sample;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

class Edge {

    //variables of class Edge
    protected double cost;
    protected Pair<String, String> pair;

    //Constructor to be called in subclasses
    Edge(double cost, String v1, String v2) {
        this.cost = cost;
        this.pair = new Pair<>(v1, v2);
    }

    //getter methods
    public String getOtherVertex(String name) {
        String v1 = this.pair.getKey();
        String v2 = this.pair.getValue();
        if (v1.equals(name)) {
            return v2;
        } else {
            return v1;
        }
    }

    public double getCost() {
        return (double) this.cost;
    }

    public Pair<String, String> getPair() {
        return pair;
    }

    public void setCost(double cost) {
        this.cost = (double) cost;
    }

}

class Vertex {
    private String name;
    private double xcoordinate, ycoordinate;
    private LinkedList<Edge> list = new LinkedList<Edge>();

    Vertex(String name, double xcoordinate, double ycoordinate) {
        this.name = name;
        this.xcoordinate = xcoordinate;
        this.ycoordinate = ycoordinate;
    }

    //getter method
    public String getName() {
        return this.name;
    }

    public LinkedList<Edge> getList() {
        return this.list;
    }

    public double getXcoordinate() {
        return xcoordinate;
    }

    public double getYcoordinate() {
        return ycoordinate;
    }

    public void setXcoordinate(double d) {
        this.xcoordinate = d;
    }

    public void setYcoordinate(double d) {
        this.ycoordinate = d;
    }

    public void addEdge(Edge e) {
        int position = 0;
        Edge current_edge;
        Iterator<Edge> itr = list.iterator();
        while (itr.hasNext()) {
            current_edge = itr.next();
            if (current_edge.getCost() > e.getCost()) {
                list.add(position, e);
                return;
            }
            position++;
        }
        list.add(e);
    }

    public Edge findEdge(String vertex) {
        try {
            Iterator<Edge> itr = list.iterator();
            Edge e;
            while (itr.hasNext()) {
                e = itr.next();
                if (e.getOtherVertex(name).equals(vertex)) {
                    System.out.println("Found Edge " + e.getPair());
                    return e;
                }
            }
            System.out.println("NOT FOUND EDGE IN Vertex: " + name);
        } catch (Exception ex) {
            System.out.println("Exception in findEdge");
        }
        return null;
    }

    public void removeEdge(Edge e1) {
        for (Edge e : list) {
            if (e.getOtherVertex(name).equals(e1.getOtherVertex(name))) {
                list.remove(e);
                return;
            }
        }
    }

    public void removeEdge(String vertex) {
        list.remove(findEdge(vertex));
    }

    public String chooseVertex(TreeMap<String, Boolean> status, TreeMap<String, Double> cost) {
        double min_cost = 100000000;
        String chosen_vertex = "";

        for (Map.Entry<String, Double> entry : cost.entrySet()) {
            if (!status.get(entry.getKey())) {
                if (entry.getValue() < min_cost) {
                    min_cost = entry.getValue();
                    chosen_vertex = entry.getKey();
                }
            }
        }
        status.put(chosen_vertex, true);

        return chosen_vertex;
    }

    public TreeMap<String, Edge> getPath(TreeMap<String, Vertex> vertex_map, String vertex1, String vertex2) {
        int V = vertex_map.size();

        TreeMap<String, Boolean> status = new TreeMap<>();
        TreeMap<String, Edge> parent = new TreeMap<>();
        TreeMap<String, Double> cost = new TreeMap<>();

        for (Map.Entry<String, Vertex> entry : vertex_map.entrySet()) {
            status.put(entry.getKey(), false);
            parent.put(entry.getKey(), null);
            cost.put(entry.getKey(), 100000.0);
        }
        cost.put(vertex1, 0.0);
        String current_vertex;

        for (int i = 0; i < V; i++) {
            current_vertex = chooseVertex(status, cost);
            Vertex currentVertex = vertex_map.get(current_vertex);

            if (current_vertex.equals(vertex2)) {
                break;
            }

            for (Edge e : currentVertex.getList()) {
                String new_vertex = e.getOtherVertex(current_vertex);
//                Vertex newVertex = vertex_map.get(new_vertex);

                if (status.get(new_vertex)) {
                    continue;
                }
                double newCost = cost.get(current_vertex) + e.getCost();
                if (newCost < cost.get(new_vertex)) {
                    cost.put(new_vertex, newCost);
                    parent.put(new_vertex, e);
                }
            }
        }

        return parent;
    }

    public int removedVertex(String v1) {
        try {
            Edge e = findEdge(v1);
            if (e != null) {
                System.out.println("Removed Edge " + e.getPair());
                removeEdge(e);
                return 1;
            }
        } catch (Exception e) {
            System.out.println("Exception in removedVertex");
        }
        return 0;
    }
}

class EdgeComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        Edge e1 = (Edge) o1;
        Edge e2 = (Edge) o2;

        String a1 = e1.getPair().getKey();
        String a2 = e1.getPair().getValue();
        String b1 = e2.getPair().getKey();
        String b2 = e2.getPair().getValue();

        if (a1.compareTo(b1) > 0) {
            return 1;
        } else if (a1.compareTo(a2) == 0) {
            if (b1.compareTo(b2) > 0) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
}

class VertexNotFound extends Exception {

    VertexNotFound(String message) {
        super(message);
    }

}

class EdgeNotFound extends Exception {

    EdgeNotFound(String message) {
        super(message);
    }

}

public class Question3 extends Application {

    private TreeMap<String, Vertex> vertex_map = new TreeMap<>();
    private List<Edge> edge_list = new LinkedList<>();
    private Vertex selectedVertex = null;
    private int isSelected = 0;

    public void testInput() {
        Vertex v[] = new Vertex[4];
        Edge e[] = new Edge[5];

        v[0] = new Vertex("v1", 0.0, 0.0);
        v[1] = new Vertex("v2", 5.0, 0.0);
        v[2] = new Vertex("v3", 5.0, 5.0);
        v[3] = new Vertex("v4", 0.0, 5.0);

        vertex_map.put("v1", v[0]);
        vertex_map.put("v2", v[1]);
        vertex_map.put("v3", v[2]);
        vertex_map.put("v4", v[3]);


        e[0] = new Edge(3, "v1", "v2");
        e[1] = new Edge(5, "v1", "v3");
        e[2] = new Edge(1, "v2", "v3");
        e[3] = new Edge(2, "v3", "v4");
        e[4] = new Edge(4, "v2", "v4");

        for (int i = 0; i < 5; i++) {
            edge_list.add(e[i]);
            String v1 = e[i].getPair().getKey();
            String v2 = e[i].getPair().getValue();
            vertex_map.get(v1).addEdge(e[i]);
            vertex_map.get(v2).addEdge(e[i]);
        }


    }

    public String getPathOutput(TreeMap<String, Edge> parent, String fromVertex, String toVertex) {
        String output = "";
        LinkedList<Edge> edges = new LinkedList<>();
        LinkedList<String> vertices = new LinkedList<>();

        String current = toVertex;

        if (parent.get(current) == null) {
            output = "no path exists";
            return output;
        }

        while (true) {
            if (current.equals(fromVertex)) {
                vertices.add(0, current);
                break;
            }
            Edge e = parent.get(current);
            edges.add(0, parent.get(current));
            vertices.add(0, current);
            current = e.getOtherVertex(current);
        }

        int position = 0;
        for (Edge e : edges) {
            output += vertices.get(position) + " -> ";
            position++;
        }
        output += toVertex;
        return output;
    }

    public int findEdge(String fromVertex, String toVertex) {
        int position = 0;
        for (Edge e : edge_list) {
            if (e.getPair().getKey().equals(fromVertex) && e.getPair().getValue().equals(toVertex)) {
                return position;
            }
            position++;
        }
        return -1;
    }

    public int deleteEdgesOfVertex(String vertex) {
        for (Map.Entry<String, Vertex> entry : vertex_map.entrySet()) {
            if (entry.getKey().equals(vertex)) {
                System.out.println("NOT DELETED PROPERLY");
            }
            try {
                entry.getValue().removedVertex(vertex);
            } catch (Exception e) {
                System.out.println("Exception in " + entry.getKey());
            }
        }
        Iterator<Edge> itr = edge_list.iterator();
        Edge e;
        while (itr.hasNext()) {
            e = itr.next();
            if (e.getPair().getKey().equals(vertex) || e.getPair().getValue().equals(vertex)) {
                itr.remove();
                System.out.println(e.getPair());

            }
        }
        return 1;
    }

    public void invalidNumber(Stage primaryStage, Scene scene) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Button tryAgain = new Button("Try Again");
        tryAgain.setOnAction(e -> {
            primaryStage.setScene(scene);
            dialogStage.close();
        });

        VBox vbox = new VBox(new Text("Invalid Co-ordinates"), tryAgain);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));

        dialogStage.setScene(new Scene(vbox, 200, 100));
        dialogStage.show();
    }

    public void invalidVertex(Stage primaryStage, Scene scene) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Button tryAgain = new Button("Try Again");
        tryAgain.setOnAction(e -> {
            primaryStage.setScene(scene);
            dialogStage.close();
        });

        VBox vbox = new VBox(new Text("Invalid Vertex"), tryAgain);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));

        dialogStage.setScene(new Scene(vbox, 200, 100));
        dialogStage.show();
    }

    public void invalidEdge(Stage primaryStage, Scene scene) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Button tryAgain = new Button("Try Again");
        tryAgain.setOnAction(e -> {
            primaryStage.setScene(scene);
            dialogStage.close();
        });

        VBox vbox = new VBox(new Text("Invalid Edge"), tryAgain);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));

        dialogStage.setScene(new Scene(vbox, 200, 100));
        dialogStage.show();
    }

    public void invalidGraph(Stage primaryStage, Scene scene) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Button tryAgain = new Button("Try Again");
        tryAgain.setOnAction(e -> {
            primaryStage.setScene(scene);
            dialogStage.close();
        });

        VBox vbox = new VBox(new Text("Invalid Graph"), tryAgain);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));

        dialogStage.setScene(new Scene(vbox, 200, 100));
        dialogStage.show();
    }

    public void somethingWentWrong(Stage primaryStage, Scene scene) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Button tryAgain = new Button("Try Again");
        tryAgain.setOnAction(e -> {
            primaryStage.setScene(scene);
            dialogStage.close();
        });

        VBox vbox = new VBox(new Text("Something Went Wrong"), tryAgain);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));

        dialogStage.setScene(new Scene(vbox, 200, 100));
        dialogStage.show();
    }

    public Group createCircle(Vertex v, Stage stage, Scene scene, Group root) {

        Stage editStage = new Stage();
        editStage.setTitle("Edit Vertex");

        Group g = new Group();
        Circle circle = new Circle();
        circle.setCenterX(13*v.getXcoordinate());
        circle.setCenterY(750 - v.getYcoordinate() * 7);
        circle.setRadius(20);
        circle.setFill(Color.GHOSTWHITE);
        circle.setStroke(Color.DARKGREEN);
        circle.setStrokeWidth(5);

        EventHandler<MouseEvent> e = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.isShiftDown() && (!event.isControlDown())) {
                    editVertex(v);
                }
                else if(event.isControlDown() && isSelected == 0) {
                    selectedVertex = v;
                    isSelected = 1;
                    vertexSelected();
                }
                else if(event.isControlDown() && isSelected == 1) {
                    addNewEdge(v, selectedVertex, root);
                    isSelected = 0;
//                    stage.close();
//                    Group root1 = rootRefresh(root);
//                    root.getChildren().clear();
//                    simulateGraph(stage, scene, root);
                }

            }
        };

        circle.addEventFilter(MouseEvent.MOUSE_CLICKED, e);

        EventHandler<KeyEvent> key_event = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
//                deleteVertex();
            }
        };


        EventHandler<MouseDragEvent> drag_enter = new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {

            }
        };

        Text t = new Text(v.getXcoordinate() * 13, 750 - v.getYcoordinate() * 7, v.getName());
        t.setFont(new Font("Georgia", 20));
        t.setFill(Color.BLACK);
        g.getChildren().addAll(circle, t);
        g.setAccessibleText("VERTEX");

        return g;
    }

    public Group rootRefresh(Group root) {
        root.getChildren().clear();
        return root;
    }

    public void vertexSelected() {
        Stage selectedStage = new Stage();
        selectedStage.setTitle("Vertex Selected");

        Label alert = new Label("Select another Vertex to create Edge");
        alert.setFont(new Font("Georgia", 20));

        Button dump = new Button("De-select");
        Button ok = new Button("OK");

        ok.setOnAction(e -> {
            selectedStage.close();
        });
        dump.setOnAction(e -> {
            selectedVertex = null;
            isSelected = 0;
            selectedStage.close();
        });

        VBox vbox = new VBox(alert, ok, dump);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(20);

        Scene selectedScene = new Scene(vbox, 600, 200);
        selectedStage.setScene(selectedScene);
        selectedStage.show();
    }

    public void addNewEdge(Vertex v1, Vertex v2, Group root) {
        Stage weightStage = new Stage();
        weightStage.setTitle("Add Weight to Edge");
        Label heading = new Label("Weight of the Edge");
        heading.setFont(new Font("Georgia", 24));

        TextField weight = new TextField("1.0");
        Button submit = new Button("Save");

        VBox vbox = new VBox(heading, weight, submit);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(15);

        Scene weightScene = new Scene(vbox, 400, 200);

        submit.setOnAction(e -> {
            String vertex1;
            String vertex2;
            double w;
            try {
                vertex1 = v1.getName();
                vertex2 = v2.getName();
                w = Double.parseDouble(weight.getText());

                if (vertex_map.containsKey(vertex1) && vertex_map.containsKey(vertex2)) {
                    Edge edge = new Edge(w, vertex1, vertex2);
                    vertex_map.get(vertex1).addEdge(edge);
                    //                vertex_map.get(vertex2).addEdge(edge);
                    edge_list.add(edge);
                    System.out.println("ADDED: " + edge.getPair());
                    Line l = createLine(edge);
                    root.getChildren().add(l);
                    weightStage.close();
                } else {
                    throw new VertexNotFound("Vertex Not Found");
                }
            } catch (VertexNotFound ex1) {
                System.out.println("Vertex Not Found");
            } catch (NumberFormatException ex2) {
                invalidNumber(weightStage, weightScene);
            }

        });
        weightStage.setScene(weightScene);
        weightStage.show();
    }

    public Circle circleTraveller() {
        Circle circle = new Circle();
        circle.setFill(Color.YELLOW);
        circle.setRadius(10);
        return circle;
    }

    public Rectangle rectangleTraveller() {
        Rectangle rect = new Rectangle();
        rect.setWidth(20);
        rect.setHeight(20);
        rect.setFill(Color.DARKGREEN);
        return rect;
    }

    public Polygon triangleTraveller() {
        Polygon tri = new Polygon();
        tri.getPoints().addAll(new Double[]{
            40.0, 20.0,
            30.0, 40.0,
            50.0, 40.0 });

        tri.setFill(Color.GREY);
        return tri;
    }

    public Polygon plusTraveller() {
        Polygon plus = new Polygon();
        plus.getPoints().addAll(new Double[]{
                5.0, 0.0,
                10.0, 0.0,
                10.0, 5.0,
                15.0, 5.0,
                15.0, 10.0,
                10.0, 10.0,
                10.0, 15.0,
                5.0, 15.0,
                5.0, 10.0,
                0.0, 10.0,
                0.0, 5.0,
                5.0, 5.0
        });
        plus.setFill(Color.DARKRED);
        return plus;
    }

    public Polygon xTraveller() {
        Polygon xt = new Polygon();
        xt.getPoints().addAll(new Double[]{
                5.0, 0.0,
                10.0, 0.0,
                10.0, 5.0,
                15.0, 5.0,
                15.0, 10.0,
                10.0, 10.0,
                10.0, 15.0,
                5.0, 15.0,
                5.0, 10.0,
                0.0, 10.0,
                0.0, 5.0,
                5.0, 5.0
        });
        xt.setFill(Color.DARKBLUE);
        xt.setRotate(45);
        return xt;
    }

    public double getX(double x) {
        return 13 * x;
    }

    public double getY(double y) {
        return 750 - 7*y;
    }

    public Line createLine(Edge e) {
        Line line = new Line();
        Vertex start = vertex_map.get(e.getPair().getKey());
        Vertex end = vertex_map.get(e.getPair().getValue());

        line.setStartX(getX(start.getXcoordinate()));
        line.setStartY(getY(start.getYcoordinate()));
        line.setEndX(getX(end.getXcoordinate()));
        line.setEndY(getY(end.getYcoordinate()));
        line.setStroke(Color.DARKSLATEGRAY);
        line.setStrokeWidth(3);
        line.setAccessibleText("Edge");

        return line;
    }

    public void simulateGraph(Stage simulationStage, Scene simulationScene, Group root) {

        for (Map.Entry<String, Vertex> entry : vertex_map.entrySet()) {
            Group g = createCircle(entry.getValue(), simulationStage, simulationScene, root);
            root.getChildren().addAll(g.getChildren());
        }

        for (Edge e : edge_list) {
            try {
                Line line = createLine(e);
                root.getChildren().add(line);
            }
            catch(NullPointerException e1) {
                System.out.println("Null Pointer Exception For Edge: " + e.getPair());
            }
        }

        Button path = new Button("Find Path");
        path.setLayoutX(700);
        path.setLayoutY(700);
        root.getChildren().add(path);
        path.setOnAction(e -> {
           findPath(simulationScene, simulationStage, root);
        });

        simulationScene.setFill(Color.BLACK);

        simulationScene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();
                if(!event.isShiftDown() && !event.isControlDown())
                    addVertex(x, y, simulationStage, simulationScene, root);
            }
        });

        root.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (root.getClip() instanceof Circle) {

                }
                root.setOnDragExited(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {

                    }
                });
            }
        });

        simulationStage.setScene(simulationScene);
        simulationStage.show();
    }

    public void findPath(Scene scene, Stage stage, Group root) {
        Stage pathStage = new Stage();
        pathStage.setTitle("Find Path");

        Label heading = new Label("Find Path");
        heading.setFont(new Font("Georgia", 20));

        TextField name1 = new TextField("vertex1: ");
        TextField name2 = new TextField("vertex2: ");

        Button travellerBtn = new Button("Set Traveller");
        travellerBtn.setOnAction(e -> {
            Label heading2 = new Label("Choose Your Traveller");
            heading2.setFont(new Font("Georgia", 24));
            Button circleBtn = new Button("Circle");
            Button rectangleBtn = new Button("Square");
            Button triangleBtn = new Button("Triangle");
            Button xBtn = new Button("X");
            Button plusBtn = new Button("+");

            String v1 = name1.getText();
            String v2 = name2.getText();

            circleBtn.setOnAction(e1 -> {
                simulatePath("circle", stage, scene, v1, v2, root);
                pathStage.close();
            });

            rectangleBtn.setOnAction(e1 -> {
                simulatePath("rect", stage, scene, v1, v2, root);
                pathStage.close();
            });

            triangleBtn.setOnAction(e1 -> {
                simulatePath("tri", stage, scene, v1, v2, root);
                pathStage.close();
            });
            xBtn.setOnAction(e1 -> {
                simulatePath("x", stage, scene,v1, v2, root);
                pathStage.close();
            });
            plusBtn.setOnAction(e1 -> {
                simulatePath("plus", stage, scene, v1, v2, root);
                pathStage.close();
            });
            HBox hbox1 = new HBox(circleBtn, rectangleBtn, triangleBtn, xBtn, plusBtn);
            hbox1.setAlignment(Pos.TOP_CENTER);
            hbox1.setSpacing(5);
            VBox vbox1 = new VBox(heading2, hbox1);
            vbox1.setAlignment(Pos.TOP_CENTER);
            vbox1.setSpacing(20);
            Scene scene1 = new Scene(vbox1, 900, 200);
            pathStage.setScene(scene1);
        });
        HBox hbox = new HBox(name1, name2);
        hbox.setAlignment(Pos.TOP_CENTER);
        hbox.setSpacing(5);
        VBox vbox = new VBox(heading, hbox, travellerBtn);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(20);
        Scene pathScene = new Scene(vbox, 900, 400);
        pathStage.setScene(pathScene);
        pathStage.show();
    }

    public void simulatePath(String type, Stage stage, Scene scene, String v1, String v2, Group root) {
        Vertex ver = vertex_map.get(v1);
        TreeMap<String, Edge> parent = ver.getPath(vertex_map, v1, v2);
        String[] pathVertices = getPathOutput(parent, v1, v2).split(" -> ");
        String current = v1;
        int count = 0;
        Path path = new Path();
        double x = getX(ver.getXcoordinate());
        double y = getY(ver.getYcoordinate());
        path.getElements().add(new MoveTo(x, y));
        PathTransition pathTransition = new PathTransition();
        for(String s: pathVertices) {
            Vertex ver1 = vertex_map.get(current);
            Vertex ver2 = vertex_map.get(s);
            double x1 = getX(ver1.getXcoordinate());
            double y1 = getY(ver1.getYcoordinate());

            double x2 = getX(ver2.getXcoordinate());
            double y2 = getY(ver2.getYcoordinate());

            path.getElements().add(new LineTo(x2, y2));

//            pathTransition.setDelay(count);

            pathTransition.setPath(path);
            current = ver2.getName();
//            count.add(Duration.millis(1000));
            count++;
        }

        pathTransition.setCycleCount(10);

        if(type.equals("circle")) {
            Circle circle = circleTraveller();
            pathTransition.setNode(circle);
            root.getChildren().add(circle);
        }
        else if(type.equals("rect")) {
            Rectangle rect = rectangleTraveller();
            pathTransition.setNode(rect);
            root.getChildren().add(rect);
        }
        else if(type.equals("tri")) {
            System.out.println("Inside Circle Animation");
            Polygon tri = triangleTraveller();
            pathTransition.setNode(tri);
            root.getChildren().add(tri);
        }
        else if(type.equals("plus")) {
            Polygon plus = plusTraveller();
            pathTransition.setNode(plus);
            root.getChildren().add(plus);
        }
        else {
            Polygon xt = xTraveller();
            pathTransition.setNode(xt);
            root.getChildren().add(xt);
        }

        pathTransition.setDuration(Duration.millis(count * 1000));
        pathTransition.play();
        stage.setScene(scene);
    }

    public void addVertex(double a, double b, Stage stage, Scene scene, Group root) {

        Stage newStage = new Stage();
        newStage.setTitle("Add Vertex");
        Label heading = new Label("ADD NEW VERTEX");
        heading.setFont(new Font("Georgia", 20));
        TextField name = new TextField();
        name.setText("Name");

        Button submit = new Button("Save");
        Button cancel = new Button("Cancel");

        submit.setOnAction(action -> {
            String vertex_name;
            double x = a/13.0;
            double y = (750.0 - b)/7.0;
            vertex_name = name.getText();

            try {

                Vertex v = new Vertex(vertex_name, x, y);
                vertex_map.put(vertex_name, v);
                System.out.println("ADDED: " + vertex_name + " X:" + x + " Y:" + y);


            } catch (NumberFormatException ex) {
                invalidNumber(stage, scene);
            }
            newStage.close();
            simulateGraph(stage, scene, root);
        });

        cancel.setOnAction(e -> {
            newStage.close();
        });

        HBox add_hbox = new HBox(name);
        add_hbox.setAlignment(Pos.TOP_CENTER);
        add_hbox.setSpacing(5);

        VBox vbox_2 = new VBox(heading, add_hbox, submit, cancel);
        vbox_2.setAlignment(Pos.TOP_CENTER);
        vbox_2.setSpacing(20);

        Scene addScene = new Scene(vbox_2, 900, 400);
        newStage.setScene(addScene);
        newStage.show();
    }

    public void editVertex(Vertex vertex) {
        Stage editStage = new Stage();
        editStage.setTitle("Edit Vertex");

        Label heading = new Label("Edit Vertex " + vertex.getName());
        heading.setFont(new Font("Georgia", 24));

        TextField xcoordinate = new TextField();
        TextField ycoordinate = new TextField();
        xcoordinate.setText(Double.toString(vertex.getXcoordinate()));
        ycoordinate.setText(Double.toString(vertex.getYcoordinate()));

        HBox hbox = new HBox(xcoordinate, ycoordinate);
        hbox.setAlignment(Pos.TOP_CENTER);
        hbox.setSpacing(5);

        Button submit = new Button("Update");
        Button cancel = new Button("Cancel");

        cancel.setOnAction(e -> {
            editStage.close();
        });

        VBox vbox = new VBox(heading, hbox, submit, cancel);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(15);

        Scene scene = new Scene(vbox, 900, 400);

        submit.setOnAction(updateAction -> {
            String vertex_name;
            double x;
            double y;
            try {
                vertex_name = vertex.getName();
                x = Double.parseDouble(xcoordinate.getText());
                y = Double.parseDouble(ycoordinate.getText());

                Vertex v = new Vertex(vertex_name, x, y);
                vertex_map.put(vertex_name, v);
                System.out.println("UPDATED: " + vertex_name);

                editStage.close();
            } catch (NumberFormatException ex) {
                invalidNumber(editStage, scene);
            }
        });

        editStage.setScene(scene);
        editStage.show();
    }

    public void updateVertex(Stage primaryStage, Scene scene) {
        Label heading = new Label("SELECT VERTEX TO BE UPDATED");
        heading.setFont(new Font("Georgia", 20));
        Label heading2 = new Label("UPDATE VERTEX");
        heading2.setFont(new Font("Georgia", 20));
        Button btns[] = new Button[vertex_map.size()];
        Button goBack = new Button("Go To Menu");

        goBack.setOnAction(menuAction -> {
            primaryStage.setScene(scene);
        });
        int i = 0;
        Iterator<Map.Entry<String, Vertex>> itr = vertex_map.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, Vertex> entry = itr.next();
            btns[i] = new Button(entry.getKey());
            btns[i].setOnAction(action -> {

                TextField xcoordinate = new TextField();
                TextField ycoordinate = new TextField();
                xcoordinate.setText(Double.toString(entry.getValue().getXcoordinate()));
                ycoordinate.setText(Double.toString(entry.getValue().getYcoordinate()));


                Button submit = new Button("Update");

                submit.setOnAction(updateAction -> {
                    String vertex_name;
                    double x;
                    double y;
                    try {
                        vertex_name = entry.getKey();
                        x = Double.parseDouble(xcoordinate.getText());
                        y = Double.parseDouble(ycoordinate.getText());

                        Vertex v = new Vertex(vertex_name, x, y);
                        vertex_map.put(vertex_name, v);
                        System.out.println("UPDATED: " + vertex_name);

                        primaryStage.setScene(scene);
                    } catch (NumberFormatException ex) {
                        invalidNumber(primaryStage, scene);
                    }
                });

                HBox add_hbox = new HBox(xcoordinate, ycoordinate);
                add_hbox.setAlignment(Pos.TOP_CENTER);
                add_hbox.setSpacing(5);
                VBox vbox1 = new VBox(heading2, add_hbox, submit, goBack);
                vbox1.setAlignment(Pos.TOP_CENTER);
                vbox1.setSpacing(20);
                Scene addScene = new Scene(vbox1, 900, 400);
                primaryStage.setScene(addScene);
            });
            i++;
        }
        HBox update_hbox = new HBox(btns);
        update_hbox.setAlignment(Pos.TOP_CENTER);
        update_hbox.setSpacing(5);
        VBox vbox1 = new VBox(heading, update_hbox, goBack);
        vbox1.setAlignment(Pos.TOP_CENTER);
        vbox1.setSpacing(20);
        Scene updateScene = new Scene(vbox1, 900, 400);
        primaryStage.setScene(updateScene);
    }

    public void addVertex(Stage primaryStage, Scene scene) {

        Label heading = new Label("ADD NEW VERTEX");
        heading.setFont(new Font("Georgia", 20));
        TextField name = new TextField();
        TextField xcoordinate = new TextField();
        TextField ycoordinate = new TextField();
        name.setText("Name");
        xcoordinate.setText("0.00");
        ycoordinate.setText("0.00");

        Button submit = new Button("Save");
        Button goBack = new Button("Go To Menu");

        goBack.setOnAction(menuAction -> {
            primaryStage.setScene(scene);
        });

        submit.setOnAction(action -> {
            String vertex_name;
            double x = 0;
            double y = 0;
            vertex_name = name.getText();
            try {
                x = Double.parseDouble(xcoordinate.getText());
                y = Double.parseDouble(ycoordinate.getText());

                Vertex v = new Vertex(vertex_name, x, y);
                vertex_map.put(vertex_name, v);
                System.out.println("ADDED: " + vertex_name);

                primaryStage.setScene(scene);
            } catch (NumberFormatException ex) {
                invalidNumber(primaryStage, scene);
            }
        });

        HBox add_hbox = new HBox(name, xcoordinate, ycoordinate);
        add_hbox.setAlignment(Pos.TOP_CENTER);
        add_hbox.setSpacing(5);

        VBox vbox_2 = new VBox(heading, add_hbox, submit, goBack);
        vbox_2.setAlignment(Pos.TOP_CENTER);
        vbox_2.setSpacing(20);

        Scene addScene = new Scene(vbox_2, 900, 400);
        primaryStage.setScene(addScene);
    }

    public void deleteVertex(Stage primaryStage, Scene scene) {

        TextField name = new TextField();
        name.setText("Vertex-Name");
        name.setMaxWidth(200);

        Button deletebtn = new Button("Delete");
        Label heading = new Label("INPUT VERTEX TO DELETE");
        heading.setFont(new Font("Georgia", 20));
        Button goBack = new Button("Go To Menu");

        goBack.setOnAction(menuAction -> {
            primaryStage.setScene(scene);
        });

        deletebtn.setOnAction(action -> {
            try {
                if (vertex_map.containsKey(name.getText())) {
                    vertex_map.remove(name.getText());
                    try {
                        deleteEdgesOfVertex(name.getText());
                    } catch (Exception ex) {
                        System.out.println("exception");
                    }
                    System.out.println("DELETED: " + name.getText());

                } else {
                    throw new VertexNotFound(name.getText() + " NOT FOUND");
                }

                primaryStage.setScene(scene);
            } catch (VertexNotFound ex) {
                invalidVertex(primaryStage, scene);
            }
        });

        VBox delete_vbox = new VBox(heading, name, deletebtn, goBack);
        delete_vbox.setAlignment(Pos.TOP_CENTER);
        delete_vbox.setSpacing(20);
        Scene deleteScene = new Scene(delete_vbox, 900, 400);
        primaryStage.setScene(deleteScene);
    }

    public void searchVertex(Stage primaryStage, Scene scene) {
        Label heading = new Label("SEARCH VERTEX");
        heading.setFont(new Font("Georgia", 20));
        TextField query = new TextField();
        query.setMaxWidth(200);
        Button submit = new Button("Search");
        Button goBack = new Button("Go To Menu");

        goBack.setOnAction(menuAction -> {
            primaryStage.setScene(scene);
        });

        submit.setOnAction(action -> {
            try {
                if (vertex_map.containsKey(query.getText())) {
                    Label heading2 = new Label("Details: ");
                    Vertex v = vertex_map.get(query.getText());
                    Label name = new Label("Name: " + v.getName());
                    Label x = new Label("X: " + v.getXcoordinate());
                    Label y = new Label("Y: " + v.getYcoordinate());

                    Button menuButton = new Button("Go To Menu");
                    menuButton.setOnAction(menuAction -> {
                        primaryStage.setScene(scene);
                    });

                    name.setFont(new Font("Times New Roman", 24));
                    heading2.setFont(new Font("Georgia", 24));
                    x.setFont(new Font("Arial", 20));
                    y.setFont(new Font("Arial", 20));

                    VBox details = new VBox(name, x, y, menuButton);
                    details.setAlignment(Pos.TOP_CENTER);
                    details.setSpacing(15);
                    Scene detailScene = new Scene(details, 900, 400);
                    primaryStage.setScene(detailScene);
                } else {
                    throw new VertexNotFound(query.getText() + "NOT FOUND");
                }
            } catch (VertexNotFound ex) {
                invalidVertex(primaryStage, scene);
            }
        });

        VBox queryBox = new VBox(heading, query, submit, goBack);
        queryBox.setAlignment(Pos.TOP_CENTER);
        queryBox.setSpacing(15);
        Scene searchScene = new Scene(queryBox, 900, 400);
        primaryStage.setScene(searchScene);
    }

    public void addEdge(Stage primaryStage, Scene scene) {

        Label heading = new Label("ADD EDGE");
        heading.setFont(new Font("Georgia", 20));

        TextField fromVertex = new TextField();
        TextField toVertex = new TextField();
        TextField weight = new TextField();
        fromVertex.setText("From-Vertex");
        toVertex.setText("To-Vertex");
        weight.setText("1.00");

        Button submit = new Button("Save");
        Button goBack = new Button("Go To Menu");

        goBack.setOnAction(menuAction -> {
            primaryStage.setScene(scene);
        });

        submit.setOnAction(action -> {
            String vertex1;
            String vertex2;
            double w;
            try {
                vertex1 = fromVertex.getText();
                vertex2 = toVertex.getText();
                w = Double.parseDouble(weight.getText());

                if (vertex_map.containsKey(vertex1) && vertex_map.containsKey(vertex2)) {
                    Edge edge = new Edge(w, vertex1, vertex2);
                    vertex_map.get(vertex1).addEdge(edge);
                    //                vertex_map.get(vertex2).addEdge(edge);
                    edge_list.add(edge);
                    System.out.println("ADDED: " + edge.getPair());

                    primaryStage.setScene(scene);
                } else {
                    throw new VertexNotFound("Vertex Not Found");
                }
            } catch (VertexNotFound ex1) {
                invalidVertex(primaryStage, scene);
            } catch (NumberFormatException ex2) {
                invalidNumber(primaryStage, scene);
            }
        });

        HBox add_hbox = new HBox(fromVertex, toVertex, weight);
        add_hbox.setAlignment(Pos.TOP_CENTER);
        add_hbox.setSpacing(5);
        VBox vBox_2 = new VBox(heading, add_hbox, submit, goBack);
        vBox_2.setAlignment(Pos.TOP_CENTER);
        vBox_2.setSpacing(15);

        Scene addScene = new Scene(vBox_2, 900, 400);
        primaryStage.setScene(addScene);
    }

    public void searchEdge(Stage primaryStage, Scene scene) {
        Label heading = new Label("SEARCH EDGE");
        heading.setFont(new Font("Georgia", 20));
        TextField fromVertex = new TextField();
        TextField toVertex = new TextField();
        fromVertex.setText("From-Vertex");
        toVertex.setText("To-Vertex");

        Button searchbtn = new Button("Search");
        Button goBack = new Button("Go To Menu");

        goBack.setOnAction(menuAction -> {
            primaryStage.setScene(scene);
        });

        searchbtn.setOnAction(action -> {
            try {
                String vertex1 = fromVertex.getText();
                String vertex2 = toVertex.getText();
                if (findEdge(vertex1, vertex2) != -1) {
                    Label name1 = new Label("From: " + vertex1);
                    Label name2 = new Label("To: " + vertex2);
                    name1.setFont(new Font("Georgia", 24));
                    name2.setFont(new Font("Georgia", 24));
                    Vertex v1 = vertex_map.get(vertex1);
                    Edge e1 = v1.findEdge(vertex2);
                    Label w = new Label("Weight" + e1.getCost());
                    w.setFont(new Font("Arial", 20));
                    Button goBackBtn = new Button("Go To Menu");

                    goBackBtn.setOnAction(menuAction -> {
                        primaryStage.setScene(scene);
                    });

                    VBox details = new VBox(name1, name2, goBackBtn);
                    Scene detailScene = new Scene(details, 600, 200);
                    primaryStage.setScene(detailScene);
                } else {
                    throw new EdgeNotFound("Edge Not Found");
                }
            } catch (EdgeNotFound ex) {
                invalidEdge(primaryStage, scene);
            }
        });

        HBox search_box = new HBox(fromVertex, toVertex);
        search_box.setAlignment(Pos.TOP_CENTER);
        search_box.setSpacing(5);
        VBox vBox1 = new VBox(heading, search_box, searchbtn, goBack);
        vBox1.setAlignment(Pos.TOP_CENTER);
        vBox1.setSpacing(20);
        Scene searchScene = new Scene(vBox1, 900, 400);
        primaryStage.setScene(searchScene);
    }

    public void updateEdge(Stage primaryStage, Scene scene) {

        Label heading = new Label("SELECT EDGE TO BE UPDATED");
        heading.setFont(new Font("Georgia", 20));
        Button goBack = new Button("Go To Menu");

        goBack.setOnAction(menuAction -> {
            primaryStage.setScene(scene);
        });
        Button btns[] = new Button[edge_list.size()];
        int i = 0;
        for (Edge e1 : edge_list) {
            btns[i] = new Button("<" + e1.getPair().getKey() + ", " + e1.getPair().getValue() + ">");
            btns[i].setOnAction(btnAction -> {

                TextField fromVertex = new TextField();
                TextField toVertex = new TextField();
                TextField weight = new TextField();
                fromVertex.setText(e1.getPair().getKey());
                toVertex.setText(e1.getPair().getValue());
                weight.setText(Double.toString(e1.getCost()));

                Button submit = new Button("Save");

                submit.setOnAction(action -> {
                    String vertex1;
                    String vertex2;
                    double w;
                    try {
                        vertex1 = fromVertex.getText();
                        vertex2 = toVertex.getText();
                        if (findEdge(vertex1, vertex2) == -1) {
                            throw new EdgeNotFound("Edge Not Found");
                        } else {
                            w = Double.parseDouble(weight.getText());

                            Edge edge = new Edge(w, vertex1, vertex2);
                            edge_list.remove(findEdge(vertex1, vertex2));

                            vertex_map.get(vertex1).removeEdge(vertex2);
                            System.out.println("Deleted " + vertex1 + "->" + vertex2);
                            vertex_map.get(vertex1).addEdge(edge);

                            edge_list.add(edge);
                            System.out.println("UPDATED: " + edge.getPair());

                            primaryStage.setScene(scene);
                        }
                    } catch (NumberFormatException ex) {
                        invalidNumber(primaryStage, scene);
                    } catch (EdgeNotFound ex2) {
                        invalidEdge(primaryStage, scene);
                    }
                });
                Label heading2 = new Label("Update Edge");
                heading2.setFont(new Font("Georgia", 20));
                HBox add_hbox = new HBox(fromVertex, toVertex, weight);
                add_hbox.setAlignment(Pos.TOP_CENTER);
                add_hbox.setSpacing(5);
                VBox vBox1 = new VBox(heading2, add_hbox, submit, goBack);
                vBox1.setAlignment(Pos.TOP_CENTER);
                vBox1.setSpacing(15);
                Scene addScene = new Scene(vBox1, 900, 400);
                primaryStage.setScene(addScene);
            });
            i++;
        }
        HBox update_hbox = new HBox(btns);
        update_hbox.setAlignment(Pos.TOP_CENTER);
        update_hbox.setSpacing(5);
        VBox vBox2 = new VBox(heading, update_hbox, goBack);
        vBox2.setAlignment(Pos.TOP_CENTER);
        vBox2.setSpacing(20);
        Scene updateScene = new Scene(vBox2, 900, 400);
        primaryStage.setScene(updateScene);
    }

    public void deleteEdge(Stage primaryStage, Scene scene) {

        TextField fromVertex = new TextField();
        TextField toVertex = new TextField();
        fromVertex.setText("From-Vertex");
        toVertex.setText("To-Vertex");

        Button deletebtn = new Button("Delete");
        Label heading = new Label("INPUT EDGE TO BE DELETED");
        heading.setFont(new Font("Georgia", 20));
        Button goBack = new Button("Go To Menu");

        goBack.setOnAction(menuAction -> {
            primaryStage.setScene(scene);
        });

        deletebtn.setOnAction(action -> {
            String v1 = fromVertex.getText();
            String v2 = toVertex.getText();
            int i = 0;
            try {
                if (findEdge(v1, v2) == -1) {
                    throw new EdgeNotFound("Edge not found");
                } else {
                    for (Edge e1 : edge_list) {
                        if (e1.getPair().getKey().equals(v1) && e1.getPair().getValue().equals(v2)) {
                            vertex_map.get(v1).removeEdge(e1);
                            //                        vertex_map.get(v2).removeEdge(e1);
                            System.out.println("DELETED EDGE: " + e1.getPair());
                            break;
                        }
                        i++;
                    }
                    edge_list.remove(i);
                    primaryStage.setScene(scene);
                }
            } catch (EdgeNotFound ex) {
                invalidEdge(primaryStage, scene);
            }
        });

        HBox delete_box = new HBox(fromVertex, toVertex);
        delete_box.setAlignment(Pos.TOP_CENTER);
        delete_box.setSpacing(5);
        VBox vbox1 = new VBox(heading, delete_box, deletebtn, goBack);
        vbox1.setAlignment(Pos.TOP_CENTER);
        vbox1.setSpacing(20);
        Scene deleteScene = new Scene(vbox1, 900, 400);
        primaryStage.setScene(deleteScene);
    }

    public void loadFromFile(Stage primaryStage, Scene scene) {

        Label heading = new Label("CHOOSE FILE TO IMPORT GRAPH");
        heading.setFont(new Font("Georgia", 20));

        FileChooser fileChooser = new FileChooser();

        Button select = new Button("Choose File");
        Button backToMenu = new Button("Go to Menu");

        backToMenu.setOnAction(menuAction -> {
            primaryStage.setScene(scene);
        });

        select.setOnAction(action -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            try {
                Scanner sc = new Scanner(selectedFile);
                int vertices = sc.nextInt();
                for (int i = 0; i < vertices; i++) {
                    String name = sc.next();
                    double x = sc.nextDouble();
                    double y = sc.nextDouble();

                    Vertex v = new Vertex(name, x, y);
                    vertex_map.put(name, v);
                }
                int edges = sc.nextInt();
                for (int i = 0; i < edges; i++) {
                    String v1 = sc.next();
                    String v2 = sc.next();
                    double w = sc.nextDouble();

                    Edge e1 = new Edge(w, v1, v2);
                    vertex_map.get(v1).addEdge(e1);
                    edge_list.add(e1);
//                        vertex_map.get(v2).addEdge(e1);
                }
            } catch (Exception e1) {
                invalidGraph(primaryStage, scene);
            }
        });
        VBox vbox_2 = new VBox(heading, select, backToMenu);
        vbox_2.setAlignment(Pos.TOP_CENTER);
        vbox_2.setSpacing(20);
        Scene selectScene = new Scene(vbox_2, 900, 400);
        primaryStage.setScene(selectScene);
    }

    public void exportToFile(Stage primaryStage, Scene scene) {

        Label heading = new Label("CHOOSE FILE TO EXPORT GRAPH");
        heading.setFont(new Font("Georgia", 20));

        FileChooser fileChooser = new FileChooser();

        Button select = new Button("Choose File");
        Button backToMenu = new Button("Go to Menu");

        backToMenu.setOnAction(menuAction -> {
            primaryStage.setScene(scene);
        });

        select.setOnAction(action -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            FileWriter fileWriter = null;

            try {
                fileWriter = new FileWriter(selectedFile);
                String data = "";
                data += vertex_map.size() + "\n";
                for (Map.Entry<String, Vertex> entry : vertex_map.entrySet()) {
                    data += entry.getKey() + " " + entry.getValue().getXcoordinate() + " " + entry.getValue().getYcoordinate() + "\n";
                }
                data += edge_list.size() + "\n";
//                    System.out.println(edge_list.size());
                edge_list.sort(new EdgeComparator());
                for (Edge e1 : edge_list) {
                    data += e1.getPair().getKey() + " " + e1.getPair().getValue() + " " + e1.getCost() + "\n";
                }
                fileWriter.write(data);
            } catch (Exception ex) {
                System.out.println("NOT WRITTEN");
            } finally {
                try {
                    fileWriter.close();
                } catch (Exception ex) {
                    System.out.println("NOT CLOSED");
                }
            }
        });
        VBox vBox1 = new VBox(heading, select, backToMenu);
        vBox1.setAlignment(Pos.TOP_CENTER);
        vBox1.setSpacing(20);
        Scene exportScene = new Scene(vBox1, 900, 400);
        primaryStage.setScene(exportScene);
    }

    public void pathButton(Stage primaryStage, Scene scene) {
        Label heading = new Label("Find Path");
        heading.setFont(new Font("Georgia", 20));
        Button goBack = new Button("Go To Menu");

        goBack.setOnAction(menuAction -> {
            primaryStage.setScene(scene);
        });

        TextField vertex1 = new TextField();
        TextField vertex2 = new TextField();
        vertex1.setText("From-Vertex");
        vertex2.setText("To-Vertex");
        vertex1.setMaxWidth(200);
        vertex2.setMaxWidth(200);

        Button findBtn = new Button("Find");

        findBtn.setOnAction(action -> {
            try {
                String fromVertex = vertex1.getText();
                String toVertex = vertex2.getText();

                if (vertex_map.containsKey(fromVertex) && vertex_map.containsKey(toVertex)) {
                    Vertex source = vertex_map.get(fromVertex);

                    TreeMap<String, Edge> parent = source.getPath(vertex_map, fromVertex, toVertex);

                    String output = getPathOutput(parent, fromVertex, toVertex);
                    Label heading2 = new Label("The Path from " + fromVertex + " to " + toVertex + ": ");
                    heading2.setFont(new Font("Georgia", 20));
                    Label path = new Label(output);
                    path.setFont(new Font("ComicSans", 20));

                    VBox path_box = new VBox(heading2, path, goBack);
                    path_box.setAlignment(Pos.TOP_CENTER);
                    path_box.setSpacing(15);
                    Scene pathScene = new Scene(path_box, 900, 400);
                    primaryStage.setScene(pathScene);
                } else {
                    throw new VertexNotFound("Vertex not Found");
                }
            } catch (VertexNotFound ex1) {
                invalidVertex(primaryStage, scene);
            } catch (Exception ex) {
                somethingWentWrong(primaryStage, scene);
            }
        });

        VBox findBox = new VBox(heading, vertex1, vertex2, findBtn, goBack);
        findBox.setAlignment(Pos.TOP_CENTER);
        findBox.setSpacing(15);
        Scene findScene = new Scene(findBox, 900, 400);
        primaryStage.setScene(findScene);
    }

    public void simulate(Stage primaryStage, Scene scene) {
        final Stage simulationStage = new Stage();
        simulationStage.setTitle("Graph");
        Group root = new Group();
        final Scene simulationScene = new Scene(root, 1440, 900);
        simulateGraph(simulationStage, simulationScene, root);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

//        testInput();

        primaryStage.setTitle("Menu");

        Label menuHeading = new Label("CHOOSE OPERATION");
        menuHeading.setFont(new Font("Georgia", 20));

        MenuItem addVertex = new MenuItem("Add Vertex");
        MenuItem deleteVertex = new MenuItem("Delete Vertex");
        MenuItem updateVertex = new MenuItem("Update Vertex");
        MenuItem searchVertex = new MenuItem("Search Vertex");
        MenuItem addEdge = new MenuItem("Add Edge");
        MenuItem searchEdge = new MenuItem("Search Edge");
        MenuItem updateEdge = new MenuItem("Update Edge");
        MenuItem deleteEdge = new MenuItem("Delete Edge");
        MenuItem loadFromFile = new MenuItem("Load From File");
        MenuItem exportToFile = new MenuItem("Export to File");

        MenuButton vertexButton = new MenuButton("Vertex Operations", null, addVertex, searchVertex, updateVertex, deleteVertex);
        MenuButton edgeButton = new MenuButton("Edge Operations", null, addEdge, searchEdge, updateEdge, deleteEdge);
        MenuButton fileButton = new MenuButton("File Facilities", null, loadFromFile, exportToFile);
        Button pathButton = new Button("Find Path");
        Button simulate = new Button("Simulate Graph");

        HBox hbox = new HBox(vertexButton, edgeButton, fileButton, pathButton);
        hbox.setAlignment(Pos.TOP_CENTER);
        hbox.setSpacing(10);

        VBox vBox = new VBox(menuHeading, hbox, simulate);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setSpacing(20);

        Scene scene = new Scene(vBox, 900, 400);

        addVertex.setOnAction(e -> {
            addVertex(primaryStage, scene);
        });

        deleteVertex.setOnAction(e -> {
            deleteVertex(primaryStage, scene);
        });

        updateVertex.setOnAction(e -> {
            updateVertex(primaryStage, scene);
        });

        searchVertex.setOnAction(e -> {
            searchVertex(primaryStage, scene);
        });

        addEdge.setOnAction(e -> {
            addEdge(primaryStage, scene);
        });

        searchEdge.setOnAction(e -> {
            searchEdge(primaryStage, scene);
        });

        updateEdge.setOnAction(e -> {
            updateEdge(primaryStage, scene);
        });

        deleteEdge.setOnAction(e -> {
            deleteEdge(primaryStage, scene);
        });

        loadFromFile.setOnAction(e -> {
            loadFromFile(primaryStage, scene);
        });

        exportToFile.setOnAction(e -> {
            exportToFile(primaryStage, scene);
        });

        pathButton.setOnAction(e -> {
            pathButton(primaryStage, scene);
        });

        simulate.setOnAction(e -> {
            simulate(primaryStage, scene);
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
