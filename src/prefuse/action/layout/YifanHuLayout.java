package prefuse.action.layout;


import java.util.Iterator;
import java.util.Random;

import javafx.geometry.Rectangle2D;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.Tuple;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableVisualItem;
import prefuse.util.PrefuseLib;
import prefuse.util.force.ForceItem;
import prefuse.util.force.yifanhu.AbstractForce;
import prefuse.util.force.yifanhu.BarnesHut;
import prefuse.util.force.yifanhu.Displacement;
import prefuse.util.force.yifanhu.ForceVector;
import prefuse.util.force.yifanhu.QuadTree;

/**
 * Layout action that positions visual items along a circle. By default,
 * items are sorted in the order in which they iterated over.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class YifanHuLayout extends Layout {
    
	private Random r = new Random(12345678L);
	
    private float optimalDistance;
    private float relativeStrength;
    private float step;
    private float initialStep;
    private int progress;
    private float stepRatio;
    private int quadTreeMaxLevel;
    private float barnesHutTheta;
    private float convergenceThreshold;
    private boolean adaptiveCooling;
    private Displacement displacement;
    private double energy0;
    private double energy;
    private boolean converged;

    protected String m_nodeGroup;
    protected String m_edgeGroup;
    
    TupleSet ts;
    

    public YifanHuLayout(String group, Displacement displacement) {
        super(group);
        m_nodeGroup = PrefuseLib.getGroupName(group, Graph.NODES);
        m_edgeGroup = PrefuseLib.getGroupName(group, Graph.EDGES);
        this.displacement = displacement;
    }
    
    protected void postAlgo() {
        updateStep();
        if (Math.abs((energy - energy0) / energy) < getConvergenceThreshold()) {
            setConverged(true);
        }
    }

    private Displacement getDisplacement() {
        displacement.setStep(step);
        return displacement;
    }
    
    private AbstractForce getEdgeForce() {
        return new SpringForce(getOptimalDistance());
    }

    private AbstractForce getNodeForce() {
        return new ElectricalForce(getRelativeStrength(), getOptimalDistance());
    }
    
    private void updateStep() {
        if (isAdaptiveCooling()) {
            if (energy < energy0) {
                progress++;
                if (progress >= 5) {
                    progress = 0;
                    setStep(step / getStepRatio());
                }
            } else {
                progress = 0;
                setStep(step * getStepRatio());
            }
        } else {
            setStep(step * getStepRatio());
        }
    }
    
    public void resetPropertiesValues() {
        setStepRatio((float) 0.95);
        setRelativeStrength((float) 0.2);
     //   if (graph != null) {
     //       setOptimalDistance((float) (Math.pow(getRelativeStrength(), 1.0 / 3) * getAverageEdgeLength(graph)));
     //   } else {
            setOptimalDistance(100.0f);
     //   }

        setInitialStep(optimalDistance / 5);
        setStep(initialStep);
        setQuadTreeMaxLevel(10);
        setBarnesHutTheta(1.2f);
        setAdaptiveCooling(true);
        setConvergenceThreshold(1e-4f);
    }
    
    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {
    	
    	ts = m_vis.getGroup(m_group);
    	
    	resetPropertiesValues();
    	circle();
    	initAlgo();
    	   	
        long i = 0;
        while (!isConverged()) {
      //  while (i<300) {
            goAlgo();
            i++;
//            if (iterations != null && iterations.longValue() == i) {
//                break;
//            }
        }
//        endAlgo();
    	
    	
        long end = System.currentTimeMillis();      //结束时间    
        System.out.println("布局运行时间："+end+"毫秒");
        m_vis.notifyAll();
        

    }

	private void goAlgo() {
		
		
		
		Iterator nodes;
		
		QuadTree tree = QuadTree.buildTree(ts, 10);
		BarnesHut barnes = new BarnesHut(getNodeForce());
		barnes.setTheta(getBarnesHutTheta());
		
		nodes = m_vis.visibleItems("graph.nodes");
        while ( nodes.hasNext() ) {
        	TableVisualItem node = (TableVisualItem)nodes.next();
        	ForceItem fitem = (ForceItem)node.get(FORCEITEM);
        	ForceVector layoutData = new ForceVector();
        	layoutData.setX(fitem.location[0]);
        	layoutData.setY(fitem.location[1]);
        	
            ForceVector f = barnes.calculateForce(node, tree);
            layoutData.add(f);
            fitem.location[0] = layoutData.x();
            fitem.location[1] = layoutData.y();
        }
     
        // Apply edge forces.
        Iterator edges = m_vis.visibleItems("graph.edges");
        for (int i=0; edges.hasNext(); i++) {
        	EdgeItem e = (EdgeItem)edges.next();
        	VisualItem n1 = e.getSourceItem();
        	ForceItem fi1 = (ForceItem)n1.get(FORCEITEM);
        	VisualItem n2 = e.getTargetItem();
        	ForceItem fi2 = (ForceItem)n2.get(FORCEITEM);
        	ForceVector f1 = new ForceVector();
        	ForceVector f2 = new ForceVector();
        	
        	f1.setX(fi1.location[0]);
        	f1.setY(fi1.location[1]);
        	f2.setX(fi2.location[0]);
        	f2.setY(fi2.location[1]);
        	
            ForceVector f = getEdgeForce().calculateForce(n1, n2);
            f1.add(f);
            fi1.location[0] = f1.x();
            fi1.location[1] = f1.y();
            f2.subtract(f);
            fi2.location[0] = f2.x();
            fi2.location[1] = f2.y();
        }
        
        
        // Calculate energy and max force.
        energy0 = energy;
        energy = 0;
        double maxForce = 1;
		nodes = m_vis.visibleItems("graph.nodes");
        while ( nodes.hasNext() ) {
        	TableVisualItem node = (TableVisualItem)nodes.next();
        	ForceItem fitem = (ForceItem)node.get(FORCEITEM);
        	ForceVector force = new ForceVector();
        	
        	force.setX(fitem.location[0]);
        	force.setY(fitem.location[1]);
        	
            energy += force.getNorm();
            maxForce = Math.max(maxForce, force.getNorm());
        }
        
        // Apply displacements on nodes.
        nodes = m_vis.visibleItems("graph.nodes");
        while ( nodes.hasNext() ) {
        	TableVisualItem node = (TableVisualItem)nodes.next();
        	ForceItem fitem = (ForceItem)node.get(FORCEITEM);
        	ForceVector force = new ForceVector();
        	
        	force.setX(fitem.location[0]);
        	force.setY(fitem.location[1]);
        	        	
            force.multiply((float) (1.0 / maxForce));
            fitem.location[0] = force.x();
            fitem.location[1] = force.y();
            
            getDisplacement().moveNode(node, force);
            
        }
        
        postAlgo();
        
	}
	
    public void circle() {
    	ts = m_vis.getGroup("graph.nodes");
    	int nn = ts.getTupleCount();
    	
        double radius = 300;
        double cx = 300;
        double cy = 300;
        
        int i = 0;
        Iterator nodes = m_vis.visibleItems("graph.nodes");
        while ( nodes.hasNext() ) {
        	TableVisualItem node = (TableVisualItem)nodes.next();
            double angle = (2*Math.PI*i) / nn;
            double x = Math.cos(angle)*radius + cx;
            double y = Math.sin(angle)*radius + cy;
            setX(node, null, x);
            setY(node, null, y);
            i++;
         }
}

	private void initAlgo() {
        energy = Float.POSITIVE_INFINITY;
                
//        Rectangle2D b = getLayoutBounds();
//        double x, y;
//        double w = b.getWidth();
//        double h = b.getHeight();
//        Iterator iter = getVisualization().visibleItems("graph.nodes");
//        while ( iter.hasNext() ) {
//            VisualItem item = (VisualItem)iter.next();
//            x = (int)(b.getMinX() + r.nextDouble()*w);
//            y = (int)(b.getMinY() + r.nextDouble()*h);
//            setX(item,null,x);
//            setY(item,null,y);
//        }
                
        progress = 0;
        setConverged(false);
        setStep(initialStep);
        
        TupleSet ts = m_vis.getGroup("graph.nodes");
        if ( ts == null ) return;
        try {
            ts.addColumns(FORCEITEM_SCHEMA);
        } catch ( IllegalArgumentException iae ) { /* ignored */ }
		
	}
	

	
	
    /* Maximum level for Barnes-Hut's quadtree */
    public Integer getQuadTreeMaxLevel() {
        return quadTreeMaxLevel;
    }

    public void setQuadTreeMaxLevel(Integer quadTreeMaxLevel) {
        this.quadTreeMaxLevel = quadTreeMaxLevel;
    }

    /* theta is the parameter for Barnes-Hut opening criteria */
    public Float getBarnesHutTheta() {
        return barnesHutTheta;
    }

    public void setBarnesHutTheta(Float barnesHutTheta) {
        this.barnesHutTheta = barnesHutTheta;
    }

    /**
     * @return the optimalDistance
     */
    public Float getOptimalDistance() {
        return optimalDistance;
    }

    /**
     * @param optimalDistance the optimalDistance to set
     */
    public void setOptimalDistance(Float optimalDistance) {
        this.optimalDistance = optimalDistance;
    }

    /**
     * @return the relativeStrength
     */
    public Float getRelativeStrength() {
        return relativeStrength;
    }

    /**
     * @param relativeStrength the relativeStrength to set
     */
    public void setRelativeStrength(Float relativeStrength) {
        this.relativeStrength = relativeStrength;
    }

    /**
     * @param step the step to set
     */
    public void setStep(Float step) {
        this.step = step;
    }

    /**
     * @return the adaptiveCooling
     */
    public Boolean isAdaptiveCooling() {
        return adaptiveCooling;
    }

    /**
     * @param adaptiveCooling the adaptiveCooling to set
     */
    public void setAdaptiveCooling(Boolean adaptiveCooling) {
        this.adaptiveCooling = adaptiveCooling;
    }

    /**
     * @return the stepRatio
     */
    public Float getStepRatio() {
        return stepRatio;
    }

    /**
     * @param stepRatio the stepRatio to set
     */
    public void setStepRatio(Float stepRatio) {
        this.stepRatio = stepRatio;
    }

    /**
     * @return the convergenceThreshold
     */
    public Float getConvergenceThreshold() {
        return convergenceThreshold;
    }

    /**
     * @param convergenceThreshold the convergenceThreshold to set
     */
    public void setConvergenceThreshold(Float convergenceThreshold) {
        this.convergenceThreshold = convergenceThreshold;
    }

    /**
     * @return the initialStep
     */
    public Float getInitialStep() {
        return initialStep;
    }

    /**
     * @param initialStep the initialStep to set
     */
    public void setInitialStep(Float initialStep) {
        this.initialStep = initialStep;
    }
	
    public void setConverged(boolean converged) {
        this.converged = converged;
    }
	
    public boolean isConverged() {
        return converged;
    }
	
    /**
     * Fa = (n2 - n1) * ||n2 - n1|| / K
     *
     * @author Helder Suzuki <heldersuzuki@gephi.org>
     */
    public class SpringForce extends AbstractForce {

        private float optimalDistance;

        public SpringForce(float optimalDistance) {
            this.optimalDistance = optimalDistance;
        }

        @Override
        public ForceVector calculateForce(VisualItem node1, VisualItem node2,
                float distance) {
            ForceVector f = new ForceVector((float)node2.getX() - (float)node1.getX(),
            		(float)node2.getY() - (float)node1.getY());
            f.multiply(distance / optimalDistance);
            return f;
        }

        public void setOptimalDistance(Float optimalDistance) {
            this.optimalDistance = optimalDistance;
        }

        public Float getOptimalDistance() {
            return optimalDistance;
        }
    }

    /**
     * Fr = -C*K*K*(n2-n1)/||n2-n1||
     *
     * @author Helder Suzuki <heldersuzuki@gephi.org>
     */
    public class ElectricalForce extends AbstractForce {

        private float relativeStrength;
        private float optimalDistance;

        public ElectricalForce(float relativeStrength, float optimalDistance) {
            this.relativeStrength = relativeStrength;
            this.optimalDistance = optimalDistance;
        }

        @Override
        public ForceVector calculateForce(VisualItem node1, VisualItem node2,
                float distance) {
            ForceVector f = new ForceVector((float)node2.getX() - (float)node1.getX(),
            		(float)node2.getY() - (float)node1.getY());
            float scale = -relativeStrength * optimalDistance * optimalDistance / (distance * distance);
            if (Float.isNaN(scale) || Float.isInfinite(scale)) {
                scale = -1;
            }

            f.multiply(scale);
            return f;
        }
    }
    
    // ------------------------------------------------------------------------
    // ForceItem Schema Addition
    
    /**
     * The data field in which the parameters used by this layout are stored.
     */
    public static final String FORCEITEM = "_forceItem";
    /**
     * The schema for the parameters used by this layout.
     */
    public static final Schema FORCEITEM_SCHEMA = new Schema();
    static {
        FORCEITEM_SCHEMA.addColumn(FORCEITEM,
                                   ForceItem.class,
                                   new ForceItem());
    }

} // end of class CircleLayout
