package in.kr.main.exceptions;

public class CategoryAlreadyExistsException extends RuntimeException {
	public CategoryAlreadyExistsException(String message) {
		super(message);
	}
}
