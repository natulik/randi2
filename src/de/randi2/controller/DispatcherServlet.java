package de.randi2.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.*;

import de.randi2.model.fachklassen.beans.BenutzerkontoBean;
import de.randi2.model.fachklassen.*;
/**
 * <p>
 * Diese Klasse repraesentiert den DISPATCHER (== Weiterleiter). Dieser wird von
 * jeder Anfrage im Projekt angesprochen und leitet diese dann an die
 * entsprechenden Unterservlets bzw. direkt an JSPs weiter.
 * </p>
 * 
 * @version $Id$
 * @author Daniel Haehn <dhaehn@stud.hs-heilbronn.de>
 * @author Lukasz Plotnicki <lplotni@stud.hs-heilbronn.de>
 * 
 */
public class DispatcherServlet extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public DispatcherServlet() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO In dieser Methode soll unterschieden werden, was zu tun ist,
		// wenn eine GET-Anfrage an dieses Servlet geschickt wird.
		// Je nach Anfrage, wird eine Aktion durchgefuehrt.
		// GET-Requests duerfen nur verwendet werden, wenn keine Daten
		// uebermittelt werden.
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String id=(String)request.getParameter("anfrage_id");
		String idAttribute=(String)request.getAttribute("anfrage_id");
		if(idAttribute!=null)
		{
			id=idAttribute;
		}
		Logger.getLogger(this.getClass()).debug(id);
		if(id.equals("JSP_INDEX_LOGIN"))
		{
			request.setAttribute("anfrage_id", "CLASS_DISPATCHERSERVLET_LOGIN1");
			request.getRequestDispatcher("BenutzerServlet").forward(request, response);
		}
		else if(id.equals("CLASS_BENUTZERSERVLET_LOGIN_ERROR"))
		{
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		}
		else if (id.equals("CLASS_BENUTZERSERVLET_LOGIN_OK")){
			
			BenutzerkontoBean aBenutzer=(BenutzerkontoBean)request.getSession().getAttribute("aBenutzer");
			Logger.getLogger(this.getClass()).debug(aBenutzer);
			if(aBenutzer.getRolle().getRollenname()==Rolle.Rollen.STUDIENARZT){
				request.getRequestDispatcher("/studie_auswaehlen.jsp").forward(request, response);
			}
			else if(aBenutzer.getRolle().getRollenname()==Rolle.Rollen.STUDIENLEITER){
				request.getRequestDispatcher("/studie_auswaehlen.jsp").forward(request, response);
			}
			else if(aBenutzer.getRolle().getRollenname()==Rolle.Rollen.STATISTIKER){
				request.getRequestDispatcher("/studie_ansehen.jsp").forward(request, response);
			}
			else if(aBenutzer.getRolle().getRollenname()==Rolle.Rollen.ADMIN){
				request.getRequestDispatcher("/global_welcome.jsp").forward(request, response);
			}
			else if(aBenutzer.getRolle().getRollenname()==Rolle.Rollen.SYSOP){
				request.getRequestDispatcher("/global_welcome.jsp").forward(request, response);
			}//else if
		}//else if				
	}//doPost
}//DispatcherServlet