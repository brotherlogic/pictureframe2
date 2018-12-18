package com.github.brotherlogic.pictureframe;

import com.google.protobuf.InvalidProtocolBufferException;

public class Config {

	private proto.ConfigOuterClass.Config protoConfig;

	public Config(byte[] data) throws InvalidProtocolBufferException {
		protoConfig = proto.ConfigOuterClass.Config.parseFrom(data);
	}

	public Config() {
		// Set a default value of 10 recent images
		protoConfig = proto.ConfigOuterClass.Config.newBuilder().setRecentImages(10).build();
	}

	public byte[] dumpConfig() {
		return protoConfig.toByteArray();
	}

	public void setNumberOfPhotos(int val) {
		if (val > 0) {
			protoConfig = proto.ConfigOuterClass.Config.newBuilder(protoConfig).setRecentImages(val).build();
		}
	}

	public proto.ConfigOuterClass.Config getConfig() {
		return protoConfig;
	}
}
