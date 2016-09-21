package de.rnd7.imagegrid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestMain {

	public static void main(final String [] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);

		shell.setLayout(new GridLayout());

		final ImageGrid grid = new ImageGrid(shell, SWT.BORDER);
		for (int i = 0; i < 100; i++) {
			grid.addItem(new ImageItem().setName("A " + i));
			grid.addItem(new ImageItem().setName("B " + i));
			grid.addItem(new ImageItem().setName("C " + i));
		}

		grid.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Rectangle clientArea = shell.getClientArea();
		shell.setBounds(clientArea.x + 10, clientArea.y + 10, 200, 200);
		shell.open ();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}