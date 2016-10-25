package myBatchProcessor;

public class BatchProcessor {
	public static void main(String[] args) {
		String filename = null;
		Batch batch;
		if (args.length > 0) {
			filename = args[0];
		} else {
			filename = "batch4.xml";
		}
		BatchParser batchParser = new BatchParser();
		try {
			batchParser.parse(filename);
			batch = batchParser.getBatch();
			batch.executeBatch();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Finished Batch");
	}
}
