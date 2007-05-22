package de.randi2.model.fachklassen.beans;

import java.util.GregorianCalendar;

import de.randi2.datenbank.Filter;
import de.randi2.model.exceptions.AktivierungException;
import de.randi2.model.exceptions.BenutzerkontoException;
import de.randi2.model.fachklassen.Benutzerkonto;
import de.randi2.utility.KryptoUtil;
import de.randi2.utility.NullKonstanten;

/**
 * <p>
 * Dieses Klasse repräsentiert die Aktivierungsmöglichkeit eines Benutzers im
 * System
 * </p>
 * 
 * @author Andreas Freudling [afreudling@stud.hs-heilbronn.de]
 * @version $Id: AktivierungBean.java 2448 2007-05-07 13:45:09Z afreudli $
 */
public class AktivierungBean extends Filter {

	/**
	 * Name des Attributes fuer die Aktivierung
	 */
	public static final String ATTRIBUT_LINK = "link";

	/**
	 * Kompletter Url Preafix String
	 */
	public static final String PRAEFIX_LINK = "http://localhost/Aktivierung?"
			+ ATTRIBUT_LINK + "=";

	/**
	 * Die ID der Aktivierung.
	 */
	private long id = NullKonstanten.NULL_LONG;

	/**
	 * Das Versanddatum, wann die Anmeldungsmail versand wurde.
	 */
	private GregorianCalendar aVersanddatum = null;

	/**
	 * Das Benutzerkonto, mit dem eine Anmeldung möglich sein soll.
	 */
	private BenutzerkontoBean aBenutzerkonto = null;

	/**
	 * Die ID des Benutzerkontos.
	 */
	private long aBenutzerkontoId = NullKonstanten.NULL_LONG;

	/**
	 * Der Aktivierungslink für die Vervollständigung der Anmeldung.
	 */
	private String aAktivierungsLink = null;

	/**
	 * Get-Methode für die Aktivierungs-ID.
	 * 
	 * @return Liefert die Aktivierungs-ID.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set-Methode für die Aktivierungs-ID.
	 * 
	 * @param aktivierungsId
	 *            Setzt die Aktivierungs-ID.
	 */
	public void setId(long aktivierungsId) {
		this.id = aktivierungsId;
	}

	/**
	 * Get-Methode für den Aktivierungslink.
	 * 
	 * @return Liefert den Aktivierungslink.
	 */
	public String getAktivierungsLink() {
		return aAktivierungsLink;
	}

	/**
	 * Set-Methode für den Aktivierungslink.
	 * 
	 * @param aktivierungsLink
	 *            Setzt den Aktivierungslink (nur die Id)
	 * @throws AktivierungException
	 *             falls der Aktivierungslink
	 */
	public void setAktivierungsLink(String aktivierungsLink)
			throws AktivierungException {
		if (aktivierungsLink == null
				|| aktivierungsLink.length() != KryptoUtil.AKTIVIERUNGSCODE_LAENGE) {
			throw new AktivierungException(
					AktivierungException.AKTIVIERUNGSLINK_FALSCHE_LAENGE);
		}
		this.aAktivierungsLink = aktivierungsLink;
	}

	/**
	 * Get-Methode für das Benutzerkonto.
	 * 
	 * @return Liefert das Benutzerkonto.
	 * @throws BenutzerkontoException
	 *             Fehler
	 */
	public BenutzerkontoBean getBenutzerkonto() throws BenutzerkontoException {
		if (aBenutzerkonto == null) {
			aBenutzerkonto = Benutzerkonto.get(aBenutzerkontoId);
		}

		return aBenutzerkonto;
	}

	/**
	 * Set-Methode für das Benutzerkonto.
	 * 
	 * @param benutzerkonto
	 *            Setzt das Benutzerkonto.
	 * @throws AktivierungException
	 *             EIn Benutzerkonto darf nicht null sein
	 */
	public void setBenutzerkonto(BenutzerkontoBean benutzerkonto)
			throws AktivierungException {
		if (benutzerkonto == null) {
			throw new AktivierungException(
					AktivierungException.BENUTZERKONTO_NICHT_GESETZT);
		}

		this.aBenutzerkonto = benutzerkonto;
		this.setBenutzerkontoId(benutzerkonto.getId());
	}

	/**
	 * Get-Methode für die Benutzerkonto-ID.
	 * 
	 * @return Liefert die Benutzerkonto-ID.
	 */
	public long getBenutzerkontoId() {
		return aBenutzerkontoId;
	}

	/**
	 * Set-Methode für die Benutzerkonto-Id.
	 * 
	 * @param benutzerkontoId -
	 *            Setzt die Benutzerkonto-Id.
	 * @throws AktivierungException
	 *             Das Benutzerkonto muss gesetzt sein
	 */
	public void setBenutzerkontoId(long benutzerkontoId)
			throws AktivierungException {
		if (benutzerkontoId == NullKonstanten.NULL_LONG) {
			throw new AktivierungException(
					AktivierungException.BENUTZERKONTO_NICHT_GESPEICHERT);
		}
		this.aBenutzerkontoId = benutzerkontoId;
	}

	/**
	 * Get-Methode für das Versanddatum.
	 * 
	 * @return Liefert das Versanddatum.
	 */
	public GregorianCalendar getVersanddatum() {
		return aVersanddatum;
	}

	/**
	 * Set-Methode für das Versanddatum.
	 * 
	 * @param versanddatum
	 *            Setzt das Versanddatum.
	 */
	public void setVersanddatum(GregorianCalendar versanddatum) {
		this.aVersanddatum = versanddatum;
	}

	/**
	 * Datenbank-Konstruktor. Der komplette Konstruktor an den alle Attribute
	 * dieser Klasse übergeben werden, mit der Ausnahme von dem
	 * BenutzerkontoBean.
	 * 
	 * @param id
	 *            Die ID der Aktivierung.
	 * @param versanddatum
	 *            Das Versanddatum der Aktivierungsmail.
	 * @param benutzerkontoId
	 *            Die ID des Benutzerkontos.
	 * @param aktivierungsLink
	 *            Der Aktivierungslink für die Vervollständigung der Anmeldung.
	 * @throws AktivierungException
	 *             Die Akivierungsdaten sind unzulaessig, Details sieh
	 *             setMethoden.
	 */
	public AktivierungBean(long id, GregorianCalendar versanddatum,
			long benutzerkontoId, String aktivierungsLink)
			throws AktivierungException {
		
		this.id = id;
		this.aVersanddatum = versanddatum;
		this.aBenutzerkontoId = benutzerkontoId;
		this.aAktivierungsLink = aktivierungsLink;
	}

	/**
	 * Konstruktor für Null-Objekt
	 */
	public AktivierungBean() {

	}

	/**
	 * Liefert einen String der alle Parameter formatiert enthaelt.
	 * 
	 * @return String der alle Parameter formatiert enthaelt.
	 * @see java.lang.Object#toString()
	 * 
	 */
	@Override
	public String toString() {
		return "id:\t" + this.id + "\tlink:\t"
				+ this.aAktivierungsLink + "\tversand:\t" + this.aVersanddatum
				+ "\tbenutzerid\t" + this.aBenutzerkontoId;
	}

	/**
	 * Diese Methode prueft, ob zwei Kontos identisch sind. Zwei Kontos sind
	 * identisch, wenn Benutzernamen identisch sind.
	 * 
	 * @param zuvergleichendesObjekt
	 *            das zu vergleichende Objekt vom selben Typ
	 * @return <code>true</code>, wenn beide Kontos gleich sind, ansonstenm
	 *         <code>false</code>
	 */
	@Override
	public boolean equals(Object zuvergleichendesObjekt) {
		if (zuvergleichendesObjekt instanceof AktivierungBean) {
			AktivierungBean beanZuvergleichen = (AktivierungBean) zuvergleichendesObjekt;
			if (beanZuvergleichen.id != this.id) {
				return false;
			}
			if (beanZuvergleichen.aAktivierungsLink == null
					&& this.aAktivierungsLink != null) {
				return false;
			} else if (beanZuvergleichen.aAktivierungsLink != null
					&& !beanZuvergleichen.aAktivierungsLink
							.equals(this.aAktivierungsLink)) {
				return false;
			}
			if (beanZuvergleichen.aBenutzerkontoId != this.aBenutzerkontoId) {
				return false;
			}
			if (beanZuvergleichen.aVersanddatum == null
					&& this.aVersanddatum != null) {
				return false;
			} else if (beanZuvergleichen.aVersanddatum != null
					&& !(beanZuvergleichen.aVersanddatum.getTimeInMillis()==
							this.aVersanddatum.getTimeInMillis())) {
				return false;
			}
			return true;

		}
		return false;
	}
}
