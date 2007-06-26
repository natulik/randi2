package de.randi2.controller;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.tagplugins.jstl.core.Param;
import org.apache.log4j.Logger;

import de.randi2.datenbank.Datenbank;
import de.randi2.datenbank.DatenbankFactory;
import de.randi2.datenbank.Filter;
import de.randi2.datenbank.exceptions.DatenbankExceptions;
import de.randi2.model.exceptions.BenutzerException;
import de.randi2.model.exceptions.BenutzerkontoException;
import de.randi2.model.exceptions.NachrichtException;
import de.randi2.model.exceptions.PersonException;
import de.randi2.model.exceptions.ZentrumException;
import de.randi2.model.fachklassen.AutomatischeNachricht;
import de.randi2.model.fachklassen.Benutzerkonto;
import de.randi2.model.fachklassen.Person;
import de.randi2.model.fachklassen.Rolle;
import de.randi2.model.fachklassen.Zentrum;
import de.randi2.model.fachklassen.Recht.Rechtenamen;
import de.randi2.model.fachklassen.beans.AktivierungBean;
import de.randi2.model.fachklassen.beans.BenutzerkontoBean;
import de.randi2.model.fachklassen.beans.PersonBean;
import de.randi2.model.fachklassen.beans.ZentrumBean;
import de.randi2.utility.Jsp;
import de.randi2.utility.KryptoUtil;
import de.randi2.utility.LogAktion;
import de.randi2.utility.LogLayout;
import de.randi2.utility.NullKonstanten;
import de.randi2.utility.Parameter;
import de.randi2.utility.SystemException;

/**
 * Diese Klasse repraesentiert das BENUTZERSERVLET, welches Aktionen an die
 * Benutzerkonto-Fachklasse und an den DISPATCHER weiterleitet.
 * <p>
 * Versucht sich ein Benutzer am System anzumelden, so leitet ihn der Dispatcher
 * bei entsprechender anfrage_ID ({@link DispatcherServlet.anfrage_id}) an das
 * BenutzerServlet weiter.<br>
 * Das BenutzerServlet nimmt die Anfrage an und verarbeitet diese weiter<br>
 * </p>
 * 
 * @version $Id: BenutzerServlet.java 2450 2007-05-07 13:57:04Z afreudli $
 * @author Andreas Freudling [afreudling@stud.hs-heilbronn.de]
 * @autor BTheel [btheel@stud.hs-heilbronn.de]
 * 
 * 
 */
@SuppressWarnings("serial")
public class BenutzerServlet extends javax.servlet.http.HttpServlet {

	/**
	 * Die Anfrage-Ids, die vom Dispatcherservlet zu verwenden sind.
	 * 
	 */
	public enum anfrage_id {
		/**
		 * Benutzer moechte sich einloggen
		 */
		CLASS_DISPATCHERSERVLET_LOGIN1,

		/**
		 * Letzter Schritt der Benutzeranmeldung, Eingabe der Persondaten
		 */
		CLASS_DISPATCHERSERVLET_BENUTZER_REGISTRIEREN_VIER,

		/**
		 * Nutzer lässt sich ein neues Passwort zuschicken
		 */
		CLASS_DISPATCHERSERVLET_PASSWORT_VERGESSEN,

		/**
		 * Aufforderung, aus den uebergebenen Daten einen Benutzer zu generieren
		 */
		AKTION_BENUTZER_ANLEGEN,

		/**
		 * Aufforderung, aus den uebergebenen Daten einen Benutzer zu generieren
		 */
		AKTION_BENUTZER_SUCHEN,

		/**
		 * Benutzerdaten aendern
		 */
		BENUTZERDATEN_AENDERN
	}

	/**
	 * Konstruktor.
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public BenutzerServlet() {
		super();

	}

	/**
	 * Diese Methode nimmt HTTP-POST-Request gemaess HTTP-Servlet Definition
	 * entgegen. Hier werden Anfragen abgearbeitet, die den Benutzer betreffen.
	 * 
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String id = (String) request.getAttribute("anfrage_id");
		Logger.getLogger(this.getClass()).debug("BenutzerServlet.doPost()"); // Trace
		Logger.getLogger(this.getClass()).debug("anfrage_id: " + id);// ID
		// loggen

		// Login
		if (id.equals(BenutzerServlet.anfrage_id.CLASS_DISPATCHERSERVLET_LOGIN1
				.name())) {
			this.classDispatcherservletLogin1(request, response);
		}// if

		// Letzter Schritt Benutzer registrieren
		else if (id
				.equals(BenutzerServlet.anfrage_id.CLASS_DISPATCHERSERVLET_BENUTZER_REGISTRIEREN_VIER
						.name())) {
			Logger.getLogger(this.getClass()).debug(
					"id '" + id + "' korrekt erkannt");
			this.classDispatcherservletBenutzerRegistrierenVier(request,
					response);
		} else if (id.equals(anfrage_id.AKTION_BENUTZER_ANLEGEN.name())) {
			classDispatcherservletStudienleiterAdminAnlegen(request, response);
		} else if (id.equals(BenutzerServlet.anfrage_id.AKTION_BENUTZER_SUCHEN
				.name())) {
			classDispatcherServletBenutzerSuchen(request, response);
		} else if (id.equals(BenutzerServlet.anfrage_id.BENUTZERDATEN_AENDERN
				.name())) {
			aendernBenutzer(request, response);
		} else if (id
				.equals(anfrage_id.CLASS_DISPATCHERSERVLET_PASSWORT_VERGESSEN
						.name())) {
			this.classDispatcherServletPasswortVergessen(request, response);
		}

		// if
	}// doPost

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Die Methode aendert die Benutzerdaten bei einem bereits bestehenden
	 * Benutzer in der Datenbank.
	 * 
	 * @param request
	 *            Der request fuer das Servlet.
	 * @param response
	 *            Der Response des Servlets.
	 * @throws IOException
	 *             Falls Fehler in der E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 */
	private void aendernBenutzer(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getParameter("loeschenA") == null) {

			// Alle aenderbaren Attribute des request inititalisieren
			String titel = request.getParameter("Titel");
			PersonBean.Titel titelenum = null;
			String telefon = request.getParameter("Telefon");
			String handynummer = request.getParameter("Handy");
			String nachnameA = request.getParameter("NachnameA");
			String vornameA = request.getParameter("VornameA");
			String telefonA = request.getParameter("TelefonA");
			// char geschlechtA = request.getParameter("");
			String emailA = request.getParameter("EmailA");
			String fax = request.getParameter("Fax");
			String passwort = null;

			// Konvertierung String enum
			for (PersonBean.Titel t : PersonBean.Titel.values()) {
				if (titel.equals(t.toString())) {
					titelenum = t;
					break;
				}
			}

			// Wiederholte Passworteingabe pruefen
			if (request.getParameter("Passwort") != null
					&& request.getParameter("Passwort_wh") != null) {
				if (request.getParameter("Passwort").equals(
						request.getParameter("Passwort_wh"))) {
					passwort = request.getParameter("Passwort");
				} else {
					passwort = "";
				}
			}

			BenutzerkontoBean aBenutzer = (BenutzerkontoBean) (request
					.getSession()).getAttribute("aBenutzer");
			try {
				PersonBean aPerson = aBenutzer.getBenutzer();
				try {
					aPerson.setTitel(titelenum);
					aPerson.setTelefonnummer(telefon);
					aPerson.setHandynummer(handynummer);
					if (aPerson.getStellvertreter() != null) {
						aPerson.getStellvertreter().setNachname(nachnameA);
						aPerson.getStellvertreter().setVorname(vornameA);
						aPerson.getStellvertreter().setTelefonnummer(telefonA);
						aPerson.getStellvertreter().setEmail(emailA);
						// siehe unten
						// aPerson.getStellvertreter().setGeschlecht(geschlechtA);
					} else {
						PersonBean bPerson = new PersonBean();
						bPerson.setNachname(nachnameA);
						bPerson.setVorname(vornameA);
						bPerson.setTelefonnummer(telefonA);
						bPerson.setEmail(emailA);
						// TODO muss noch ueberlegen wie ich das mit dem
						// Geschlecht mache. twillert
						bPerson.setGeschlecht('m');
						bPerson = DatenbankFactory.getAktuelleDBInstanz()
								.schreibenObjekt(bPerson);
						aPerson.setStellvertreterId(bPerson.getId());
					}
					aPerson.setFax(fax);
					if (passwort != null) {
						if (!(passwort.trim().equals(""))) {
							String hash = KryptoUtil.getInstance()
									.hashPasswort(passwort);
							aBenutzer.setPasswort(hash);
						}
					}
				} catch (PersonException e) {
					request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
							.getMessage());
				} catch (BenutzerkontoException e) {
					request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
							.getMessage());
				}
				DatenbankFactory.getAktuelleDBInstanz()
						.schreibenObjekt(aPerson);
				DatenbankFactory.getAktuelleDBInstanz().schreibenObjekt(
						aBenutzer);
				// TODO hier noch erfolgreich nachricht einfuegen
				request.getRequestDispatcher("global_welcome.jsp").forward(
						request, response);
			} catch (DatenbankExceptions e) {
				request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
						.getMessage());
			}
		} else {
			try {
				// datenbank muss dann quasi selber den fremdschluessel ueberall
				// loeschen
				BenutzerkontoBean aBenutzer = (BenutzerkontoBean) (request
						.getSession()).getAttribute("aBenutzer");
				PersonBean aPerson = aBenutzer.getBenutzer();
				if (aPerson.getStellvertreter() != null) {
					DatenbankFactory.getAktuelleDBInstanz().loeschenObjekt(
							aPerson.getStellvertreter());
					// TODO hier noch erfolgreich nachricht einfuegen
					request.getRequestDispatcher("global_welcome.jsp").forward(
							request, response);
				} else {
					request.getRequestDispatcher("daten_aendern.jsp").forward(
							request, response);
				}
			} catch (Exception e) {
				request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
						.getMessage());
			}
		}
	}

	/**
	 * Prueft, ob der Benutzer Sysop ist und leitet die Anfrage entsprechend
	 * weiter
	 * <p>
	 * Ist das System gesperrt (diese Pruefung findet hier nicht erneut statt!)
	 * so kann sich nur der Sysop in das System einloggen.<br>
	 * Besitzt das uebergebene Konto die Rolle des Systemoperators, so wird die
	 * Anfrage entsprechend weiter geleitet<br>
	 * Anderenfalls wird der Login-Versuch abgewiesen.<br>
	 * In beiden Faellen wird das Ergebnis geloggt.
	 * </p>
	 * 
	 * @param aBenutzer
	 *            Benutzerkonto
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	private void weiterleitungLoginKorrektBeiSystemGesperrt(
			BenutzerkontoBean aBenutzer, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Logger.getLogger(this.getClass()).debug(
				"System gesperrt, Kandidat == Sysop?");
		if (aBenutzer.getRolle().getRollenname() == Rolle.Rollen.SYSOP) {
			// FIXME Doppelt gemoppelt. Lieber Pruefung ob gesperrt mit in die
			// normale weiterleitung nehmen
			// Sicherstellen, das sich Sysop einloggen kann
			weiterleitungLoginKorrekt(aBenutzer, request, response);
		} else {

			request
					.setAttribute(DispatcherServlet.FEHLERNACHRICHT,
							" Das Systen ist derzeit gesperrt.<br> Login nicht m&ouml;glich!");
			request.getRequestDispatcher("/index_gesperrt.jsp").forward(
					request, response);
			// Ergebnis loggen
			Logger.getLogger(this.getClass()).debug(
					"System gesperrt, Benutzer abgewiesen");
			LogAktion a = new LogAktion("Benutzer abgewiesen, System gesperrt",
					aBenutzer);
			Logger.getLogger(LogLayout.LOGIN_LOGOUT).info(a);
		}
	}

	/**
	 * Leitet den Benutzer an die fuer ihn bestimmte Einstiegsseite in das
	 * System weiter und loggt das Ergebnis in das Anwendungslog
	 * <p>
	 * Es findet keine Pruefung statt, ob das System gesperrt ist (Vgl.
	 * {@link #weiterleitungLoginKorrektBeiSystemGesperrt(BenutzerkontoBean, HttpServletRequest, HttpServletResponse)})
	 * </p>
	 * 
	 * @param aBenutzer
	 *            Benutzerkonto anhand dessen Rolle weitergeleitet wird
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	private void weiterleitungLoginKorrekt(BenutzerkontoBean aBenutzer,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Logger.getLogger(this.getClass()).debug(
				"BenutzerServlet.weiterleitungLoginKorrekt()");

		(request.getSession()).setAttribute("aBenutzer", aBenutzer);// Benutzerkontobean
		// an
		// Session
		// binden
		Logger.getLogger(this.getClass()).debug("Binde Benutzer an Session");

		if (aBenutzer.getRolle().getRollenname() == Rolle.Rollen.ADMIN) {
			request.getRequestDispatcher("/global_welcome.jsp").forward(
					request, response);
			loggeKorrekteanmeldung(aBenutzer);
			return;
		} else if (aBenutzer.getRolle().getRollenname() == Rolle.Rollen.SYSOP) {
			request.getRequestDispatcher("/systemadministration.jsp").forward(
					request, response);
			loggeKorrekteanmeldung(aBenutzer);
			return;
		} else {
			request.setAttribute(DispatcherServlet.requestParameter.ANFRAGE_Id
					.name(), StudieServlet.anfrage_id.AKTION_STUDIE_AUSWAEHLEN
					.name());
			request.getRequestDispatcher("StudieServlet").forward(request,
					response);
			loggeKorrekteanmeldung(aBenutzer);
			return;
		}

	}

	/**
	 * Loggt die Anmeldung des uebergebenen Benutzers im Anwendungslog mit der
	 * Meldung '<i>Benutzer hat sich erfolgreich eingeloggt</i>
	 * 
	 * @param aBenutzer
	 *            neu angemeldeter Benutzer
	 */
	private void loggeKorrekteanmeldung(BenutzerkontoBean aBenutzer) {
		LogAktion a = new LogAktion("Benutzer hat sich erfolgreich eingeloggt",
				aBenutzer);
		Logger.getLogger(LogLayout.LOGIN_LOGOUT).info(a);
	}

	/**
	 * Weiterleitung der Anfrage bei Fehler
	 * <p>
	 * Leitet den Benutzer auf die entsprechende Indexseite weiter, abhaengig
	 * davon, ob das System gesperrt ist oder nicht.
	 * </p>
	 * <p>
	 * Die uebergebene Fehlermeldung wird an den Request als
	 * {@link DispatcherServlet#FEHLERNACHRICHT} angefuegt.
	 * </p>
	 * 
	 * @param fehlermeldungAnBenutzer
	 *            Fehlermeldung, die dem Benutzer angzeigt wird
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	private void weiterleitungBeiFehler(String fehlermeldungAnBenutzer,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Fehlermeldung anfuegen
		request.setAttribute(DispatcherServlet.FEHLERNACHRICHT,
				fehlermeldungAnBenutzer);

		if ((Boolean) request
				.getAttribute(DispatcherServlet.requestParameter.IST_SYSTEM_GESPERRT
						.toString())) {
			request.getRequestDispatcher("/index_gesperrt.jsp").forward(
					request, response);
		} else {
			request.getRequestDispatcher("/index.jsp").forward(request,
					response);
		}
	}

	/**
	 * Realisiert den Benutzer Login.
	 * 
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	private void classDispatcherservletLogin1(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Logger.getLogger(this.getClass()).debug(
				"BenutzerServlet.class_dispatcherservlet_login1()");
		BenutzerkontoBean sBenutzer = null;

		boolean isSystemGesperrt = (Boolean) request
				.getAttribute(DispatcherServlet.requestParameter.IST_SYSTEM_GESPERRT
						.toString());

		try {
			sBenutzer = new BenutzerkontoBean((String) request
					.getParameter(Parameter.benutzerkonto.LOGINNAME.name()),
					(String) request
							.getParameter(Parameter.benutzerkonto.PASSWORT
									.name()));
			// Filter setzen
			sBenutzer.setFilter(true);
			Vector<BenutzerkontoBean> gBenutzer = null;

			try { // Benutzer in der DB suchen
				gBenutzer = Benutzerkonto.suchenBenutzer(sBenutzer);
			} catch (DatenbankExceptions e) {
				// Interner Fehler, wird nicht an Benutzer weitergegeben
				Logger.getLogger(this.getClass()).fatal(
						"Fehler bei Benutzerfilterung", e);
			}
			if (gBenutzer.size() == 1) { // _genau_ ein Konto gefunden
				Logger
						.getLogger(this.getClass())
						.debug(
								"BenutzerServlet.class_dispatcherservlet_login1(): Genau einen Benutzer gefunden");
				// Gefundenen kontoBean aus dem Vector holen
				BenutzerkontoBean aBenutzer = gBenutzer.get(0);

				if (!aBenutzer.isGesperrt()
						&& new Benutzerkonto(aBenutzer)
								.pruefenPasswort((String) request
										.getParameter(Parameter.benutzerkonto.PASSWORT
												.name()))) {
					// Konto nicht gesperrt und PW korrekt
					if (isSystemGesperrt) {
						// Konto korrekt, aber System gesperrt
						weiterleitungLoginKorrektBeiSystemGesperrt(aBenutzer,
								request, response);
					} else {
						// Konto korrekt, normaler Ablauf
						if (aBenutzer.getLetzterLogin() == null) {
							// aBenutzer.setLetzterLogin(new
							// GregorianCalendar());
							aBenutzer.setErsterLogin(new GregorianCalendar());
							// Logging Konto setzten
							aBenutzer.setBenutzerkontoLogging(Filter
									.getSystemdummy());
							// Erster Login setzten
							aBenutzer = DatenbankFactory.getAktuelleDBInstanz()
									.schreibenObjekt(aBenutzer);
						}

						weiterleitungLoginKorrekt(aBenutzer, request, response);
					}
				}// if
				else { // PW inkorrekt oder Konto gesperrt
					weiterleitungBeiFehler(
							"Loginfehler:<br> Bitte Benutzername und Passwort &uuml;berpr&uuml;fen",
							request, response);
					// Aktion loggen
					LogAktion a;
					if (aBenutzer.isGesperrt()) {
						// Konto gesperrt
						a = new LogAktion(
								"Versuchter Zugriff auf gesperrtes Konto",
								aBenutzer);
					} else {
						a = new LogAktion("Falsches Passwort eingegeben.",
								aBenutzer);
					}
					Logger.getLogger(LogLayout.LOGIN_LOGOUT).warn(a);
				}
			}// if
			else {// Gefundene Konten != 1
				weiterleitungBeiFehler(
						"Loginfehler:<br> Bitte Benutzername und Passwort &uuml;berpr&uuml;fen",
						request, response);

				if (gBenutzer.size() == 0) {
					// Unbekanntes Konto
					LogAktion a = new LogAktion("Unbekannter Benutzername.",
							sBenutzer);
					Logger.getLogger(LogLayout.LOGIN_LOGOUT).warn(a);
				} else { // 
					Logger.getLogger(this.getClass()).fatal(
							"Mehr als einen Benutzer fuer '"
									+ sBenutzer.getBenutzername()
									+ "' in der Datenbank gefunden!");
					// FIXME ist das Loggin hier Korrekt? Loggt der die Aktion
					// in das Richtige Log? -BTheel
				}
			}// else
		} catch (BenutzerkontoException e) {
			// Ungueltiger Benutzername/Passwort
			weiterleitungBeiFehler(
					"Loginfehler:<br> Bitte Benutzername und Passwort &uuml;berpr&uuml;fen",
					request, response);

			BenutzerkontoBean dummy = new BenutzerkontoBean();
			dummy.setFilter(true);
			try {
				dummy
						.setBenutzername((String) request
								.getParameter(Parameter.benutzerkonto.LOGINNAME
										.name()));
			} catch (BenutzerkontoException e1) {
			}

			LogAktion a = new LogAktion(
					"Ungueltige Benutzername/Passwort Kombination eingegeben.",
					dummy);
			// FIXME LogMsg eindeutig genug?--Btheel
			Logger.getLogger(LogLayout.LOGIN_LOGOUT).warn(a);
		}// catch
	}

	/**
	 * Realisiert den letzten Schritt des Benutzerregistierens. Die Eingabe der
	 * Personendaten
	 * 
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	private void classDispatcherservletBenutzerRegistrierenVier(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Alle Attribute des request inititalisieren
		// String fehlernachricht = "";
		String vorname = request.getParameter(Parameter.person.VORNAME.name());
		String nachname = request
				.getParameter(Parameter.person.NACHNAME.name());
		char geschlecht = '\0';
		String passwort = null;
		String email = request.getParameter(Parameter.person.EMAIL.name());
		String telefon = request.getParameter(Parameter.person.TELEFONNUMMER
				.name());
		String fax = request.getParameter(Parameter.person.FAX.name());
		String handynummer = request.getParameter(Parameter.person.HANDYNUMMER
				.name());
		String titel = request.getParameter(Parameter.person.TITEL.name());
		PersonBean.Titel titelenum = null;
		try {
			// Geschlecht gesetzt pruefen
			if (request.getParameter(Parameter.person.GESCHLECHT.name()) == null) {
				throw new BenutzerkontoException(
						"Bitte Geschlecht ausw&auml;hlen");
			}
			geschlecht = request.getParameter(
					Parameter.person.GESCHLECHT.name()).charAt(0);
			// Konvertierung String enum
			for (PersonBean.Titel t : PersonBean.Titel.values()) {
				if (titel.equals(t.toString())) {
					titelenum = t;
					break;
				}
			}

			// Wiederholte Passworteingabe prüfen
			if (request.getParameter(Parameter.benutzerkonto.PASSWORT.name()) != null
					&& request
							.getParameter(Parameter.benutzerkonto.PASSWORT_WIEDERHOLUNG
									.name()) != null) {
				if (request
						.getParameter(Parameter.benutzerkonto.PASSWORT.name())
						.equals(
								request
										.getParameter(Parameter.benutzerkonto.PASSWORT_WIEDERHOLUNG
												.name()))) {
					passwort = request
							.getParameter(Parameter.benutzerkonto.PASSWORT
									.name());
				} else {
					throw new BenutzerkontoException(
							BenutzerkontoException.PASSWORT_STIMMT_NICHT_UEBEREIN);

				}
			}
			// Benutzer anlegen
			// Hier findet die Überprüfung der Daten auf Serverseite statt,
			// Fehler wird an Benutzer weiter gegeben
			// Person erzeugen und in DB speichern
			PersonBean aPerson = null;
			aPerson = new PersonBean();
			aPerson.setNachname(nachname);
			aPerson.setVorname(vorname);
			aPerson.setTitel(titelenum);
			aPerson.setGeschlecht(geschlecht);
			aPerson.setEmail(email);
			aPerson.setTelefonnummer(telefon);
			aPerson.setHandynummer(handynummer);
			aPerson.setFax(fax);
			aPerson.setBenutzerkontoLogging(Filter.getSystemdummy());
			aPerson = DatenbankFactory.getAktuelleDBInstanz().schreibenObjekt(
					aPerson);

			// Zugehöriges Benutzerkonto erstellen und in DB Speichern
			BenutzerkontoBean aBenutzerkonto;
			aBenutzerkonto = new BenutzerkontoBean(email, KryptoUtil
					.getInstance().hashPasswort(passwort), aPerson);
			aBenutzerkonto
					.setZentrum((ZentrumBean) request
							.getSession()
							.getAttribute(
									DispatcherServlet.sessionParameter.ZENTRUM_BENUTZER_ANLEGEN
											.toString()));
			aBenutzerkonto.setRolle(Rolle.getStudienarzt());
			aBenutzerkonto.setErsterLogin(null);
			aBenutzerkonto.setLetzterLogin(null);
			aBenutzerkonto.setGesperrt(true);
			aBenutzerkonto.setBenutzerkontoLogging(Filter.getSystemdummy());
			aBenutzerkonto = Benutzerkonto.anlegenBenutzer(aBenutzerkonto)
					.getBenutzerkontobean();
			request.getRequestDispatcher("/benutzer_anlegen_vier.jsp").forward(
					request, response);

			// Aktivierung erstellen
			// TODO -->afreudliUNIQUE AKTIVIERUNGSLINK BEACHTEN
			AktivierungBean aktivierung = new AktivierungBean(
					NullKonstanten.DUMMY_ID, new GregorianCalendar(),
					aBenutzerkonto.getId(), KryptoUtil.getInstance()
							.getAktivierungslink());
			aktivierung.setBenutzerkontoLogging(Filter.getSystemdummy());
			aktivierung = DatenbankFactory.getAktuelleDBInstanz()
					.schreibenObjekt(aktivierung);
			AutomatischeNachricht aktivierungMail = new AutomatischeNachricht(
					aPerson, AutomatischeNachricht.autoNachricht.AKTIVIERUNG);
			aktivierungMail.senden();

			// Falls ein Fehler aufgetreten ist, request wieder auffüllen
		} catch (Exception e) {

			if (e instanceof BenutzerException) {

				request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
						.getMessage());

			} else if (e instanceof DatenbankExceptions
					&& e.getMessage().equals(
							DatenbankExceptions.TESTER_EXISTIERT_ERR)) {
				request
						.setAttribute(DispatcherServlet.FEHLERNACHRICHT,
								"Benutzer ist bereits vorhanden. Bitte anderen Benutzernamen w&auml;hlen.");
			} else
				throw new SystemException("Fehler bei Aktivierung");

			request.getRequestDispatcher("/benutzer_anlegen_drei.jsp").forward(
					request, response);
		}

	}

	/**
	 * Methode für die Suchanfrage an die Datenbank für die Ausgabe der
	 * Benutzer.
	 * 
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	private void classDispatcherServletBenutzerSuchen(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		BenutzerkontoBean bKonto = null;
		PersonBean person = null;
		ZentrumBean zentrum = null;
		Vector<BenutzerkontoBean> benutzerVec = null;
		Vector<PersonBean> personVec = new Vector<PersonBean>();
		Vector<ZentrumBean> zentrumVec = new Vector<ZentrumBean>();
		Vector<BenutzerkontoBean> tmpBVec = new Vector<BenutzerkontoBean>();
		Vector<ZentrumBean> tmpZVec = new Vector<ZentrumBean>();
		PersonBean[] pArr = null;
		BenutzerkontoBean[] bArr = null;
		ZentrumBean[] zArr = null;
		Iterator<BenutzerkontoBean> it_B = null;
		Iterator<PersonBean> it_P = null;
		Iterator<ZentrumBean> it_Z = null;
		boolean gesuchtKonto = false;
		boolean gesuchtPerson = false;
		boolean gesuchtZentrum = false;

		if (((String) request.getParameter("Aktualisieren")) != null) {
			try {
				if ((String) request
						.getParameter(Parameter.benutzerkonto.LOGINNAME.name()) != null) {
					bKonto = new BenutzerkontoBean();
					bKonto.setBenutzername(request
							.getParameter(Parameter.benutzerkonto.LOGINNAME
									.name()));
					bKonto.setFilter(true);
					benutzerVec = new Vector<BenutzerkontoBean>();
					benutzerVec = Benutzerkonto.suchenBenutzer(bKonto);
					bKonto.setGesperrt(true);
					benutzerVec.addAll(Benutzerkonto.suchenBenutzer(bKonto));
					gesuchtKonto = true;
				}

				if (((String) request.getParameter(Parameter.person.VORNAME
						.name())) != null
						|| ((String) request
								.getParameter(Parameter.person.NACHNAME.name())) != null
						|| ((String) request
								.getParameter(Parameter.person.EMAIL.name())) != null) {
					person = new PersonBean();
					person.setFilter(true);
					person.setVorname(request
							.getParameter(Parameter.person.VORNAME.name()));
					person.setNachname(request
							.getParameter(Parameter.person.NACHNAME.name()));
					person.setEmail(request.getParameter(Parameter.person.EMAIL
							.name()));
					personVec = new Vector<PersonBean>();
					personVec = Person.suchenPerson(person);

					pArr = new PersonBean[personVec.size()];
					personVec.toArray(pArr);
					gesuchtPerson = true;

					if (!gesuchtKonto) {
						benutzerVec = new Vector<BenutzerkontoBean>();
						int counter = 0;
						bKonto = new BenutzerkontoBean();
						PersonBean gefundenePerson = new PersonBean();
						while (counter < personVec.size()) {
							gefundenePerson = (PersonBean) pArr[counter++];

							bKonto.setBenutzerId(gefundenePerson.getId());
							bKonto.setFilter(true);
							tmpBVec = Benutzerkonto.suchenBenutzer(bKonto);
							bKonto.setGesperrt(true);
							tmpBVec
									.addAll(Benutzerkonto
											.suchenBenutzer(bKonto));
							it_B = tmpBVec.iterator();
							while (tmpBVec.size() > 0 && it_B.hasNext()) {
								benutzerVec.add(it_B.next());
							}
						}
						gesuchtKonto = true;
					}
				}
				if ((String) request.getParameter(Parameter.zentrum.INSTITUTION
						.name()) != null) {
					zentrum = new ZentrumBean();
					zentrum
							.setInstitution(request
									.getParameter(Parameter.zentrum.INSTITUTION
											.name()));
					zentrum.setFilter(true);
					zentrumVec = new Vector<ZentrumBean>();
					zentrumVec = Zentrum.suchenZentrum(zentrum);
					zentrum.setIstAktiviert(true);
					zentrumVec.addAll(Zentrum.suchenZentrum(zentrum));

					zArr = new ZentrumBean[zentrumVec.size()];
					zentrumVec.toArray(zArr);

					if (!gesuchtKonto) {
						int counter = 0;
						bKonto = new BenutzerkontoBean();
						while (counter < zentrumVec.size()) {
							zentrum = (ZentrumBean) zArr[counter++];

							bKonto.setZentrumId(zentrum.getId());
							bKonto.setFilter(true);

							tmpBVec = Benutzerkonto.suchenBenutzer(bKonto);
							bKonto.setGesperrt(true);
							tmpBVec
									.addAll(Benutzerkonto
											.suchenBenutzer(bKonto));
							it_B = tmpBVec.iterator();
							while (tmpBVec.size() > 0 && it_B.hasNext()) {
								benutzerVec.add(it_B.next());
							}
						}
						gesuchtKonto = true;
					}
					if (!gesuchtPerson) {
						int counter = 0;
						person = new PersonBean();
						while (counter < zentrumVec.size()) {
							zentrum = (ZentrumBean) zArr[counter++];

							person = Zentrum.getZugehoerigePerson(zentrum
									.getId());
							personVec.add(person);
						}
						gesuchtPerson = true;
					}
					gesuchtZentrum = true;
				} else {
					zentrum = new ZentrumBean();
					zentrum.setFilter(true);
					zentrumVec = Zentrum.suchenZentrum(zentrum);
					// TODO --kkrupka zu implementierende Fälle
					if (!gesuchtZentrum && !gesuchtKonto) {

					} else if (!gesuchtZentrum || !gesuchtPerson) {

					} else if (!gesuchtZentrum) {

					}
				}
				if (personVec.size() != benutzerVec.size()
						|| personVec.size() != zentrumVec.size()
						|| benutzerVec.size() != zentrumVec.size()) {
					// TODO wegen 1:n Beziehungsprobleme
				}

			} catch (PersonException e) {
				request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
						.getMessage());
			} catch (ZentrumException e) {
				request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
						.getMessage());
			} catch (BenutzerkontoException e) {
				request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
						.getMessage());
			}
		} else {
			bKonto = new BenutzerkontoBean();
			bKonto.setFilter(true);
			benutzerVec = Benutzerkonto.suchenBenutzer(bKonto);
			it_B = benutzerVec.iterator();

			while (it_B.hasNext()) {
				bKonto = it_B.next();

				person = Person.get(bKonto.getBenutzerId());
				personVec.add(person);

				zentrum = Zentrum.getZentrum(bKonto.getZentrumId());
				zentrumVec.add(zentrum);
			}
		}

		request.setAttribute("listeBenutzer", benutzerVec);
		request.setAttribute("listePerson", personVec);
		request.setAttribute("listeZentrum", zentrumVec);
		request.getRequestDispatcher(Jsp.ADMIN_LISTE)
				.forward(request, response);

	}

	/**
	 * Methode zum Erstellen eines Studienleiteraccounts
	 * 
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 */
	/**
	 * Methode zum Erstellen eines Studienleiteraccounts
	 * 
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 */
	private void classDispatcherservletStudienleiterAdminAnlegen(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Alle Attribute des request inititalisieren
		// String fehlernachricht = "";
		String vorname = request.getParameter(Parameter.person.VORNAME.name());
		String nachname = request
				.getParameter(Parameter.person.NACHNAME.name());
		char geschlecht = '\0';
		String passwort = KryptoUtil.getInstance().generatePasswort(10);
		String email = request.getParameter(Parameter.person.EMAIL.name());
		String telefon = request.getParameter(Parameter.person.TELEFONNUMMER
				.name());
		String fax = request.getParameter(Parameter.person.FAX.name());

		String institut = request
				.getParameter(Parameter.benutzerkonto.ZENTRUM_FK.name());
		String titel = request.getParameter(Parameter.person.TITEL.name());
		PersonBean.Titel titelenum = null;
		String vornameA = request
				.getParameter(Parameter.person.STELLVERTRETER_VORNAME.name());
		String nachnameA = request
				.getParameter(Parameter.person.STELLVERTRETER_NACHNAME.name());
		String telefonA = request
				.getParameter(Parameter.person.STELLVERTRETER_TELEFONNUMMER
						.name());
		String benutzername = request
				.getParameter(Parameter.benutzerkonto.LOGINNAME.name());
		long zentrumId = -1l;
		System.out.println("Zentrum: " + institut);

		Rolle rolle = ((BenutzerkontoBean) request.getSession().getAttribute(
				"aBenutzer")).getRolle();

		try {
			// Geschlecht gesetzt pruefen
			if (request.getParameter(Parameter.person.GESCHLECHT.name()) == null) {
				throw new BenutzerkontoException(
						"Bitte Geschlecht ausw&auml;hlen");
			}
			geschlecht = request.getParameter(
					Parameter.person.GESCHLECHT.name()).charAt(0);
			// Konvertierung String enum
			for (PersonBean.Titel t : PersonBean.Titel.values()) {
				if (titel.equals(t.toString())) {
					titelenum = t;
					break;
				}
			}

			// Benutzer anlegen
			PersonBean aPerson = null;
			aPerson = new PersonBean();
			aPerson.setNachname(nachname);
			aPerson.setVorname(vorname);
			aPerson.setTitel(titelenum);
			aPerson.setGeschlecht(geschlecht);
			aPerson.setEmail(email);
			aPerson.setTelefonnummer(telefon);
			aPerson.setFax(fax);

			PersonBean rolf = new PersonBean();
			rolf.setNachname(nachnameA);
			rolf.setVorname(vornameA);
			rolf.setTelefonnummer(telefonA);
			rolf.setEmail("dummyEmail@fuerden.nl"); // Sonst knallts inner Db
			rolf.setTitel(PersonBean.Titel.KEIN_TITEL);// Sonst knallts inner
			// Db

			BenutzerkontoBean anleger = (BenutzerkontoBean) request
					.getSession().getAttribute("aBenutzer");

			rolf.setBenutzerkontoLogging(anleger);
			rolf = DatenbankFactory.getAktuelleDBInstanz()
					.schreibenObjekt(rolf);

			aPerson.setStellvertreter(rolf);

			aPerson.setBenutzerkontoLogging(anleger);
			aPerson = DatenbankFactory.getAktuelleDBInstanz().schreibenObjekt(
					aPerson);

			// Zugehöriges Benutzerkonto erstellen und in DB Speichern

			BenutzerkontoBean aBenutzerkonto;
			aBenutzerkonto = new BenutzerkontoBean(benutzername, KryptoUtil
					.getInstance().hashPasswort(passwort), aPerson);

			zentrumId = Long.valueOf(institut);
			aBenutzerkonto.setZentrumId(zentrumId);

			if (rolle.besitzenRolleRecht(Rechtenamen.STULEIACCOUNTS_VERWALTEN)) {
				aBenutzerkonto.setRolle(Rolle.getStudienleiter());
			}
			if (rolle.besitzenRolleRecht(Rechtenamen.ADMINACCOUNTS_VERWALTEN)) {
				aBenutzerkonto.setRolle(Rolle.getAdmin());
			}

			aBenutzerkonto.setErsterLogin(null);
			aBenutzerkonto.setLetzterLogin(null);
			aBenutzerkonto.setGesperrt(true);
			aBenutzerkonto.setBenutzerkontoLogging(anleger);

			Benutzerkonto konto = Benutzerkonto.anlegenBenutzer(aBenutzerkonto);

			// Mitteilungsversand
			// Passwort
			AutomatischeNachricht passwortmail = new AutomatischeNachricht(
					aBenutzerkonto.getBenutzer(), passwort);
			passwortmail.senden();
			// Aktivierungsmail
			AktivierungBean aktivierung = new AktivierungBean(
					NullKonstanten.DUMMY_ID, new GregorianCalendar(), konto
							.getBenutzerkontobean().getId(), KryptoUtil
							.getInstance().getAktivierungslink());

			aktivierung.setBenutzerkontoLogging(Filter.getSystemdummy());
			aktivierung = DatenbankFactory.getAktuelleDBInstanz()
					.schreibenObjekt(aktivierung);
			AutomatischeNachricht aktivierungMail = new AutomatischeNachricht(
					konto.getBenutzerkontobean().getBenutzer(),
					AutomatischeNachricht.autoNachricht.AKTIVIERUNG);
			aktivierungMail.senden();
			// Nachricht an anleger

			request.setAttribute(DispatcherServlet.NACHRICHT_OK,
					"Account erfolgreich angelegt.");
			request.setAttribute(DispatcherServlet.requestParameter.ANFRAGE_Id
					.toString(), DispatcherServlet.anfrage_id.JSP_ADMIN_ANLEGEN
					.name());
			request.getRequestDispatcher("/DispatcherServlet").forward(request,
					response);

		} catch (Exception e) {

			request.setAttribute(Parameter.person.VORNAME.name(), vorname);
			request.setAttribute(Parameter.person.NACHNAME.name(), nachname);

			request.setAttribute(Parameter.person.EMAIL.name(), email);
			request
					.setAttribute(Parameter.person.TELEFONNUMMER.name(),
							telefon);
			request.setAttribute(Parameter.person.FAX.name(), fax);

			request.setAttribute(Parameter.benutzerkonto.ZENTRUM_FK.name(),
					zentrumId);
			request.setAttribute(Parameter.person.TITEL.name(), titelenum
					.toString());
			request.setAttribute(
					Parameter.person.STELLVERTRETER_VORNAME.name(), vornameA);
			request.setAttribute(Parameter.person.STELLVERTRETER_NACHNAME
					.name(), nachnameA);
			request.setAttribute(Parameter.person.STELLVERTRETER_TELEFONNUMMER
					.name(), telefonA);
			request.setAttribute(Parameter.benutzerkonto.LOGINNAME.name(),
					benutzername);

			if (geschlecht == 'm') {
				request.setAttribute(Parameter.person.GESCHLECHT.name(), "m");

			} else if (geschlecht == 'w') {
				request.setAttribute(Parameter.person.GESCHLECHT.name(), "w");
			}

			request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
					.getMessage());

			request.setAttribute(DispatcherServlet.requestParameter.ANFRAGE_Id
					.toString(), DispatcherServlet.anfrage_id.JSP_ADMIN_ANLEGEN
					.name());
			request.getRequestDispatcher("/DispatcherServlet").forward(request,
					response);
		}
	}

	/**
	 * Methode zum Zusenden eines neuen Passworts an den Studienarzt
	 * 
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 */
	private void classDispatcherServletPasswortVergessen(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean fehler = false;

		// Benutzerkonto auslesen
		String benutzername = request
				.getParameter(Parameter.benutzerkonto.LOGINNAME.name());
		BenutzerkontoBean aBenutzerkonto = new BenutzerkontoBean();
		try {
			// TODO --afreudli wer darf sein Passwort ändern?!
			aBenutzerkonto.setBenutzername(benutzername);
			aBenutzerkonto.setFilter(true);
			aBenutzerkonto = DatenbankFactory.getAktuelleDBInstanz()
					.suchenObjekt(aBenutzerkonto).firstElement();

			// Neues Passwort generieren und reinschreiben;
			String neuesPasswort = KryptoUtil.getInstance()
					.generatePasswort(12);

			aBenutzerkonto.setPasswort(KryptoUtil.getInstance().hashPasswort(
					neuesPasswort));
			aBenutzerkonto.setBenutzerkontoLogging(Filter.getSystemdummy());
			aBenutzerkonto = DatenbankFactory.getAktuelleDBInstanz()
					.schreibenObjekt(aBenutzerkonto);

			AutomatischeNachricht autoNachricht = new AutomatischeNachricht(
					aBenutzerkonto.getBenutzer(), neuesPasswort);
			Logger.getLogger(this.getClass()).debug(
					"Neues Passwort ist:\t" + neuesPasswort);
			// TODO --afreudli NAch debuggen Kommentar entfernen
			// autoNachricht.senden();

			// Am Benutzernamen ist was falsch
		} catch (BenutzerkontoException e) {
			fehler = true;
		}
		// Nutzer ist nicht in DB
		catch (NoSuchElementException e) {
			fehler = true;
		} catch (NachrichtException e) {
			fehler = true;
		}

		// Logging
		if (fehler) {
			Logger.getLogger(this.getClass()).info(
					"Neue Passwort fuer:\t" + benutzername
							+ " wurde NICHT versandt.");

		} else {
			Logger.getLogger(this.getClass()).info(
					"Neue Passwort fuer:\t" + benutzername
							+ "  WURDE versandt.");
		}
		// Es kommt immer OK Nachricht, da Sicherheitsrelevant
		request
				.setAttribute(
						DispatcherServlet.NACHRICHT_OK,
						"Sollte dieser Loginname im System vorhanden sein, so erhalten sie in Kürze eine Email mit einem neuen Passwort");
		request.getRequestDispatcher(Jsp.PASSWORT_VERGESSEN).forward(request,
				response);

	}

}