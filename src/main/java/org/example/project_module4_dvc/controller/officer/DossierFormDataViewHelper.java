package org.example.project_module4_dvc.controller.officer;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
@Component
public class DossierFormDataViewHelper {
    public static String getFormFragmentPath(BigInteger serviceId) {
        if (serviceId == null) return "components/forms/default-form :: form-detail";

        if (serviceId.equals(BigInteger.valueOf(1))) { // Mã thủ tục Khai sinh
            return "components/form/formData/birth-registration :: form-detail";
        } else if (serviceId.equals(BigInteger.valueOf(2))) { // Mã thủ tục Kết hôn
            return "components/form/formData/marriage-registration :: form-detail";
        } else if (serviceId.equals(BigInteger.valueOf(3))) {
            return "components/form/formData/death-registration :: form-detail";
        } else if (serviceId.equals(BigInteger.valueOf(4))) {
            return "components/form/formData/marital-status-certificate :: form-detail";
        } else if (serviceId.equals(BigInteger.valueOf(5))) {
            return "components/form/formData/land-registration :: form-detail";
        } else if (serviceId.equals(BigInteger.valueOf(6))) {
            return "components/form/formData/land-purpose-change :: form-detail";
        } else if (serviceId.equals(BigInteger.valueOf(7))) {
            return "components/form/formData/land-split-merge :: form-detail";
        } else if (serviceId.equals(BigInteger.valueOf(8))) {
            return "components/form/formData/household-business-registration :: form-detail";
        }
        return "components/forms/default-form :: form-detail";
    }
}
