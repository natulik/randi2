/**
 * @todo - korrekte Eingaben der regul�ren Ausdr�cke nach wie vor fehlerhaft
 *       - assertionError bei allen Methoden mit aktivem Filter
 *       - char Nullkonstante f�r gender definieren
 */

package de.randi2.junittests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.randi2.model.exceptions.PersonException;
import de.randi2.model.fachklassen.beans.PersonBean;

/**
 * Die JUnit Klasse PersonBeanTest testet alle SetterMethoden der Klasse
 * PersonBean. Auf ein Testen der getter-Methoden kann verzichtet werden, da
 * dortige Fehler auf Kompilierebene liegen m�ssten.
 * 
 * @author Tino Noack <tnoack@stud.hs-heilbronn.de>
 * @version $Id$
 */
public class PersonBeanTest {

	private PersonBean testPB; // Objekt der Klasse PersonBean

	/**
	 * Erzeugt eine neue Instanz der Klasse PersonBean.
	 */
	@Before
	public void setUp() throws Exception {

		testPB = new PersonBean();
	}

	/**
	 * Weist dem PersonBean-Objekt den Wert "null" zu.
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {

		testPB = null;
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Emailparameter null ist (da Pflichtfeld). Filter deaktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetEmailNullF() {
		try {
			testPB.setFilter(false);
			testPB.setEmail(null);
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.EMAIL_FEHLT, e.getMessage());
		}
	}
	
	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Emailparameter null ist (da Pflichtfeld). Filter aktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetEmailNullT() {
		try {
			testPB.setFilter(true);
			testPB.setEmail(null);
			
		} catch (Exception e) {
			fail("Darf eigentlich keine Exception schmeissen.");
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Emailparameter ein leerer String ist. Filter deaktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetEmailLeerF() {

		try {
			testPB.setFilter(false);
			testPB.setEmail("");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.EMAIL_FEHLT, e.getMessage());
		}
	}
	
	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Emailparameter ein leerer String ist. Filter aktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetEmailLeerT() {

		try {
			testPB.setFilter(true);
			testPB.setEmail("");
			
		} catch (Exception e) {
			fail("Darf eigentlich keine Exception schmeissen.");
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Emailparameter laenger als in der Definition vereinbart ist. Filter deaktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetEmailUeberlangF() {
		try {
			testPB.setFilter(false);
			testPB
					.setEmail("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa."
							+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
							+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@aaaaaaaaaaaaaaaa"
							+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
							+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
							+ "aaaaaaaaaaaaaaaaaaaa");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.EMAIL_UNGUELTIG, e.getMessage());
		}

	}
	
	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Emailparameter laenger als in der Definition vereinbart ist. Filter aktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetEmailUeberlangT() {
		try {
			testPB.setFilter(true);
			testPB
					.setEmail("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa."
							+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
							+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@aaaaaaaaaaaaaaaa"
							+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
							+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
							+ "aaaaaaaaaaaaaaaaaaaa");
			
		} catch (Exception e) {
			fail("Darf eigentlich keine Exception schmeissen.");
		}

	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Emailparameter nicht per Definition zugelassene Zeichen enthaelt.
	 */
	@Test(expected = PersonException.class)
	public void testSetEmailSonderzeichen() {
		try {
			testPB.setEmail("���.�@&$.---");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.EMAIL_UNGUELTIG, e.getMessage());
		}

	}

	/**
	 * Testet, ob eine Exception geworfen wird, obwohl der Emailparameter
	 * korrekt per Definition ist.
	 */
	@Test
	public void testSetEmailKorrekt() {

		try {
			testPB.setEmail("blub@bla-blub.net");
			testPB.setEmail("a.b@d.de");

		} catch (Exception e) {
			fail("Exception geworfen bei korrekter Emailadresse");
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Faxparameter ein leerer String ist.
	 */
	@Test(expected = PersonException.class)
	public void testSetFaxLeer() {
		try {
			testPB.setFax("");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.FAX_UNGUELTIG, e.getMessage());
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Faxparameter mehr oder weniger Zeichen enthaelt als per Definition
	 * vereinbart.
	 */
	@Test(expected = PersonException.class)
	public void testSetFaxLaenge() {

		try {
			testPB.setFax("1/1");
			testPB.setFax("1/1234");
			testPB.setFax("1/1234567890123456");
			testPB.setFax("123/1");
			testPB.setFax("123/1234567890123456");
			testPB.setFax("12345678901/1");
			testPB.setFax("12345678901/1234");
			testPB.setFax("12345678901/1234567890123456");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.FAX_UNGUELTIG, e.getMessage());
		}

	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Faxparameter nicht per Definition zugelassene Zeichen enthaelt.
	 */
	@Test(expected = PersonException.class)
	public void testSetFaxSonderzeichen() {
		try {
			testPB.setFax("we/ergfe");
			testPB.setFax("�$�%.)(9");
			testPB.setFax("0908/56477-");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.FAX_UNGUELTIG, e.getMessage());
		}

	}

	/**
	 * Testet, ob eine Exception geworfen wird, obwohl der Faxparameter korrekt
	 * per Definition ist.
	 */
	@Test
	public void testSetFaxKorrekt() {
		try {
			testPB.setFax(null);
			testPB.setFax("012/123456");
			testPB.setFax("23/35682345678");

		} catch (Exception e) {
			fail("Exception geworfen bei korrekter Faxnummer.");
		}
	}

//	/**
//	 * Testet, ob eine PersonException geworfen wird, wenn der
//	 * Geschlechtsparameter nicht per Definition zugelassen ist.
//	 */
//	@Test(expected = PersonException.class)
//	public void testSetGeschlechtFehltF() {
//
//		try {
//			testPB.setFilter(false);
//			testPB.setGeschlecht(' ');
//			fail();
//		} catch (Exception e) {
//			assertTrue(e instanceof PersonException);
//			assertEquals(PersonException.GESCHLECHT_FEHLT, e.getMessage());
//		}
//	}
//	
//	/**
//	 * Testet, ob eine PersonException geworfen wird, wenn der
//	 * Geschlechtsparameter nicht per Definition zugelassen ist.
//	 */
//	@Test(expected = PersonException.class)
//	public void testSetGeschlechtFehltT() {
//
//		try {
//			testPB.setFilter(true);
//			testPB.setGeschlecht(' '); //hier richtigen char einsetzen!
//			
//		} catch (Exception e) {
//			fail("Darf eigentlich keine Exception schmei�en.");
//		}
//	}
	
	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Geschlechtsparameter nicht per Definition zugelassen ist.
	 */
	@Test(expected = PersonException.class)
	public void testSetGeschlechtFalsch() {

		try {
			testPB.setGeschlecht(' ');
			testPB.setGeschlecht('0');
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.GESCHLECHT_UNGUELTIG, e.getMessage());
		}
	}

	/**
	 * Testet, ob eine Exception geworfen wird, obwohl der Geschlechtsparameter
	 * korrekt per Definition ist.
	 */
	@Test
	public void testSetGeschlechtKorrekt() {

		try {
			testPB.setGeschlecht('w');
			testPB.setGeschlecht('m');

		} catch (Exception e) {
			fail("Exception geworfen bei korrekter Geschlechtseingabe.");
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Handynummerparameter ein leerer String ist.
	 */
	@Test(expected = PersonException.class)
	public void testSetHandynummerLeer() {
		try {
			testPB.setHandynummer("");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.HANDY_UNGUELTIG, e.getMessage());
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Handynummerparameter laenger oder kuerzer als per Definition vereinbart
	 * ist.
	 */
	@Test(expected = PersonException.class)
	public void testSetHandynummerLaenge() {
		try {
			testPB.setHandynummer("1/1");
			testPB.setHandynummer("1/1234");
			testPB.setHandynummer("1/1234567890123456");
			testPB.setHandynummer("123/1");
			testPB.setHandynummer("123/1234567890123456");
			testPB.setHandynummer("12345678901/1");
			testPB.setHandynummer("12345678901/1234");
			testPB.setHandynummer("12345678901/1234567890123456");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.HANDY_UNGUELTIG, e.getMessage());
		}

	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Handynummerparameter nicht zugelassene Sonderzeichen enthealt.
	 */
	@Test(expected = PersonException.class)
	public void testSetHandynummerSonderzeichen() {
		try {
			testPB.setHandynummer("we/ergfe");
			testPB.setHandynummer("�$�%.)(9");
			testPB.setHandynummer("0908/56477-");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.HANDY_UNGUELTIG, e.getMessage());
		}
	}

	/**
	 * Testet, ob eine Exception geworfen wird, obwohl der Handynummerparameter
	 * korrekt per Definition ist.
	 */
	@Test
	public void testSetHandynummerKorrekt() {
		try {
			testPB.setHandynummer(null);
			testPB.setHandynummer("012/123456");
			testPB.setHandynummer("23678/35682345678");

		} catch (Exception e) {
			fail("Exception geworfen bei korrekter Faxnummer.");
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Nachnameparameter null ist (da Pflichtfeld). Filter deaktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetNachnameNullF() {
		try {
			testPB.setFilter(false);
			testPB.setNachname(null);
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.NACHNAME_FEHLT, e.getMessage());
			fail();
		}
	}
	
	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Nachnameparameter null ist (da Pflichtfeld). Filter aktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetNachnameNullT() {
		try {
			testPB.setFilter(true);
			testPB.setNachname(null);
		} catch (Exception e) {
			fail("Sollte keine Exception schmeissen.");
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Nachnameparameter ein leerer String ist. Filter deaktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetNachnameLeerF() {
		try {
			testPB.setFilter(false);
			testPB.setNachname("");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.NACHNAME_FEHLT, e.getMessage());
		}
	}
	
	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Nachnameparameter ein leerer String ist. Filter aktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetNachnameLeerT() {
		try {
			testPB.setFilter(true);
			testPB.setNachname("");
			
		} catch (Exception e) {
			fail("Sollte keine Exception schmeissen.");
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Nachnameparameter laenger oder kuerzer als per Definition vereinbart ist. Filter deaktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetNachnameLaengeF() {
		try {
			testPB.setFilter(false);
			testPB.setNachname("X");
			testPB.setNachname("abcdefghijklmopqrstuvwxyzabcdefghijk"+
						"lmopqrstuvwxyzabcdefghijklmopqrstuvwxyz");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.NACHNAME_UNGUELTIG, e.getMessage());
		}

	}
	
	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Nachnameparameter laenger oder kuerzer als per Definition vereinbart ist. Filter aktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetNachnameLaengeT() {
		try {
			testPB.setFilter(true);
			testPB.setNachname("X");
			testPB.setNachname("abcdefghijklmopqrstuvwxyzabcdefghijk"+
						"lmopqrstuvwxyzabcdefghijklmopqrstuvwxyz");
			
		} catch (Exception e) {
			fail("Sollte keine Exception schmeissen.");
		}

	}

	/**
	 * Testet, ob eine Exception geworfen wird, obwohl der Nachnameparameter
	 * korrekt per Definition ist.
	 */
	@Test
	public void testSetNachnameKorrekt() {

		try {
			testPB.setNachname("Jameson");
			testPB.setNachname("Engelen-Kefer");
			testPB.setNachname("Anding Rost");
		} catch (Exception e) {
			fail("Exception aufgetreten trotz richtiger Parameter.");
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Telefonnummerparameter null ist (da Pflichtfeld). Filter deaktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetTelefonnummerNullF() {
		try {
			testPB.setFilter(false);
			testPB.setTelefonnummer(null);
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.TELEFONNUMMER_FEHLT, e.getMessage());
		}
	}
	
	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Telefonnummerparameter null ist (da Pflichtfeld). Filter aktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetTelefonnummerNullT() {
		try {
			testPB.setFilter(true);
			testPB.setTelefonnummer(null);
			} catch (Exception e) {
			fail("Sollte keine Exception schmeissen.");
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Telefonnummerparameter null ist (da Pflichtfeld). Filter deaktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetTelefonnummerLeerF() {
		try {
			testPB.setFilter(false);
			testPB.setTelefonnummer(null);
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.TELEFONNUMMER_FEHLT, e.getMessage());
		}
	}
	
	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Telefonnummerparameter null ist (da Pflichtfeld). Filter aktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetTelefonnummerLeerT() {
		try {
			testPB.setFilter(true);
			testPB.setTelefonnummer(null);
			} catch (Exception e) {
			fail("Sollte keine Exception schmeissen.");
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Telefonnummerparameter laenger oder kuerzer als per Definition vereinbart
	 * ist. Filter deaktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetTelefonnummerLaengeF() {
		try {
			testPB.setFilter(false);
			testPB.setTelefonnummer("1/1");
			testPB.setTelefonnummer("1/1234");
			testPB.setTelefonnummer("1/1234567890123456");
			testPB.setTelefonnummer("123/1");
			testPB.setTelefonnummer("123/1234567890123456");
			testPB.setTelefonnummer("12345678901/1");
			testPB.setTelefonnummer("12345678901/1234");
			testPB.setTelefonnummer("12345678901/1234567890123456");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.TELEFONNUMMER_UNGUELTIG, e.getMessage());
		}

	}
	
	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Telefonnummerparameter laenger oder kuerzer als per Definition vereinbart
	 * ist. Filter aktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetTelefonnummerLaengeT() {
		try {
			testPB.setFilter(true);
			testPB.setTelefonnummer("1/1");
			testPB.setTelefonnummer("1/1234");
			testPB.setTelefonnummer("1/1234567890123456");
			testPB.setTelefonnummer("123/1");
			testPB.setTelefonnummer("123/1234567890123456");
			testPB.setTelefonnummer("12345678901/1");
			testPB.setTelefonnummer("12345678901/1234");
			testPB.setTelefonnummer("12345678901/1234567890123456");
			
		} catch (Exception e) {
			fail("Sollte keine Exception schmeissen.");
		}

	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Telefonnummerparameter nicht zugelassene Sonderzeichen enthealt.
	 */
	@Test(expected = PersonException.class)
	public void testSetTelefonnummerSonderzeichen() {
		try {
			testPB.setTelefonnummer("we/ergfe");
			testPB.setTelefonnummer("�$�%.)(9");
			testPB.setTelefonnummer("0908/56477-");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.TELEFONNUMMER_UNGUELTIG, e.getMessage());
		}

	}

	/**
	 * Testet, ob eine Exception geworfen wird, obwohl der
	 * Telefonnummerparameter korrekt per Definition ist.
	 */
	@Test
	public void testSetTelefonnummerKorrekt() {
		try {
			testPB.setTelefonnummer("012/123456");
			testPB.setTelefonnummer("23/35682345678");

		} catch (Exception e) {
			fail("Exception geworfen bei korrekter Faxnummer.");
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Titelparameter nicht der Definition entspricht. Filter deaktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetTitelFalschF() {

		try {
			testPB.setFilter(false);
			testPB.setTitel("");
			testPB.setTitel("123");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.TITEL_UNGUELTIG, e.getMessage());
		}

	}
	
	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Titelparameter nicht der Definition entspricht. Filter aktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetTitelFalschT() {

		try {
			testPB.setFilter(true);
			testPB.setTitel("");
			testPB.setTitel("123");
			
		} catch (Exception e) {
			fail("Sollte keine Exception schmeissen.");
		}

	}

	/**
	 * Testet, ob eine Exception geworfen wird, obwohl der Titelparameter
	 * korrekt per Definition ist.
	 */
	@Test
	public void testSetTitelKorrekt() {

		try {
			testPB.setTitel(null);
			testPB.setTitel("Prof.");
			testPB.setTitel("Dr.");
			testPB.setTitel("Prof. Dr.");
		} catch (Exception e) {
			fail("Exception aufgetreten trotz richtiger Parameter.");
		}
	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Vornameparameter null ist (da Pflichtfeld). Filter deaktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetVornameNullF() {
		try {
			testPB.setFilter(false);
			testPB.setVorname(null);
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.VORNAME_FEHLT, e.getMessage());
		}

	}
	
	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Vornameparameter null ist (da Pflichtfeld). Filter aktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetVornameNullT() {
		try {
			testPB.setFilter(true);
			testPB.setVorname(null);
			
		} catch (Exception e) {
			fail("Sollte keine Exception schmeissen.");
		}

	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Vornameparameter ein leerer String ist. Filter deaktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetVornameLeerF() {
		try {
			testPB.setFilter(false);
			testPB.setVorname("");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.VORNAME_FEHLT, e.getMessage());
		}

	}
	
	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Vornameparameter ein leerer String ist. Filter aktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetVornameLeerT() {
		try {
			testPB.setFilter(true);
			testPB.setVorname("");
			
		} catch (Exception e) {
			fail("Sollte keine Exception schmeissen.");
		}

	}

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Vornameparameter laenger oder kuerzer als per Definition vereinbart ist. Filter deaktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetVornameLaengeF() {
		try {
			testPB.setFilter(false);
			testPB.setVorname("X");
			testPB.setVorname("abcdefghijklmopqrstuvwxyzabcdefghijklmopqrst"+
					"uvwxyzabcdefghijklmopqrstuvwxyz");
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof PersonException);
			assertEquals(PersonException.VORNAME_UNGUELTIG, e.getMessage());
		}

	}
	

	/**
	 * Testet, ob eine PersonException geworfen wird, wenn der
	 * Vornameparameter laenger oder kuerzer als per Definition vereinbart ist. Filter aktiviert.
	 */
	@Test(expected = PersonException.class)
	public void testSetVornameLaengeT() {
		try {
			testPB.setFilter(true);
			testPB.setVorname("X");
			testPB.setVorname("abcdefghijklmopqrstuvwxyzabcdefghijklmopqrst"+
					"uvwxyzabcdefghijklmopqrstuvwxyz");
			
		} catch (Exception e) {
			fail("Sollte keine Exception schmeissen.");
		}

	}

	/**
	 * Testet, ob eine Exception geworfen wird, obwohl der Vornameparameter
	 * korrekt per Definition ist.
	 */
	@Test
	public void testSetVornameKorrekt() {

		try {
			testPB.setVorname("Markus");
			testPB.setVorname("Jan Josef");
			testPB.setVorname("Elisabeth-Sophie");
		} catch (Exception e) {
			fail("Exception aufgetreten trotz richtiger Parameter.");
		}
	}

}
