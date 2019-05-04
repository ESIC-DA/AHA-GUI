package esic;


//Copyright 2018 ESIC at WSU distributed under the MIT license. Please see LICENSE file for further info.

import org.graphstream.graph.*;

public class AHAGUI extends javax.swing.JFrame implements org.graphstream.ui.view.ViewerListener, java.awt.event.ActionListener, java.awt.event.MouseWheelListener, java.awt.event.WindowListener
{
	protected javax.swing.JLabel[] m_overlayLabels= new javax.swing.JLabel[4]; 
	protected org.graphstream.ui.view.Viewer m_graphViewer=null;
	
	protected org.graphstream.ui.swing_viewer.ViewPanel m_graphViewPanel=null;//.swingViewer.ViewPanel ;
	protected org.graphstream.ui.view.ViewerPipe m_graphViewPump=null;
	protected AHAModel m_model=null;
	
	protected javax.swing.JLabel m_btmPnlSearchStatus=new javax.swing.JLabel("");
	protected javax.swing.JCheckBox m_btmPnlHideProcsCheckbox=new javax.swing.JCheckBox("Hide OS Procs"), m_btmPnlHideExtCheckbox=new javax.swing.JCheckBox("Hide Ext Node"), m_btmPnlShowFQDN=new javax.swing.JCheckBox("DNS Names"), m_btmPnlChangeOnMouseOver=new javax.swing.JCheckBox("Update on MouseOver",false);
	protected javax.swing.JTextField m_btmPnlSearch=new javax.swing.JTextField("Search...");
	protected javax.swing.JPanel m_inspectorPanel=null;
	protected javax.swing.JCheckBox m_infoPnlShowOnlyMatchedMetrics=new javax.swing.JCheckBox("Show Only Matched Metrics",true), m_infoPnlShowScoringSpecifics=new javax.swing.JCheckBox("Show Score Metric Specifics",false);
	protected final String[][] m_infoPnlColumnHeaders={{"Info"},{"Open Internal Port", "Proto"},{"Open External Port", "Proto"},{"Connected Process", "PID"}, {"Score Metric", "Value"}}; //right now things would break if the number of these ever got changed at runtime, so made static.
	protected final String[][] m_infoPnlColumnTooltips={{"Info"},{"Port that is able to be connected to from other processes internally.", "Protocol in use."},{"Port that is able to be connected to from other external hosts/processes.", "Protocol in use."},{"Names of processes connected to this one", "Process Identifier"}, {"The scoring metric checked against.", "Result of the checked metric."}};
	protected final javax.swing.JTable[] m_infoPnlTables= new javax.swing.JTable[m_infoPnlColumnHeaders.length]; //if you need more tables just add another column header set above
	protected java.util.concurrent.atomic.AtomicReference<Thread> m_graphRefreshThread=new java.util.concurrent.atomic.AtomicReference<>(null);
	protected java.awt.event.ComponentAdapter windowResizeHandler=null;
	private java.util.concurrent.Semaphore m_GUIActive=new java.util.concurrent.Semaphore(0); //semaphore halts main until the current instance of the GUI dies, so we can either start a new one, or kill the application
	private java.util.concurrent.atomic.AtomicReference<Node> m_currentlyDisplayedNode=new java.util.concurrent.atomic.AtomicReference<>(null);
	private int m_preferredInfoPnlWidth=270;
	
	private static java.util.concurrent.atomic.AtomicBoolean s_userWantsToRelaunch=new java.util.concurrent.atomic.AtomicBoolean(true); //flag that indicates that the user wants to open a file (a file chooser will be presented) --default true so on first run of the application the user is asked
	private static java.awt.Dimension s_preferredTotalSize=new java.awt.Dimension(1400, 800); //store the preferred size so if they open a new file we keep that size

	public AHAGUI(AHAModel model, boolean useMultiLineGraph)
	{
		System.err.println("Starting GUI Construction.");
		m_model=model;
		setBackground(java.awt.Color.BLACK); //this does indeed reduce a bit of flicker on load/reload of the frame
		setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(this);
		
		pack(); //populate all the info about the insets
		setSize(s_preferredTotalSize);
		setPreferredSize(s_preferredTotalSize);
		String title="AHA-GUI";
		try { title=AHAGUI.class.getPackage().getImplementationVersion().split(" B")[0]; } catch (Exception e) {}
		setTitle(title); //This should result in something like "AHA-GUI v0.5.6b1" being displayed
		
		//System.setProperty("org.graphstream.ui", "org.graphstream.ui.swing.util.Display");
		//System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		if (useMultiLineGraph) { m_model.m_graph = new org.graphstream.graph.implementations.MultiGraph("MultiGraph", false, true, 256, 2048); }
		else { m_model.m_graph = new org.graphstream.graph.implementations.SingleGraph("SingleGraph", false, true, 256, 2048); }
		m_model.m_graph.setAttribute("ui.stylesheet", m_model.styleSheet);
		
		m_graphViewer = new org.graphstream.ui.swing_viewer.SwingViewer(m_model.m_graph, org.graphstream.ui.view.Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		m_graphViewPanel=(org.graphstream.ui.swing_viewer.ViewPanel)m_graphViewer.addDefaultView(false);
		m_graphViewPanel.setBackground(java.awt.Color.YELLOW);
		
		addMouseWheelListener(this);
		org.graphstream.ui.view.util.MouseManager mouseManager=new AHAGUIMouseAdapter(500,this);
		//mouseManager.init(m_graphViewer.getGraphicGraph(), m_graphViewPanel);
		//mouseManager.release(); //want it to be initialized, but we want to add it to the graph ourselves on the next line, otherwise we will get 2x the events.
		m_graphViewPanel.setMouseManager(null); 
		m_graphViewPanel.setMouseManager(mouseManager); 
		m_model.m_graph.setAttribute("layout.gravity", 0.000001); //layout.quality
		m_model.m_graph.setAttribute("layout.quality", 4);
		m_model.m_graph.setAttribute("layout.stabilization-limit", 0.95);
		m_model.m_graph.setAttribute("ui.antialias", true); //enable anti aliasing (looks way better this way)

		//this.add(m_graphViewPanel);
		
		javax.swing.JPanel bottomPanel=new javax.swing.JPanel(), topLeftOverlay=new javax.swing.JPanel();
		java.awt.Font boldFont=m_btmPnlSearchStatus.getFont().deriveFont(java.awt.Font.BOLD);
		topLeftOverlay.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,2,2));
		{
			for (int i=0;i<m_overlayLabels.length;i++)
			{
				m_overlayLabels[i]=new javax.swing.JLabel(" ");
				m_overlayLabels[i].setFont(boldFont);
				m_overlayLabels[i].setForeground(java.awt.Color.BLACK);
				m_overlayLabels[i].setOpaque(true);
				topLeftOverlay.add(m_overlayLabels[i]);
			}
			m_overlayLabels[0].setBackground(java.awt.Color.GREEN);
			m_overlayLabels[1].setBackground(java.awt.Color.YELLOW);
			m_overlayLabels[2].setBackground(java.awt.Color.ORANGE);
			m_overlayLabels[3].setBackground(java.awt.Color.RED);
			topLeftOverlay.setOpaque(false);
		}
		bottomPanel.setLayout(new java.awt.GridBagLayout());
		javax.swing.JPanel bottomButtons=new javax.swing.JPanel(); //silly, but had to do this or the clickable area of the JCheckbox gets stretched the whole width...which makes for strange clicking
		{
			java.awt.GridBagConstraints gbc=new java.awt.GridBagConstraints();
			gbc.fill=java.awt.GridBagConstraints.HORIZONTAL;
			bottomButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,1,0));

			javax.swing.JButton dataViewBtn=new javax.swing.JButton(" Show Data View ");
			dataViewBtn.setActionCommand("dataView");
			dataViewBtn.addActionListener(this);
			dataViewBtn.setToolTipText(AHAGUIHelpers.styleToolTipText("Shows the list of listening processes to aid in creation of firewall rules."));

			javax.swing.JButton resetBtn=new javax.swing.JButton(" Reset Zoom ");
			resetBtn.setActionCommand("resetZoom");
			resetBtn.addActionListener(this);
			resetBtn.setToolTipText(AHAGUIHelpers.styleToolTipText("Resets the zoom of the graph view to default."));

			javax.swing.JButton openNewFileBtn=new javax.swing.JButton(" Open New File ");
			openNewFileBtn.setActionCommand("openNewFile");
			openNewFileBtn.addActionListener(this);
			openNewFileBtn.setToolTipText(AHAGUIHelpers.styleToolTipText("Close this file and open a new one."));
			
			javax.swing.JComboBox<AHAModel.ScoreMethod> scoreMethod=new javax.swing.JComboBox<>(AHAModel.ScoreMethod.values());
			scoreMethod.setActionCommand("scoreMethod");
			scoreMethod.addActionListener(this);
			scoreMethod.setToolTipText(AHAGUIHelpers.styleToolTipText("Select the scoring method used to calculate node scores"));
			((javax.swing.JLabel)scoreMethod.getRenderer()).setHorizontalAlignment(javax.swing.JLabel.CENTER);
			java.awt.Dimension newDimension=scoreMethod.getPreferredSize();
			newDimension.setSize(newDimension.getWidth(), dataViewBtn.getPreferredSize().height);
			scoreMethod.setPreferredSize(newDimension); //prevent this from being off by one pixel from the other buttons in height (which was annoying me)

			m_btmPnlHideProcsCheckbox.setActionCommand("hideOSProcs");
			m_btmPnlHideProcsCheckbox.addActionListener(this);
			m_btmPnlHideProcsCheckbox.setToolTipText(AHAGUIHelpers.styleToolTipText("Hides the usual Windows™ operating system processes, while interesting these processes can get in the way of other analysis."));

			m_btmPnlHideExtCheckbox.setActionCommand("hideExtNode");
			m_btmPnlHideExtCheckbox.addActionListener(this);
			m_btmPnlHideExtCheckbox.setToolTipText(AHAGUIHelpers.styleToolTipText("Hides the main 'External' node from the graph that all nodes which listen on externally accessible addresses connect to."));

			m_btmPnlShowFQDN.setActionCommand("showFQDN");
			m_btmPnlShowFQDN.addActionListener(this);
			m_btmPnlShowFQDN.setToolTipText(AHAGUIHelpers.styleToolTipText("Show the DNS names of external nodes rather than IPs."));

			javax.swing.JCheckBox useCustom=new javax.swing.JCheckBox("Custom ScoreFile");
			useCustom.setActionCommand("useCustom");
			useCustom.setSelected(m_model.m_useCustomOverlayScoreFile);
			useCustom.addActionListener(this);
			useCustom.setToolTipText(AHAGUIHelpers.styleToolTipText("If a custom score file was loaded, this option will apply those custom directives to the graph view."));

			m_btmPnlChangeOnMouseOver.setToolTipText(AHAGUIHelpers.styleToolTipText("Enable change of the inspector above on hovering over nodes in addition to clicking."));

			bottomButtons.add(dataViewBtn);
			bottomButtons.add(resetBtn);
			bottomButtons.add(scoreMethod);
			bottomButtons.add(openNewFileBtn);
			bottomButtons.add(m_btmPnlHideProcsCheckbox);
			bottomButtons.add(m_btmPnlHideExtCheckbox);
			bottomButtons.add(m_btmPnlShowFQDN);
			bottomButtons.add(useCustom);
			bottomButtons.add(m_btmPnlChangeOnMouseOver);
			bottomButtons.setBorder(null);

			gbc.gridx=0; gbc.gridy=0; gbc.anchor=java.awt.GridBagConstraints.WEST; gbc.weightx=10; gbc.insets=new java.awt.Insets(0,2,0,0); gbc.gridwidth=java.awt.GridBagConstraints.REMAINDER;
			
			m_btmPnlSearch.addFocusListener(new java.awt.event.FocusListener()
			{
				public void focusGained(java.awt.event.FocusEvent e)
				{
					String txt=m_btmPnlSearch.getText();
					if (txt!=null && txt.equals("Search...")) { m_btmPnlSearch.setText(""); }
				}
				public void focusLost(java.awt.event.FocusEvent e)
				{
					String txt=m_btmPnlSearch.getText();
					if (txt!=null && txt.equals("")) { m_btmPnlSearch.setText("Search..."); }
				}
			});

			m_btmPnlSearch.addActionListener(this);
			m_btmPnlSearch.setActionCommand("search");
			m_btmPnlSearch.setToolTipText(AHAGUIHelpers.styleToolTipText("Search\nTo emphasize nodes you're looking for:\nex: processname==svchost.exe\nwill highlight all nodes and connections\nYou can also highlight all nodes except the search term by inverting the search:\nex: processname!=svchost.exe\nwill highlight everything that is not svchost.exe\n\n To hide nodes prepend '~' ex: ~processname==unknown\n\n you can also create complex searches using the || symbol\n ex: processname==svchost.exe || ~processname==unknown"));
			gbc.insets=new java.awt.Insets(0,1,1,1);
			gbc.gridwidth=1;
			gbc.weightx=1000;
			bottomPanel.add(m_btmPnlSearch, gbc);
			gbc.gridx++;
			gbc.weightx=0;
			gbc.insets=new java.awt.Insets(0,0,0,0);
			bottomPanel.add(m_btmPnlSearchStatus, gbc);

			gbc.gridy++;
			gbc.gridx=0;
			gbc.gridwidth=java.awt.GridBagConstraints.REMAINDER;
			bottomPanel.add(bottomButtons, gbc);
			bottomPanel.setBorder( new javax.swing.border.MatteBorder(1, 0, 0, 0, java.awt.Color.GRAY));
		}
		javax.swing.ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		javax.swing.ToolTipManager.sharedInstance().setInitialDelay(500);
		setLayout(new java.awt.BorderLayout(1,0));
//		javax.swing.JLayeredPane layeredPane = new javax.swing.JLayeredPane();
		
		m_inspectorPanel=new javax.swing.JPanel();
		{
			m_infoPnlShowOnlyMatchedMetrics.setToolTipText(AHAGUIHelpers.styleToolTipText("Only displays metrics which were matched, for example if ASLR was true."));
			m_infoPnlShowScoringSpecifics.setToolTipText(AHAGUIHelpers.styleToolTipText("Shows the specific metric in the inspector above that matched."));
	
			m_inspectorPanel.setBorder(new javax.swing.border.MatteBorder(0,1,0,0,java.awt.Color.GRAY));
			m_inspectorPanel.setLayout(new java.awt.GridBagLayout());
			java.awt.GridBagConstraints gbc=new java.awt.GridBagConstraints();
			gbc.insets = new java.awt.Insets(0, 0, 0, 0);
			gbc.fill=gbc.fill=java.awt.GridBagConstraints.BOTH;
			gbc.gridx=0; gbc.gridy=0;  gbc.weightx=1; gbc.weighty=100;
	
			String[][][] initialData={{{"None"}},{{"None"}},{{"None"}},{{"None"}},{{"None"}},}; //digging this new 3d array literal initializer: this is a String[5][1][1] where element[i][0][0]="None".
			m_inspectorPanel.add(AHAGUIHelpers.createTablesInScrollPane(m_infoPnlColumnHeaders, m_infoPnlColumnTooltips, initialData, m_infoPnlTables, new int[]{160,40}), gbc);

			gbc.gridy++;
			gbc.weighty=1;
			gbc.fill=java.awt.GridBagConstraints.HORIZONTAL;
			m_infoPnlShowOnlyMatchedMetrics.setActionCommand("refreshInfoPanel");
			m_infoPnlShowScoringSpecifics.setActionCommand("refreshInfoPanel");
			m_infoPnlShowOnlyMatchedMetrics.addActionListener(this);
			m_infoPnlShowScoringSpecifics.addActionListener(this);
			m_inspectorPanel.add(m_infoPnlShowOnlyMatchedMetrics, gbc);
			gbc.gridy++;
			m_inspectorPanel.add(m_infoPnlShowScoringSpecifics, gbc);
		}


		topLeftOverlay.setBounds(0, 0, topLeftOverlay.getPreferredSize().width, topLeftOverlay.getPreferredSize().height);
		((java.awt.FlowLayout)m_graphViewPanel.getLayout()).setAlignment(java.awt.FlowLayout.LEFT); //reset the existing flowlayout of m_graphViewPane
		m_graphViewPanel.add(topLeftOverlay);
		
		if (m_inspectorPanel.getPreferredSize().width>m_preferredInfoPnlWidth) { m_preferredInfoPnlWidth=m_inspectorPanel.getPreferredSize().width+20; }
		javax.swing.JSplitPane mainContentSplitPane=new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT, m_graphViewPanel, m_inspectorPanel);
		{
			mainContentSplitPane.setDividerSize(2);
			mainContentSplitPane.setBorder(null);
			mainContentSplitPane.setDividerLocation(getContentPane().getSize().width-m_preferredInfoPnlWidth-mainContentSplitPane.getDividerSize());
			mainContentSplitPane.setResizeWeight(1);
		}
		add(mainContentSplitPane, java.awt.BorderLayout.CENTER);
		add (bottomPanel, java.awt.BorderLayout.SOUTH);
		
		windowResizeHandler=new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent componentEvent) 
			{
				s_preferredTotalSize=getSize();
			}
		}; //this is basically the layout manager for the overall frame including the graph itself, the bottom section, and the top overlay
		addComponentListener(windowResizeHandler);
		
		getRootPane().setBorder(new javax.swing.border.LineBorder(java.awt.Color.GRAY,2)); //TODO: tried this to clean up the weird dashed appearance of the right gray border on macOS, but to no avail. figure it out later.
		setVisible(true);
		AHAGUIHelpers.tryCancelSplashScreen();

		System.err.println("Finished Laying out GUI.");
		
		java.io.File testInputFile=AHAModel.getFileAtPath(m_model.m_inputFileName);
		if ( m_model.m_inputFileName==null || m_model.m_inputFileName.equals("") || testInputFile==null || !testInputFile.exists() )
		{
			javax.swing.JFileChooser fc=new javax.swing.JFileChooser("./");
			fc.setDialogTitle("Select Input File");
			javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("Comma Separated Value","csv");
	    fc.setFileFilter(filter);
			int retVal=fc.showOpenDialog(this);
			if (retVal==javax.swing.JFileChooser.APPROVE_OPTION)
			{ 
				m_model.m_inputFileName=fc.getSelectedFile().getAbsolutePath();
				System.err.println("User selected filename="+m_model.m_inputFileName);
			}
		}
		s_userWantsToRelaunch.set(false);
		try
		{
			java.io.File fname=new java.io.File(m_model.m_inputFileName);
			title+=" "+fname.getName();
		} catch(Exception e) { e.printStackTrace(); }
		setTitle(title); //This should result in something like "AHA-GUI v0.5.6b1" being displayed
		m_graphRefreshThread.set(new Thread("GraphViewEventPump"){ //Don't do this until after we explore and score, to reduce odd concurrency errors that seem to occur
			public void run()
			{
				while (!Thread.interrupted())
				{
					try
					{
						int pumpErrs=0;
						//m_model.m_graph.clearSinks(); //executing this line will put it in the state that it gets into when it breaks...if i could figure out how to get it out of this state...then everything would work? simply adding //m_model.m_graph.addSink(m_model.m_graph); doesnt do anything useful
						//m_model.m_graph.addSink(m_model.m_graph);
						//m_model.m_graph.addSink(m_viewer.newThreadProxyOnGraphicGraph());
						m_graphViewPump = m_graphViewer.newViewerPipe();
						m_graphViewPump.addViewerListener(m_model.m_gui);
						m_graphViewPump.addSink(m_model.m_graph);
						
						while (!Thread.interrupted())
						{
							try { m_graphViewPump.blockingPump(); }
							catch (InterruptedException ie) { throw ie; }
							catch (Exception e) { e.printStackTrace(); if (++pumpErrs > 5) { e.printStackTrace(); System.err.println("Breaking loop due to pump errors."); break; } }
						}
						
					}
					catch (InterruptedException ie) { System.err.println("Graph refresh thread exiting."); return; }
					catch (Exception e) { e.printStackTrace();}
				}
				System.err.println("Graph refresh thread exiting, possibly unexpectedly?");
			}
		});
	}
	public void startGraphRefreshThread() { m_graphRefreshThread.get().start(); }

	public void updateOverlayLegendScale(int maxScore)
	{ 
		javax.swing.SwingUtilities.invokeLater(new Runnable() //perform task on gui thread
		{
			public void run()
			{
				m_overlayLabels[0].setText(" Best >"+(int)(0.75*maxScore)+" ");
				m_overlayLabels[1].setText(" >"+(int)(0.50*maxScore)+" ");
				m_overlayLabels[2].setText(" >"+(int)(0.25*maxScore)+" ");
				m_overlayLabels[3].setText(" Worst ");
			}
		});
	}

	public void actionPerformed(java.awt.event.ActionEvent e) //swing actions go to here
	{
		if (e.getActionCommand().equals("hideOSProcs")) { m_model.hideOSProcs(m_model.m_graph, m_btmPnlHideProcsCheckbox.isSelected()); }
		if (e.getActionCommand().equals("hideExtNode")) { m_model.hideFalseExternalNode(m_model.m_graph, m_btmPnlHideExtCheckbox.isSelected()); }
		if (e.getActionCommand().equals("showFQDN")) { m_model.useFQDNLabels(m_model.m_graph, m_btmPnlShowFQDN.isSelected()); }
		if (e.getActionCommand().equals("dataView")) { showDataView(this); }
		if (e.getActionCommand().equals("resetZoom")) { m_graphViewPanel.getCamera().resetView(); }
		if (e.getActionCommand().equals("openNewFile")) { s_userWantsToRelaunch.set(true); terminateGUI(false, false); } //close window
		if (e.getActionCommand().equals("refreshInfoPanel")) { this.updateInfoDisplays(m_currentlyDisplayedNode.get(), false, m_model); } //update info display because a checkbox somewhere changed
		if (e.getActionCommand().equals("search")) { m_btmPnlSearchStatus.setText(m_model.handleSearch(m_btmPnlSearch.getText())); }
		if (e.getActionCommand().equals("scoreMethod")) 
		{ 	
			m_model.swapNodeStyles(((AHAModel.ScoreMethod)((javax.swing.JComboBox<?>)e.getSource()).getSelectedItem()), System.currentTimeMillis());
			this.updateInfoDisplays(m_currentlyDisplayedNode.get(), false, m_model); //refresh the info panel now that we're probably on a new score mode
		}
		if (e.getActionCommand().equals("useCustom"))
		{
			m_model.m_useCustomOverlayScoreFile=((javax.swing.JCheckBox)e.getSource()).isSelected();
			m_model.exploreAndScore(m_model.m_graph);
		}
	}

	private Object synch_dataViewLock=new Object();
	private javax.swing.JFrame synch_dataViewFrame=null;
	private void showDataView(AHAGUI parent) //shows the window that lists the listening sockets
	{
		synchronized (synch_dataViewLock)
		{
			if (synch_dataViewFrame==null)
			{
				synch_dataViewFrame=new javax.swing.JFrame("Data View")
				{
					{ 
						setBackground(java.awt.Color.black);
						setLayout(new java.awt.BorderLayout());
						setSize(new java.awt.Dimension(parent.getSize().width-40,parent.getSize().height-40));
						setLocation(parent.getLocation().x+20, parent.getLocation().y+20); //move it down and away a little bit so people understand it's a new window
						getRootPane().setBorder(new javax.swing.border.LineBorder(java.awt.Color.GRAY,2));
						javax.swing.JTabbedPane tabBar=new javax.swing.JTabbedPane();
						tabBar.setBorder(javax.swing.BorderFactory.createEmptyBorder());
						add(tabBar, java.awt.BorderLayout.CENTER);
						{ // Find data for, create table, etc for the "Graph Data" view
							AHAModel.TableDataHolder t=parent.m_model.generateReport();
							String[][] columTooltips= {{"Global data from the scan which took place.", "The result for this metric."},{"The name of the process.","Process ID of the process.", "User under which the process is running.", "The number of connections this process has.", "The number of ports this process has opened that external hosts/processes could connect to.", "Whether or not this process is codesigned. Code signing is recomended and allows executalbes to be authenticated as genuine.", "Address Space Layout Randomization is a recomended security feature which helps to reduce succeptability to malicious attacks.", "Data Execution Prevention is a recomended security feature which ensures that areas of memory which are writable (and could have code stored to by an attacker) are not executable.", "Control Flow Guard is a recomended security feature which helps prevent attackers from subverting normal code execution, reducing ease of attack.", "HiVA is an improved ASLR with additional etropy to further complicate any possible attacks.", "This is the score granted to the process by the 'Normal' scoring methodology which uses the MetricsTable.cfg to determine the score.","This is a beta scoring method.","This is a beta scoring method."}};
							tabBar.add("Vulnerability Metrics", AHAGUIHelpers.createTablesInScrollPane(t.columnNames, columTooltips, t.tableData, new javax.swing.JTable[t.tableData.length], new int[]{180,40,200,86,80,50,44,44,44,44,44,44,60}) ); //TODO //FIXME
						}
						{ // Find data for, create table, etc for the "Listening Processes" tab
							javax.swing.JTable[] fwTables=new javax.swing.JTable[2];
							String[][] columnHeaders={{"Listening Internally", "PID", "Proto", "Address", "Port", "Connections"},{"Listening Externally", "PID", "Proto", "Address", "Port", "Connections"}};
							Object[][][] tableData=new Object[2][][];
							java.util.TreeMap<String,String> dataset=m_model.m_intListeningProcessMap;
							for (int i=0;i<2;i++)
							{
								java.util.TreeMap<String,Object[]> sortMe=new java.util.TreeMap<>();
								for (java.util.Map.Entry<String, String> entry : dataset.entrySet() )
								{
									String key=entry.getKey(), value=entry.getValue();
									String[] keyTokens=key.split("_"), valueTokens=value.split("_");
									Object strArrVal[]=new Object[6];
									Node n=m_model.m_graph.getNode(value); //grab the node from the graph so we can use it for additional metrics
									String addr="";
									try
									{
										addr=(String)n.getAttribute("localaddress");
									} catch (Exception e) { e.printStackTrace(); }
									strArrVal[0]=valueTokens[0]; // process name
									strArrVal[1]=AHAModel.strAsInt(valueTokens[1]); //PID
									strArrVal[2]=keyTokens[0].toUpperCase(); //Protocol
									strArrVal[3]=addr; //the address of the process
									strArrVal[4]=AHAModel.strAsInt(keyTokens[1]); //Port
									strArrVal[5]=m_model.m_listeningPortConnectionCount.get(key); //Number of connections
									String newKey=valueTokens[0]+valueTokens[1]+"-"+keyTokens[0].toUpperCase()+keyTokens[1];
									sortMe.put(newKey, strArrVal);
								}
								Object[][] data = new Object[sortMe.size()][6];
								int j=0;
								for (Object[] lineDat : sortMe.values()) { data[j++]=lineDat; }
								tableData[i]=data;
								dataset=m_model.m_extListeningProcessMap;
							}
							String[][] columTooltips= {{"Processes which have open ports that can only be connected to by processes on this host.", "Process ID of the process.", "The protocol of this listening port, such as TCP or UDP.", "The port number.", "The number of connections to this open port."},{"Processes which have open ports that can be connected to by remote hosts/processes.", "Process ID of the process.", "The protocol of this listening port, such as TCP or UDP.", "The port number.", "The number of connections to this open port."}};
							tabBar.add("Listening Processes", AHAGUIHelpers.createTablesInScrollPane(columnHeaders,columTooltips, tableData, fwTables, new int[]{200,50,50,50})); //TODO: FIXME:
						}
					}
				};
			}
			synch_dataViewFrame.setVisible(true);
		}
	}

	protected void updateInfoDisplays(Node node, boolean occuredFromMouseOver, AHAModel model)
	{
		if ( node==null || (occuredFromMouseOver && !m_btmPnlChangeOnMouseOver.isSelected()) ) { return; } //if element is null, or triggered from mosueover and we're presently supposed to ignore that, return
		m_currentlyDisplayedNode.set(node);
		//final String bottomAreaConnnection="Connections: "+getNodeConnectionString(node), bottomAreaName=getNameString(node,"  "), bottomAreaScoreInfo="Score: "+getNodeScoreReasonString(node, false);
		Object[][] infoData=null, intPorts=null, extPorts=null, connectionData=null, scoreReasons=null;
		try
		{ //update the top info panel table
			java.util.ArrayList<String> aDolusInfo=new java.util.ArrayList<>();
			String[] aDolusAttributes= {"aDolusScore", "aDolusKnownMalware", "aDolusCVEs", "aDolusNumCVEs", "aDolusCVEScore", "aDolusWorstCVEScore", "aDolusDataSource"};
			
			for (String attrib : aDolusAttributes)
			{
				String value=(String)node.getAttribute(attrib.toLowerCase());
				if (value!=null && !value.equals("") && !value.equals("null"))
				{
					aDolusInfo.add(attrib+": "+value);
					//System.out.println(attrib+": "+value);
				}
				//else { System.out.println("null || empty"); }
			}
			
			
			String[] infoLines=getNameString(node,"\n").trim().split("\n");
			infoData=new String[infoLines.length+aDolusInfo.size()][1];
			for (int i=0;i<infoLines.length;i++) { infoData[i][0]=infoLines[i]; }
			for (int i=0;i<aDolusInfo.size();i++) { infoData[i+infoLines.length][0]=aDolusInfo.get(i); }
		} catch (Exception e) { e.printStackTrace(); }
		try
		{ //update the "Open Internal Ports" second table
			String[] ports=AHAModel.getCommaSepKeysFromStringMap(node.getAttribute("ui.localListeningPorts")).split(", ");
			intPorts=new Object[ports.length][2];
			for (int i=0;i<ports.length;i++)
			{
				String[] temp=ports[i].split("_");
				if (temp.length > 1)
				{
					intPorts[i]=new Object[2];
					intPorts[i][0]=AHAModel.strAsInt(temp[1]);
					intPorts[i][1]=temp[0].toUpperCase(); //reverse array
				} else { intPorts[i][0]="None"; }
			}
		} catch (Exception e) { e.printStackTrace(); }
		try
		{ //update the third "Open External Ports" table
			String[] ports=AHAModel.getCommaSepKeysFromStringMap(node.getAttribute("ui.extListeningPorts")).split(", ");
			extPorts=new Object[ports.length][2];
			for (int i=0;i<ports.length;i++)
			{
				String[] temp=ports[i].split("_");
				if (temp.length > 1)
				{
					extPorts[i]=new Object[2];
					extPorts[i][0]=AHAModel.strAsInt(temp[1]);
					extPorts[i][1]=temp[0].toUpperCase(); //reverse array
				} else { extPorts[i][0]="None"; }
			}
		} catch (Exception e) { e.printStackTrace(); }
		try
		{ //update the fourth "Connected Process Name" table
			String[] connections=getNodeConnectionString(node).split(", ");
			connectionData=new Object[connections.length][2];
			for (int i=0;i<connections.length;i++) 
			{ 
				String[] tokens=connections[i].split("_");
				connectionData[i][0]=tokens[0];
				if (tokens[0].equals("Ext")) { connectionData[i][0]=connections[i]; }
				else if (tokens.length > 1) { connectionData[i][1]=AHAModel.strAsInt(tokens[1]); }
			}
		} catch (Exception e) { e.printStackTrace(); }
		try
		{ //update the fifth "Score Metric" table
			String score=getNodeScoreReasonString(node, true);
			String[] scores=score.split(", ");
			int length=0;
			for (int i=0;i<scores.length;i++) 
			{ 
				if (scores[i].toLowerCase().endsWith("false") && m_infoPnlShowOnlyMatchedMetrics.isSelected()) {continue;}
				length++;
			}
			scoreReasons=new Object[length][2];
			int j=0;
			for (int i=0;i<scores.length;i++) 
			{ 
				String[] scrTokens=scores[i].split("=");
				if (scrTokens!=null && scrTokens.length>=2)
				{
					if (m_infoPnlShowOnlyMatchedMetrics.isSelected()==true && scrTokens[1].toLowerCase().contains("false")) { continue; } 
					scoreReasons[j][0]=scrTokens[0];
					scoreReasons[j][1]=scrTokens[1];
					if (!m_infoPnlShowScoringSpecifics.isSelected()) 
					{ 
						String input=(String)scoreReasons[j][0];
						if (input!=null && input.contains("[") && input.contains("]:")) 
						{ 
							String scoreString=input.split("\\.")[0], scoreValue=input.split("\\]:")[1];
							boolean isNegativeScore=scoreValue.charAt(0)=='-';
							if (!isNegativeScore) { scoreValue="+"+scoreValue; } //scoreReasons[j][0]=scoreString+" (+"+scoreValue+")";
							String output=scoreString+" ("+scoreValue+")";
							if (isNegativeScore) { output="<html><font color=red>"+output+"</font></html>"; }
							//if (isNegativeScore) { output="<html><p style='color:red;overflow:elipsis'>"+output+"</p></html>"; }

//							java.text.AttributedString s=new java.text.AttributedString(scoreString+" ("+scoreValue+")");
//							if (isNegativeScore) { s.setAttribute(java.awt.font.TextAttribute.FOREGROUND, java.awt.Color.red); }
							
							scoreReasons[j][0]=output;
						}
					}
					j++;
				}
			}
			if (scores.length==0 || score.trim().trim().equals("N/A") ){ scoreReasons=new String[][]{{"Scoring not applicable."}}; }
		} catch (Exception e) { e.printStackTrace(); }

		final Object[][][] data={infoData,intPorts,extPorts,connectionData,scoreReasons}; // create final pointer to pass to swing.infokelater. as long as this order of these object arrays is correct, everything will work :)
		javax.swing.SwingUtilities.invokeLater(new Runnable() //perform task on gui thread
		{
			public void run()
			{
				for (int i=0;i<data.length;i++)
				{
					try
					{
						javax.swing.table.DefaultTableModel dm=(javax.swing.table.DefaultTableModel)m_infoPnlTables[i].getModel();
						dm.setDataVector(data[i], m_infoPnlColumnHeaders[i]);
						m_infoPnlTables[i].getColumnModel().getColumn(0).setPreferredWidth(160);
						if (m_infoPnlTables[i].getColumnModel().getColumnCount() > 1)
						{
							m_infoPnlTables[i].getColumnModel().getColumn(1).setPreferredWidth(40);
						}
//						m_btmPnlName.setText(bottomAreaName);
//						m_btmPnlName.setToolTipText(AHAGUIHelpers.styleToolTipText(bottomAreaName));
//						m_btmPnlConnections.setText(bottomAreaConnnection);
//						m_btmPnlConnections.setToolTipText(AHAGUIHelpers.styleToolTipText(bottomAreaConnnection));
//						m_btmPnlScore.setText(bottomAreaScoreInfo);
//						m_btmPnlScore.setToolTipText(AHAGUIHelpers.styleToolTipText(bottomAreaScoreInfo));
					} catch (Exception e) { e.printStackTrace(); }
				}
			}
		});
	}
	
	// Begin Node info gathering helpers
	public static String getNodeScoreReasonString(Node node, boolean extendedReason)
	{ 
		if (node==null) { return " "; }
		String score=(String)node.getAttribute("ui.scoreReason");
		if (extendedReason) { score=(String)node.getAttribute("ui.scoreExtendedReason"); }
		if (score==null) { score=" "; }
		if (node.getAttribute("ui.class")!=null && node.getAttribute("ui.class").toString().toLowerCase().equals("external")) { score="N/A"; } //this was requested to make the UI feel cleaner, since nothing can be done to help the score of an external node anyway.
		return score;
	}

	public static String getNodeConnectionString(Node node)
	{
		if (node==null || node.getDegree()<1) { return " "; }
		String connections="";
		java.util.Iterator<Edge> it=node.iterator();
		while (it.hasNext())
		{
			Edge e=it.next();
			Node tempNode=e.getOpposite(node);
			String t2=(String)tempNode.getAttribute("ui.label");
			if (t2!=null && !connections.contains(t2)) { connections+=t2+", "; } //some vertices have multiple connections between them, only print once
		}
		connections=AHAModel.substringBeforeInstanceOf(connections,", ");
		if (connections==null || connections.length() < 1) { return "None"; }
		return connections;
	}

	public static String getNameString(Node node, String separator)
	{
		if (node==null) { return " "; }
		String nameTxt="Name: "+node.getAttribute("ui.label")+separator+"User: "+node.getAttribute("username")+separator+"Path: "+node.getAttribute("processpath");
		String services=(String)node.getAttribute("processservices");
		String uiclass=(String)node.getAttribute("ui.class");
		if (uiclass!=null && uiclass.equalsIgnoreCase("external")) 
		{ 
			if (node.getAttribute("IP")!=null)
			{
				nameTxt="Name: "+node.getAttribute("ui.label")+separator+"IP: "+node.getAttribute("IP")+separator+"DNS Name: "+node.getAttribute("hostname"); 
			}
			else if (node.getId().toLowerCase().equals("external"))
			{
				nameTxt="Name:"+node.getAttribute("ui.label")+separator+"dummy node which connects to any node listening for outside connections. This can be hidden using 'Hide Ext Node' checkbox.";
			}
		}
		if (services!=null && !services.equals("") && !services.equals("null")) { nameTxt+=separator+"Services: "+services; }
		return nameTxt;
	}
	// END Node info gathering helpers

	// Begin graph interaction handlers
	public synchronized void viewClosed(String arg0) {} //graph viewer interface function
	public synchronized void mouseWheelMoved(java.awt.event.MouseWheelEvent e) //zooms graph in and out using mouse wheel
	{
		double newZoom=m_graphViewPanel.getCamera().getViewPercent()+((double)e.getWheelRotation()/100d);
		if (newZoom <= 0) { newZoom=0.01d; }
		if (newZoom > 0 && newZoom < 20 ) { m_graphViewPanel.getCamera().setViewPercent(newZoom); }
	}
	
	public synchronized void buttonPushed(String id) //called when you click on a graph node/edge
	{
		if (id==null || id.equals("")) { return; } //try { m_graphViewPump.pump(); } catch (Exception e) {e.printStackTrace();}  //non blocking pump to clear anything out before we heckle the graph
		try
		{
			Node node=m_model.m_graph.getNode(id);
			if (node==null) { return; }

			java.util.Iterator<Edge> it=node.iterator(); //TODO rewrite in gs2.0 parlance
			while (it.hasNext())
			{
				Edge e=it.next();
				String currentClasses=(String)e.getAttribute("ui.class");
				if (currentClasses==null) { currentClasses=""; }
				if (!currentClasses.contains("clickedAccent")) { e.setAttribute("ui.class", "clickedAccent, "+currentClasses); } //System.out.println("Adding classes: old uiclass was |"+currentClasses+"| is now |"+e.getAttribute("ui.class")+"|");
			}
			updateInfoDisplays(node, false, m_model);
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public synchronized void buttonReleased(String id) 
	{ 
		if (id==null || id.equals("")) { return; }
		try
		{
			Node node=m_model.m_graph.getNode(id);
			if (node==null) { System.out.println("node is null, returning early"); return;}

			java.util.Iterator<Edge> it=node.iterator(); //TODO rewrite in gs2.0 parlance
			while (it.hasNext())
			{
				Edge e=it.next();
				try 
				{
					String currentClasses=(String)e.getAttribute("ui.class");
					if (currentClasses!=null) { e.setAttribute("ui.class", currentClasses.replaceAll("clickedAccent, ", "")); } //System.out.println("Removing classes: old uiclass was |"+currentClasses+"| is now |"+e.getAttribute("ui.class")+"|");
				} catch (Exception ex) { ex.printStackTrace(); }
			}
		} catch (Exception e2) { e2.printStackTrace(); }
	} //graph mouse function
	
	public synchronized void stoppedHoveringOverElement(org.graphstream.ui.graphicGraph.GraphicElement element) {}
	public synchronized void startedHoveringOverElement(org.graphstream.ui.graphicGraph.GraphicElement element)
	{
		if (element==null) { return; }
		Node node=m_model.m_graph.getNode(element.getId());
		updateInfoDisplays(node, true, m_model);
	}
	
	public void mouseOver(String id) {}
	public void mouseLeft(String id) {}
	//END graph interaction handlers
	
	public void windowOpened(java.awt.event.WindowEvent e) {} //Window listener related events
	public void windowIconified(java.awt.event.WindowEvent e) {}
	public void windowDeiconified(java.awt.event.WindowEvent e) {}
	public void windowActivated(java.awt.event.WindowEvent e) {}
	public void windowDeactivated(java.awt.event.WindowEvent e) {}
	public void windowClosing(java.awt.event.WindowEvent e) { setVisible(false); }
	public void windowClosed(java.awt.event.WindowEvent e)
	{ 
		terminateGUI(false, true);
	} //lets us start a new window and open a new file
	
	protected void terminateGUI (boolean reOpenSameFile, boolean triggeredFromWindowClosed)
	{
		if (this.isEnabled()==false) { return; }
		this.setEnabled(false);
		if (!triggeredFromWindowClosed) { this.dispatchEvent(new java.awt.event.WindowEvent(this, java.awt.event.WindowEvent.WINDOW_CLOSING)); }
		Thread t=m_graphRefreshThread.get();
		if (t!=null) { t.interrupt(); } 
		synchronized(synch_dataViewLock) { if (synch_dataViewFrame!=null) { synch_dataViewFrame.dispose(); } }
		if (!reOpenSameFile) { m_model.m_inputFileName=""; }
		else { AHAGUI.s_userWantsToRelaunch.set(true); }
		try
		{
			if (m_graphViewer!=null) { m_graphViewer.close(); } 
			if (m_model.m_graph!=null) { m_model.m_graph.clearSinks(); m_model.m_graph.clear(); }
		} catch (Exception e) { }
		System.err.println("Window closed."); 
		m_GUIActive.release(); //release this last since we're going to be in a potential race the second it's released.
	}

	public static void main(String args[])
	{ 
		boolean debug=false, verbose=false, useMultiLineGraph=true, useOverlayScoreFile=false, applyTheme=true, force100percentScale=true;
		String scoreFileName="", inputFileName="";
		java.awt.Font uiFont=new java.awt.Font(java.awt.Font.MONOSPACED,java.awt.Font.PLAIN,12);
		for (String s : args)
		{
			try
			{
				String[] argTokens=s.split("=");
				if (argTokens[0]==null) { continue; }
				if (argTokens[0].equalsIgnoreCase("--debug")) { debug=true; } //print more debugs while running
				if (argTokens[0].equalsIgnoreCase("--verbose")) { verbose=true; } //print more debugs while running
				if (argTokens[0].equalsIgnoreCase("--single")) { useMultiLineGraph=false; } //draw single lines between nodes
				if (argTokens[0].equalsIgnoreCase("--notheme")) { applyTheme=false; } //draw single lines between nodes
				if (argTokens[0].equalsIgnoreCase("--noforcescale")) { force100percentScale=false; } //draw single lines between nodes
				if (argTokens[0].equalsIgnoreCase("--bigfont")) { uiFont=new java.awt.Font(java.awt.Font.MONOSPACED,java.awt.Font.PLAIN,18); } //use 18pt font instead of default
				if (argTokens[0].equalsIgnoreCase("scorefile")) { scoreFileName=argTokens[1]; useOverlayScoreFile=true; } //path to custom score file, and enable since...that makes sense in this case
				if (argTokens[0].equalsIgnoreCase("inputFile")) { inputFileName=argTokens[1]; } //path to input file. ignore CLI filename on second time through here (means user asked to open a new file)
				
				if (argTokens[0].equals("help")||argTokens[0].equals("?")) 
				{  
					System.out.println
					(
							"Arguments are as follows:"+
							"--debug : print additional information to console while running\n"+
							"--single : use single lines between nodes with multiple connections\n"+
							"--bigfont : use 18pt font instead of the default 12pt font (good for demos). Will have no effect if used with --notheme\n"+
							"--notheme : attempt to minimize theming information set on gui components so that the OS theme will be used. Unsupported / components may be oddly sized.\n"+
							"scorefile=scorefile.csv : use the scorefile specified after the equals sign\n"+
							"inputFile=inputFile.csv : use the inputFile specified after the equals sign\n"
					); return;
				}
			} catch (Exception e) { e.printStackTrace(); }
		}
		if (force100percentScale) { System.setProperty("sun.java2d.uiScale", "100%"); } //sad little hack to work around current issues on HiDPI machines 
		if (applyTheme) { AHAGUIHelpers.applyTheme(uiFont); }
		
		try
		{
			while (!Thread.interrupted() && AHAGUI.s_userWantsToRelaunch.get()) //quit out if they didn't ask to open a new file
			{
				AHAModel model=new AHAModel(inputFileName, useOverlayScoreFile, scoreFileName, debug, verbose);
				model.m_gui=new AHAGUI(model, useMultiLineGraph);
				model.start();
				model.m_gui.m_GUIActive.acquire(); //block here until the GUI is closed
				inputFileName=model.m_inputFileName; //allow user to specify filename to load next time or relaod the file depending on what just happened.
			}
		} catch (Exception e) { e.printStackTrace(System.err); }
		System.err.println("Reqeusting exit.");
		System.exit(0);
	}
}
