package it.polimi.tiw.progetti.controllers;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import it.polimi.tiw.progetti.beans.Appello;
import it.polimi.tiw.progetti.beans.Corso;
import it.polimi.tiw.progetti.beans.User;
import it.polimi.tiw.progetti.dao.StudenteDAO;
import it.polimi.tiw.progetti.utils.ConnectionHandler;

/**
 * Servlet implementation class DocenteHomePage
 */
@WebServlet("/StudenteHomePage")
public class StudenteHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public StudenteHomePage() {
		super();
	}

	public void init() throws ServletException {
		this.connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();

		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(servletContext);
		WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(webApplication);

		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		StudenteDAO studenteDAO = new StudenteDAO(connection, user.getId());

		JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(getServletContext());
		IWebExchange webExchange = application.buildExchange(request, response);
		WebContext ctx = new WebContext(webExchange, request.getLocale());
		ctx.setVariable("username", user.getUsername());

		// carico la lista dei corsi relativi ad un certo studente
		try {
			List<Corso> corsi = studenteDAO.cercaCorsi();
			ctx.setVariable("corso", corsi);
			String corsoIdParam = request.getParameter("corsoId");
			// carico la lista degli appelli relativi al corso selezionto
			if (corsoIdParam != null) {
				int corsoId = Integer.parseInt(corsoIdParam);
				List<Integer> studenti = studenteDAO.cercaIdStudentiPerCorso(corsoId);
				if (!studenti.contains(user.getId())) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Non sei iscritto a questo corso");
					return;
				}
				List<Appello> appelli = studenteDAO.cercaAppelliStudente(corsoId);
				ctx.setVariable("appelli", appelli);
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare gli appelli");
			return;
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Il parametro corsoId deve essere un intero valido");
			return;
		}
		templateEngine.process("/WEB-INF/studenteHomePage.html", ctx, response.getWriter());

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
