package prefuse.render;



import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import prefuse.Constants;
import prefuse.Display;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.GraphicsLib;
import prefuse.util.StringLib;
import prefuse.visual.VisualItem;




public class LabelRenderer extends AbstractShapeRenderer {

    private static Image img;
    
    ImageView imageView;
    ImageView imageView2;
    Font m_font;
    Text m_text;
    
    protected String m_labelName = "label";
    protected String m_imageName = null;
    
    double shapeX;
    double shapeY;
    double shapeWidth;
    double shapeHeight;
    
    protected int m_imageMargin = 8;
    int k = 5;
	
	static {
		try {
			FileInputStream inputstream = new FileInputStream("C:\\ren30.png");
			img = new Image(inputstream);
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
    public LabelRenderer(String textField) {
        this.setTextField(textField);
    }
	
    public LabelRenderer(String textField, String imageField) {
        setTextField(textField);
        setImageField(imageField);
    }
    
    public void render(Display display, VisualItem item) {
    	computeDimension(item);
//    	k = k + 30;
//        imageView2 = new ImageView(img);
//        imageView2.setX(300+k); 
//        imageView2.setY(300+k);
    	display.getChildren().addAll(imageView);
    	//display.getChildren().addAll(m_text);
    }
	
    protected String getText(VisualItem item) {
        String s = null;
        if ( item.canGetString(m_labelName) ) {
            return item.getString(m_labelName);            
        }
        return s;
    }
    
    public void setTextField(String textField) {
        m_labelName = textField;
    }
    
    public void setImageField(String imageField) {
      //  if ( imageField != null ) m_images = new ImageFactory();
        m_imageName = imageField;
    }


	@Override
	protected Rectangle2D getRawShape(VisualItem item) {
		computeDimension(item);
        return new Rectangle2D(shapeX, 
        		               shapeY, 
        		               shapeWidth,
        		               shapeHeight);
	}

	
	protected void computeDimension(VisualItem item) {
		
		double tx = item.getX();
		double ty = item.getY();
		double ix = item.getX();
		double iy = item.getY();
		
		double size = item.getSize();
		
        double iw = size * Constants.IMAGEVIEWWIDTH;
        double ih = size * Constants.IMAGEVIEWWIDTH;
        double tw = 0;
        double th = 0;
        
        imageView = new ImageView(img);
        imageView.setX(ix); 
        imageView.setY(iy);
        imageView.setFitWidth(iw);
        imageView.setFitHeight(ih);
        //保持原图片的大小比例
        imageView.setPreserveRatio(true);

     
//        ty = iy + ih + m_imageMargin;
//        		
//        m_font = item.getFont();
//
//        //根据配置条件，放大缩小字体
//        m_font = FontLib.getFont(m_font.getName(), m_font.getStyle(),
//                size*m_font.getSize());
//     
//		String text = getText(item);
//        
//        m_text = new Text();
//        m_text.setFont(m_font);
//        m_text.setText(text);
//        tw = m_text.getBoundsInLocal().getWidth();
//        th = m_text.getBoundsInLocal().getHeight();
//        // 文字居中处理
//        tx = ix + iw/2 - tw/2;
//        
//        m_text.setX(tx);
//        m_text.setY(ty);

        shapeX = (iw>tw) ? ix : tx;
        shapeY = iy;
        shapeWidth = (iw>tw) ? iw : tw;
        shapeHeight = ih + m_imageMargin + th;
		
	}
	

	
} // end of class LabelRenderer
