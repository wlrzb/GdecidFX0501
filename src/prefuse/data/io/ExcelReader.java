package prefuse.data.io;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.collections.ObservableList;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.parser.DataParseException;
import prefuse.data.parser.DataParser;
import prefuse.data.parser.ParserFactory;

public class ExcelReader {
	
	protected ParserFactory m_pf = ParserFactory.getDefaultFactory();
	
    protected static final String SRC = Graph.DEFAULT_SOURCE_KEY;
    protected static final String TRG = Graph.DEFAULT_TARGET_KEY;
    
    public static final String ENTITYID = "name";
    
    protected Schema m_nsch = new Schema();
    protected Schema m_esch = new Schema();
    
    protected Graph m_graph = null;
    protected Table m_nodes;
    protected Table m_edges;
    
    // schema parsing
    protected String m_id;
    protected String m_for;
    protected String m_name;
    protected String m_type;
    protected String m_dflt;
    
    protected StringBuffer m_sbuf = new StringBuffer();
    
    // node,edge,data parsing
    private String m_key;
    private int m_noderow = -1;
    private int m_edgerow = -1;
    private Table m_table = null;
    protected HashMap m_nodeMap = new HashMap();
    protected HashMap m_idMap = new HashMap();
    protected HashSet m_nodeidSet = new HashSet();
       
    public static final String INT = "int";
    public static final String INTEGER = "integer";
    public static final String LONG = "long";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String REAL = "real";
    public static final String BOOLEAN = "boolean";
    public static final String STRING = "string";
    public static final String DATE = "date";
    
    String sourceid;
    String targetid;
    
	Set<String[]> SRTGSet = new HashSet<String[]>(); 
	Set<String[]> TGSRSet = new HashSet<String[]>(); 
    
    public void AddEntityIDCol() {
    	m_name = ENTITYID;
    	m_type = "STRING";
        try {
            Class type = parseType(m_type);
            Object dflt = m_dflt==null ? null : parse(m_dflt, type);
            
            m_nsch.addColumn(m_name, type, dflt);
            
            m_dflt = null;
        } catch ( DataParseException dpe ) {
            error(dpe);
        } 
        m_nsch.lockSchema();
        m_nodes = m_nsch.instantiate();
    	
    }
	

    
    //…Ë÷√±Í ∂
    public Table SetEntityID(ObservableList<Map<String, Object>> items, String Icon1CBSel, String Icon2CBSel) {  
    	
    	sourceid = Icon1CBSel;
    	targetid = Icon2CBSel;
    	
    	Iterator it = items.iterator();
    	while(it.hasNext()) {
    		Map<String, Object> nodeDatamap = (Map<String, Object>) it.next();

    		m_nodeidSet.add(nodeDatamap.get(sourceid));
    		m_nodeidSet.add(nodeDatamap.get(targetid));
    	}
    	
    	Iterator nodeids = m_nodeidSet.iterator();
    	while(nodeids.hasNext()) {
    		String nodeid = (String) nodeids.next();
    		m_noderow = m_nodes.addRow(); 
    		
    		String name = ENTITYID;
    		String val = nodeid;
    		m_nodes.set(m_noderow, name, val);
    		m_nodeMap.put(val, new Integer(m_noderow));
    	}
    	
    	return m_nodes;

    }
    
    public void UniqSRTG(ObservableList<Map<String, Object>> items) {

    	
    	Iterator it = items.iterator();
    	while(it.hasNext()) {
    		Map<String, Object> nodeDatamap = (Map<String, Object>) it.next();
            String[] SRTG = {(String) nodeDatamap.get(sourceid), (String) nodeDatamap.get(targetid)};
            String[] TGSR = {(String) nodeDatamap.get(targetid), (String) nodeDatamap.get(sourceid)};
    		
            if (!(SRTGSet.contains(SRTG) || SRTGSet.contains(TGSR) ||
                  TGSRSet.contains(SRTG) || TGSRSet.contains(TGSR)) ) {
            	SRTGSet.add(SRTG);
            	TGSRSet.add(TGSR);
            }
    	}
    }
    
    public void AddEdgeCol() {
        m_esch.addColumn(SRC, int.class);
        m_esch.addColumn(TRG, int.class);

        m_esch.lockSchema();
        m_edges = m_esch.instantiate();    	
    }
    
    public Table SetScTgVal() {
    	Iterator edges = SRTGSet.iterator();
    	while(edges.hasNext()) {
    		String[] edge = (String[]) edges.next();
    		m_edgerow = m_edges.addRow(); 
    		
            int s = ((Integer) m_nodeMap.get(edge[0])).intValue();
            m_edges.setInt(m_edgerow, SRC, s);
            int t = ((Integer) m_nodeMap.get(edge[1])).intValue();
            m_edges.setInt(m_edgerow, TRG, t);
            
//            m_edges.setString(m_edgerow, SRC, edge[0]);
//            m_edges.setString(m_edgerow, TRG, edge[1]);
    	}
    	return m_edges;
    }
    
    public void ReadField(List<String> columnNames) {     
    	Iterator it = columnNames.iterator();
        while(it.hasNext()) {
            m_name = (String) it.next();
            //m_type = entry.getValue();
      	    m_type = "STRING";
      	    
            try {
                Class type = parseType(m_type);
                Object dflt = m_dflt==null ? null : parse(m_dflt, type);
                
                m_nsch.addColumn(m_name, type, dflt);
                
                m_dflt = null;
            } catch ( DataParseException dpe ) {
                error(dpe);
            }      	    
        } 
        m_nsch.lockSchema();
        m_nodes = m_nsch.instantiate();
  	    
    }
    
//    public void ReadNode(ObservableList<Map<String, Object>> items) {  
//    	
//    	Iterator it = items.iterator();
//    	while(it.hasNext()) {
//    		Map<String, Object> nodeDatamap = (Map<String, Object>) it.next();
//
//    		m_row = m_nodes.addRow();  
//    		
//            for (Entry<String, Object> entry:nodeDatamap.entrySet()) {
//            	String name = entry.getKey();
//                String value = (String) entry.getValue();
//                Class type = m_nodes.getColumnType(name);
//                try {
//                    Object val = parse(value, type);
//                    m_nodes.set(m_row, name, val);
//                } catch ( DataParseException dpe ) {
//                    error(dpe);
//                }
//            }
//    	}
//
//    }
    
 
    
    protected Class parseType(String type) {
        type = type.toLowerCase();
        if ( type.equals(INT) || type.equals(INTEGER) ) {
            return int.class;
        } else if ( type.equals(LONG) ) {
            return long.class;
        } else if ( type.equals(FLOAT) ) {
            return float.class;
        } else if ( type.equals(DOUBLE) || type.equals(REAL)) {
            return double.class;
        } else if ( type.equals(BOOLEAN) ) {
            return boolean.class;
        } else if ( type.equals(STRING) ) {
            return String.class;
        } else if ( type.equals(DATE) ) {
            return Date.class;
        } else {
            error("Unrecognized data type: "+type);
            return null;
        }
    }
    
    protected Object parse(String s, Class type) throws DataParseException {
    	DataParser dp = m_pf.getParser(type);
    	return dp.parse(s);
    }
    
    protected void error(String s) {
        throw new RuntimeException(s);
    }
    
    protected void error(Exception e) {
        throw new RuntimeException(e);
    }
    

} 
