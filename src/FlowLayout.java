import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Point;

public class FlowLayout extends java.awt.FlowLayout {

	private static final long serialVersionUID = 20060804L;
	private int verticalAlignment = CENTER;
	
	public static final int BOTTOM = 2, TOP = 0;

	public FlowLayout () {
		super();
	}

	public FlowLayout (int align) {
		super(align);
	}
	
	public FlowLayout (int align, int valign) {
		super(align);
		verticalAlignment = valign;
	}

	public FlowLayout (int align, int hgap, int vgap) {
		super(align, hgap, vgap);
	}

	public void layoutContainer (Container target)
	{
		synchronized (target.getTreeLock())
		{
			super.layoutContainer(target);
			if (verticalAlignment != TOP)
			{
				// first, find the highest and lowest points
				int high=Integer.MAX_VALUE;
				int low=0;

				int nmembers = target.getComponentCount();
				for (int ii=0; ii<nmembers; ++ii)
				{
					Component cmp = target.getComponent(ii);
					Point loc = cmp.getLocation();
					int top = loc.y;
					int btm = top + cmp.getHeight();
					low = Math.max(btm, low);
					high = Math.min(top, high);
				}
				// Now, calculate how far to drop each component.
				Insets insets = target.getInsets();
				int maxht = target.getHeight() - (insets.bottom + insets.top + getVgap()*2);

				// delta is the amount to move each component
				int delta = maxht - low + high;    // bottom alignment
				if (verticalAlignment == CENTER)
					delta /= 2;                      // center alignment

				// Now, move each component down.
				for (int ii=0; ii<nmembers; ++ii)
				{
					Component cmp = target.getComponent(ii);
					Point newLoc = cmp.getLocation();
					newLoc.y += delta;
					cmp.setLocation(newLoc);
				}
			}
		}
	}

	public int getVerticalAlignment () {
		return verticalAlignment;
	}

	public void setVerticalAlignment (int valign) {
		verticalAlignment = valign;
	}

}