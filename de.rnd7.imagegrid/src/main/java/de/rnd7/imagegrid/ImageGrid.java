package de.rnd7.imagegrid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

public class ImageGrid extends Canvas {

	private final List<ImageItem> items = new ArrayList<>();

	private final ImageGridLayout layout = new ImageGridLayout(this);

	private final HashSet<ImageItem> selection = new HashSet<>();
	private ImageItem focusItem = null;

	private final boolean singleSelection = false;

	public ImageGrid(final Composite parent, final int style) {
		super(parent, style | SWT.V_SCROLL | SWT.DOUBLE_BUFFERED);

		this.addPaintListener(new ImageGridRenderer(layout, this)::onPaint);
		this.getVerticalBar().setVisible(false);
		this.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent event) {
				ImageGrid.this.updateScrollBar();
			}
		});

		this.getVerticalBar().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				ImageGrid.this.redraw();
			}
		});

		this.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseExit(final MouseEvent event) {
				ImageGrid.this.setCursor(null);
			}
		});

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent event) {
				ImageGrid.this.onMouseDown(event);
			}
		});

		this.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(final MouseEvent event) {
				final int x = event.x;
				final int y = event.y + ImageGrid.this.getVerticalBar().getSelection();

				if (ImageGrid.this.layout.elementAt(x, y).isPresent()) {
					ImageGrid.this.setCursor(ImageGrid.this.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
				}
				else {
					ImageGrid.this.setCursor(null);
				}
			}
		});

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent event) {
				switch (event.keyCode) {
				case SWT.ARROW_UP:
					ImageGrid.this.up(event.stateMask);
					break;
				case SWT.ARROW_DOWN:
					ImageGrid.this.down(event.stateMask);
					break;
				case SWT.ARROW_LEFT:
					ImageGrid.this.left(event.stateMask);
					break;
				case SWT.ARROW_RIGHT:
					ImageGrid.this.right(event.stateMask);
					break;
				case SWT.SPACE:
					if (ImageGrid.this.isToggleSelectionModifier(event.stateMask)) {
						ImageGrid.this.toggleSelection(ImageGrid.this.focusItem);
						ImageGrid.this.redraw();
					}
					break;
				}
			}
		});
	}

	private void onMouseDown(final MouseEvent event) {
		final int x = event.x;
		final int y = event.y + this.getVerticalBar().getSelection();

		if (this.singleSelection || !this.isToggleSelectionModifier(event.stateMask)) {
			this.selection.clear();
		}
		this.focusItem = null;

		this.layout.elementAt(x, y).ifPresent(item -> {
			if (this.isToggleSelectionModifier(event.stateMask)) {
				this.toggleSelection(item);
			}
			else {
				this.select(item);
			}

			this.focusItem = item;
			this.show(this.focusItem);
		});

		this.redraw();
	}

	private boolean isToggleSelectionModifier(final int stateMask) {
		return checkState(stateMask, SWT.CTRL);
	}

	private void toggleSelection(final ImageItem item) {
		final Consumer<ImageItem> consumer = this.selection.contains(item) ? this.selection::remove : this.selection::add;
		consumer.accept(item);
	}

	private void select(final ImageItem item) {
		this.selection.add(item);
	}

	private static boolean checkState(final int state, final int flag) {
		return ((state & flag) == flag);
	}

	private void up(final int stateMask) {
		this.navigate(-this.layout.getColumns(), stateMask);
	}

	private void down(final int stateMask) {
		this.navigate(this.layout.getColumns(), stateMask);
	}

	private void left(final int stateMask) {
		this.navigate(-1, stateMask);
	}

	private void right(final int stateMask) {
		this.navigate(1, stateMask);
	}

	private void navigate(final int offset, final int stateMask) {
		final int index = this.items.indexOf(this.focusItem) + offset;
		if ((index >= 0) && (index < this.items.size())) {
			final ImageItem item = this.items.get(index);

			if (!this.isToggleSelectionModifier(stateMask)) {
				this.selection.clear();
				this.selection.add(item);
			}

			this.focusItem = item;
			this.show(this.focusItem);

			this.redraw();
		}
	}

	public void addItem(final ImageItem item) {
		this.items.add(item);
	}

	public void clearItems() {
		this.items.clear();
	}

	private void updateScrollBar() {
		final int totalHeight = this.layout.getTotalHeight();
		final Rectangle clientArea = this.getClientArea();

		final ScrollBar bar = this.getVerticalBar();
		bar.setPageIncrement(totalHeight);
		bar.setIncrement(16);
		bar.setMaximum(totalHeight);
		bar.setThumb(clientArea.height);
		bar.setVisible(clientArea.height < totalHeight);
	}

	public List<ImageItem> getItems() {
		return this.items;
	}

	public void show(final ImageItem item) {
		final int index = this.items.indexOf(item);
		if (index >= 0) {
			final Rectangle bounds = this.layout.boundsOf(index);
			final Rectangle clientArea = this.getClientArea();
			final ScrollBar scrollBar = this.getVerticalBar();
			final int yDelta = scrollBar.getSelection();

			final Rectangle viewRange = new Rectangle(clientArea.x, clientArea.y + yDelta, clientArea.width, clientArea.height);

			if (!viewRange.contains(bounds.x, bounds.y) || !viewRange.contains(bounds.x + bounds.width, bounds.y + bounds.height)) {
				final int distance = this.layout.getMargin();
				if (viewRange.y > bounds.y) {
					scrollBar.setSelection(bounds.y - distance);

					this.redraw();
				}
				else if (viewRange.y < (bounds.y + bounds.height)) {
					scrollBar.setSelection(((bounds.y + bounds.height) - clientArea.height) + distance);

					this.redraw();
				}
			}
		}
	}

	public boolean isSelected(ImageItem item) {
		return this.selection.contains(item);
	}

	public ImageItem getFocusItem() {
		return focusItem;
	}
}
