package loyalty;

import atg.userdirectory.Role;
import atg.userdirectory.User;
import atg.userdirectory.UserDirectory;

public class LoyaltyValidation {

	public LoyaltyValidation() {}

	public static boolean validateInputNumber(Object obj) {

		@SuppressWarnings("unused")
		int amountValue;
		try {
			amountValue = (Integer) obj;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean validateUserRole(UserDirectory userDirectory, String profileId, String rolePath) {

		User profileDir = userDirectory.findUserByPrimaryKey(profileId);
		Role role = userDirectory.getRoleByPath(rolePath);

		return profileDir != null && profileDir.hasAssignedRole(role);
	}

}
