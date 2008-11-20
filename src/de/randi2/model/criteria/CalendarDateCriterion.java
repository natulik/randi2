package de.randi2.model.criteria;

import java.util.GregorianCalendar;

import de.randi2.model.SubjectProperty;

/**
 * <p>
 * This class represents properties of a trial subject based on calendar dates.
 * </p>
 * 
 * @author Lukasz Plotnicki <lplotni@users.sourceforge.net>
 * 
 */
public class CalendarDateCriterion extends AbstractCriterion {
	
	private GregorianCalendar date = null;

	@Override
	public void applyConstraints(SubjectProperty prop) {
		// TODO Auto-generated method stub

	}

	@Override
	public SubjectProperty createPropertyPrototype() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return CalendarDateCriterion.class.getName();
	}

}
