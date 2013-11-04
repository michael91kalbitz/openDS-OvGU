/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2013 Rafael Math
*
*  OpenDS is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  OpenDS is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with OpenDS. If not, see <http://www.gnu.org/licenses/>.
*/

package eu.opends.niftyGui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.jme3.system.AppSettings;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.CheckBoxStateChangedEvent;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.DropDownSelectionChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import eu.opends.basics.SimulationBasics;
import eu.opends.input.KeyBindingEntry;
import eu.opends.niftyGui.dropDown.ColorDepthDropDownModel;
import eu.opends.niftyGui.dropDown.ColorDepthDropDownViewConverter;
import eu.opends.niftyGui.dropDown.FrequencyDropDownModel;
import eu.opends.niftyGui.dropDown.FrequencyDropDownViewConverter;
import eu.opends.niftyGui.dropDown.ResolutionDropDownModel;
import eu.opends.niftyGui.dropDown.ResolutionDropDownViewConverter;
import eu.opends.niftyGui.dropDown.SamplesDropDownModel;
import eu.opends.niftyGui.dropDown.SamplesDropDownViewConverter;
import eu.opends.tools.PanelCenter;


/**
 * This class handles display and user interaction with the key mapping 
 * and graphic settings window generated by nifty-gui.
 * 
 * @author Rafael Math
 */
@SuppressWarnings("unchecked")
public class KeyMappingGUIController implements ScreenController 
{
	private SimulationBasics sim;
	private Nifty nifty;
	private KeyMappingGUI keyMappingGUI;
	private DropDown<ResolutionDropDownModel> resolutionDropDown;
	private DropDown<FrequencyDropDownModel> frequencyDropDown;
	private DropDown<ColorDepthDropDownModel> colorDepthDropDown;
	private DropDown<SamplesDropDownModel> samplesDropDown;
	private CheckBox fullScreenCheckBox;
	private CheckBox vSyncCheckBox;
	private List<DisplayMode> availableDisplayModes = new ArrayList<DisplayMode>();
	private ResolutionDropDownModel lastResolutionDropDownSelection;
	
	
	/**
	 * Creates a new controller instance for the key mapping and graphic 
	 * settings nifty-gui.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param keyMappingGUI
	 * 			Instance of the key mapping and graphic settings GUI.
	 */
	public KeyMappingGUIController(SimulationBasics sim, KeyMappingGUI keyMappingGUI) 
	{
		this.sim = sim;
		this.keyMappingGUI = keyMappingGUI;
		this.nifty = keyMappingGUI.getNifty();
		
		try {
			this.availableDisplayModes = Arrays.asList(Display.getAvailableDisplayModes());
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void bind(Nifty arg0, Screen arg1) 
	{
		
	}

	
	/**
	 * Will be called when GUI is closed.
	 */
	@Override
	public void onEndScreen() 
	{

	}

	
	/**
	 * Will be called when GUI is started.
	 */
	@Override
	public void onStartScreen() 
	{
		showKeyMapping();
		showGraphicSettings();
	}

	
	/**
	 * Handler for "Back"-button. This will close the GUI.
	 */
	public void clickBackButton()
	{
		keyMappingGUI.hideDialog();
	}
	
	
	/**
	 * Handler for "Apply"-button. This will apply the currently selected 
	 * values from the graphic settings GUI to the simulator's settings.
	 */
    public void clickApplyButton() 
    {
    	// read selected values
    	int width = resolutionDropDown.getSelection().getWidth();
    	int height = resolutionDropDown.getSelection().getHeight();
    	int frequency = frequencyDropDown.getSelection().getFrequency();
    	int colorDepth = colorDepthDropDown.getSelection().getColorDepth();
    	int samples = samplesDropDown.getSelection().getSamples();    	
		boolean isFullScreen = fullScreenCheckBox.isChecked();
		boolean isVSync = vSyncCheckBox.isChecked();
		
		// apply values to simulator settings
		AppSettings settings = sim.getSettings();
		settings.setResolution(width, height);
		settings.setFrequency(frequency);
		settings.setBitsPerPixel(colorDepth);
		settings.setSamples(samples);
		settings.setFullscreen(isFullScreen);
		settings.setVSync(isVSync);
        
        // restart simulator renderer
        sim.restart();            
        
        // close nifty window due to resolution problem
        keyMappingGUI.hideDialog();
        
        // report resolution change in order to adjust panel positions 
        // relative to the screen width and height (dialog will also be 
        // restarted with graphic change menu open)
        PanelCenter.reportResolutionChange();
        
		//System.out.println("New: " + width + "x" + height + " @" + frequency + 
		//		", " + colorDepth + "bpp, samples: " + samples + 
		//		", fullScreen: " + isFullScreen + ", vsync: " + isVSync);
    }
    
    
	/**
	 * When the status of the fullscreenCheckBox changes this method 
	 * updates all drop down lists.
	 */
	@NiftyEventSubscriber(id = "fullscreenCheckBox")
	public void onFullScreenCheckBoxStateChanged(final String id, final CheckBoxStateChangedEvent event)
	{
		updateDropDownLists();
	}
	
	
	/**
	 * When the selection of the resolutionDropDown changes this method 
	 * updates all drop down lists and stores the new selection.
	 */
	@NiftyEventSubscriber(id = "resolutionDropDown")
	public void onResolutionDropDownChanged(final String id, final DropDownSelectionChangedEvent event)
	{
		if(!((ResolutionDropDownModel)event.getSelection()).equals(lastResolutionDropDownSelection))
		{
			lastResolutionDropDownSelection = (ResolutionDropDownModel)event.getSelection();
			updateDropDownLists();
		}
	}
    
	
	/**
	 * Makes the content of both pages of the key mapping GUI available. 
	 * This method will be called when the GUI is opened.
	 */
	private void showKeyMapping()
	{
		// get mapping data
		List<KeyBindingEntry> keyBindingList = sim.getKeyBindingCenter().getKeyBindingList();
		Iterator<KeyBindingEntry> iterator = keyBindingList.iterator();
		String[] pages = new String[]{"A", "B"};
		
		// generate output	
		// for page A and B
		for(String page : pages)
		{
			// for column 1-3
			for(int i=0;i<=2;i++)
			{
				// for row 1 - 10
				for(int j=0;j<=9;j++)
				{
					if(iterator.hasNext())
					{
						// write description/key pair to current elements
						KeyBindingEntry entry = (KeyBindingEntry)iterator.next();
	
						setTextToElement("labelFunction" + page + i + j, entry.getDescription() + ":");
						setTextToElement("labelKey" + page + i + j, entry.getKeyList());
					}
					else
					{
						// write empty strings to current elements
						setTextToElement("labelFunction" + page + i + j, "");
						setTextToElement("labelKey" + page + i + j, "");
					}
				}
			}
		}
	}
	
	
	/**
	 * Sets given text to the given element (e.g. label).
	 * 
	 * @param elementID
	 * 			Element's ID to assign a text to.
	 * 
	 * @param text
	 * 			Text to set.
	 */
    private void setTextToElement(String elementID, String text) 
    {
    	getElementByID(elementID).getRenderer(TextRenderer.class).setText(text);
    }
    
    
    /**
     * Looks up an element by ID.
     * 
     * @param elementID
     * 			ID to  look up.
     * 
     * @return
     * 			Element with given ID.
     */
    private Element getElementByID(String elementID)
    {
    	return nifty.getCurrentScreen().findElementByName(elementID);
    }
	
	
	/**
	 * Makes the content of the graphic settings GUI available. 
	 * This method will be called when the GUI is opened.
	 */
	private void showGraphicSettings() 
	{
		// initialize drop down lists and check boxes
    	initUserInterface();
		
		// read current simulator settings
		int width = sim.getSettings().getWidth();
		int height = sim.getSettings().getHeight();
		int frequency = sim.getSettings().getFrequency();
		int colorDepth = sim.getSettings().getBitsPerPixel();
		int samples = sim.getSettings().getSamples();	
		boolean isFullScreen = sim.getSettings().isFullscreen();
		boolean isVSync = sim.getSettings().isVSync();
		
		// fill drop down lists and select current setting as default
		updateDropDownLists(width, height, frequency, colorDepth, samples, isFullScreen);
		
		// set check boxes with current setting as default
		fullScreenCheckBox.setChecked(isFullScreen);
		vSyncCheckBox.setChecked(isVSync);
		
		// store current resolution
		lastResolutionDropDownSelection = new ResolutionDropDownModel(width,height);
    }


	/**
	 * Initialize drop down lists and check boxes.
	 */
	private void initUserInterface() 
	{
		Screen screen = nifty.getCurrentScreen();
    	
		resolutionDropDown = (DropDown<ResolutionDropDownModel>) 
				screen.findNiftyControl("resolutionDropDown", DropDown.class);		
		resolutionDropDown.setViewConverter(new ResolutionDropDownViewConverter());
		
		frequencyDropDown = (DropDown<FrequencyDropDownModel>) 
				screen.findNiftyControl("frequencyDropDown", DropDown.class);		
		frequencyDropDown.setViewConverter(new FrequencyDropDownViewConverter());

		colorDepthDropDown = (DropDown<ColorDepthDropDownModel>) 
				screen.findNiftyControl("colordepthDropDown", DropDown.class);
		colorDepthDropDown.setViewConverter(new ColorDepthDropDownViewConverter());
		
		samplesDropDown = (DropDown<SamplesDropDownModel>) 
				screen.findNiftyControl("samplesDropDown", DropDown.class);
		samplesDropDown.setViewConverter(new SamplesDropDownViewConverter());
		
		fullScreenCheckBox = screen.findNiftyControl("fullscreenCheckBox", CheckBox.class);
		
		vSyncCheckBox = screen.findNiftyControl("vsyncCheckBox", CheckBox.class);
	}
	
	
	/**
	 * Update drop down lists with the current selection.
	 */
	private void updateDropDownLists()
	{
		// read graphic settings from drop down lists and check box
		int width = resolutionDropDown.getSelection().getWidth();
		int height = resolutionDropDown.getSelection().getHeight();
		int frequency = frequencyDropDown.getSelection().getFrequency();
		int colorDepth = colorDepthDropDown.getSelection().getColorDepth();
		int samples = samplesDropDown.getSelection().getSamples();    	
		boolean isFullScreen = fullScreenCheckBox.isChecked();
		
		// update drop down lists for current selection
		updateDropDownLists(width, height, frequency, colorDepth, samples, isFullScreen);
	}
	
	
	/**
	 * Update drop down lists and select the given parameters.
	 * E.g. Content of drop down list resolution depends on the 
	 * state of check box fullScreen.
	 */
	private void updateDropDownLists(int width, int height, int frequency, 
			int colorDepth, int samples, boolean isFullScreen) 
	{
		//System.out.println("Current: " + width + "x" + height + " @" + frequency + ", " + colorDepth + 
		//		"bpp, full screen: " + isFullScreen);

		// clear drop down lists
		clearDropDownLists();
		
		// add available resolutions to drop down list and select current setting
		ArrayList<ResolutionDropDownModel> availableResolutions = getAvailableResolutions(isFullScreen);
		for(ResolutionDropDownModel resolutionDropDownModel : availableResolutions)
			resolutionDropDown.addItem(resolutionDropDownModel);
		resolutionDropDown.selectItem(new ResolutionDropDownModel(width,height));
		
		// add available frequencies to drop down list and select current setting
		List<FrequencyDropDownModel> availableFrequencies = getAvailableFrequencies(isFullScreen, width, height);
		for(FrequencyDropDownModel frequencyDropDownModel : availableFrequencies)
			frequencyDropDown.addItem(frequencyDropDownModel);
		frequencyDropDown.selectItem(new FrequencyDropDownModel(frequency));
		
		// add available color depths to drop down list and select current setting
		List<ColorDepthDropDownModel> availableColorDepths = getAvailableColorDepths(isFullScreen, width, height);
		for(ColorDepthDropDownModel colorDepthDropDownModel : availableColorDepths)
			colorDepthDropDown.addItem(colorDepthDropDownModel);
		colorDepthDropDown.selectItem(new ColorDepthDropDownModel(colorDepth));
		
		// add available samples to drop down list and select current setting
		List<SamplesDropDownModel> availableSamples = getAvailableSamples();
		for(SamplesDropDownModel samplesDropDownModel : availableSamples)
			samplesDropDown.addItem(samplesDropDownModel);
		samplesDropDown.selectItem(new SamplesDropDownModel(samples));
    }

	
	/**
	 * Returns available resolution drop down models for current state 
	 * of check box "fullScreen". This method removes inappropriate 
	 * models for full screen mode.
	 *  
	 * @param isFullScreen
	 * 			Boolean denoting full screen mode.
	 * 
	 * @return
	 * 			Sorted list of available resolution drop down models.
	 */
	private ArrayList<ResolutionDropDownModel> getAvailableResolutions(boolean isFullScreen)
	{
		ArrayList<ResolutionDropDownModel> availableResolutions = new ArrayList<ResolutionDropDownModel>();
		
		for (DisplayMode dm : availableDisplayModes) 
		{
			// in full screen mode only a predefined set of resolutions can be selected
			if((!isFullScreen) || dm.isFullscreenCapable())
			{
				ResolutionDropDownModel resolutionDropDownModel = 
					new ResolutionDropDownModel(dm.getWidth(), dm.getHeight());
				
				// if not yet contained --> add to available resolutions
				if(!availableResolutions.contains(resolutionDropDownModel))
					availableResolutions.add(resolutionDropDownModel);
			}
		}
		
		// sort list
		Collections.sort(availableResolutions);
		
		return availableResolutions;
	}
	
	
	/**
	 * Returns available frequency drop down models for current resolution 
	 * and state of check box "fullScreen". This method removes inappropriate 
	 * models for full screen mode and models not suiting to current resolution.
	 *  
	 * @param isFullScreen
	 * 			Boolean denoting full screen mode.
	 * 
	 * @param width
	 * 			Width of current screen.
	 * 
	 * @param height
	 * 			Height of current screen.
	 * 
	 * @return
	 * 			Sorted list of available frequency drop down models.
	 */
	private ArrayList<FrequencyDropDownModel> getAvailableFrequencies(boolean isFullScreen, int width, int height)
	{
		ArrayList<FrequencyDropDownModel> availableFrequencies = new ArrayList<FrequencyDropDownModel>();
		if(!isFullScreen)
			availableFrequencies.add(new FrequencyDropDownModel(-1, "Auto"));
		else
		{
			for (DisplayMode dm : availableDisplayModes) 
			{
				if(dm.isFullscreenCapable() && (dm.getWidth() == width) && (dm.getHeight() == height))
				{
					FrequencyDropDownModel frequencyDropDownModel = new FrequencyDropDownModel(dm.getFrequency());
					
					// if not yet contained --> add to available frequencies
					if(!availableFrequencies.contains(frequencyDropDownModel))
						availableFrequencies.add(frequencyDropDownModel);
				}
			}	
			// sort list
			Collections.sort(availableFrequencies);
		}
		
		return availableFrequencies;
	}
	
	
	/**
	 * Returns available color depth drop down models for current resolution 
	 * and state of check box "fullScreen". This method removes inappropriate 
	 * models for full screen mode and models not suiting to current resolution.
	 *  
	 * @param isFullScreen
	 * 			Boolean denoting full screen mode.
	 * 
	 * @param width
	 * 			Width of current screen.
	 * 
	 * @param height
	 * 			Height of current screen.
	 * 
	 * @return
	 * 			Sorted list of available color depth drop down models.
	 */
	private ArrayList<ColorDepthDropDownModel> getAvailableColorDepths(boolean isFullScreen, int width, int height)
	{
		ArrayList<ColorDepthDropDownModel> availableColorDepths = new ArrayList<ColorDepthDropDownModel>();
		
		for (DisplayMode dm : availableDisplayModes) 
		{
			if((!isFullScreen) || dm.isFullscreenCapable())
			{				
				if((dm.getWidth() == width) && (dm.getHeight() == height))
				{						
					ColorDepthDropDownModel colorDepthDropDownModel = new ColorDepthDropDownModel(dm.getBitsPerPixel());
					
					// if not yet contained --> add to available color depths
					if(!availableColorDepths.contains(colorDepthDropDownModel))
						availableColorDepths.add(colorDepthDropDownModel);
				}
			}
		}
		// sort list
		Collections.sort(availableColorDepths);
		
		return availableColorDepths;
	}
	
	
	/**
	 * Returns available samples drop down models.
	 * 
	 * @return
	 * 			Sorted list of available samples drop down models.
	 */
	private ArrayList<SamplesDropDownModel> getAvailableSamples()
	{
		ArrayList<SamplesDropDownModel> availableSamples = new ArrayList<SamplesDropDownModel>();
		availableSamples.add(new SamplesDropDownModel(0, "Deactivated"));
		availableSamples.add(new SamplesDropDownModel(2));
		availableSamples.add(new SamplesDropDownModel(4));
		availableSamples.add(new SamplesDropDownModel(8));
		availableSamples.add(new SamplesDropDownModel(16));
		
		return availableSamples;
	}
	
	
	/**
	 * Clears every drop down list.
	 */
	private void clearDropDownLists()
	{
		resolutionDropDown.clear();
		frequencyDropDown.clear();
		colorDepthDropDown.clear();
		samplesDropDown.clear();
	}
}
