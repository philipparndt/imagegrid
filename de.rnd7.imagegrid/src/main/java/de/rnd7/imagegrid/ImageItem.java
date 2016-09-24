package de.rnd7.imagegrid;

import org.eclipse.jface.resource.ImageDescriptor;

public class ImageItem {
	private String name;
	private ImageDescriptor descriptor;
	private Object data;

	public ImageDescriptor getDescriptor() {
		return this.descriptor;
	}

	public ImageItem setDescriptor(final ImageDescriptor descriptor) {
		this.descriptor = descriptor;

		return this;
	}

	public String getName() {
		return this.name;
	}

	public ImageItem setName(final String name) {
		this.name = name;

		return this;
	}

	public Object getData() {
		return this.data;
	}

	public void setData(final Object data) {
		this.data = data;
	}
}
