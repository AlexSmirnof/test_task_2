package loyalty;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.droplet.DropletException;
import atg.repository.RepositoryException;
import atg.repository.servlet.RepositoryFormHandler;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userdirectory.Role;
import atg.userdirectory.User;
import atg.userdirectory.UserDirectory;

public class LoyaltyFormHandler extends RepositoryFormHandler {

	private static final String PROFILE_ID_PARAM_PATH = "/loyalty/LoyaltyFormHandler.profileId";

	private LoyaltyManager loyaltyManager;
	private UserDirectory userDirectory;
	private String profileId;

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public UserDirectory getUserDirectory() {
		return userDirectory;
	}

	public void setUserDirectory(UserDirectory userDirectory) {
		this.userDirectory = userDirectory;
	}

	public LoyaltyManager getLoyaltyManager() {
		return loyaltyManager;
	}

	public void setLoyaltyManager(LoyaltyManager loyaltyManager) {
		this.loyaltyManager = loyaltyManager;
	}

	@Override
	protected void preCreateItem(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {

		if (isLoggingDebug()) {
			logDebug("preCreateItem method called");
		}

		UserDirectory userDirectory = getUserDirectory();
		String profileId = getProfileId();
		String rolePath = getLoyaltyManager().getRolePath();

		if (!LoyaltyValidation.validateUserRole(userDirectory, profileId, rolePath)) {

			if (isLoggingError()) {
				logError("Profile has no rights");
			}
			this.addFormException(new DropletException("Your has no rights for this operation"));

		}

		else if (!LoyaltyValidation.validateInputNumber(this.getValueProperty("amount"))) {

			if (isLoggingError()) {
				logError("Validation error: input amount not a number");
			}
			this.addFormException(new DropletException("Validation error: input amount not a number"));

		}

	}

	@Override
	protected void postCreateItem(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws javax.servlet.ServletException, java.io.IOException {

		if (!this.getFormError()) {

			if (isLoggingDebug()) {
				logDebug("postCreateItem method called, item created: " + getRepositoryItem());
			}

			LoyaltyManager loyaltyManager = getLoyaltyManager();

			try {

				loyaltyManager.addLoyaltyPointsToUser(this.getRepositoryId());

			} catch (Exception e) {
				if (isLoggingError()) {
					logError("Cannot add loyalty points to user", e);
				}
				this.addFormException(
						new DropletException("Cannot add loyalty points to user" + "\nCause: " + e.getMessage()));
			}
		}
	}
}