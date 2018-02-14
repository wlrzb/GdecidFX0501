package prefuse;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import prefuse.Display2;
import prefuse.Visualization;
import prefuse.data.expression.AndPredicate;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.util.display.RenderingQueue;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.VisiblePredicate;

public class Display extends Pane{
	
//	private static final Display display = new Display(); 
	
//	TupleSet ts;
	Image img;
	
	protected Visualization m_vis;
	
	protected AndPredicate  m_predicate = new AndPredicate();
	
	// rendering queue
	protected RenderingQueue m_queue = new RenderingQueue();
	protected int            m_visibleCount = 0;
	
	
	int k =0;
	
	
	
	public Display() {
		Button btn = new Button("按钮");
		this.getChildren().addAll(btn);

	}
	
    public Display(Visualization visualization) {
        this(visualization, (Predicate)null);
    }
    
    public Display(Visualization visualization, String predicate) {
        this(visualization,
                (Predicate)ExpressionParser.parse(predicate, true));
    }
	
    public Display(Visualization visualization, Predicate predicate) {
    	this.setPrefSize(1440, 900);
		Button btn = new Button("按钮");
		this.getChildren().addAll(btn);
    	setVisualization(visualization);
    	setPredicate(predicate);
    	//animate();
    }
    
    public void animate() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
            	paintDisplay();
            }
        };
        timer.start();
    }
    
    public void onUpdate() {
    	k = k + 20;
    	Rectangle rec1 = new Rectangle(400+k, 400+k, 40, 20);
    	this.getChildren().add(rec1);
    }
    
    public void setVisualization(Visualization vis) {
        // TODO: synchronization?
        if ( m_vis == vis ) {
            // nothing need be done
            return;
        } else if ( m_vis != null ) {
            // remove this display from it's previous registry
            m_vis.removeDisplay(this);
        }
        m_vis = vis;
        if ( m_vis != null )
            m_vis.addDisplay(this);
    }
    
    public void paintDisplay() {
    	synchronized ( m_vis ) {
    		long end = System.currentTimeMillis();      //结束时间    
    		System.out.println("主线运行时间："+end+"毫秒");
    		this.getChildren().clear();
    		m_queue.clear();   // clear the queue
    		Iterator items = m_vis.items(m_predicate);
    		for ( m_visibleCount=0; items.hasNext(); ++m_visibleCount ) {
    			VisualItem item = (VisualItem)items.next();
             
    			m_queue.addToRenderQueue(item);
    		}
         
    		// sort the rendering queue
    		m_queue.sortRenderQueue();
    	
    		// render each visual item
    		for ( int i=0; i<m_queue.rsize; ++i ) {
    			m_queue.ritems[i].render(this);
    		}
    			

    		try{
    			m_vis.wait();
    		} catch (Exception e) {}
    
    	}

    }
    
    /**
     * Sets the filtering Predicate used to control what items are drawn by
     * this Display.
     * @param expr the filtering predicate to use. The predicate string will be
     * parsed by the {@link prefuse.data.expression.parser.ExpressionParser}.
     * If the parse fails or does not result in a
     * {@link prefuse.data.expression.Predicate} instance, an exception will
     * be thrown.
     */
    public void setPredicate(String expr) {
        Predicate p = (Predicate)ExpressionParser.parse(expr, true);
        setPredicate(p);
    }
    
    /**
     * Sets the filtering Predicate used to control what items are drawn by
     * this Display.
     * @param p the filtering {@link prefuse.data.expression.Predicate} to use
     */
    public synchronized void setPredicate(Predicate p) {
        if ( p == null ) {
            m_predicate.set(VisiblePredicate.TRUE);
        } else {
            m_predicate.set(new Predicate[] {p, VisiblePredicate.TRUE});
        }
    }
    
    
	
//	public static Display getInstance() {
//		return display;
//	}
	
//	public void add(TupleSet ts) {
//		this.ts = ts;
//		paintDisplay();
//	}
	
	
//	public void paintComponent(Graphics g) {	
//		//Graphics2D g2D = (Graphics2D) g;
//		Graphics2D g2D = new Graphics2D();
//		paintDisplay(g2D);
//	}
	
//	public void paintDisplay() {
//		LabelRenderer labelRenderer = new LabelRenderer();
//		Iterator nodes = ts.getNodes().entrySet().iterator();
//		for (int i=0; nodes.hasNext(); i++) {
//            Map.Entry node = (Map.Entry)nodes.next();
//            TableNodeItem tableNodeItem = (TableNodeItem) node.getValue();
//			labelRenderer.Render(this, tableNodeItem);
//		}
//		
////		EdgeRenderer edgeRenderer = new EdgeRenderer();
////		Iterator edges = ts.getEdges().iterator();
////		for (int i=0; edges.hasNext(); i++) {
////            TableEdgeItem tableEdgeItem = (TableEdgeItem)edges.next();
////			edgeRenderer.Render(this, tableEdgeItem, ts.getNodes());
////		}
//	}

	
	

}
