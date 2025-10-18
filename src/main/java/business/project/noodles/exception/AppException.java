package business.project.noodles.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException{

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // ke thua tu lop runtimeexception voi message cua errorcode
        this.errorCode = errorCode;
    }

}
