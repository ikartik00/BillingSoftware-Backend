package in.kr.main.exceptions;

public class ItemAlreadyExistsException extends RuntimeException {
	public ItemAlreadyExistsException(String message) {
		super(message);
	}
}
