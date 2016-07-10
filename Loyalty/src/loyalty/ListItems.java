package loyalty;

import java.util.HashMap;
import java.util.Map;

import atg.adapter.gsa.EnumPropertyDescriptor;
import atg.adapter.gsa.GSAItemDescriptor;
import atg.adapter.gsa.GSAPropertyDescriptor;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Query;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryPropertyDescriptor;
import atg.repository.RepositoryView;

public class ListItems {

	public static Map<String, String[]> getLists(Repository repository, String itemDescriptorName,
			String displayedPropertyName, String paramValuePropertyName) throws RepositoryException {

		Map<String, String[]> map = new HashMap<String, String[]>();

		try {

			if (itemDescriptorName != null && displayedPropertyName != null && paramValuePropertyName != null) {

				RepositoryItemDescriptor rid = repository.getItemDescriptor(itemDescriptorName);
				RepositoryView rView = rid.getRepositoryView();
				QueryBuilder queryBuilder = rView.getQueryBuilder();
				Query queryAll = queryBuilder.createUnconstrainedQuery();
				RepositoryItem[] items = rView.executeQuery(queryAll);

				if (items != null) {

					String[] dislayedPropertyValues = new String[items.length];
					String[] paramPropertyValues = new String[items.length];
					for (int i = 0; i < items.length; i++) {
						dislayedPropertyValues[i] = (String) items[i].getPropertyValue(displayedPropertyName);
						paramPropertyValues[i] = (String) items[i].getPropertyValue(paramValuePropertyName);
					}
					map.put(displayedPropertyName, dislayedPropertyValues);
					map.put(paramValuePropertyName, paramPropertyValues);

					return map;

				} else {
					return null;
				}

			} else {
				throw new RepositoryException("input parameters cannot be null");
			}

		} catch (Exception e) {
			throw new RepositoryException("Exception in getListItems " + "\nCause: " + e.getMessage());
		}

	}

}
