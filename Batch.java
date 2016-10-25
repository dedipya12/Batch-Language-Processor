package myBatchProcessor;

import java.util.LinkedHashMap;
import java.util.Map;

public class Batch {
	Map<String, Command> commands;

	public Batch() {
		commands = new LinkedHashMap<>();
	}

	public void addCommand(Command command) {
		commands.put(command.id, command);
	}

	public Command getCommand(String id) {
		Command command = commands.get(id);
		if (command != null)
			return command;
		else
			return null;
	}

	public void executeBatch() throws Exception {
		for (Object value : commands.values()) {
			Command commandToExecute = (Command) value;
			commandToExecute.describe();
			commandToExecute.execute();
		}
	}
}
