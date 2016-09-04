package org.rong.task.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import org.rong.task.util.TaskConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputTask implements Runnable {
	final static Logger logger = LoggerFactory.getLogger(OutputTask.class);
	private String fileName;
	public static boolean loop = true;

	public OutputTask(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void run() {

		while (OutputTask.loop) {
			try {
				sleep(TaskConstants.OUTPUT_INTERNAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LinkedList<String> list = WriterQueue.getQueue().takeAll();
			writeToDisk(list);
			list = null;
		}

		LinkedList<String> list = WriterQueue.getQueue().takeAll();
		writeToDisk(list);
		list = null;
	}

	private void writeToDisk(LinkedList<String> list) {

		if (list == null || list.size() == 0) {
			return;
		}
		File outputFile = new File(fileName);
		if (outputFile == null || !outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		FileOutputStream out = null;
		OutputStreamWriter writer = null;
		BufferedWriter bw = null;

		try {
			out = new FileOutputStream(outputFile, true);
			writer = new OutputStreamWriter(out);
			bw = new BufferedWriter(writer);

			for (String content : list) {
				bw.write(content);
				bw.newLine();
				bw.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (!OutputTask.loop) {
					writer.close();
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sleep(int millis) throws InterruptedException {
		Thread.sleep(millis);
	}
}