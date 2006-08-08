/**
 * 
 */
package org.mwc.debrief.core.editors;

import java.beans.*;
import java.io.*;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.mwc.cmap.core.DataTypes.Temporal.ControllablePeriod;
import org.mwc.cmap.core.DataTypes.TrackData.*;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackDataListener;
import org.mwc.cmap.core.interfaces.INamedItem;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.plotViewer.editors.chart.*;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.CustomisedSWTCanvas;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.editors.painters.LayerPainterManager;
import org.mwc.debrief.core.interfaces.IPlotLoader;
import org.mwc.debrief.core.loaders.LoaderManager;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;
import org.mwc.debrief.core.operations.PlotOperations;
import org.osgi.framework.Bundle;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Tools.Tote.*;
import Debrief.Wrappers.*;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.PlainProjection.RelativeProjectionParent;
import MWC.GUI.*;
import MWC.GenericData.*;
import MWC.TacticalData.IRollingNarrativeProvider;

/**
 * @author ian.mayo
 */
public class PlotEditor extends org.mwc.cmap.plotViewer.editors.CorePlotEditor
{
	// Extension point tag and attributes in plugin.xml
	private static final String EXTENSION_POINT_ID = "DebriefPlotLoader";

	private static final String EXTENSION_TAG = "loader";

	private static final String EXTENSION_TAG_LABEL_ATTRIB = "name";

	private static final String EXTENSION_TAG_EXTENSIONS_ATTRIB = "extensions";

	private static final String EXTENSION_TAG_ICON_ATTRIB = "icon";

	// private static final String EXTENSION_TAG_CLASS_ATTRIB = "class";

	// Plug-in ID from <plugin> tag in plugin.xml
	private static final String PLUGIN_ID = "org.mwc.debrief.core";

	/**
	 * helper object which loads plugin file-loaders
	 */
	private LoaderManager _loader;

	/**
	 * we keep the reference to our track-type adapter
	 */
	private TrackDataProvider _trackDataProvider;

	/**
	 * something to look after our layer painters
	 */
	private LayerPainterManager _layerPainterManager;

	private PlotOperations _myOperations;

	/**
	 * support tool that provides a relative plot
	 */
	private RelativeProjectionParent _myRelativeWrapper;

	/**
	 * constructor - quite simple really.
	 */
	public PlotEditor()
	{
		super();

		// create the track manager to manage the primary & secondary tracks
		_trackDataProvider = new TrackManager(_myLayers);

		// and listen out form modifications, because we want to mark ourselves as
		// dirty once they've updated
		_trackDataProvider.addTrackDataListener(new TrackDataListener()
		{
			public void tracksUpdated(WatchableList primary, WatchableList[] secondaries)
			{
				fireDirty();
			}
		});

		_layerPainterManager = new LayerPainterManager(_trackDataProvider);
		_layerPainterManager.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent arg0)
			{
				// ok, trigger repaint of plot
				if (getChart() != null)
					getChart().update();
			}
		});

		// listen out for when our input changes, since we will change the editor
		// window title
		this.addPropertyListener(new IPropertyListener()
		{

			public void propertyChanged(Object source, int propId)
			{
				if (propId == PROP_INPUT)
				{
					IFileEditorInput inp = (IFileEditorInput) getEditorInput();
					setPartName(inp.getName());
				}
			}
		});

		_myOperations = new PlotOperations()
		{
			// just provide with our complete set of layers
			public Object[] getTargets()
			{
				// ok, return our top level layers as objects
				Vector res = new Vector(0, 1);
				for (int i = 0; i < _myLayers.size(); i++)
				{
					res.add(_myLayers.elementAt(i));
				}
				return res.toArray();
			}

			/**
			 * override performing the operation, since we'll do a screen update on
			 * completion
			 */
			public Vector performOperation(AnOperation operationName)
			{
				// make the actual change
				Vector res = super.performOperation(operationName);

				if (res != null)
				{
					if (res.size() != 0)
					{
						for (Iterator iter = res.iterator(); iter.hasNext();)
						{
							Layer thisL = (Layer) iter.next();
							// and update the screen
							_myLayers.fireReformatted(thisL);

						}
					}
				}

				return res;

			}
		};
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInputWithNotify(input);

		// ok - declare and load the supplemental plugins which can load datafiles
		initialiseFileLoaders();

		// and start the load
		loadThisFile(input);

		// lastly, set the title (if we have one)
		this.setPartName(input.getName());
	}

	/**
	 * @param input
	 *          the file to insert
	 */
	private void loadThisFile(IEditorInput input)
	{
		try
		{
			IFileEditorInput ife = (IFileEditorInput) input;
			InputStream is = ife.getFile().getContents();
			loadThisStream(is, input.getName());
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param input
	 *          the file to insert
	 */
	private void loadThisFile(String filePath)
	{
		try
		{
			FileInputStream ifs = new FileInputStream(filePath);
			loadThisStream(ifs, filePath);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadThisStream(InputStream is, String fileName)
	{
		// right, see if any of them will do our edit
		IPlotLoader[] loaders = _loader.findLoadersFor(fileName);
		// did we find any?
		if (loaders.length > 0)
		{
			// cool, give them a go...
			try
			{
				for (int i = 0; i < loaders.length; i++)
				{
					IPlotLoader thisLoader = loaders[i];

					// get it to load. Just in case it's an asychronous load operation, we
					// rely on it calling us back (loadingComplete)
					thisLoader.loadFile(this, is, fileName);
				}
			}
			catch (RuntimeException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	private void initialiseFileLoaders()
	{
		// hey - sort out our plot readers
		_loader = new LoaderManager(EXTENSION_POINT_ID, EXTENSION_TAG, PLUGIN_ID)
		{

			public INamedItem createInstance(IConfigurationElement configElement, String label)
			{
				// get the attributes
				label = configElement.getAttribute(EXTENSION_TAG_LABEL_ATTRIB);
				String icon = configElement.getAttribute(EXTENSION_TAG_ICON_ATTRIB);
				String fileTypes = configElement.getAttribute(EXTENSION_TAG_EXTENSIONS_ATTRIB);

				// create the instance
				INamedItem res = new IPlotLoader.DeferredPlotLoader(configElement, label, icon,
						fileTypes);

				// and return it.
				return res;
			}

		};
	}

	private static TimePeriod getPeriodFor(Layers theData)
	{
		TimePeriod res = null;

		for (Enumeration iter = theData.elements(); iter.hasMoreElements();)
		{
			Layer thisLayer = (Layer) iter.nextElement();

			// and through this layer
			if (thisLayer instanceof TrackWrapper)
			{
				TrackWrapper thisT = (TrackWrapper) thisLayer;
				res = extend(res, thisT.getStartDTG());
				res = extend(res, thisT.getEndDTG());
			}
			else if (thisLayer instanceof BaseLayer)
			{
				Enumeration elements = thisLayer.elements();
				while (elements.hasMoreElements())
				{
					Plottable nextP = (Plottable) elements.nextElement();
					if (nextP instanceof Watchable)
					{
						Watchable wrapped = (Watchable) nextP;
						HiResDate dtg = wrapped.getTime();
						if (dtg != null)
							res = extend(res, dtg);
					}
				}
			}
		}

		return res;
	}

	private static TimePeriod extend(TimePeriod period, HiResDate date)
	{
		// have we received a date?
		if (date != null)
		{
			if (period == null)
			{
				period = new TimePeriod.BaseTimePeriod(date, date);
			}
			else
				period.extend(date);
		}

		return period;
	}

	/**
	 * method called when a helper object has completed a plot-load operation
	 * 
	 * @param source
	 */
	public void loadingComplete(Object source)
	{

		// ok, stop listening for dirty calls - since there will be so many and we
		// don't want
		// to start off with a dirty plot
		startIgnoringDirtyCalls();

		DebriefPlugin.logError(Status.INFO, "File load received", null);

		// and update the time management bits
		TimePeriod timePeriod = getPeriodFor(_myLayers);

		if (timePeriod != null)
		{
			super._timeManager.setPeriod(this, timePeriod);

			// also give it a current DTG (if it doesn't have one)
			if (_timeManager.getTime() == null)
				_timeManager.setTime(this, timePeriod.getStartDTG(), false);
		}

		// done - now we can process dirty calls again
		stopIgnoringDirtyCalls();

	}

	protected void filesDropped(String[] fileNames)
	{
		super.filesDropped(fileNames);

		// ok, iterate through the files
		for (int i = 0; i < fileNames.length; i++)
		{
			final String thisFilename = fileNames[i];
			System.out.println("should be loading:" + thisFilename);
			loadThisFile(thisFilename);

			// File newFile = new java.io.File(thisFilename);
			// File thisFile = new File(thisFilename);
			// org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage localF
			// = new
			// org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage(thisFile);
			// IFile theFile = (IFile) localF.getAdapter(IFile.class);

			// IEditorInput theInput = new JavaFileEditorInput(newFile);
			// this.loadThisFile(theInput);
			// DialogFactory.showMessage("Load plot", "Sorry, plots must be loaded
			// from a project");

		}

		// ok, we're probably done - fire the update
		this._myLayers.fireExtended();

		// and resize to make sure we're showing all the data
		this._myChart.rescale();

		// hmm, we may have loaded more track data - but we don't track
		// loading of individual tracks - just fire a "modified" flag
		_trackDataProvider.fireTracksChanged();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.cmap.plotViewer.editors.CorePlotEditor#timeChanged()
	 */
	protected void timeChanged(HiResDate newDTG)
	{
		super.timeChanged(newDTG);

		// ok - update our painter
		if (getChart() != null)
		{
			getChart().getCanvas().updateMe();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.cmap.plotViewer.editors.CorePlotEditor#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter)
	{
		Object res = null;

		if (adapter == TrackManager.class)
		{
			res = _trackDataProvider;
		}
		else if (adapter == TrackDataProvider.class)
		{
			res = _trackDataProvider;
		}
		else if (adapter == PlainProjection.class)
		{
			res = super.getChart().getCanvas().getProjection();
		}
		else if (adapter == LayerPainterManager.class)
		{
			res = _layerPainterManager;
		}
		else if (adapter == ControllablePeriod.class)
		{
			res = _myOperations;
		}
		else if (adapter == IRollingNarrativeProvider.class)
		{
			// so, do we have any narrative data?
			Layer narr = _myLayers.findLayer(ImportReplay.NARRATIVE_LAYER);

			if (narr != null)
			{
				// did we find it?
				// cool, cast to object
				final NarrativeWrapper wrapper = (NarrativeWrapper) narr;

				res = wrapper;
			}
		}
		else if (adapter == RelativeProjectionParent.class)
		{
			if (_myRelativeWrapper == null)
			{
				_myRelativeWrapper = new RelativeProjectionParent()
				{
					public double getHeading()
					{
						double res = 0.0;
						// do we have a primary?
						Watchable[] thePositions = _trackDataProvider.getPrimaryTrack().getNearestTo(
								_timeManager.getTime());
						if (thePositions != null)
						{
							// yup, get the centre point
							res = thePositions[0].getCourse();
						}
						return res;
					}

					public WorldLocation getLocation()
					{
						MWC.GenericData.WorldLocation res = null;
						// do we have a primary?
						Watchable[] thePositions = _trackDataProvider.getPrimaryTrack().getNearestTo(
								_timeManager.getTime());
						if (thePositions != null)
						{
							if (thePositions.length > 0)
							{
								// yup, get the centre point
								res = thePositions[0].getBounds().getCentre();
							}
						}
						return res;
					}

				};
			}
			res = _myRelativeWrapper;
		}

		// did we find anything?
		if (res == null)
		{
			// nope, don't bother.
			res = super.getAdapter(adapter);
		}

		// ok, done
		return res;
	}

	/**
	 * @param parent
	 */
	protected SWTChart createTheChart(Composite parent)
	{
		// TODO Auto-generated method stub
		SWTChart res = new SWTChart(_myLayers, parent)
		{

			public SWTCanvas createCanvas(Composite parent)
			{
				return new CustomisedSWTCanvas(parent)
				{

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void parentFireSelectionChanged(ISelection selected)
					{
						chartFireSelectionChanged(selected);
					}

					public void doSupplementalRightClickProcessing(MenuManager menuManager, Plottable selected, Layer theParentLayer)
					{
//					 hmm, is it a fix. if it is, also flash up the track
						if (selected instanceof FixWrapper)
						{
							// get the parent track
							FixWrapper fix = (FixWrapper) selected;
							TrackWrapper parent = fix.getTrackWrapper();
							RightClickSupport.getDropdownListFor(menuManager, new Editable[] { parent },
									new Layer[] { theParentLayer }, new Layer[] { theParentLayer }, getLayers(), true);
						}
					}
				};
			}

			public void chartFireSelectionChanged(ISelection sel)
			{
				// TODO Auto-generated method stub
				fireSelectionChanged(sel);
			};

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * @param thisLayer
			 * @param dest
			 */
			protected void paintThisLayer(Layer thisLayer, CanvasType dest)
			{
				// get the current time
				HiResDate tNow = _timeManager.getTime();

				// do we know the time?
				if (tNow != null)
				{
					// yes. cool, get plotting
					_layerPainterManager.getCurrentPainter().paintThisLayer(thisLayer, dest, tNow);
				}
			}

		};
		return res;
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#doSave(IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor)
	{

		IEditorInput input = getEditorInput();

		if (input.exists())
		{
			// is this the correct type of file?
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			IPath path = file.getFullPath();
			String ext = path.getFileExtension();
			if (ext == null || ext.equalsIgnoreCase("rep"))
			{
				// not, we have to do a save-as
				doSaveAs();
			}
			else
				doSaveTo(file, monitor);
		}
	}

	/**
	 * save our plot to the indicated location
	 * 
	 * @param destination
	 *          where to save plot to
	 * @param monitor
	 *          somebody/something to be informed about progress
	 */
	private void doSaveTo(IFile destination, IProgressMonitor monitor)
	{
		boolean itWorked = false;

		if (destination != null)
		{

			IProduct prod = Platform.getProduct();
			Bundle bund = prod.getDefiningBundle();
			String version = "" + new Date(bund.getLastModified());

			// ok, now write to the file
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DebriefEclipseXMLReaderWriter.exportThis(this, bos, version);

			// now convert to String
			byte[] output = bos.toByteArray();
			InputStream is = new ByteArrayInputStream(output);

			try
			{
				destination.setContents(is, true, false, monitor);

				itWorked = true;
			}
			catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// ok, lastly indicate that the save worked (if it did!)
			_plotIsDirty = !itWorked;
			firePropertyChange(PROP_DIRTY);
		}
		else
		{
			DebriefPlugin.logError(Status.ERROR, "Unable to identify source file for plot",
					null);
		}

	}

	public void doSaveAs()
	{
		String message = "Save as";
		SaveAsDialog dialog = new SaveAsDialog(getEditorSite().getShell());
		dialog.setTitle("Save Plot As");
		if (getEditorInput() instanceof FileEditorInput)
		{
			IFile oldFile = ((FileEditorInput) getEditorInput()).getFile();
			// dialog.setOriginalFile(oldFile);

			IPath oldPath = oldFile.getFullPath();
			IPath newStart = oldPath.removeFileExtension();
			IPath newPath = newStart.addFileExtension("xml");
			File asFile = newPath.toFile();
			String newName = asFile.getName();
			// dialog.setOriginalFile(newName);
			dialog.setOriginalName(newName);
		}
		dialog.create();
		if (message != null)
			dialog.setMessage(message, IMessageProvider.WARNING);
		else
			dialog.setMessage("Save file to another location.");
		dialog.open();
		IPath path = dialog.getResult();

		if (path == null)
		{
			return;
		}
		else
		{
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if (!file.exists())
				try
				{
					System.out.println("creating:" + file.getName());
					file.create(new ByteArrayInputStream(new byte[] {}), false, null);
				}
				catch (CoreException e)
				{
					DebriefPlugin.logError(IStatus.ERROR,
							"Failed trying to create new file for save-as", e);
					return;
				}

			// ok, write to the file
			doSaveTo(file, new NullProgressMonitor());

			// also make this new file our input
			IFileEditorInput newInput = new FileEditorInput(file);
			setInputWithNotify(newInput);
		}

		_plotIsDirty = false;
		firePropertyChange(PROP_DIRTY);
	}

	public boolean isSaveAsAllowed()
	{
		return true;
	}
}
