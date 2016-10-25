package myBatchProcessor;

import org.w3c.dom.Element;

public class FileNameCommand extends Command {

	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public void parse(Element elem) throws ProcessException {
		id = elem.getAttribute("id");
		path = null;
		try {
			if (id == null || id.isEmpty()) {
				throw new ProcessException("Missing ID in CMD Command");
			}
			path = elem.getAttribute("path");
			if (path == null || path.isEmpty()) {
				throw new ProcessException("Missing PATH in CMD Command");
			}
		} catch (ProcessException e) {
			throw e;
		}
	}

	@Override
	public void execute() {
		System.out.println("Executing file");
		System.out.println("Found file" + "  " + getPath());
	}

}
