package com.bookmyvenue.backend.user.service.interfaces;

import com.bookmyvenue.backend.auth.dto.request.RegisterRequest;
import com.bookmyvenue.backend.common.dto.SuccessResponse;
import com.bookmyvenue.backend.user.enums.Role;

public interface RegistrationServiceInterface {
    SuccessResponse registerUser(RegisterRequest registerRequest, Role role);
}
