package wlrzbView;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.layout.CircleLayout;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.YifanHuLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.activity.Activity;
import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.io.DataIOException;
import prefuse.data.io.DelimitedTextTableReader;
import prefuse.data.io.GraphMLReader;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.force.yifanhu.StepDisplacement;

public class one5 extends Application {
	
	Point2D start;
	Point2D drag;
	
	private Stage primaryStage;
	private HTMLEditor resumeEditor;	
	private final FileChooser fileChooser = new FileChooser();
	
	private Desktop desktop = Desktop.getDesktop();
	
	Display d;
	
	Visualization vis;
	ActionList layoutActionList;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
	    d = new Display();
	    
		// add the scale handler
		addScaleHandler(d);
	    // also add the drag handler to pane
	    addDragHandler(d);
	    
	    MenuBar menuBar = menubar();
//		BorderPane root = new BorderPane();
//		root.setTop(menuBar);
//		root.setCenter(d);
	    
	    Parent zoomPane = new Pane(d);
		
	    VBox root = new VBox();
	    root.getChildren().setAll(
	        menuBar,
	        zoomPane
	    );
		
	    VBox.setVgrow(d, Priority.ALWAYS);
		
	    zoomPane.onMousePressedProperty().bind(d.onMousePressedProperty());
	    zoomPane.onMouseDraggedProperty().bind(d.onMouseDraggedProperty());
	    zoomPane.setStyle("-fx-background-color: yellow;");
		
	    zoomPane.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
	        @Override public void changed(ObservableValue<? extends Bounds> observable, Bounds oldBounds, Bounds bounds) {
	        zoomPane.setClip(new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()));
	        }
	      });
		
		Scene scene = new Scene(root);
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		vis = new Visualization();
		layoutActionList = new ActionList();
	
//		Button btn = new Button("按钮");
//		btn.setOnAction( e -> save(d));
//		d.getChildren().addAll(btn); 

	}
	
//	private void save(Display d1) {
//		Circle c1 = new Circle(0, 0, 40);
//		c1.setFill(Color.LIGHTGRAY);
//		d1.getChildren().addAll(c1); 
//	}

	private void addScaleHandler(Node n) {
		  ScaleHandler scaleEvent = new ScaleHandler(n);
		  n.setOnScroll(scaleEvent);
	}
	
	private void addDragHandler(Node n) {
		  DragAndDropHandler dndEvent = new DragAndDropHandler(n);
		  n.setOnMousePressed(dndEvent);
		  n.setOnMouseDragged(dndEvent);
	}
	
	private MenuBar menubar() {		
		Menu fileMenu = getFileMenu();
		Menu analyseMenu = getAnalyseMenu();
		Menu dataMenu = getDataMenu();

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, analyseMenu, dataMenu);
		
		return menuBar;
	}
	
	public Menu getDataMenu() {
		Menu fileMenu = new Menu("数据");
		
		RadioMenuItem fileImport = new RadioMenuItem("从文件导入...");
		RadioMenuItem circleItem = new RadioMenuItem("从Excel电子表格导入...");
		RadioMenuItem ellipseItem = new RadioMenuItem("保存的导入规范...");
   
		fileImport.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        configureFileChooser(fileChooser);
                        File file = fileChooser.showOpenDialog(primaryStage);                     
                        if (file != null) {
                            try {
								openFile(file);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
                        }
                    }
                });

		// Add menu items to the File menu
		fileMenu.getItems().addAll(fileImport, 
		                           circleItem, 
		                           ellipseItem
		                           );
		return fileMenu;
	}
	
    private static void configureFileChooser(
            final FileChooser fileChooser) {      
                fileChooser.setTitle("View Pictures");
                fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
                );                 
                fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Images", "*.*"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png")
                );
    }
    
    private void openFile(File file) throws Exception {
    	ReadExcelData readExcelData = new ReadExcelData();
    	readExcelData.ReadExcel(file, d, vis, layoutActionList);
//        try {
//            desktop.open(file);
//        } catch (IOException ex) {
//            Logger.getLogger(one5.class.getName()).log(
//                Level.SEVERE, null, ex
//            );
//        }
    }
	
//	private void openFile() {
//		fileDialog.setTitle("Open Resume");
//		File file = fileDialog.showOpenDialog(primaryStage);
//		if (file == null) {
//			return;
//		}
//		
//		try {
//			// Read the file and populate the HTMLEditor		
//			byte[] resume = Files.readAllBytes(file.toPath());
//			resumeEditor.setHtmlText(new String(resume));
//		}
//		catch(IOException e) {
//			e.printStackTrace();
//		}
//	}

	public Menu getFileMenu() {
		Menu optionsMenu = new Menu("文件"); 
		
		// A menu item to draw stroke
		MenuItem strokeItem = new MenuItem("创建新图表");

		optionsMenu.getItems().addAll(strokeItem);
		
		return optionsMenu;
	}
	
	public Menu getAnalyseMenu() {
		Menu analyseMenu = new Menu("分析");
		
		RadioMenuItem layoutSettingItem = new RadioMenuItem("图表布局设置");
						
	    Menu layoutMenu = new Menu("图表布局");
	    
	    CheckMenuItem CircleLayout = new CheckMenuItem("圆形布局");
	    CircleLayout.setOnAction( e -> circleLayoutAction() );
	    
	    CheckMenuItem NetworkLayout = new CheckMenuItem("网络布局");
	    NetworkLayout.setOnAction( e -> networkLayoutAction() );
	    
	    CheckMenuItem ForceLayout = new CheckMenuItem("力导引布局");
	    ForceLayout.setOnAction( e -> forceLayoutAction());
	    
	    layoutMenu.getItems().addAll(CircleLayout, NetworkLayout, ForceLayout);

	    analyseMenu.getItems().addAll(layoutSettingItem ,layoutMenu);
				
		return analyseMenu;
	}
	
	private void circleLayoutAction() {
		for (int i=0; i<layoutActionList.size(); i++) {
			layoutActionList.remove(i);
		}
		
		layoutActionList = new ActionList();
		layoutActionList.add(new RadialTreeLayout("graph")); 
		layoutActionList.add(new RepaintAction());
        vis.putAction("layout", layoutActionList);
        vis.run("layout");
		    
		d.animate();
	}
	
	private void networkLayoutAction() {
		for (int i=0; i<layoutActionList.size(); i++) {
			layoutActionList.remove(i);
		}
		
		layoutActionList = new ActionList();
		layoutActionList.add(new YifanHuLayout("graph.nodes", new StepDisplacement(1f)));
		layoutActionList.add(new RepaintAction());
        vis.putAction("layout", layoutActionList);
        vis.run("layout");
		    
		d.animate();
	}
	
	
	private void forceLayoutAction() {
		for (int i=0; i<layoutActionList.size(); i++) {
			layoutActionList.remove(i);
		}
		
		layoutActionList = new ActionList(Activity.INFINITY);
		layoutActionList.add(new ForceDirectedLayout("graph"));
		layoutActionList.add(new RepaintAction());
        vis.putAction("layout", layoutActionList);
        vis.run("layout");
		    
		d.animate();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	  private class ScaleHandler implements EventHandler<ScrollEvent> {
		  
		    final Node n;
		    double     range = 0.88d;;
		 
		    public ScaleHandler(Node n) {
		      super();
		      this.n = n;
		    }
		 
		    @Override
		    public void handle(ScrollEvent scrollEvent) {
		      if(scrollEvent.isControlDown()) {
		        double factor = (scrollEvent.getDeltaY() < 0) ? range : 1 / range;
		        n.setScaleX(n.getScaleX() * factor);
		        n.setScaleY(n.getScaleY() * factor);
		        scrollEvent.consume();
		      }
		    }
		 
	  }
	  
	  private class DragAndDropHandler implements EventHandler<MouseEvent> {
		  
		    final Node n;
		 
		    private DragAndDropHandler(Node n) {
		      super();
		      this.n = n;
		 
		    }
		 
	    @Override
	    public void handle(MouseEvent me) {
	      if(me.getEventType() == MouseEvent.MOUSE_PRESSED) {
	        start = new Point2D(n.getTranslateX(), n.getTranslateY());
	        drag = n.localToParent(me.getX(), me.getY());
	 
	        // work for scalepane and shape dragging but not when scaling
	        // drag = new Point2D(me.getSceneX(), me.getSceneY());
	      }
	      else if(me.getEventType() == MouseEvent.MOUSE_DRAGGED) {
	        Point2D moveXyPoint = n.localToParent(me.getX(), me.getY());
	 
	        // work for scalepane and shape dragging but not when scaling
	        // moveXyPoint = new Point2D(me.getSceneX(), me.getSceneY());
	 
	        double newX = moveXyPoint.getX() - drag.getX() + start.getX();
	        double newY = moveXyPoint.getY() - drag.getY() + start.getY();
	 
	        n.setTranslateX(newX);
	        n.setTranslateY(newY);
	 
	      }
	      me.consume();
	    }
	 
	  }
	

}
