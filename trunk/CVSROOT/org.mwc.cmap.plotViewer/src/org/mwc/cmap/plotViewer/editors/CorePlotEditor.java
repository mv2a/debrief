package org.mwc.cmap.plotViewer.editors;

import java.beans.*;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.SubActionBars2;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Narrative.NarrativeProvider;
import org.mwc.cmap.core.DataTypes.Temporal.*;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackDataListener;
import org.mwc.cmap.core.interfaces.*;
import org.mwc.cmap.core.property_support.PlottableWrapper;
import org.mwc.cmap.core.ui_support.LineItem;
import org.mwc.cmap.plotViewer.editors.chart.*;

import Debrief.Tools.Tote.WatchableList;
import Debrief.Wrappers.*;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.*;
import MWC.GUI.Editable.EditorType;
import MWC.GUI.Tools.Chart.DblClickEdit;
import MWC.GenericData.*;
import MWC.TacticalData.Track;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public abstract class CorePlotEditor extends EditorPart implements
		IResourceProvider, IControllableViewport, ISelectionProvider
{

	// //////////////////////////////
	// member data
	// //////////////////////////////

	/**
	 * the chart we store/manager
	 */
	SWTChart _myChart = null;

	/**
	 * the graphic data we know about
	 */
	protected Layers _myLayers;

	/**
	 * handle narrative management
	 */
	protected NarrativeProvider _theNarrativeProvider;

	/**
	 * an object to look after all of the time bits
	 */
	protected TimeManager _timeManager;

	/**
	 * the object which listens to time-change events. we remember it so that it
	 * can be deleted when we close
	 */
	protected PropertyChangeListener _timeListener;

	/**
	 * store a pending projection. we do this because sometimes we may learn about
	 * the projection before we create our child components, you see.
	 */
	protected PlainProjection _pendingProjection;

	// drag-drop bits

	protected DropTarget target;

	// ///////////////////////////////////////////////
	// dummy bits applicable for our dummy interface
	// ///////////////////////////////////////////////
	Button _myButton;

	Label _myLabel;

	private Composite _plotPanel;

	private CursorTracker _myTracker;

	// //////////////////////////////
	// constructor
	// //////////////////////////////

	public CorePlotEditor()
	{
		super();

		// create the time manager. cool
		_timeManager = new TimeManager();

		// and listen for new times
		_timeListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent arg0)
			{

				HiResDate newDTG = (HiResDate) arg0.getNewValue();
				timeChanged(newDTG);
			}
		};

		_timeManager.addListener(_timeListener,
				TimeProvider.TIME_CHANGED_PROPERTY_NAME);

	}

	public void dispose()
	{
		super.dispose();

		// stop listening to the time manager
		_timeManager.removeListener(_timeListener,
				TimeProvider.TIME_CHANGED_PROPERTY_NAME);
	}

	public void doSave(IProgressMonitor monitor)
	{
		// TODO Auto-generated method stub

	}

	public void doSaveAs()
	{
		// TODO Auto-generated method stub

	}

	public boolean isDirty()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSaveAsAllowed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void createPartControl(Composite parent)
	{
		// hey, create the chart
		_myChart = new SWTChart(_myLayers, parent);

		// and update the projection, if we have one
		if (_pendingProjection != null)
		{
			_myChart.getCanvas().setProjection(_pendingProjection);
			_pendingProjection = null;
		}

		// and the drop support
		configureFileDropSupport();

		// and add our dbl click listener
		// and add our dbl click listener
		getChart().addLeftClickListener(new DblClickEdit(_myLayers, null)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void addEditor(Plottable res, EditorType e, Layer parentLayer)
			{
				System.out.println("opening editor for:" + res);
				PlottableWrapper parentP = new PlottableWrapper(parentLayer, null,
						getChart().getLayers());
				PlottableWrapper wrapped = new PlottableWrapper(res, parentP,
						getChart().getLayers());
				ISelection selected = new StructuredSelection(wrapped);
				fireSelectionChanged(selected);
			}

			protected void handleItemNotFound(PlainProjection projection)
			{
			}
		});

		getSite().setSelectionProvider(this);

		LineItem lineItem = CorePlugin.getStatusLine(this);
		_myTracker = new CursorTracker(_myChart, lineItem);

		//		
		// Display.getDefault().asyncExec(new Runnable()
		// {
		// public void run()
		// {
		// IStatusLineManager mgr = getActionbar().getStatusLineManager();
		// CursorTracker.LineItem item = new CursorTracker.LineItem("vv aa");
		// mgr.add(item);
		// mgr.update(true);
		// item.update("bbb ccc");
		// mgr.update(true);
		// }
		// });

	}

	/**
	 * sort out the file-drop target
	 */
	private void configureFileDropSupport()
	{
		int dropOperation = DND.DROP_COPY;
		Transfer[] dropTypes = { FileTransfer.getInstance() };

		target = new DropTarget(_myChart.getCanvasControl(), dropOperation);
		target.setTransfer(dropTypes);
		target.addDropListener(new DropTargetListener()
		{
			public void dragEnter(DropTargetEvent event)
			{
				if (FileTransfer.getInstance().isSupportedType(event.currentDataType))
				{
					if (event.detail != DND.DROP_COPY)
					{
						event.detail = DND.DROP_COPY;
					}
				}
			}

			public void dragLeave(DropTargetEvent event)
			{
			}

			public void dragOperationChanged(DropTargetEvent event)
			{
			}

			public void dragOver(DropTargetEvent event)
			{
			}

			public void dropAccept(DropTargetEvent event)
			{
			}

			public void drop(DropTargetEvent event)
			{
				String[] fileNames = null;
				if (FileTransfer.getInstance().isSupportedType(event.currentDataType))
				{
					fileNames = (String[]) event.data;
				}
				if (fileNames != null)
				{
					filesDropped(fileNames);
				}
			}

		});

	}

	/**
	 * process the files dropped onto this panel
	 * 
	 * @param fileNames
	 *          list of filenames
	 */
	protected void filesDropped(String[] fileNames)
	{
		System.out.println("Files dropped");
	}

	public void setFocus()
	{
		// TODO Auto-generated method stub

	}

	public Object getAdapter(Class adapter)
	{
		Object res = null;

		// so, is he looking for the layers?
		if (adapter == Layers.class)
		{
			if (_myLayers != null)
				res = _myLayers;
		}
		else if (adapter == NarrativeProvider.class)
		{
			return _theNarrativeProvider;
		}
		else if (adapter == TimeProvider.class)
		{
			return _timeManager;
		}
		else if (adapter == ISelectionProvider.class)
		{
			return this;
		}
		else if (adapter == ControllableTime.class)
		{
			return _timeManager;
		}
		else if (adapter == IGotoMarker.class)
		{
			return new IGotoMarker()
			{
				public void gotoMarker(IMarker marker)
				{
					String lineNum = marker.getAttribute(IMarker.LINE_NUMBER, "na");
					if (lineNum != "na")
					{
						// right, convert to DTG
						HiResDate tNow = new HiResDate(0, Long.parseLong(lineNum));
						_timeManager.setTime(this, tNow);
					}
				}

			};
		}

		return res;
	}

	private static String describeData(String dataName, Layers theLayers,
			NarrativeWrapper narrative, TimeManager timeManager)
	{
		String res = dataName + "\n";

		Enumeration enumer = theLayers.elements();
		while (enumer.hasMoreElements())
		{
			Layer thisL = (Layer) enumer.nextElement();
			res = res + thisL.getName() + "\n";
		}

		if (narrative != null)
		{
			res = res + "Narrative:" + narrative.getData().size() + " elements"
					+ "\n";
		}
		else
		{
			res = res + "Narrative empty\n";
		}

		if (timeManager != null)
		{
			HiResDate tNow = timeManager.getTime();
			if (tNow != null)
				res = res + DebriefFormatDateTime.toStringHiRes(tNow);
			else
				res = res + " time not set";
		}

		return res;
	}

	protected void timeChanged(HiResDate newDTG)
	{
	}

	/**
	 * method called when a helper object has completed a plot-load operation
	 * 
	 * @param source
	 */
	abstract public void loadingComplete(Object source);

	/**
	 * return the file representing where this plot is stored
	 * 
	 * @return the file location
	 */
	public IResource getResource()
	{
		// have we been saved yet?
		return null;
	}

	public WorldArea getViewport()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setViewport(WorldArea target)
	{
		// TODO Auto-generated method stub

	}

	public PlainProjection getProjection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setProjection(PlainProjection proj)
	{
		// do we have a chart yet?
		if (_myChart == null)
		{
			// nope, better remember it
			_pendingProjection = proj;
		}
		else
		{
			// yes, just update it.
			_myChart.getCanvas().setProjection(proj);
		}

	}

	public SWTChart getChart()
	{
		return _myChart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		if (_selectionListeners == null)
			_selectionListeners = new Vector(0, 1);

		// see if we don't already contain it..
		if (!_selectionListeners.contains(listener))
			_selectionListeners.add(listener);
	}

	/**
	 * Returns the ActionbarContributor for the Editor.
	 * 
	 * @return the ActionbarContributor for the Editor.
	 */
	public SubActionBars2 getActionbar()
	{
		return (SubActionBars2) getEditorSite().getActionBars();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection()
	{
		return _currentSelection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		_selectionListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection)
	{
		// TODO Auto-generated method stub
		_currentSelection = selection;
	}

	Vector _selectionListeners;

	ISelection _currentSelection;

	protected void fireSelectionChanged(ISelection sel)
	{
		_currentSelection = sel;
		if (_selectionListeners != null)
		{
			SelectionChangedEvent sEvent = new SelectionChangedEvent(this, sel);
			for (Iterator stepper = _selectionListeners.iterator(); stepper.hasNext();)
			{
				ISelectionChangedListener thisL = (ISelectionChangedListener) stepper
						.next();
				if (thisL != null)
				{
					thisL.selectionChanged(sEvent);
				}
			}
		}

	}

	/**
	 * embedded class which manages the primary & secondary tracks
	 */
	public static class TrackManager implements TrackDataProvider,
			TrackDataProvider.TrackDataListener
	{

		private WatchableList _thePrimary;

		private WatchableList[] _theSecondaries;

		private Vector _myListeners;

		private Layers _theLayers;

		public TrackManager(Layers parentLayers)
		{
			_theLayers = parentLayers;
		}

		public void assignTracks(String primaryTrack, Vector secondaryTracks)
		{
			// ok - find the matching tracks/
			Object theP = _theLayers.findLayer(primaryTrack);
			if (theP != null)
			{
				if (theP instanceof WatchableList)
				{
					_thePrimary = (WatchableList) theP;
				}
			}

			// do we have secondaries?
			if (secondaryTracks != null)
			{
				// and now the secs
				Vector secs = new Vector(0, 1);
				Iterator iter = secondaryTracks.iterator();
				while (iter.hasNext())
				{
					String thisS = (String) iter.next();
					Object theS = _theLayers.findLayer(thisS);
					if (theS != null)
						if (theS instanceof WatchableList)
						{
							secs.add(theS);
						}
				}

				if (secs.size() > 0)
				{
					_theSecondaries = new WatchableList[] { null };
					_theSecondaries = (WatchableList[]) secs.toArray(_theSecondaries);
				}
			}
		}

		public void addTrackDataListener(TrackDataListener listener)
		{
			if (_myListeners == null)
				_myListeners = new Vector();

			_myListeners.add(listener);
		}

		public WatchableList getPrimaryTrack()
		{
			return _thePrimary;
		}

		public WatchableList[] getSecondaryTracks()
		{
			return _theSecondaries;
		}

		public void primaryUpdated(WatchableList primary)
		{
			_thePrimary = primary;
		}

		public void secondariesUpdated(WatchableList[] secondaries)
		{
			_theSecondaries = secondaries;
		}
	}

	/**
	 * PUT IN SOME TESTING
	 */
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testTrackManager extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testTrackManager(final String val)
		{
			super(val);
		}

		public void testLists()
		{
			TrackWrapper ta = new TrackWrapper();
			ta.setTrack(new Track());
			ta.setName("ta");
			TrackWrapper tb = new TrackWrapper();
			tb.setTrack(new Track());
			tb.setName("tb");
			TrackWrapper tc = new TrackWrapper();
			tc.setTrack(new Track());
			tc.setName("tc");
			Layers theLayers = new Layers();
			theLayers.addThisLayer(ta);
			theLayers.addThisLayer(tb);
			theLayers.addThisLayer(tc);

			String pri_a = "ta";
			String pri_b = "tz";
			String sec_b = "tb";
			String sec_c = "tc";
			String sec_d = "tz";
			Vector secs = new Vector(0, 1);
			secs.add(sec_b);
			secs.add(sec_c);

			// create the mgr
			TrackManager tm = new TrackManager(theLayers);

			// do some checks
			assertNull("pri empty", tm._thePrimary);
			assertNull("secs empty", tm._theSecondaries);
			assertNotNull("layers assigned", tm._theLayers);

			// now get going
			tm.assignTracks(pri_a, secs);

			// and do the tests
			assertNotNull("pri assigned", tm._thePrimary);
			assertEquals("pri matches", tm._thePrimary, ta);

			// and the secs
			assertNotNull("sec assigned", tm._theSecondaries);
			assertEquals("correct num", 2, tm._theSecondaries.length);

			// setup duff data
			secs.clear();
			secs.add(sec_b);
			secs.add(sec_d);

			// assign duff data
			tm.assignTracks(pri_b, secs);

			// and test duff data
			assertNotNull("pri still assigned", tm._thePrimary);
			assertEquals("pri matches", tm._thePrimary, ta);
			assertNotNull("sec assigned", tm._theSecondaries);
			assertEquals("correct num", 1, tm._theSecondaries.length);

			// assign more real data
			tm.assignTracks(sec_c, secs);

			// and test duff data
			assertNotNull("pri still assigned", tm._thePrimary);
			assertEquals("pri matches", tm._thePrimary, tc);
			assertNotNull("sec assigned", tm._theSecondaries);
			assertEquals("correct num", 1, tm._theSecondaries.length);
		}
	}
}
