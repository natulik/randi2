/* This file is part of RANDI2.
 * 
 * RANDI2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * RANDI2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RANDI2. If not, see <http://www.gnu.org/licenses/>.
 */
package de.randi2.jsf.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import de.randi2.dao.TrialDao;
import de.randi2.dao.TrialSiteDao;
import de.randi2.jsf.Randi2;
import de.randi2.jsf.utility.AutoCompleteObject;
import de.randi2.model.GrantedAuthorityEnum;
import de.randi2.model.Login;
import de.randi2.model.SubjectProperty;
import de.randi2.model.TreatmentArm;
import de.randi2.model.Trial;
import de.randi2.model.TrialSite;
import de.randi2.model.enumerations.TrialStatus;

/**
 * <p>
 * This class cares about the trial object and contains all the needed methods
 * to work with this object for the UI.
 * </p>
 * 
 * @author Lukasz Plotnicki <lplotni@users.sourceforge.net>
 */
public class TrialHandler {

	private Trial trial;

	private AutoCompleteObject<TrialSite> trialSitesAC = null;
	private AutoCompleteObject<Login> sponsorInvestigatorsAC = null;
	private AutoCompleteObject<TrialSite> participatingSitesAC = null;

	// TODO TEMP OBJECTS
	private Date tDate1;
	private Date tDate2;
	private TimeZone zone;
	private ArrayList<TreatmentArm> arms = null;
	private ArrayList<SubjectProperty> properties = null;

	// Trial Status as SelectItems
	private List<SelectItem> stateItems = null;

	// DB Access
	private TrialDao trialDao;
	private TrialSiteDao centerDao;

	public TrialDao getTrialDao() {
		return trialDao;
	}

	public void setTrialDao(TrialDao trialDao) {
		this.trialDao = trialDao;
	}

	public Trial getTrial() {
		if (trial == null) // TODO
			trial = new Trial();
		return trial;
	}

	public void setTrial(Trial trial) {
		this.trial = trial;
	}

	public List<SelectItem> getStateItems() {
		if (stateItems == null) {
			stateItems = new Vector<SelectItem>(TrialStatus.values().length);
			for (TrialStatus s : TrialStatus.values()) {
				stateItems.add(new SelectItem(s, s.toString()));
			}
		}
		return stateItems;
	}

	public void addTrialSite(ActionEvent event) {
		assert (participatingSitesAC.getSelectedObject() != null);
		trial.getParticipatingSites().add(
				participatingSitesAC.getSelectedObject());
	}

	public void removeTrialSite(ActionEvent event) {
		TrialSite tTrialSite = (TrialSite) (((UIComponent) event.getComponent()
				.getChildren().get(0)).getValueExpression("value")
				.getValue(FacesContext.getCurrentInstance().getELContext()));
		trial.getParticipatingSites().remove(tTrialSite);

	}

	public String createTrial() {
		// TODO Need to be implemented!
		return Randi2.SUCCESS;
	}

	public void addArm(ActionEvent event) {
		TreatmentArm temp = new TreatmentArm();
		this.getArms().add(temp);
	}

	public void removeArm(ActionEvent event) {
		this.getArms().remove(this.getArms().size() - 1);
	}

	public void addProperty(ActionEvent event) {
		SubjectProperty p = new SubjectProperty();
		this.getProperties().add(p);
	}

	public void removeProperty(ActionEvent event) {
		this.getProperties().remove(this.getProperties().size() - 1);
	}

	// TEMP
	public Date getTDate1() {
		if (tDate1 == null)
			return (new GregorianCalendar()).getTime();
		return tDate1;
	}

	public void setTDate1(Date date1) {
		tDate1 = date1;
	}

	public Date getTDate2() {
		if (tDate2 == null)
			return (new GregorianCalendar()).getTime();
		return tDate2;
	}

	public void setTDate2(Date date2) {
		tDate2 = date2;
	}

	public TimeZone getZone() {
		if (zone == null) {
			zone = TimeZone.getDefault();
		}
		return zone;
	}

	public void setZone(TimeZone zone) {
		this.zone = zone;
	}

	public ArrayList<TreatmentArm> getArms() {
		if (arms == null)
			arms = new ArrayList<TreatmentArm>();
		return arms;
	}

	public void setArms(ArrayList<TreatmentArm> arms) {
		this.arms = arms;
	}

	public ArrayList<SubjectProperty> getProperties() {
		if (properties == null)
			properties = new ArrayList<SubjectProperty>();
		return properties;
	}

	public void setProperties(ArrayList<SubjectProperty> properties) {
		this.properties = properties;
	}

	public int getTreatmentArmsCount() {
		return this.getArms().size();
	}

	public int getSubjectPropertiesCount() {
		return this.getProperties().size();
	}

	public AutoCompleteObject<TrialSite> getTrialSitesAC() {
		if (trialSitesAC == null)
			trialSitesAC = new AutoCompleteObject<TrialSite>(centerDao);
		return trialSitesAC;
	}

	public AutoCompleteObject<Login> getSponsorInvestigatorsAC() {
		if (sponsorInvestigatorsAC == null)
			sponsorInvestigatorsAC = new AutoCompleteObject<Login>(trialSitesAC
					.getSelectedObject().getMembersWithSpecifiedRole(
							GrantedAuthorityEnum.ROLE_P_INVASTIGATOR));
		return sponsorInvestigatorsAC;
	}
	
	public AutoCompleteObject<TrialSite> getParticipatingSitesAC() {
		if (participatingSitesAC == null)
			participatingSitesAC = new AutoCompleteObject<TrialSite>(centerDao);
		return participatingSitesAC;
	}

	public TrialSiteDao getCenterDao() {
		return centerDao;
	}

	public void setCenterDao(TrialSiteDao centerDao) {
		this.centerDao = centerDao;
	}

}