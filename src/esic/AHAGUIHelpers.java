package esic;

import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;

public class AHAGUIHelpers
{
	public static void applyTheme(Font uiFont) //Apply theme for JCheckbox, JComboBox, JLabel,  JSrollPane, JTabbedPane, JTable
	{ // Need to figure out what key something is named? The stuff below will search and then exit for keys containing the string
		//	java.util.List<String> t=new java.util.ArrayList<String>(2048);
		//	for (Object key : javax.swing.UIManager.getLookAndFeelDefaults().keySet()) { t.add(key.toString()); }
		//	java.util.Collections.sort(t);
		//	for (String key : t ) { if (key.toLowerCase().contains("background")) { System.out.println(key); } }
		System.err.println("Applying theme.");
		try 
		{ 
			javax.swing.InputMap im = (javax.swing.InputMap) javax.swing.UIManager.get("TextField.focusInputMap"); //TODO other input map save/store might be required later if we have more components with inputmaps
			javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getCrossPlatformLookAndFeelClassName() ); 
			javax.swing.UIManager.put("TextField.focusInputMap", im); //restore the original LAF input map (keep windows control c/v and macOS command c/v as they should be.
		}
		catch (Exception e) { System.err.println("Failed to set look and feel:"); e.printStackTrace(); }
		java.awt.Color backgroundColor=java.awt.Color.BLACK, foregroundColor=java.awt.Color.GREEN, accentColor=java.awt.Color.DARK_GRAY.darker().darker(), lightAccent=java.awt.Color.DARK_GRAY;//, dbugcolor=java.awt.Color.ORANGE;
		javax.swing.UIManager.put("Button.background", accentColor.brighter().brighter());
		javax.swing.UIManager.put("Button.darkShadow", backgroundColor);
		javax.swing.UIManager.put("Button.focus", accentColor.brighter().brighter()); //remove selection reticle
		javax.swing.UIManager.put("Button.font",uiFont);
		javax.swing.UIManager.put("Button.foreground", foregroundColor);
		javax.swing.UIManager.put("Button.select", java.awt.Color.GRAY.darker());
		javax.swing.border.Border b=new javax.swing.plaf.basic.BasicBorders.RolloverButtonBorder( lightAccent.brighter().brighter(),  lightAccent.brighter().brighter(), lightAccent.brighter().brighter(), lightAccent.brighter().brighter());
		javax.swing.UIManager.put("Button.border", new javax.swing.border.CompoundBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY),b ));
		javax.swing.UIManager.put("CheckBox.foreground", foregroundColor);
		javax.swing.UIManager.put("CheckBox.background", backgroundColor);
		javax.swing.UIManager.put("CheckBox.focus", backgroundColor);
		javax.swing.UIManager.put("CheckBox.font", uiFont);
		javax.swing.UIManager.put("CheckBox.gradient", java.util.Arrays.asList( new Object[] {Float.valueOf(0f),Float.valueOf(0f), java.awt.Color.LIGHT_GRAY, java.awt.Color.LIGHT_GRAY, java.awt.Color.GRAY.brighter() }));
		//javax.swing.UIManager.put("CheckBox.icon", new AHACheckBoxIcon(13,13)); //disabled because it does odd things on openjdk/linux. TODO: fixable? if not, delete related artifacts
		javax.swing.UIManager.put("ComboBox.background", accentColor.brighter().brighter());
		javax.swing.UIManager.put("ComboBox.font", uiFont); 
		javax.swing.UIManager.put("ComboBox.foreground", foregroundColor);
		javax.swing.UIManager.put("ComboBox.selectionForeground", foregroundColor);
		javax.swing.UIManager.put("ComboBox.selectionBackground", accentColor.brighter().brighter()); 
		javax.swing.UIManager.put("ComboBox.border", BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY),BorderFactory.createLineBorder(accentColor.brighter().brighter(),2)));
		javax.swing.UIManager.put("ComboBoxUI", javax.swing.plaf.basic.BasicComboBoxUI.class.getName());
		javax.swing.UIManager.put("Label.foreground", foregroundColor);
		javax.swing.UIManager.put("Label.background", backgroundColor);
		javax.swing.UIManager.put("Label.font", uiFont);
		
		javax.swing.UIManager.put("List.foreground", foregroundColor); //primarily here for the JFileChooser background file list pane
		javax.swing.UIManager.put("List.background", backgroundColor);
		javax.swing.UIManager.put("List.selectionForeground", backgroundColor);
		javax.swing.UIManager.put("List.selectionBackground", foregroundColor);
		javax.swing.UIManager.put("List.focusCellHighlightBorder", foregroundColor);
		javax.swing.UIManager.put("List.font", uiFont);
		javax.swing.UIManager.put("List.background", backgroundColor);
		javax.swing.UIManager.put("OptionPane.foreground", foregroundColor);
		javax.swing.UIManager.put("OptionPane.messageForeground", foregroundColor);
		javax.swing.UIManager.put("OptionPane.background", backgroundColor);
		javax.swing.UIManager.put("OptionPane.font", uiFont);
		
		javax.swing.UIManager.put("Frame.foreground", foregroundColor);
		javax.swing.UIManager.put("Frame.background", backgroundColor);
		javax.swing.UIManager.put("Panel.foreground", foregroundColor);
		javax.swing.UIManager.put("Panel.background", backgroundColor);
		javax.swing.UIManager.put("ProgressBar.selectionForeground", foregroundColor);
		javax.swing.UIManager.put("ProgressBar.selectionBackground", backgroundColor);
		javax.swing.UIManager.put("ProgressBar.foreground", foregroundColor);
		javax.swing.UIManager.put("ProgressBar.background", backgroundColor);
		javax.swing.UIManager.put("ProgressBar.border", BorderFactory.createLineBorder(accentColor.brighter().brighter(),2));
		javax.swing.UIManager.put("ProgressBar.font", uiFont);
		
		javax.swing.UIManager.put("ScrollBar.track", backgroundColor);
		javax.swing.UIManager.put("ScrollBar.thumbDarkShadow", backgroundColor);
		javax.swing.UIManager.put("ScrollBar.thumb", accentColor.brighter().brighter().brighter());
		javax.swing.UIManager.put("ScrollBar.thumbHighlight", accentColor.brighter().brighter().brighter());
		javax.swing.UIManager.put("ScrollBar.thumbShadow", accentColor.brighter().brighter().brighter());
		javax.swing.UIManager.put("ScrollBarUI", javax.swing.plaf.basic.BasicScrollBarUI.class.getName() );
		javax.swing.UIManager.put("ScrollBar.width", 8);
		javax.swing.UIManager.put("ScrollPane.foreground", foregroundColor);
		javax.swing.UIManager.put("ScrollPane.background", backgroundColor);
		javax.swing.UIManager.put("TabbedPane.foreground", foregroundColor);
		javax.swing.UIManager.put("TabbedPane.background", backgroundColor);
		javax.swing.UIManager.put("TabbedPane.light", backgroundColor);
		javax.swing.UIManager.put("TabbedPane.borderHightlightColor", backgroundColor);
		javax.swing.UIManager.put("TabbedPane.selected", accentColor.brighter().brighter().brighter());
		javax.swing.UIManager.put("TabbedPane.focus",accentColor.brighter().brighter().brighter());
		javax.swing.UIManager.put("TabbedPane.selectHighlight",accentColor.brighter().brighter().brighter());
		javax.swing.UIManager.put("TabbedPane.darkShadow", accentColor.brighter().brighter().brighter()); //removes difficult to see blue glow around inactive tab edge
		javax.swing.UIManager.put("TabbedPane.contentBorderInsets", new java.awt.Insets(0,0,0,0));
		javax.swing.UIManager.put("TabbedPane.tabsOverlapBorder", true); 
		javax.swing.UIManager.put("TableUI", javax.swing.plaf.basic.BasicTableUI.class.getName() );
		javax.swing.UIManager.put("Table.foreground", foregroundColor);
		javax.swing.UIManager.put("Table.background", backgroundColor);
		javax.swing.UIManager.put("Table.focusCellForeground", foregroundColor);
		javax.swing.UIManager.put("Table.focusCellBackground", backgroundColor);
		javax.swing.UIManager.put("Table.dropCellBackground", backgroundColor);
		javax.swing.UIManager.put("Table.gridColor", accentColor);
		javax.swing.UIManager.put("Table.font", uiFont); //
		javax.swing.UIManager.put("Table.selectionBackground", foregroundColor.darker());
		javax.swing.UIManager.put("Table.focusCellHighlightBorder", foregroundColor.darker());
		javax.swing.UIManager.put("Table.selectionForeground", backgroundColor);
		javax.swing.UIManager.put("Table.sortIconColor", foregroundColor); 
		javax.swing.UIManager.put("Table.ascendingSortIcon",new AHASortIcon(true));
		javax.swing.UIManager.put("Table.descendingSortIcon",new AHASortIcon(false));
		javax.swing.UIManager.put("TableHeaderUI", javax.swing.plaf.basic.BasicTableHeaderUI.class.getName() );
		javax.swing.UIManager.put("TableHeader.foreground", foregroundColor);
		javax.swing.UIManager.put("TableHeader.background", accentColor);
		javax.swing.UIManager.put("TableHeader.cellBorder", new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
		javax.swing.UIManager.put("TableHeader.font", uiFont); 
		
		javax.swing.UIManager.put("TextField.foreground", foregroundColor);
		javax.swing.UIManager.put("TextField.caretForeground", foregroundColor);
		javax.swing.UIManager.put("TextField.background", backgroundColor);
		javax.swing.UIManager.put("TextField.focus", backgroundColor);
		javax.swing.UIManager.put("TextField.font", uiFont);
		javax.swing.UIManager.put("TextField.border", new javax.swing.border.LineBorder(java.awt.Color.GRAY,1));
		
		javax.swing.UIManager.put("ToolTip.foreground", java.awt.Color.BLACK);
		javax.swing.UIManager.put("ToolTip.border", java.awt.Color.WHITE);
		javax.swing.UIManager.put("ToolTip.background", java.awt.Color.WHITE);
		javax.swing.UIManager.put("ToolTip.font", uiFont);
		javax.swing.UIManager.put("Viewport.foreground", foregroundColor);
		javax.swing.UIManager.put("Viewport.background", accentColor);
		javax.swing.UIManager.put("window", backgroundColor);
	}

	public static class AHASortIcon implements javax.swing.Icon
	{ //TODO maybe clean this up a little so it can be variably sized...but on the other hand, it looks good exactly with these numbers.
		boolean thingToDraw;
		public AHASortIcon(boolean icon) { thingToDraw=icon; }
		public void paintIcon(Component c, java.awt.Graphics g, int x, int y)
		{
			java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
			//System.out.printf("paintIcon called x=%d y=%d\n",x,y);
			g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(java.awt.Color.GREEN);
			if (thingToDraw==true) { g2.fillPolygon(new int[]{x,x+6,x+3}, new int[]{y+5,y+5,y}, 3); }
			else { g2.fillPolygon(new int[]{x,x+6,x+3}, new int[]{y+1,y+1,y+6}, 3); }
		}
		public int getIconWidth() { return 6; }
		public int getIconHeight() { return 6; }
	}
	
//	public static class AHACheckBoxIcon implements javax.swing.Icon
//	{ //TODO maybe clean this up a little so it can be variably sized...but on the other hand, it looks good exactly with these numbers.
//		private int width, height;
//		public java.awt.Color foreground=java.awt.Color.GREEN, background=java.awt.Color.DARK_GRAY, highlight=java.awt.Color.GRAY;
//		public java.awt.Font font=new java.awt.Font(java.awt.Font.MONOSPACED,java.awt.Font.BOLD,12);
//		public java.awt.Stroke highlightStroke=new java.awt.BasicStroke(2);
//		public AHACheckBoxIcon(int w, int h) { width=w; height=h; }
//		public void paintIcon(Component c, java.awt.Graphics g, int x, int y)
//		{
//			java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
//			boolean selected=false, rollover=false;
//			try 
//			{ 
//				javax.swing.AbstractButton aButton=(javax.swing.AbstractButton)c;
//				javax.swing.ButtonModel bModel=aButton.getModel(); 
//				selected=bModel.isSelected();
//				rollover=bModel.isRollover();
//			} catch (Exception e) {}
//			g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
//			g2.setColor(background);
//			g2.setFont(font);
//			g2.fillRect(x, y, width, height); 
//			if (selected) { g2.setColor(foreground); g2.drawString("√", x+3, y+height-1); }
//			if (rollover)
//			{ 
//				g2.setColor(highlight);
//				g2.setStroke(highlightStroke);
//				g2.drawPolygon(new int[]{x,x+width,x+width,x}, new int[]{y,y,y+height,y+height}, 4);
//			}
//		}
//		public int getIconWidth() { return width; }
//		public int getIconHeight() { return height; }
//	}
//java.awt.event.MouseAdapter passThroughToGraphView=new java.awt.event.MouseAdapter()
//{
//	public void mousePressed(java.awt.event.MouseEvent me)  { m_viewPanel.dispatchEvent(javax.swing.SwingUtilities.convertMouseEvent(me.getComponent(), me, m_viewPanel)); } 
//	public void mouseReleased(java.awt.event.MouseEvent me) { m_viewPanel.dispatchEvent(javax.swing.SwingUtilities.convertMouseEvent(me.getComponent(), me, m_viewPanel)); }
//	public void mouseDragged(java.awt.event.MouseEvent me)  { m_viewPanel.dispatchEvent(javax.swing.SwingUtilities.convertMouseEvent(me.getComponent(), me, m_viewPanel)); } 
//	//public void mouseMoved(java.awt.event.MouseEvent me) {  m_viewPanel.dispatchEvent(javax.swing.SwingUtilities.convertMouseEvent(me.getComponent(), me, m_viewPanel)); }
//	//public void mouseClicked(java.awt.event.MouseEvent me) {  m_viewPanel.dispatchEvent(javax.swing.SwingUtilities.convertMouseEvent(me.getComponent(), me, m_viewPanel)); }
//	//public void mouseEntered(java.awt.event.MouseEvent me) {  m_viewPanel.dispatchEvent(javax.swing.SwingUtilities.convertMouseEvent(me.getComponent(), me, m_viewPanel)); }
//	//public void mouseExited(java.awt.event.MouseEvent me) {  m_viewPanel.dispatchEvent(javax.swing.SwingUtilities.convertMouseEvent(me.getComponent(), me, m_viewPanel)); }
//};

	public static String styleToolTipText(String s) //format all tool tip texts by making them HTML (so we can apply text effects, and more importantly line breaks)
	{
		if (s.length()>60)
		{
			StringBuilder sb=new StringBuilder("");
			int currentLineLength=0;
			for (int i=0;i<s.length();i++)
			{
				s.replaceAll("<BR>", "\n");
				char c=s.charAt(i);
				if ( (c=='\n') || ((c==',' || c=='.' || c==';') && currentLineLength>50) || (c==' ' && currentLineLength>75))
				{
					sb.append(c);
					if (currentLineLength>0) { sb.append("<BR>"); } //should collapse a sequence of <BR><BR><BR>
					currentLineLength=0;
				}
				else
				{
					sb.append(c);
					currentLineLength++;
				}
			}
			s=sb.toString();
		}
		return "<html><p style='font-style:bold;color:black;background:white;'>"+s+"</p></html>";
	}
	
//	public static class AttributedLabel extends javax.swing.JLabel {
//
//    java.text.AttributedString str=null;
//    int length=0;
//    public AttributedLabel(Object o)
//    {
//    	if (o instanceof String) { str = new java.text.AttributedString((String)o); }
//			if (o instanceof java.text.AttributedString) { str = (java.text.AttributedString)o; }
//			if (str!=null)
//			{
//				length=str.getIterator().getEndIndex();
//			}
//    }
//    public void paintComponent(java.awt.Graphics g){
//        // Render the string
//    	if (str==null || length < 1) { return; }
//    	java.awt.Graphics2D g2=(java.awt.Graphics2D)g;
//    	java.awt.Rectangle r=g.getClipBounds();
//    	//System.out.println("height="+r.height);
//    	java.awt.Font uiFont=(java.awt.Font)javax.swing.UIManager.get("Label.font");
//			if (uiFont!=null) 
//			{ 
//				g2.setFont(uiFont); 
//				str.addAttribute(java.awt.font.TextAttribute.FONT, uiFont); 
//			}
//			
//			g2.setRenderingHint(
//	        java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
//	        java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//			
//      	g2.drawString(str.getIterator(), 1, r.height-3);
////	      java.text.AttributedCharacterIterator it = str.getIterator();
////	      StringBuilder stringBuilder = new StringBuilder();
////	
////	      char ch = it.current();
////	      while( ch != java.text.CharacterIterator.DONE)
////	      {
////	          stringBuilder.append( ch);
////	          ch = it.next();
////	      }
////	      System.out.println(stringBuilder.toString());
//    }
//
//}
	
//	public static class AttributedLabelRenderer extends javax.swing.table.DefaultTableCellRenderer {
//    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
//    {
//    	return new AttributedLabel(value);
//    }
//}
	
	public static javax.swing.JScrollPane createTablesInScrollPane(String[][] columnHeaders, String[][] columnTooltips, Object[][][] initialData, javax.swing.JTable[] tableRefs, int[] columnWidths)
	{
		javax.swing.JPanel scrollContent=new javax.swing.JPanel();
		scrollContent.setLayout(new javax.swing.BoxLayout(scrollContent, javax.swing.BoxLayout.Y_AXIS));
		javax.swing.table.DefaultTableCellRenderer tcLeftAlignRenderer=new javax.swing.table.DefaultTableCellRenderer(){{setHorizontalAlignment(javax.swing.table.DefaultTableCellRenderer.LEFT);}};
		for (int i=0; i<tableRefs.length; i++)
		{
			final String[] columnToolt=columnTooltips[i];
			if (tableRefs[i]==null) 
			{ 
				tableRefs[i]=new javax.swing.JTable() 
				{
					private String[] columnToolTips=columnToolt;
					public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) { super.changeSelection(rowIndex, columnIndex, !extend, extend); } //Always toggle on single selection (allows users to deselect rows easier)
					public boolean isCellEditable(int row, int column) { return false; } //disable cell editing
					public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) 
					{
						java.awt.Component c = super.prepareRenderer(renderer, row, column);
						if (c instanceof javax.swing.JComponent) 
						{
							javax.swing.JComponent jc = (javax.swing.JComponent) c;
							Object o=getValueAt(row, column);
							if (o!=null) {  jc.setToolTipText(o.toString());}
						}
						return c;
					}
					protected javax.swing.table.JTableHeader createDefaultTableHeader() 
					{
						return new javax.swing.table.JTableHeader(columnModel) 
						{
							public String getToolTipText(java.awt.event.MouseEvent e) 
							{
								java.awt.Point p = e.getPoint();
								int tblIdx = columnModel.getColumnIndexAtX(p.x);
								int columnIdx = columnModel.getColumn(tblIdx).getModelIndex();
								if (columnIdx<columnToolTips.length) { return AHAGUIHelpers.styleToolTipText(columnToolTips[columnIdx]); }
								return "";
							}
						};
					}
				}; 
			}

//			tableRefs[i].setDefaultRenderer(java.text.AttributedString.class, new AttributedLabelRenderer());
//			tableRefs[i].setDefaultRenderer(String.class, new AttributedLabelRenderer());
			tableRefs[i].setModel( new javax.swing.table.DefaultTableModel(initialData[i], columnHeaders[i]) 
			{ 
				public Class<?> getColumnClass(int column) //makes it so row sorters work properly
				{ 
					try
					{
						Object o=null; //System.out.println("ColDetectorCalled for column="+column); //lazy hack but seems to work so shrug
						for (int row=0;row<getRowCount();row++) 
						{
							o=getValueAt(row, column);
							if (o!=null) { break; }
						}
						if (o instanceof String) { return String.class; }//return java.text.AttributedString.class; }
						if (o instanceof Integer) { return Integer.class; }
						if (o instanceof Double) { return Double.class; }
						if (o instanceof Float) { return Float.class; }
						if (o instanceof Long) { return Long.class; }
						//if (o instanceof java.text.AttributedString) { return java.text.AttributedString.class; }
					} catch (Exception e) { e.printStackTrace(); }
					return Object.class;
				}
			});
			tableRefs[i].setDefaultRenderer(Integer.class, tcLeftAlignRenderer);
			//tableRefs[i].getTableHeader().setBorder(null);
			//tableRefs[i].setBorder(null);
			tableRefs[i].setPreferredScrollableViewportSize(tableRefs[i].getPreferredSize());
			tableRefs[i].setAlignmentY(javax.swing.JTable.TOP_ALIGNMENT);
			tableRefs[i].getTableHeader().setAlignmentY(javax.swing.JTable.TOP_ALIGNMENT);
			tableRefs[i].setAutoCreateRowSorter(true);
			for (int j=0;j<tableRefs[i].getColumnModel().getColumnCount() && j<columnWidths.length; j++) { tableRefs[i].getColumnModel().getColumn(j).setPreferredWidth(columnWidths[j]); }
			scrollContent.add(tableRefs[i].getTableHeader());
			scrollContent.add(tableRefs[i]);
		}
		javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(scrollContent);
		scrollPane.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder());
		scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		return scrollPane;
	}
	
	protected static void tryUpdateProgress(javax.swing.ProgressMonitor pm, int progress, int max, String note) //updating ignored if int is <0 or any of the args is null
	{
		try
		{
			if (pm==null) { return; }
			if (progress >= 0) pm.setProgress(progress);
			if (max >= 0) { pm.setMaximum(max); }
			if (note!=null) { pm.setNote(note); }
		}
		catch (Exception e) {}
	}
	
	protected static boolean tryGetProgressCanceled(javax.swing.ProgressMonitor pm) //updating ignored if int is <0 or any of the args is null
	{
		try
		{
			if (pm!=null) { return pm.isCanceled(); }
		}
		catch (Exception e) {}
		return false;
	}
	
	protected static void tryCancelSplashScreen()
	{
		try { java.awt.SplashScreen.getSplashScreen().close(); } catch(Exception e) {}
	}
	
}
