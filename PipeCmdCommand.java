package myBatchProcessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PipeCmdCommand extends Command {

	Map<String, Command> subCommands = new LinkedHashMap<>();

	@Override
	public void parse(Element elem) throws ProcessException {
		id = elem.getAttribute("id");
		if (id == null || id.isEmpty()) {
			try {
				throw new ProcessException("Missing or invalid ID in pipe Command");
			} catch (ProcessException e) {
				throw e;
			}
		}
		NodeList elements = elem.getElementsByTagName("exec");
		if (elements.getLength() > 0) {
			for (int i = 0; i < elements.getLength(); i++) {
				Element element = (Element) elements.item(i);
				String cmdName = element.getNodeName();
				if ("exec".equalsIgnoreCase(cmdName) || "cmd".equalsIgnoreCase(cmdName)) {
					Command command = new ExecCommand();
					command.parse(element);
					subCommands.put(command.id, command);
				}
			}
		}
	}

	@Override
	public void execute() throws Exception {
		System.out.println("Executing pipe");
		int achar;
		InputStream inputStream1;
		OutputStream outputStream2;
		ArrayList<ExecCommand> commands = new ArrayList<>();
		for (Object value : subCommands.values()) {
			commands.add((ExecCommand) value);
		}
		ProcessBuilder processBuilder1 = new ProcessBuilder(commands.get(0).command);
		ProcessBuilder processBuilder2 = new ProcessBuilder(commands.get(1).command);

		processBuilder1.redirectError(new File("error.txt"));
		processBuilder2.redirectError(new File("error.txt"));

		Process process1 = null;
		Process process2 = null;

		FileNameCommand fileNameCommand1 = (FileNameCommand) BatchParser.batch.getCommand((commands.get(0)).inID);
		FileNameCommand fileNameCommand2 = (FileNameCommand) BatchParser.batch.getCommand((commands.get(1)).outID);

		String pathName1 = fileNameCommand1.getPath();
		String pathName2 = fileNameCommand2.getPath();

		processBuilder1.redirectInput(new File(pathName1));
		processBuilder2.redirectOutput(new File(pathName2));

		try {
			process1 = processBuilder1.start();
			inputStream1 = process1.getInputStream();
			process2 = processBuilder2.start();
			outputStream2 = process2.getOutputStream();

			while ((achar = inputStream1.read()) != -1) {
				outputStream2.write(achar);
			}
			inputStream1.close();
			outputStream2.close();
			process1.waitFor();
			process2.waitFor();
		} catch (IOException | InterruptedException e) {
			throw e;
		}
	}

}
