// TableViewMapDataTest.java
package wlrzbView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.ss.usermodel.Sheet;  
import org.apache.poi.ss.usermodel.Workbook;  
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.layout.YifanHuLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.expression.Predicate;
import prefuse.data.io.DataIOException;
import prefuse.data.io.ExcelReader;
import prefuse.data.io.GraphMLReader;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.force.yifanhu.StepDisplacement;

public class ReadExcelData {
	
    private int sheetIndex;

    private FileInputStream inStream;

    Sheet poiSheet;

    List<String> columnNames;
		
    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";
	
	ExcelReader excelReader = new ExcelReader();
	
	ComboBox<String> Icon1CB;
	ComboBox<String> Icon2CB;
		
	ObservableList<Map<String, Object>> items;
	Stage stage;
	Display d;

	public void ReadExcel(File file, Display dp, Visualization vis, ActionList layoutActionList) throws Exception {

        
		this.d = dp;
		        
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        if(file.getName().endsWith(EXCEL_XLS)){     //Excel&nbsp;2003
            wb = new HSSFWorkbook(in);
        }else if(file.getName().endsWith(EXCEL_XLSX)){    // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        }
                
        //得到第一页 sheet 
        //页Sheet是从0开始索引的 
        poiSheet = wb.getSheetAt(0); 
		
		TableView<Map> table = new TableView<>();
		items = this.getMapData();
		table.getItems().addAll(items);
		this.addColumns(table);
				
//		HBox hBox = new HBox(table); 
//		Button btn = new Button("按钮");
//		btn.setOnAction( e -> save());
		
//		BorderPane root = new BorderPane();
//		root.setCenter(hBox);
//		root.setBottom(btn);
		
		stage = new Stage();
        stage.setWidth(1000);
        stage.setHeight(800);
		
		final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(table);
        
        Label Icon1 = new Label("图标1(实体):");
        Label Icon2 = new Label("图标2(实体):");
        Icon1CB = new ComboBox<>();
        Icon1CB.getItems().addAll(columnNames);
        Icon2CB = new ComboBox<>();
        Icon2CB.getItems().addAll(columnNames);
		HBox rowIcon = new HBox(Icon1, Icon1CB, Icon2, Icon2CB);
		rowIcon.setSpacing(10);
		

        vbox.getChildren().addAll(borderPane);
        vbox.getChildren().addAll(rowIcon);
        
		Button btn = new Button("导入");
		btn.setOnAction( e -> ImportData(vis, layoutActionList));
		vbox.getChildren().add(btn);

        Scene scene = new Scene(vbox);

		stage.setScene(scene);
		stage.setTitle("Using a Map as items in a TableView");
		stage.show();	
		
	}
	
	private void ImportData(Visualization vis, ActionList layoutActionList) {
		if (Icon1CB.getValue() == null || Icon1CB.getValue().toString().isEmpty() || 
		    Icon2CB.getValue() == null || Icon2CB.getValue().toString().isEmpty() ) {
			return;
		}
		
		String  Icon1CBSel = Icon1CB.getValue().toString();
		String  Icon2CBSel = Icon2CB.getValue().toString();
		
		excelReader.AddEntityIDCol();
		Table m_nodes = excelReader.SetEntityID(items, Icon1CBSel, Icon2CBSel);
		excelReader.UniqSRTG(items);
		excelReader.AddEdgeCol();
		Table m_edges = excelReader.SetScTgVal();
		stage.close();
		
	    Graph graph = new Graph(m_nodes, m_edges, false);
        //Visualization vis = new Visualization();
        vis.add("graph", graph);
        vis.setInteractive("graph.edges", null, false);
      
        d.AddVis(vis);
				
		LabelRenderer r = new LabelRenderer("name");
		vis.setRendererFactory(new DefaultRendererFactory(r));
		
		//ActionList layout = new ActionList(Activity.INFINITY);
		//ActionList layout = new ActionList();
		layoutActionList.add(new YifanHuLayout("graph.nodes", new StepDisplacement(1f)));
		layoutActionList.add(new RepaintAction());
        vis.putAction("layout", layoutActionList);
        vis.run("layout");
		    
		d.animate();
	}

	public ObservableList<Map<String, Object>> getMapData() {
		ObservableList<Map<String, Object>> items = FXCollections.<Map<String, Object>>observableArrayList();

		int rowNum = poiSheet.getLastRowNum();
		int startRow = 1;
		columnNames = genColumnNames(poiSheet);
		for (int i = startRow; i < rowNum+1; i++) {
			Row row = poiSheet.getRow(i);
			int colNum = row.getLastCellNum();
			Map<String, Object> map = new HashMap<>();
			for (int j = 0; j < colNum; j++) {
				Cell cell = row.getCell(j);
				String cellValue = null;
				if (cell != null) {
					cellValue = getCellValue(cell);
				}
				else {
                    cellValue = "";
                }
				map.put(columnNames.get(j), cellValue);
			}
			items.add(map);
		}
				
		return items;
	}
	
    public List<String> genColumnNames(Sheet sheet) {
    	List<String> ColumnNames = new ArrayList<>();
		Row row = sheet.getRow(0);
		int colNum = row.getLastCellNum();
		for (int i=0; i<colNum; i++) {
			Cell cell = row.getCell(i);
			ColumnNames.add(cell.getStringCellValue());
		}
    	return ColumnNames;
    }
    
    public String getCellValue(Cell cell) {
    	String cellValue;
		switch (cell.getCellTypeEnum()) {
        case NUMERIC:
            // 表格中返回的数字类型是科学计数法因此不能直接转换成字符串格式
            cellValue = new BigDecimal(cell.getNumericCellValue()).toPlainString();
            break;
        case STRING:
            cellValue = cell.getStringCellValue();
            break;
        case FORMULA:
            cellValue = new BigDecimal(cell.getNumericCellValue()).toPlainString();
            break;
        case BLANK:
            cellValue = "";
            break;
        case BOOLEAN:
            cellValue = Boolean.toString(cell.getBooleanCellValue());
            break;
        case ERROR:
            cellValue = "ERROR";
            break;
        default:
            cellValue = "UNDEFINE";
		}
		return cellValue;
    }
	
	
	public void addColumns(TableView table) { 
		
		Iterator<String> colnameIterator = columnNames.iterator();
	    while (colnameIterator.hasNext()) 
	    {
	        String columnName = (String)colnameIterator.next();
	        TableColumn<Map, String> column = new TableColumn<>(columnName);
	        column.setCellValueFactory(new MapValueFactory<>(columnName));
	        table.getColumns().add(column);
	    }
		
		
	}
	
//	private void openBook() throws Exception{
//	    try {
//	        File myFile = new File("test2.xls");
//	        inStream = new FileInputStream(myFile);
//	        wb = WorkbookFactory.create(inStream);
//	        //poiWorkbook = new XSSFWorkbook (inStream);
//
//	    } catch (FileNotFoundException e) {  
//            e.printStackTrace();  
//        } catch (IOException e) {  
//            e.printStackTrace();  
//        }  
//	}
//
//	private void closeBook() throws Exception{
//	    try {
//	        poiWorkbook.close();
//	        inStream.close();
//	    }catch (Exception e) {
//	        e.printStackTrace();
//	        throw e;
//	    }
//	}
//	
}
