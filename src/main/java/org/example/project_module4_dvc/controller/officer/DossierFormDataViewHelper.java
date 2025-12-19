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
        } else if (serviceId.equals(BigInteger.valueOf(3))) { // Mã thủ tục Đất đai - Đăng ký thay đổi
            return "components/form/formData/land-change-registration :: form-detail";
        } else if (serviceId.equals(BigInteger.valueOf(4))) { // Mã thủ tục Cư trú
            return "components/form/formData/residence-registration :: form-detail";
        } else if (serviceId.equals(BigInteger.valueOf(5))) { // Mã thủ tục Đất đai - Thay đổi mục đích
            return "components/form/formData/land-purpose-change :: form-detail";
        } else if (serviceId.equals(BigInteger.valueOf(6))) { // Mã thủ tục Đất đai - Tách/Hợp nhất
            return "components/form/formData/land-split-merge :: form-detail";
        } else if (serviceId.equals(BigInteger.valueOf(7))) { // Mã thủ tục Tử vong
            return "components/form/formData/death-registration :: form-detail";
        } else if (serviceId.equals(BigInteger.valueOf(8))) { // Mã thủ tục Kinh doanh hộ gia đình
            return "components/form/formData/household-business-registration :: form-detail";
        } else if (serviceId.equals(BigInteger.valueOf(9))) { // Mã thủ tục Giấy chứng nhận tình trạng hôn nhân
            return "components/form/formData/marital-status-certificate :: form-detail";
        }
        return "components/forms/default-form :: form-detail";
    }
}
