import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
		
		String text;
		FontMetrics metrics = g.getFontMetrics();

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

		// The intersection point
		Point p = new Point((int)(radius*Math.cos(theta)), (int)(radius*Math.sin(theta)));
		
		// Primary radius
		g2.drawLine(0,0,p.x,p.y);

		// Further drawing is dashed
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 2, 3 }, 0));
		
		// Sine line
		g2.setColor(new Color(0xcc33ff));
		g2.drawLine(0,p.y,p.x,p.y);
		
		// Sine label
		text = String.format("sin(Θ) = %.3f", Math.sin(theta));
		drawString(g2, text,
				(Math.abs(theta) < Math.PI/2)?10:-(10+metrics.stringWidth(text)),
				p.y+5
				);
		
		// Cosine line
		g2.setColor(new Color(0xff6600));
		g2.drawLine(p.x,0,p.x,p.y);
		
		// Cosine label
		text = String.format("cos(Θ) = %.3f", Math.cos(theta));
		drawString(g2, text,
				p.x+((Math.abs(theta) < Math.PI/2)?5:-(metrics.stringWidth(text)+5)),
				((Math.signum(theta) == -1)?-(metrics.getAscent()+5+radius/50):5+radius/50)
				);
		
		
		
		// Angle indicator partial circle
		g2.setColor(Color.ORANGE.darker());
		
		Shape centerCircle = new Ellipse2D.Double(-radius / 4, -radius / 4, radius / 2, radius / 2);
		Shape clip = g2.getClip();
		Path2D thetaClip = new Path2D.Double();
		thetaClip.moveTo(0, 0);
		thetaClip.lineTo(radius, 0);
		thetaClip.lineTo(0, radius * Math.signum(theta));
		thetaClip.lineTo(radius * Math.cos(theta), radius * Math.sin(theta));
		thetaClip.closePath();
		g2.setClip(thetaClip);
		g2.draw(centerCircle);
		g2.setClip(clip);
		
		// Angle label
		text = String.format("%.1f°", Math.toDegrees(theta));
		Rectangle2D angleBounds = metrics.getStringBounds(text, g2);
		Area angleArea = new Area(centerCircle);
		double moveTheta = theta/2;
		int distance = 0;
		Area lableArea = new Area(angleBounds);
		while(!lableArea.isEmpty()) {
			lableArea.transform(AffineTransform.getTranslateInstance(Math.cos(moveTheta), Math.sin(moveTheta)));
			lableArea.intersect(angleArea);
			distance++;
		}
		drawString(g2, text, distance*Math.cos(moveTheta), distance*Math.sin(moveTheta));
		
		g2.dispose();
	}
	
	private void drawString(Graphics g, String s, double x, double y) {
		Graphics2D g2 = (Graphics2D)g.create();
		g2.scale(1, -1);
		g2.drawString(s,(float)x,(float)-y);
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
