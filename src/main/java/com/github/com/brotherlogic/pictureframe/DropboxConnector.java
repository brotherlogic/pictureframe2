package com.github.brotherlogic.pictureframe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.FileMetadata;


public class DropboxConnector {

	DbxClientV2 client;

	DbxRequestConfig config;

	public DropboxConnector(String token) {
		config = DbxRequestConfig.newBuilder("TuckerPictureFrame/1.0").build();
		client = new DbxClientV2(config, token);
	}


	public void syncFolder(String inputDirectory, File outputDirectory) throws Exception {
		ListFolderResult result = client.files().listFolder(inputDirectory);
		for (Metadata child : result.getEntries()) {
			File f = new File(outputDirectory.getAbsolutePath() + "/" + child.getPathDisplay());
			boolean needsRefresh = false;
					if (child instanceof FileMetadata) {
						if (f.exists() && f.length() != ((FileMetadata)child).getSize()) {
						    needsRefresh = true;
						}
					}
			if (!f.exists() || needsRefresh) {
				FileOutputStream outputStream = new FileOutputStream(
						outputDirectory.getAbsolutePath() + "/" + child.getPathDisplay());
				try {
					client.files().download(child.getPathDisplay()).download(outputStream);
				} catch (Exception e) {
				    //Pass
				}finally {
					outputStream.close();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		DropboxConnector connector = new DropboxConnector(args[0]);
		connector.syncFolder("", new File("testout"));
	}
}
