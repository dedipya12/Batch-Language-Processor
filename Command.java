package myBatchProcessor;

import org.w3c.dom.Element;

public abstract class Command {
	String id;

	public abstract void parse(Element elem) throws ProcessException;

	public abstract void execute() throws Exception;

	public void describe() {
		System.out.println("Executing the following command : ");
	}
}
