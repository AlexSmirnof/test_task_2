package loyalty;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.repository.Repository;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class ListForEach extends DynamoServlet {

	@Override
	public void service(DynamoHttpServletRequest req, DynamoHttpServletResponse res)
			throws ServletException, IOException {

		String repositoryName = req.getParameter("repositoryName");
		String itemDescriptorName = req.getParameter("itemDescriptorName");
		String displayedPropertyName = req.getParameter("displayedPropertyName");
		String paramValuePropertyName = req.getParameter("paramValuePropertyName");

		Repository repository = (Repository) resolveName(repositoryName);

		try {

			Map<String, String[]> lists = ListItems.getLists(repository, itemDescriptorName, displayedPropertyName,
					paramValuePropertyName);

			if (lists != null) {

				String[] dislayedPropertyValues = lists.get(displayedPropertyName);
				String[] paramPropertyValues = lists.get(paramValuePropertyName);

				for (int i = 0; i < dislayedPropertyValues.length; i++) {
					req.setParameter("element", dislayedPropertyValues[i]);
					req.setParameter("value", paramPropertyValues[i]);
					req.serviceParameter("output", req, res);
				}

			} else {
				req.serviceParameter("empty", req, res);
			}

		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
			}
			req.serviceParameter("error", req, res);
		}

	}

	public ListForEach() {
	}

}
