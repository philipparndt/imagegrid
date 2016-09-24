package de.rnd7.imagegrid;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Control;

class ImageCache {
	
	private Map<ImageItem, Image> map = new HashMap<>();
	private Control control;
	
	ImageCache(Control control) {
		this.control = control;
	}
	
	Image getImage(ImageItem item, int w, int h) {
		Image image = this.map.get(item);
		
		if (image == null || image.isDisposed()) {
			ImageDescriptor descriptor = item.getDescriptor();
			if (descriptor != null) {
				
				ImageData imageData = scaleTo(descriptor.getImageData(), w, h);
				image = new Image(control.getDisplay(), imageData);
				this.map.put(item, image);
			}
		}
		
		return image;
	}
	
	private ImageData scaleTo(ImageData imageData, int w, int h) {
		int width = imageData.width;
		int height = imageData.width;
		
		double scale = 0;
		boolean needsScale = false;
		
		if (width > w) {
			scale =  (double) w / width;
			needsScale = true;
		}
		
		if (height > h) {
			scale = Math.min(scale, (double) h / height);
			needsScale = true;
		}
		
		if (needsScale) {
			return imageData.scaledTo((int) ((double)width * scale), (int) ((double)height * scale));
		}
		else {
			return imageData;
		}
	}
	
	void clear() {
		this.map.values().forEach(Image::dispose);
		this.map.clear();
	}
}
