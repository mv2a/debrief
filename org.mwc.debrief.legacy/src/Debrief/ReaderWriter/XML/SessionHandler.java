/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package Debrief.ReaderWriter.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */


abstract public class SessionHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private final Debrief.GUI.Frames.Application _parent;
  private Debrief.GUI.Frames.Session _session;
  //  private MWC.Algorithms.PlainProjection _projection;

  public SessionHandler(final Debrief.GUI.Frames.Application theDestination,
                        final Debrief.GUI.Frames.Session theSession,
                        final String fileName)
  {
    // inform our parent what type of class we are
    super("session");

    MWC.GUI.Layers _theLayers = null;

    _parent = theDestination;

    // check if we are creating a fresh session
    if (theSession == null)
      _session = new Debrief.GUI.Frames.Swing.SwingSession(_parent, _parent.getClipboard(), null);
    else
      _session = theSession;

    // do we know the fileName?
    if (fileName != null)
    {
      // has it already been set?
      _session.setFileName(fileName);
    }

    // and get the layers object for the session
    _theLayers = _session.getData();

    // define our handlers
    addHandler(new ProjectionHandler(_session));
    addHandler(new GUIHandler(_session));
    addHandler(new DebriefLayersHandler(_theLayers));

  }

  public final void elementClosed()
  {
    // session is complete
    addSession(_session);

    _session = null;

  }

  abstract public void addSession(Debrief.GUI.Frames.Session data);

  public static void exportThis(final Debrief.GUI.Frames.Session session, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    final org.w3c.dom.Element eSession = doc.createElement("session");

    // now the Layers
    DebriefLayersHandler.exportThis(session, eSession, doc);

    // now the projection
    final Debrief.GUI.Views.PlainView pl = session.getCurrentView();
    if (pl instanceof Debrief.GUI.Views.AnalysisView)
    {
      final Debrief.GUI.Views.AnalysisView av = (Debrief.GUI.Views.AnalysisView) pl;
      ProjectionHandler.exportProjection(av.getChart().getCanvas().getProjection(), eSession, doc);
    }

    // now the GUI
    GUIHandler.exportThis(session, eSession, doc);

    // send out the data
    parent.appendChild(eSession);
  }

}