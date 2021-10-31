import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CircleApp extends JPanel {

	static Color bgColor = new Color(0xF4E0C6), fillColor = new Color(0x40ba9b73,true), lineColor = new Color(0x826c50);

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

		occupiedLabelSpots.clear();
		String text;

		Rectangle2D bounds = new Rectangle(-getWidth() / 2 - dx, -getHeight() / 2 - dy, getWidth(), getHeight());

		double radius = Math.min(getWidth(), getHeight());
		radius /= 2;
		radius -= 10;
		radius = Math.min(radius, 300);

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setFont(new Font("Arial", Font.PLAIN, 12));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(getWidth() / 2, getHeight() / 2);
		g2.scale(1, -1);
		g2.translate(dx, dy);

		g2.setColor(lineColor);

		// Axis lines
		g2.setStroke(new BasicStroke(1.5f));
		g2.drawLine((int) bounds.getMinX(), 0, (int) bounds.getMaxX(), 0);
		g2.drawLine(0, (int) bounds.getMinY(), 0, (int) bounds.getMaxY());

		// Axis ticks
		if (radius >= 25) {
			double tickSpacing = radius / 10;
			double tickSize = radius / 50;
			for (int i = (int) (bounds.getMinX() / tickSpacing); (i * tickSpacing) < bounds.getMaxX(); i++) {
				if (i != 0)
					g2.draw(new Line2D.Double(i * tickSpacing, -tickSize, i * tickSpacing, tickSize));
			}
			for (int i = (int) (bounds.getMinY() / tickSpacing); (i * tickSpacing) < bounds.getMaxY(); i++) {
				if (i != 0)
					g2.draw(new Line2D.Double(-tickSize, i * tickSpacing, tickSize, i * tickSpacing));
			}
		}

		// Unit circle itself
		g2.draw(new Ellipse2D.Double(-radius, -radius, 2 * radius, 2 * radius));

		// The intersection point
		Point p = new Point((int) (radius * Math.cos(theta)), (int) (radius * Math.sin(theta)));
		
		// Fill the main triangle
		g2.setColor(fillColor);
		g2.fillPolygon(new int[]{0,p.x,p.x}, new int[]{0,0,p.y}, 3);
		
		// Primary radius
		g2.setColor(lineColor);
		g2.drawLine(0, 0, p.x, p.y);
		text = "r = 1";
		addLabel(g2, text, true, p.x/2, p.y/2, 0.5, 0.5);

		// Cosine line
		g2.setColor(new Color(0xcc33ff));
		g2.drawLine(0, 0, p.x, 0);

		// Cosine label
		text = String.format("cos(%.1f) = %.3f", Math.toDegrees(theta), Math.cos(theta));
		addLabel(g2, text, true, p.x / 2, -5, 0.5, 1);

		// Sine line
		g2.setColor(new Color(0xff6600));
		g2.drawLine(p.x, 0, p.x, p.y);

		// Sine label
		text = String.format("sin(%.1f) = %.3f", Math.toDegrees(theta), Math.sin(theta));
		addLabel(g2, text, true, p.x + 5, p.y / 2, 0, 0.5);

		// Tangent line
		g2.setColor(Color.BLUE.darker());
		Point p1, p2;
		text = String.format("tan(%.1f) = %.3f", Math.toDegrees(theta), Math.tan(theta));
		if (theta == 0) {
			p1 = p2 = p;
		} else {
			if (Math.abs(Math.abs(theta) - Math.PI / 2) < 0.001) {
				text = String.format("tan(%.1f) = UNDEFINED", Math.toDegrees(theta));
				p1 = new Point((int) bounds.getMinX(), p.y);
				p2 = new Point((int) bounds.getMaxX(), p.y);
				g2.drawLine(p1.x, p1.y, p2.x, p2.y);
			} else {
				p1 = (Point) p.clone();
				p2 = new Point((int) (radius / Math.cos(theta)), 0);
				if (p1.x < bounds.getMinX()) {
					p1.x = (int) bounds.getMinX();
					p1.y = (int) (p2.y - (p1.x - p2.x) / Math.tan(theta));
				} else if (p1.x > bounds.getMaxX()) {
					p1.x = (int) bounds.getMaxX();
					p1.y = (int) (p2.y - (p1.x - p2.x) / Math.tan(theta));
				}
				if (p2.x < bounds.getMinX()) {
					p2.x = (int) bounds.getMinX();
					p2.y = (int) (p1.y - (p2.x - p1.x) / Math.tan(theta));
				} else if (p2.x > bounds.getMaxX()) {
					p2.x = (int) bounds.getMaxX();
					p2.y = (int) (p1.y - (p2.x - p1.x) / Math.tan(theta));
				}

				g2.drawLine(p1.x, p1.y, p2.x, p2.y);
			}
		}

		// Tangent Label
		addLabel(g2, text, true, (p1.x + p2.x) / 2, (p1.y + p2.y) / 2, Math.abs(Math.sin(theta) / 2),
				Math.abs(Math.cos(theta) / 2));

		// Angle indicator partial circle
		g2.setColor(Color.ORANGE.darker());

		int centerRadius = (int) (radius / 4);
		Shape clip = g2.getClip();
		Path2D thetaClip = new Path2D.Double();
		thetaClip.moveTo(0, 0);
		thetaClip.lineTo(radius, 0);
		thetaClip.lineTo(0, radius * Math.signum(theta));
		thetaClip.lineTo(radius * Math.cos(theta), radius * Math.sin(theta));
		thetaClip.closePath();
		g2.setClip(thetaClip);
		g2.drawOval(-centerRadius, -centerRadius, 2 * centerRadius, 2 * centerRadius);
		g2.setClip(clip);

		// Angle label
		text = String.format("Θ = %.1f°", Math.toDegrees(theta));
		double moveTheta = theta / 2;
		addLabel(g2, text, true, centerRadius * Math.cos(moveTheta), centerRadius * Math.sin(moveTheta), 0.5, 0.5);

		drawLabels(g2);
		g2.dispose();
	}

	private class SimpleLabel extends Rectangle2D.Double {
		Color color;
		String text;
		boolean framed;

		public SimpleLabel(String text, Rectangle2D rect, Color color, boolean framed) {
			this.setFrame(rect);
			this.text = text;
			this.color = color;
			this.framed = framed;
		}

		void moveY(double amount) {
			this.y += amount;
		}

		void moveX(double amount) {
			this.x += amount;
		}
	}

	List<SimpleLabel> occupiedLabelSpots = new ArrayList<SimpleLabel>();

	private void addLabel(Graphics g, String s, boolean framed, double x, double y, double horizontalAlignment,
			double verticalAlignment) {
		Color c = g.getColor();
		FontMetrics metrics = g.getFontMetrics();
		Rectangle2D bounds = metrics.getStringBounds(s, g);
		bounds.setFrame(x - 2, y - metrics.getDescent(), bounds.getWidth() + 4, bounds.getHeight());
		bounds.setFrame(x - bounds.getWidth() * horizontalAlignment, y - bounds.getHeight() * verticalAlignment,
				bounds.getWidth(), bounds.getHeight());
		SimpleLabel newLabel = new SimpleLabel(s, bounds, c, framed);
		boolean didSomething = true;
		int iterations = 0;
		while (didSomething && iterations < 10) {
			didSomething = false;
			iterations++;
			for (SimpleLabel label : occupiedLabelSpots) {
				if (label.intersects(newLabel)) {
					double dx = absMin(label.getMinX() - newLabel.getMaxX(), label.getMaxX() - newLabel.getMinX());
					double dy = absMin(label.getMinY() - newLabel.getMaxY(), label.getMaxY() - newLabel.getMinY());
					if (Math.abs(dx) < Math.abs(dy)) {
						newLabel.moveX(dx / 2);
						label.moveX(-dx / 2);
					} else {
						newLabel.moveY(dy / 2);
						label.moveY(-dy / 2);
					}

					didSomething = true;
				}
			}
		}
		occupiedLabelSpots.add(newLabel);
	}

	private void drawLabels(Graphics g) {
		for (SimpleLabel label : occupiedLabelSpots) {
			Graphics2D g2 = (Graphics2D) g.create();
			if(label.framed) {
				g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 200));
				g2.fill(label);
				g2.setColor(label.color);
				g2.setStroke(new BasicStroke(1));
				g2.draw(label);
			}
			g2.scale(1, -1);
			g2.setColor(label.color);
			g2.drawString(label.text, (float) label.getX() + 2,
					(float) -label.getY() - g2.getFontMetrics().getDescent());
			g2.dispose();
		}
	}

	double absMin(double a, double b) {
		return (Math.abs(a) < Math.abs(b)) ? a : b;
	}

	double absMax(double a, double b) {
		return (Math.abs(a) > Math.abs(b)) ? a : b;
	}

	private class CircleListener implements MouseListener, MouseMotionListener {

		Point startRMB;

		@Override
		public void mouseDragged(MouseEvent e) {
			if (startRMB == null) {
				double x = -dx + e.getX() - getWidth() / 2;
				double y = dy + e.getY() - getHeight() / 2;
				theta = Math.atan2((y == 0) ? y : -y, x);
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
