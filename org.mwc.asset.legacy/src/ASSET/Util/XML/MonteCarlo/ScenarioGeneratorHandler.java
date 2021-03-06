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
package ASSET.Util.XML.MonteCarlo;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 * @deprecated
 */


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Date;

abstract public class ScenarioGeneratorHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  static final private String type = "ScenarioGenerator";

  public ScenarioGeneratorHandler()
  {
    // inform our parent what type of class we are
    super(type);

  }

  public void startElement(String nameSpace, String localName, String qName, Attributes attributes) throws SAXException
  {
    // don't bother...
  }

  public void elementClosed()
  {
    super.elementClosed();
  }

  // abstract public void setGenerator(ScenarioGenerator genny);

  public static org.w3c.dom.Element exportScenario(final Object scenario,
                                                   final org.w3c.dom.Document doc)
  {
    final org.w3c.dom.Element scen = doc.createElement(type);
    scen.setAttribute("Created", new Date().toString());
    scen.setAttribute("Name", "ASSET Scenario");

    return scen;
  }


}