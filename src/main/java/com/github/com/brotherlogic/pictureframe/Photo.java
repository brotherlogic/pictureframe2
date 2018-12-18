package com.github.brotherlogic.pictureframe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;

public class Photo {
	private File f;

	public Photo(File f) {
		this.f = f;
	}

	public AffineTransform getTransform(int orientation, int width, int height) {
		AffineTransform t = new AffineTransform();

		switch (orientation) {
		case 1:
			break;
		case 2: // Flip X
			t.scale(-1.0, 1.0);
			t.translate(-width, 0);
			break;
		case 3: // PI rotation
			t.translate(width, height);
			t.rotate(Math.PI);
			break;
		case 4: // Flip Y
			t.scale(1.0, -1.0);
			t.translate(0, -height);
			break;
		case 5: // - PI/2 and Flip X
			t.rotate(-Math.PI / 2);
			t.scale(-1.0, 1.0);
			break;
		case 6: // -PI/2 and -width
			t.translate(height, 0);
			t.rotate(Math.PI / 2);
			break;
		case 7: // PI/2 and Flip
			t.scale(-1.0, 1.0);
			t.translate(-height, 0);
			t.translate(0, width);
			t.rotate(3 * Math.PI / 2);
			break;
		case 8: // PI / 2
			t.translate(0, width);
			t.rotate(3 * Math.PI / 2);
			break;
		}

		return t;
	}

	public static BufferedImage transformImage(BufferedImage image, AffineTransform transform) throws Exception {

		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);

		BufferedImage destinationImage = op.createCompatibleDestImage(image,
				(image.getType() == BufferedImage.TYPE_BYTE_GRAY) ? image.getColorModel() : null);
		Graphics2D g = destinationImage.createGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, destinationImage.getWidth(), destinationImage.getHeight());
		destinationImage = op.filter(image, destinationImage);
		return destinationImage;
	}

	public BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	public Image getImage() throws IOException {
		Image img = ImageIO.read(f);

		if (img == null) {
			return img;
		}

		int imgWidth = img.getWidth(null);
		int imgHeight = img.getHeight(null);

		double scaleFactor = (imgHeight + 0.0) / 480;

		int scaledHeight = (int) (imgHeight / scaleFactor);
		int scaledWidth = (int) (imgWidth / scaleFactor);

		Image resizedImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

		Image newImage = resizedImg;
		// Do we need to rotate the image?
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(f);
			Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			int orientation = 1;
			if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
				orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
				AffineTransform transform = getTransform(orientation, scaledWidth, scaledHeight);
				newImage = transformImage(toBufferedImage(newImage), transform);
			}
		} catch (MetadataException me) {
		    //Pass
		} catch (ImageProcessingException e) {
		    //Pass
		} catch (Exception e) {
		    //Pass
		}

		return newImage;
	}

	public String getName() {
		return f.getName();
	}
}
