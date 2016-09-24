package de.rnd7.imagegrid;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Control;

class ImageCache {

	private final Map<ImageItem, Image> map = new HashMap<>();
	private final Control control;

	ImageCache(final Control control) {
		this.control = control;
	}

	Image getImage(final ImageItem item, final int w, final int h) {
		Image image = this.map.get(item);

		if (image == null || image.isDisposed()) {
			final ImageDescriptor descriptor = item.getDescriptor();
			if (descriptor != null) {

				final ImageData imageData = this.scaleTo(descriptor.getImageData(), w, h);
				image = new Image(this.control.getDisplay(), imageData);
				this.map.put(item, image);
			}
		}

		return image;
	}

	private ImageData scaleTo(final ImageData imageData, final int w, final int h) {
		final int width = imageData.width;
		final int height = imageData.width;

		double scale = 0;
		boolean needsScale = false;

		if (width > w) {
			scale = (double) w / width;
			needsScale = true;
		}

		if (height > h) {
			scale = Math.min(scale, (double) h / height);
			needsScale = true;
		}

		if (needsScale) {
			return imageData.scaledTo((int) (width * scale), (int) (height * scale));
		} else {
			return imageData;
		}
	}

	void clear() {
		this.map.values().forEach(Image::dispose);
		this.map.clear();
	}
}
