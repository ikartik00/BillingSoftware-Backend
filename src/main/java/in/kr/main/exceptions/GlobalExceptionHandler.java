package in.kr.main.exceptions;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler({CategoryNotFoundException.class, ItemNotExistException.class})
	public ResponseEntity<Map<String, Object>> EmailAlreadyExists(Exception e){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("timeStamp", LocalDateTime.now());
		map.put("message", e.getMessage());
		return new ResponseEntity<Map<String,Object>>(map, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({EmailAlreadyExistsException.class, GstAlreadyExistsException.class, UserAlreadyExistsException.class, ItemAlreadyExistsException.class})
	public ResponseEntity<Map<String, Object>> AlreadyExists(Exception e){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("timeStamp", LocalDateTime.now());
		map.put("message", e.getMessage());
		return new ResponseEntity<Map<String,Object>>(map, HttpStatus.CONFLICT);
	}
}
