package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.states.ProblemSpaceView;

/**
 * interface to generate solutions on constrained problem space 
 */
public interface ISolutionGenerator
{

	/** someone's interested in solution generation
	 * 
	 * @param listener
	 */
	void addReadyListener(IGenerateSolutionsListener listener);

	/** someone's no longer interested in solution generation
	 * 
	 * @param listener
	 */
	void removeReadyListener(IGenerateSolutionsListener listener);
	
	/** specify how detailed the maths should be
	 * 
	 * @param precision
	 */
	void setPrecision(Precision precision);
	
	/**
	 * returns problem space which is used by solution generator  
	 */
	ProblemSpaceView getProblemSpace();
	
	/**
	 * clears 
	 */
	void clear();
	
	/**
	 * starts generate solutions job
	 */
	void generateSolutions();
}