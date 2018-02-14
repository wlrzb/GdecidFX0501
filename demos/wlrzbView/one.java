package wlrzbView;



import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.layout.RandomLayout;
import prefuse.data.Table;
import prefuse.data.io.DelimitedTextTableReader;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;

public class one extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
        String datafile = "/amazon.txt";
        Table data = null;
        try {
            data = (new DelimitedTextTableReader()).readTable(datafile);
            data.addColumn("image","CONCAT('/images/',id,'.01.MZZZZZZZ.jpg')");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
		
		Visualization m_vis = new Visualization();
		Display d = new Display(m_vis);
		
		m_vis.addTable("data", data);
		
		LabelRenderer nodeRenderer = new LabelRenderer(null, "image");
		m_vis.setRendererFactory(new DefaultRendererFactory(nodeRenderer));
		
        ActionList init = new ActionList();
        init.add(new RandomLayout());
        m_vis.putAction("init", init);
        m_vis.run("init");
		
		// Display d = new Display(m_vis);
		
		Scene scene = new Scene(d, 800, 600);
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		d.paintDisplay();
		
		
//        // 载入node和edge的原始数据
//        TupleSet ts = new TupleSet(); 
//        
//        new CircleLayout().run(ts);;
//        d.add(ts);
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
