package business.project.noodles.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    FILE_UPLOAD_ERROR(105, "Upload file error", HttpStatus.BAD_REQUEST),
    DELETE_OBJECT_EXCEPTION(105, "Delete object exception", HttpStatus.BAD_REQUEST),
    UN_AUTHENTICATED(101, "Un Authenticated", HttpStatus.UNAUTHORIZED),
    UN_AUTHORIZED_TO_DELETE_USER(101, "Un authorized to delete user", HttpStatus.UNAUTHORIZED),
    INVALID_KEY(103, "Uncategorized error", HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(103, "Token Invalid or Expired", HttpStatus.UNAUTHORIZED),
    USER_EXISTED(104, "User existed !", HttpStatus.BAD_REQUEST),
    ROLE_EXISTED(104, "Role existed !", HttpStatus.BAD_REQUEST),
    ORDERS_ITEM_EXISTED(106, "Orders Item existed !", HttpStatus.BAD_REQUEST),
    ORDERS_EXISTED(104, "Orders existed !", HttpStatus.BAD_REQUEST),
    CATEGORY_EXISTED(104, "Category existed !", HttpStatus.BAD_REQUEST),
    PERMISSION_EXISTED(104, "Permission existed !", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(102, "User not found !", HttpStatus.NOT_FOUND),
    ORDERS_NOT_FOUND(102, "Orders not found !", HttpStatus.NOT_FOUND),
    ORDERS_ITEM_NOT_FOUND(102, "Orders item not found !", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(102, "Category not found !", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND(102, "Cart Items not found !", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(102, "Role not found !", HttpStatus.NOT_FOUND),
    PASSWORD_NOT_MATCHED(107, "Password not matched", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(103, "Password at least {min} characters", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(103, "Username at least {min} characters", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(101, "Uncategorized exception !", HttpStatus.INTERNAL_SERVER_ERROR),
    PERMISSION_NOT_FOUND(102, "Permission not found !", HttpStatus.NOT_FOUND);

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.http_status = httpStatus;
    }

    private int code;
    private String message;
    private HttpStatus http_status;

}
