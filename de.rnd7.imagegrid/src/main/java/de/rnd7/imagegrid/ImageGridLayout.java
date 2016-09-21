package de.rnd7.imagegrid;

import java.util.List;
import java.util.Optional;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

class ImageGridLayout {

	private final int itemWidth = 80;
	private final int itemHeight = 80;

	private final int spacer = 10;

	private final ImageGrid grid;

	ImageGridLayout(final ImageGrid grid) {
		this.grid = grid;
	}

	int getItemWidth() {
		return this.itemWidth;
	}

	int getItemHeight() {
		return this.itemHeight;
	}

	int getSpacer() {
		return this.spacer;
	}

	int getColumns() {
		final Rectangle clientArea = this.grid.getClientArea();
		return Math.max(1, clientArea.width / (this.itemWidth + this.spacer));
	}

	int getRows() {
		final int columns = this.getColumns();
		final int size = this.grid.getItems().size();

		int rows = size / columns;
		if ((size % columns) > 0) {
			rows++;
		}
		rows = Math.max(1, rows);
		return rows;
	}

	int getMargin() {
		return this.getSpacer() / 2;
	}

	int getTotalHeight() {
		return this.getRows() * (this.itemHeight + this.spacer);
	}

	Optional<ImageItem> elementAt(final int x, final int y) {
		final List<ImageItem> items = this.grid.getItems();

		final int margin = this.getMargin();
		return this.indexAt(x - margin, y - margin)
				.filter(index -> this.boundsOf(index).contains(new Point(x, y)))
				.map(items::get);
	}

	Rectangle boundsOf(final int index) {
		final int columns = this.getColumns();

		final int row = (index / columns);
		final int column = (index % columns);

		final int margin = this.getMargin();

		final int x = (column * (this.itemWidth + this.spacer)) + margin;
		final int y = (row * (this.itemHeight + this.spacer)) + margin;

		return new Rectangle(x, y, this.itemWidth, this.itemHeight);
	}

	private Optional<Integer> indexAt(final int x, final int y) {
		final int columns = this.getColumns();

		final int column = x / (this.itemWidth + this.spacer);
		final int row = (y / (this.itemHeight + this.spacer));

		final int index = ((row * columns) + column);
		final List<ImageItem> items = this.grid.getItems();
		if (index < items.size()) {
			return Optional.of(index);
		}
		else {
			return Optional.empty();
		}
	}
}
