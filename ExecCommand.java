package myBatchProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

public class ExecCommand extends Command {
	private String path;
	protected List<String> command = new ArrayList<String>();
	private ArrayList<String> cmdArgs = new ArrayList<>();
	protected String inID;
	protected String outID;

	@Override
	public void parse(Element elem) throws ProcessException {
		id = elem.getAttribute("id");
		try {
			if (id == null || id.isEmpty()) {
				throw new ProcessException("Missing or invalid ID in CMD Command");
			}
			path = elem.getAttribute("path");
			if (path == null || path.isEmpty()) {
				throw new ProcessException("Missing or invalid PATH in CMD Command");
			} else {
				command.add(path);
			}
		} catch (ProcessException e) {
			throw e;
		}

		String arg = elem.getAttribute("args");
		if (!(arg == null || arg.isEmpty())) {
			StringTokenizer st = new StringTokenizer(arg);
			while (st.hasMoreTokens()) {
				String tok = st.nextToken();
				cmdArgs.add(tok);
			}
		}
		for (String argi : cmdArgs) {
			command.add(argi);
		}

		inID = elem.getAttribute("in");
		if (!(inID == null || inID.isEmpty())) {
			if (BatchParser.batch.getCommand(inID) == null)
				throw new Error("Invalid argument for getting file, error getting the " + inID + " in " + id);
		}
		outID = elem.getAttribute("out");
		if (!(outID == null || outID.isEmpty())) {
			if (BatchParser.batch.getCommand(outID) == null)
				throw new Error("Invalid argument for getting file, error getting the " + outID + " in " + id);
		}
	}

	@Override
	public void execute() throws Exception {
		System.out.println("Executing exec");
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(command);
		builder.redirectError(new File("error.txt"));
		String inputFile;
		String outputFile;
		Command outFileNameCommand = null;
		Command inFileNameCommand = null;
		if (inID != null)
			inFileNameCommand = BatchParser.batch.getCommand(inID);
		if (outID != null)
			outFileNameCommand = BatchParser.batch.getCommand(outID);
		if (inFileNameCommand instanceof FileNameCommand) {
			inputFile = ((FileNameCommand) inFileNameCommand).getPath();
			builder.redirectInput(new File(inputFile));
		}
		if (outFileNameCommand instanceof FileNameCommand) {
			outputFile = ((FileNameCommand) outFileNameCommand).getPath();
			builder.redirectOutput(new File(outputFile));
		}
		try {
			Process process = builder.start();
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			throw e;
		}

	}

}
