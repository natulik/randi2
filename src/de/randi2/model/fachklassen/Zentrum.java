package de.randi2.model.fachklassen;

import java.util.Vector;

import de.randi2.datenbank.DatenbankFactory;
import de.randi2.datenbank.exceptions.DatenbankFehlerException;
import de.randi2.model.exceptions.ZentrumException;
import de.randi2.model.fachklassen.beans.ZentrumBean;
import de.randi2.utility.KryptoUtil;

/**
 * Die Klasse Zentrum beinhaltet das ZentrumBean und zugehoerige Methoden.
 * 
 * @version $Id: Zentrum.java 2426 2007-05-06 17:34:32Z twillert $
 * @author Andreas Freudling [afreudling@stud.hs-heilbronn.de]
 * 
 */
public class Zentrum {

	/**
	 * Das zugehörige ZentrumBean-Objekt.
	 */
	private ZentrumBean aZentrum;

	/**
	 * Ein Konstruktor dieser Klasse.
	 * 
	 * @param aZentrum
	 *            das zugeöhrige ZentrumBean
	 */
	public Zentrum(ZentrumBean aZentrum) {
		this.aZentrum = aZentrum;
	}

	/**
	 * Diese statische Methode sucht die gewünschten Objekte und falls sie
	 * vorhanden sind, liefert sie zurück.
	 * 
	 * @param sZentrum
	 *            ein ZentrumBean mit gesuchten Eigenschaften (alle irrelevante
	 *            Felder entsprechen den Null-Werten aus der
	 *            de.randi2.utility.NullKonstanten Klasse)
	 * @return ein Vector mit gefundenen Objekten
	 * @throws ZentrumException
	 *             Falls ein Fehler in der Datenbank auftritt.
	 */
	public static Vector<ZentrumBean> suchenZentrum(ZentrumBean sZentrum)
			throws ZentrumException {

		Vector<ZentrumBean> gefundeneZentren = new Vector<ZentrumBean>();
		try {
			gefundeneZentren = DatenbankFactory.getAktuelleDBInstanz()
					.suchenObjekt(sZentrum);
		} catch (DatenbankFehlerException e) {
			// TODO Konstante in ZentrumException
			throw new ZentrumException("Zentren konnten nicht gefunden werden");
		}

		return gefundeneZentren;
	}

	/**
	 * Diese Methode prueft, ob das uebergebene Passwort (KLARTEXT) richtig ist.
	 * 
	 * @param passwort
	 *            Das Passwort (im KLARTEXT), das ueberprueft werden soll.
	 * @return true, wenn das Passwort richtig ist. False, bei falchem Passwort.
	 */
	public boolean pruefenPasswort(String passwort) {
		if (KryptoUtil.getInstance().hashPasswort(passwort).equals(
				this.getZentrumBean().getPasswort())) {
			return true;
		}
		return false;
	}

	/**
	 * Eine typische get() Methode
	 * 
	 * @return ZentrumBean - das aktuelle ZentrumBean, das Daten dieses Zentrums
	 *         enthaelt.
	 */
	public ZentrumBean getZentrumBean() {
		return aZentrum;
	}

	/**
	 * Die Methode soll das ZentrumBean Objekt zurueckgeben.
	 * 
	 * @param zentrumId
	 *            Die ID des angeforderten Zentrums.
	 * @return ZentrumBean Ein ZentrumBean wird zurueckgegeben.
	 */
	public static ZentrumBean get(long zentrumId) {
		// TODO Auto-generated method stub
		return null;
	}

}
