package org.ahv.passwordprotectorback.validator;

import java.util.List;

public class NameValidator {
    public static NameValidator nameValidator;

    private NameValidator() {
    }

    public static NameValidator getInstance() {
        if (nameValidator == null) {
            nameValidator = new NameValidator();
        }
        return nameValidator;
    }

    public boolean isNotValid(List<String> nameList, String oldName, String newName) {
        boolean isValid = true;

        if (oldName == null) {
            isValid = !nameList.contains(newName);
        } else {
            if (!oldName.equals(newName) && nameList.contains(newName)) {
                isValid = false;
            }
        }

        return !isValid;
    }
}
