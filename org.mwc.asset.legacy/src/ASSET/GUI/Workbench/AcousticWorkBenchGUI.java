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
package ASSET.GUI.Workbench;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import ASSET.GUI.Core.CoreGUISwing;
import ASSET.GUI.Painters.NoiseSourcePainter;
import ASSET.GUI.Painters.ScenarioNoiseLevelPainter;
import ASSET.Models.Environment.EnvironmentType;
import MWC.GenericData.WorldLocation;

public class AcousticWorkBenchGUI extends WorkBenchGUI
{

  /****************************************************
   * constructor
   ***************************************************/
  /**
   *  constructor, of course
   *
   * @param  theParent   the toolparent = where we control the cursor from
   * @since
   */
  private AcousticWorkBenchGUI(final ASSETParent theParent)
  {
    super(theParent);
  }
  /****************************************************
   * member methods
   ***************************************************/
  /** allow our child classes to add new gui components
   *
   */
  protected void addUniqueComponents(final CoreGUISwing.MyToolbarHolder assetHolder)
  {
    // add the core workbench bits
    super.addUniqueComponents(assetHolder);

    // add now our acoustic painters
    final ScenarioNoiseLevelPainter ap = new ScenarioNoiseLevelPainter(_theScenario.getEnvironment(),
      this,
      EnvironmentType.BROADBAND_PASSIVE,
      this.getChart().getLayers());
    this.getChart().getLayers().addThisLayer(ap);
    ap.setName("Original noise level plotter");

    final WorldLocation noiseSource =new WorldLocation(0.089992801,0.089992801, 0);
    final NoiseSourcePainter nsp = new NoiseSourcePainter(noiseSource, 180, _theScenario.getEnvironment(), EnvironmentType.BROADBAND_PASSIVE);
    this.getChart().getLayers().addThisLayer(nsp);
    nsp.setName("Original noise source plotter");
  }


  public static void main(String[] args)
  {
//    try{
//    UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.KunststoffLookAndFeel());
//    }catch(UnsupportedLookAndFeelException e)
//    {
//      e.printStackTrace();
//    }

    // create the interface
    final JFrame parent = new JFrame("ASSET Acoustic Workbench");

    parent.setSize(1100, 600);
    parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // open the splash screen
    CoreGUISwing.showSplash(parent, "images/NoiseLogo.gif");

    // create the tool-parent
    final CoreGUISwing.ASSETParent pr = new CoreGUISwing.ASSETParent(parent);

    // create the workbench
    final CoreGUISwing viewer = new AcousticWorkBenchGUI(pr);

    // collate the parent
    parent.getContentPane().setLayout(new BorderLayout());
    parent.getContentPane().add("Center", viewer.getPanel());
    parent.doLayout();

    parent.setVisible(true);

  }

}
