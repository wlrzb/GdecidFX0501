package wlrzbView;



import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.layout.CircleLayout;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.YifanHuLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.io.DataIOException;
import prefuse.data.io.DelimitedTextTableReader;
import prefuse.data.io.GraphMLReader;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.force.yifanhu.StepDisplacement;

public class one3 extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
        Graph graph = null;
        try {
            graph = new GraphMLReader().readGraph("/socialnet.xml");
        } catch ( DataIOException e ) {
            e.printStackTrace();
            System.err.println("Error loading graph. Exiting...");
            System.exit(1);
        }
		
        Visualization vis = new Visualization();
        vis.add("graph", graph);
        vis.setInteractive("graph.edges", null, false);
		
		
		Display d = new Display(vis);
		
		LabelRenderer r = new LabelRenderer("name");
		vis.setRendererFactory(new DefaultRendererFactory(r));
		
		//ActionList layout = new ActionList(Activity.INFINITY);
		ActionList layout = new ActionList();
        //layout.add(new CircleLayout("graph.nodes"));
		layout.add(new YifanHuLayout("graph.nodes", new StepDisplacement(1f)));
		layout.add(new RepaintAction());
        vis.putAction("layout", layout);
        vis.run("layout");
		
		Scene scene = new Scene(d);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	
        
     //   d.paintDisplay();
        d.animate();
		

	
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
