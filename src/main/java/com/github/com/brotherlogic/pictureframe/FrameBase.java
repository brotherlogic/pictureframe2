package com.github.brotherlogic.pictureframe;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import com.github.brotherlogic.javaserver.JavaServer;

public abstract class FrameBase extends JavaServer {

	public abstract Config getConfig();

	LinkedList<File> rFiles;
	int rPointer = 0;
	int oldTime = 0;

	protected int compareFiles(File o1, File o2) {
		if (o1.lastModified() == o2.lastModified())
			return 0;
		else if (o1.lastModified() > o2.lastModified())
			return 1;
		else
			return -1;
	}

	protected Photo getLatestPhoto(String directory) {
		File[] files = new File(directory).listFiles();
		if (files != null && files.length > 0) {
			Arrays.sort(files, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return compareFiles(o1, o2);
				}
			});

			if (files.length > 0)
				return new Photo(files[files.length - 1]);
		}
		return null;
	}

	protected Photo getTimedLatestPhoto(String directory) {
		File[] files = new File(directory).listFiles();
		if (files != null && files.length > 0) {
			Map<String, File> lsMap = new TreeMap<String, File>();
			for (File f : files) {
				lsMap.put(f.getName().toLowerCase(), f);
			}
			files = lsMap.values().toArray(new File[0]);

			Arrays.sort(files, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return compareFiles(o1, o2);
				}
			});

			Calendar cal = Calendar.getInstance();
			int index = Math.abs(cal.get(Calendar.HOUR_OF_DAY) - 7)
					% Math.min(files.length, getConfig().getConfig().getRecentImages());

			if (files.length > index) {
				return new Photo(files[files.length - 1 - index]);
			}
		}
		return null;
	}

	protected Photo getRandomPhoto(String directory) {
		File[] files = new File(directory).listFiles();

		if (rFiles == null || files.length != rFiles.size()) {
			rPointer = 0;
			rFiles = new LinkedList<File>(Arrays.asList(files));
			Collections.shuffle(rFiles);
		}

		if (Math.abs(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) - 7) != oldTime) {
			rPointer = (rPointer + 1) % rFiles.size();
			oldTime = Math.abs(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) - 7);
			return new Photo(rFiles.get(rPointer));
		}

		return null;
	}

	protected String getGreeting() {
		return "Running";
	}

}
