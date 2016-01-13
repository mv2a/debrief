package org.debrief.limpet_integration;

import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStoreItem;
import info.limpet.data.operations.admin.OperationsLibrary;
import info.limpet.ui.data_provider.data.LimpetWrapper;
import info.limpet.ui.editors.DataManagerEditor;
import info.limpet.ui.editors.RCPOperationsLibrary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.mwc.cmap.core.property_support.RightClickSupport.AlternateRightClickContextItemGenerator;

import MWC.GUI.Editable;
import MWC.GUI.Layers;

public class DebriefLimpetMenuGenerator implements
    AlternateRightClickContextItemGenerator
{
  
  

  private ArrayList<IAdapterFactory> _adapters;

  @Override
  public void generate(IMenuManager parent, Layers theLayers,
      Collection<Editable> parentLayers, Collection<Editable> subjects)
  {
    // build up a selection
    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    Iterator<Editable> iter = subjects.iterator();
    while (iter.hasNext())
    {
      Editable editable = (Editable) iter.next();
      // see if it's of interest
      if (editable instanceof LimpetWrapper)
      {
        LimpetWrapper wrapper = (LimpetWrapper) editable;
        IStoreItem item = (IStoreItem) wrapper.getSubject();
        selection.add(item);
      }
      else
      {
        // hmm, do we have another way of getting it?
        if(_adapters != null)
        {
          Iterator<IAdapterFactory> aIter = _adapters.iterator();
          while (aIter.hasNext())
          {
            IAdapterFactory iAdapterFactory = (IAdapterFactory) aIter.next();
            Object match = iAdapterFactory.getAdapter(editable, IStoreItem.class);
            if(match != null)
            {
              selection.add((IStoreItem)match);
            }
          }
        }
      }
    }

    // get the list of operations
    HashMap<String, List<IOperation<?>>> ops = OperationsLibrary
        .getOperations();

    // and the RCP-specific operations
    HashMap<String, List<IOperation<?>>> rcpOps = RCPOperationsLibrary
        .getOperations();
    ops.putAll(rcpOps);

    // did we find anything?
    Iterator<String> hIter = ops.keySet().iterator();

    while (hIter.hasNext())
    {
      // ok, we're in a menu grouping
      String name = (String) hIter.next();

      // create a new menu tier
      MenuManager newM = new MenuManager(name);
      parent.add(newM);

      // now loop through this set of operations
      List<IOperation<?>> values = ops.get(name);

      IStore store = null;
      IContext context = null;
      DataManagerEditor.showThisList(selection, newM, values, store, context);
    }

  }

  public void addAdapterFactory(
      IAdapterFactory newFactory)
  {
    if(_adapters == null)
    {
      _adapters = new ArrayList<IAdapterFactory>();
    }
    _adapters.add(newFactory);
  }

}
