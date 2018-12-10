import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CircleApp extends JPanel {

	static Color bgColor = new Color(0xF4E0C6), fillColor = new Color(0xba9b73), lineColor = fillColor.darker();

	public static void main(String[] args) {
		JFrame hostFrame = new JFrame();
		hostFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		hostFrame.add(new CircleApp());
		hostFrame.pack();
		hostFrame.setLocationRelativeTo(null);
		hostFrame.setVisible(true);
	}

	double theta;
	int dx, dy;

	public CircleApp() {
		this.setPreferredSize(new Dimension(640, 480));
		this.setBackground(bgColor);
		CircleListener listener = new CircleListener();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		double radius = Math.min(getWidth(), getHeight());
		radius /= 2;
		radius -= 10;
		radius = Math.min(radius, 300);

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(getWidth() / 2, getHeight() / 2);
		g2.scale(1, -1);
		g2.translate(dx, dy);


		// Axis lines
		g2.setStroke(new BasicStroke(1.5f));
		g2.drawLine(-dx - getWidth(), 0, -dx + getWidth(), 0);
		g2.drawLine(0, -dy - getHeight(), 0, -dy + getHeight());

		// Axis ticks
		if(radius >= 25) {
			double tickSpacing = radius/10;
			double tickSize = radius/50;
			for(int i = (int)((-dx-getWidth()/2)/tickSpacing); (i*tickSpacing) < (-dx+getWidth()/2); i++) {
				if(i != 0) g2.draw(new Line2D.Double(i*tickSpacing, -tickSize, i*tickSpacing, tickSize));
			}
			for(int i = (int)((-dy-getHeight()/2)/tickSpacing); (i*tickSpacing) < (-dy+getHeight()/2); i++) {
				if(i != 0) g2.draw(new Line2D.Double(-tickSize, i*tickSpacing, tickSize, i*tickSpacing));
			}
		}

		// Unit circle itself
		g2.draw(new Ellipse2D.Double(-radius, -radius, 2 * radius, 2 * radius));

		// Primary radius
		g2.draw(new Line2D.Double(0, 0, radius * Math.cos(theta), radius * Math.sin(theta)));

		// Angle indicator
		Shape clip = g2.getClip();
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 2, 3 }, 0));
		Path2D thetaClip = new Path2D.Double();
		thetaClip.moveTo(0, 0);
		thetaClip.lineTo(radius, 0);
		thetaClip.lineTo(0, radius * Math.signum(theta));
		thetaClip.lineTo(radius * Math.cos(theta), radius * Math.sin(theta));
		thetaClip.closePath();
		g2.setClip(thetaClip);
		g2.draw(new Ellipse2D.Double(-radius / 4, -radius / 4, radius / 2, radius / 2));
		g2.setClip(clip);

		g2.dispose();
	}

	private class CircleListener implements MouseListener, MouseMotionListener {

		Point startRMB;

		@Override
		public void mouseDragged(MouseEvent e) {
			if (startRMB == null) {
				double x = -dx + e.getX() - getWidth() / 2;
				double y = dy + e.getY() - getHeight() / 2;
				theta = Math.atan2(-y, x);
			} else {
				dx = e.getX() - startRMB.x;
				dy = startRMB.y - e.getY();
			}
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				startRMB = e.getPoint();
				startRMB.translate(-dx, dy);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				startRMB = null;
			}
		}

	}

}
