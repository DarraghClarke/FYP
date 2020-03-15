package service.orchestrator;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//TODO rename to no service exception
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "quotation does not exist")
public class NoSuchNodeException extends Throwable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;


}
