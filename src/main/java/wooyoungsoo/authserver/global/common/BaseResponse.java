package wooyoungsoo.authserver.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse<T> {
    private static final String SUCCESS_STATUS = "success";
    private static final String FAIL_STATUS = "fail";
    private static final String ERROR_STATUS = "error";

    private String status;
    private T data;
    private String message;

    public static <T> BaseResponse<T> createSuccessResponse(T data) {
        return new BaseResponse<> (SUCCESS_STATUS, data, null);
    }

    public static BaseResponse<?> createSuccessResponseWithNoContent() {
        return new BaseResponse<> (SUCCESS_STATUS, null, null);
    }

    public static BaseResponse<?> createFailResponseWithNoContent(String message) {
        return new BaseResponse<> (FAIL_STATUS, null, message);
    }

    public static <T> BaseResponse<T> createErrorResponse(T data, String message) {
        return new BaseResponse<> (ERROR_STATUS, data, message);
    }

    public static BaseResponse<?> createErrorResponseWithNoContent(String message) {
        return new BaseResponse<> (ERROR_STATUS, null, message);
    }
}
