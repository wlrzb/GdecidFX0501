package prefuse.render;



import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Line;
import prefuse.Constants;
import prefuse.Display;
import prefuse.util.GraphicsLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

public class EdgeRenderer extends AbstractShapeRenderer {
		
    protected int     m_xAlign1   = Constants.CENTER;
    protected int     m_yAlign1   = Constants.CENTER;
    protected int     m_xAlign2   = Constants.CENTER;
    protected int     m_yAlign2   = Constants.CENTER;
    
    protected Point2D m_tmpPoints[]  = new Point2D[2];
    protected Point2D m_ctrlPoints[] = new Point2D[2];
    protected Point2D m_isctPoints[] = new Point2D[2];
    
    
    public EdgeRenderer() {
        m_tmpPoints[0]  = new Point2D(0, 0);
        m_tmpPoints[1]  = new Point2D(0, 0);
        m_ctrlPoints[0] = new Point2D(0, 0);
        m_ctrlPoints[1] = new Point2D(0, 0);      
        m_isctPoints[0] = new Point2D(0, 0);
        m_isctPoints[1] = new Point2D(0, 0);      
    }
	
	public void render(Display display, VisualItem item) {
        EdgeItem   edge = (EdgeItem)item;
        VisualItem item1 = edge.getSourceItem();
        VisualItem item2 = edge.getTargetItem();	
        
        m_tmpPoints[0] = getAlignedPoint(item1.getBounds(),
        				m_xAlign1, m_yAlign1);
        m_tmpPoints[1] = getAlignedPoint(item2.getBounds(),
                		m_xAlign2, m_yAlign2);
        
        
        EdgeItem e = (EdgeItem)item;
        Point2D start = null, end = null;
        start = m_tmpPoints[0];
        end   = m_tmpPoints[1];
        
//        VisualItem src = e.getSourceItem(); 
//        Object[] objectsrc = GraphicsLib.intersectLineRectangle(start, end,
//                src.getBounds(), m_isctPoints);
//        int i = (int)objectsrc[0];
//        m_isctPoints = (Point2D[])objectsrc[1];
//        if ( i > 0 ) start = m_isctPoints[0];
//        
//        VisualItem dest = e.getTargetItem(); 
//        Object[] objectdest = GraphicsLib.intersectLineRectangle(start, end,
//                dest.getBounds(), m_isctPoints);
//        int j = (int)objectdest[0];
//        m_isctPoints = (Point2D[])objectdest[1];
//        if ( j > 0 ) end = m_isctPoints[0];
//        
        double n1x = start.getX();
        double n1y = start.getY();
        double n2x = end.getX();
        double n2y = end.getY();
        
        
        Line m_line = new Line();
        
        m_line.setStartX(n1x);
        m_line.setStartY(n1y);
        m_line.setEndX(n2x);
        m_line.setEndY(n2y);
        
        display.getChildren().addAll(m_line);
	}
	
	
    protected Point2D getAlignedPoint(Rectangle2D r, int xAlign, int yAlign) {
        double x = r.getMinX(), y = r.getMinY(), w = r.getWidth(), h = r.getHeight();
        if ( xAlign == Constants.CENTER ) {
            x = x+(w/2);
        } else if ( xAlign == Constants.RIGHT ) {
            x = x+w;
        }
        if ( yAlign == Constants.CENTER ) {
            y = y+(h/2);
        } else if ( yAlign == Constants.BOTTOM ) {
            y = y+h;
        }
        return new Point2D(x,y);
    }


	@Override
	public void setBounds(VisualItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Rectangle2D getRawShape(VisualItem item) {
		// TODO Auto-generated method stub
		return null;
	}
 

} 
