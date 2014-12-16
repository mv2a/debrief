package org.mwc.cmap.gt2plot.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.opengis.feature.Property;

import MWC.GenericData.NamedWorldLocation;
import MWC.GenericData.NamedWorldPath;
import MWC.GenericData.NamedWorldPathList;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

public class CachedNauticalEarthFile
{

	private ArrayList<NamedWorldPath> _polygons;
	private ArrayList<NamedWorldLocation> _points;
	private ArrayList<NamedWorldPathList> _lines;

	private final String _filename;

	public CachedNauticalEarthFile(String fName)
	{
		_filename = fName;
	}

	public void load()
	{
		FileDataStore store;
		_polygons = new ArrayList<NamedWorldPath>();
		try
		{
			final File openFile = new File(_filename);
			store = FileDataStoreFinder.getDataStore(openFile);
			final SimpleFeatureSource featureSource = store.getFeatureSource();

			// hey, can we parse it?
			final SimpleFeatureCollection fs = featureSource.getFeatures();

			final String fType = fs.getSchema().getSuper().getName().getLocalPart()
					.toString();
			switch (fType)
			{
			case "polygonFeature":
				_polygons = loadPolygons(fs.features());
				break;
			case "pointFeature":
				_points = loadPoints(fs.features());
				break;
			case "lineFeature":
				_lines = loadLines(fs.features());
				break;
			default:
				System.out.println("could not handle:" + fType);
				break;
			}

		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	private static ArrayList<NamedWorldPath> loadPolygons(
			SimpleFeatureIterator features)
	{
		ArrayList<NamedWorldPath> res = new ArrayList<NamedWorldPath>();
		while (features.hasNext())
		{
			// get ready to load this feature
			WorldPath path = null;
			String name = null;

			final SimpleFeatureImpl thisF = (SimpleFeatureImpl) features.next();
			final Collection<? extends Property> values = thisF.getValue();
			final Iterator<? extends Property> iter = values.iterator();
			while (iter.hasNext())
			{
				final Property thisProp = iter.next();
				final String propName = thisProp.getName().toString();

				// is this the geometry?
				if (propName.equals("the_geom"))
				{
					path = getPolygon(thisProp.getValue());
				}
				else if (propName.equals("name"))
				{
					name = thisProp.getValue().toString();
				}
			}

			// are we done?
			if ((path != null))
			{
				NamedWorldPath nwa = new NamedWorldPath(path);
				nwa.setName(name);

				res.add(nwa);
			}
		}
		return res;
	}

	private static ArrayList<NamedWorldPathList> loadLines(
			SimpleFeatureIterator features)
	{
		ArrayList<NamedWorldPathList> res = new ArrayList<NamedWorldPathList>();
		while (features.hasNext())
		{
			// get ready to load this feature
			ArrayList<WorldPath> path = null;
			String name = null;

			final SimpleFeatureImpl thisF = (SimpleFeatureImpl) features.next();
			final Collection<? extends Property> values = thisF.getValue();
			final Iterator<? extends Property> iter = values.iterator();
			while (iter.hasNext())
			{
				final Property thisProp = iter.next();
				final String propName = thisProp.getName().toString();

				// is this the geometry?
				if (propName.equals("the_geom"))
				{
					path = getLine(thisProp.getValue());
				}
				else if (propName.equals("name"))
				{
					name = thisProp.getValue().toString();
				}
			}

			// are we done?
			if ((path != null))
			{
				NamedWorldPathList nwa = new NamedWorldPathList();
				nwa.addAll(path);
				nwa.setName(name);

				res.add(nwa);
			}
		}
		return res;
	}

	private static ArrayList<NamedWorldLocation> loadPoints(
			SimpleFeatureIterator features)
	{
		ArrayList<NamedWorldLocation> res = new ArrayList<NamedWorldLocation>();
		while (features.hasNext())
		{
			// get ready to load this feature
			WorldLocation location = null;
			String name = null;

			final SimpleFeatureImpl thisF = (SimpleFeatureImpl) features.next();
			final Collection<? extends Property> values = thisF.getValue();
			final Iterator<? extends Property> iter = values.iterator();
			while (iter.hasNext())
			{
				final Property thisProp = iter.next();
				final String propName = thisProp.getName().toString();

				// is this the geometry?
				if (propName.equals("the_geom"))
				{
					location = getPoint(thisProp.getValue());
				}
				else if (propName.equals("name"))
				{
					name = thisProp.getValue().toString();
				}
			}

			// are we done?
			if ((location != null))
			{
				NamedWorldLocation nwa = new NamedWorldLocation(location);
				nwa.setName(name);
				res.add(nwa);
			}
		}
		return res;
	}

	private static WorldLocation getPoint(final Object value)
	{
		WorldLocation res = null;
		if (value instanceof Point)
		{
			final Point mp = (Point) value;
			res = new WorldLocation(mp.getY(), mp.getX(), 0);
		}
		return res;
	}

	private static ArrayList<WorldPath> getLine(final Object value)
	{
		ArrayList<WorldPath> res = new ArrayList<WorldPath>();
		if (value instanceof MultiLineString)
		{
			final MultiLineString mp = (MultiLineString) value;
			int len = mp.getDimension();
			for (int j = 0; j < len; j++)
			{
				Geometry thisGeom = mp.getGeometryN(j);
				final Coordinate[] coords = thisGeom.getCoordinates();
				if (coords != null)
				{
					WorldPath newP = new WorldPath();
					res.add(newP);

					for (int i = 0; i < coords.length; i++)
					{
						final Coordinate coordinate = coords[i];
						final double zDepth;
						if (Double.isNaN(coordinate.z))
							zDepth = 0;
						else
							zDepth = coordinate.z;
						final WorldLocation newL = new WorldLocation(coordinate.y,
								coordinate.x, zDepth);
						newP.addPoint(newL);
					}
				}

			}

		}
		return res;
	}

	private static WorldPath getPolygon(final Object value)
	{
		WorldPath res = null;
		if (value instanceof MultiPolygon)
		{
			final MultiPolygon mp = (MultiPolygon) value;
			Coordinate[] coords = mp.getBoundary().getCoordinates();
			if (coords != null)
			{
				res = new WorldPath();

				for (int i = 0; i < coords.length; i++)
				{
					final Coordinate coordinate = coords[i];
					final double zDepth;
					if (Double.isNaN(coordinate.z))
						zDepth = 0;
					else
						zDepth = coordinate.z;
					final WorldLocation newL = new WorldLocation(coordinate.y,
							coordinate.x, zDepth);
					res.addPoint(newL);
				}
			}

		}
		return res;
	}

	public ArrayList<NamedWorldPath> getPolygons()
	{
		if (_polygons == null)
			load();
		return _polygons;
	}

	public ArrayList<NamedWorldLocation> getPoints()
	{
		if (_points == null)
			load();
		return _points;
	}

	public ArrayList<NamedWorldPathList> getLines()
	{
		if (_lines == null)
			load();
		return _lines;
	}
}