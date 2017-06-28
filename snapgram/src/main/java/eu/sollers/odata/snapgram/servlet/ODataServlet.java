package eu.sollers.odata.snapgram.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import eu.sollers.odata.snapgram.provider.ODataEdmProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * Entrance to OData.
 * Handler consumes request and populates the response using implemented processors.
 */
@Slf4j
public class ODataServlet extends HttpServlet {
    @Autowired
    private ODataEdmProvider provider;

    @Autowired
    private List<Processor> processors;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            OData odata = OData.newInstance();
            ServiceMetadata edm = odata.createServiceMetadata(provider, new ArrayList<>());
            ODataHttpHandler handler = odata.createHandler(edm);

            processors.forEach(handler::register);

            handler.process(req, resp);
        } catch (RuntimeException e) {
            log.error("Server Error occurred in ExampleServlet", e);
            throw new ServletException(e);
        }
    }
}
