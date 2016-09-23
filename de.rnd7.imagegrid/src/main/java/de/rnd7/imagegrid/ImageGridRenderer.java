package de.rnd7.imagegrid;

import java.util.List;
import java.util.Objects;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

class ImageGridRenderer {

	private static final int FONT_SIZE = 10;
	private final ImageGridLayout layout;
	private final ImageGrid grid;
	private final Font font;
	private final Color bgcolor;
	private final Color tagcolor;

	public ImageGridRenderer(final ImageGridLayout layout, final ImageGrid grid) {
		this.layout = layout;
		this.grid = grid;

		final FontDescriptor descriptor = JFaceResources.getDefaultFontDescriptor().setHeight(FONT_SIZE);
		this.font = descriptor.createFont(grid.getDisplay());
		this.bgcolor = new Color(grid.getDisplay(), 0xDD, 0xDD, 0xDD);
		this.tagcolor = new Color(grid.getDisplay(), 0xE8, 0xF4, 0xFF);
		grid.addDisposeListener(e -> {
			this.font.dispose();
			this.bgcolor.dispose();
			this.tagcolor.dispose();
		});
	}

	public void onPaint(final PaintEvent e) {
		final int columns = this.layout.getColumns();
		final int margin = this.layout.getMargin();

		int x = margin;
		int y = margin - this.grid.getVerticalBar().getSelection();

		final GC gc = e.gc;
		final List<ImageItem> items = this.grid.getItems();
		for (int i = 0; i < this.grid.getItems().size(); i++) {
			if (this.checkNewLine(columns, i)) {
				x = margin;
				y += this.layout.getItemHeight() + this.layout.getSpacer();
			}

			final ImageItem item = items.get(i);

			this.paint(gc, x, y, item);
			x += this.layout.getItemWidth() + this.layout.getSpacer();
		}
	}

	private boolean checkNewLine(final int columns, final int i) {
		return (i != 0) && ((i % columns) == 0);
	}

	public void paint(final GC gc, final int x, final int y, final ImageItem item) {
		final int textHeight = 15;

		gc.setBackground(this.bgcolor);
		gc.fillRectangle(x, y, this.layout.getItemWidth(), this.layout.getItemHeight() - textHeight);
		if (this.grid.isSelected(item)) {
			gc.setAlpha(255);
			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_LIST_SELECTION));
		} else {
			gc.setBackground(this.tagcolor);
		}

		gc.fillRectangle(x, y, 10, this.layout.getItemHeight() - textHeight);
		gc.setFont(this.font);
		gc.drawText(item.getName(), x, (y + this.layout.getItemHeight()) - textHeight, true);

		if (Objects.equals(item, this.grid.getFocusItem())) {
			final Rectangle frame = focusFrame(x, y, this.layout, textHeight);
			gc.drawFocus(frame.x, frame.y, frame.width, frame.height);
		}
	}

	private static Rectangle focusFrame(final int x, final int y, final ImageGridLayout layout, final int textHeight) {
		return new Rectangle(x - 1, y - 1, layout.getItemWidth() + 2, (layout.getItemHeight() - textHeight) + 2);
	}
}
