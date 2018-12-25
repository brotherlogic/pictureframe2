package com.github.brotherlogic.pictureframe;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

public class FrameDisplay extends JFrame {

	public FrameDisplay() {
		super();
		getContentPane().setBackground(Color.BLACK);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(800, 480);
	}
}
