import javax.swing.*;
import java.awt.event.*;
import java.awt.Shape;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.awt.*;
import java.util.*;

public class Paint extends JFrame implements ActionListener{

	JMenu archivo,dibujar,acercade;
	JMenuItem info,salir,nuevo,guardar,abrir,color;
	JRadioButtonMenuItem linea,rectangulo,elipse;
	JCheckBoxMenuItem rellenar;
	JColorChooser colorChooser = new JColorChooser();
	ButtonGroup figura;
	Panel panel;
	public Paint() {
		creamenu();
		addlisteners();

		panel = new Panel();
		this.add(panel );
		this.setSize( 800, 600 );
		this.setVisible( true );
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setTitle("Paint");
	}

	private void addlisteners() {
		info.addActionListener(this);
		salir.addActionListener(this);
		nuevo.addActionListener(this);
		guardar.addActionListener(this);
		abrir.addActionListener(this);
		color.addActionListener(this);
		linea.addActionListener(this);
		rectangulo.addActionListener(this);
		elipse.addActionListener(this);
		rellenar.addActionListener(this);
	}

	public void creamenu() {
		JMenuBar menu=new JMenuBar();
		archivo= new JMenu("Archivo");
		nuevo=new JMenuItem("Nuevo");
		abrir=new JMenuItem("Abrir");
		guardar=new JMenuItem("Guardar");
		salir=new JMenuItem("Salir");
		archivo.add(nuevo);
		archivo.add(abrir);
		archivo.add(guardar);
		archivo.add(salir);
		menu.add(archivo);
		dibujar= new JMenu("Dibujar");
		figura= new ButtonGroup();
		linea=new JRadioButtonMenuItem("Linea");
		rectangulo= new JRadioButtonMenuItem("Rectangulo");
		elipse= new JRadioButtonMenuItem("Elipse");
		figura.add(elipse);
		figura.add(rectangulo);
		figura.add(linea);
		figura.setSelected(linea.getModel(), true);
		rellenar =new JCheckBoxMenuItem("Figura rellena");
		color = new JMenuItem("Selecciona color");
		dibujar.add(linea);
		dibujar.add(rectangulo);
		dibujar.add(elipse);
		dibujar.add(rellenar);
		dibujar.add(color);
		menu.add(dibujar);
		acercade= new JMenu("Acerca de");
		info= new JMenuItem("Informacion");
		acercade.add(info);
		menu.add(acercade);
		this.setJMenuBar(menu);
	}

	public static void main( String[] args ){
		Paint ventana = new Paint( );

		ventana.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}

	public void actionPerformed(ActionEvent e) {

		if(e.getSource()==nuevo){
			panel.resetAll();
		}
		if(e.getSource()==abrir){
			panel.abrir();	

		}	
		if(e.getSource()==guardar){
			panel.guardar();	

		}	
		if(e.getSource()==salir){
			System.exit(0);

		}
		if (e.getSource()==linea){
			panel.linea=true;
			panel.rectangulo=false;

		}
		if (e.getSource()==rectangulo){
			panel.linea=false;
			panel.rectangulo=true;

		}
		if (e.getSource()==elipse){
			panel.linea=false;
			panel.rectangulo=false;

		}
		if (e.getSource()==rellenar){
			if(panel.relleno){
				panel.relleno=false;
			}else{
				panel.relleno=true;
			}
		}
		
		if(e.getSource()==color){
			Color color = JColorChooser.showDialog(this, "Color", this.panel.getColorActual());
		     this.panel.setColorActual(color);
		}
		if(e.getSource()==info){
			JOptionPane.showMessageDialog(null, "Este programa nos perimte dibujar ciertas figuras, ya sea solo su contorno o rellenas. ");
		}
	}
}

class Panel extends JPanel {
	Point punto1;
	Point punto2;
	Shape figura;
	Random random=new Random();
   public Color coloractual = Color.black;
	BufferedImage myImage;
	Graphics2D g2D;
	boolean rectangulo=false;
	boolean linea= true;
	boolean relleno= false;

	public Panel(){
		OyenteDeRaton oyente1 = new OyenteDeRaton();
		OyenteDeMovimiento oyente2 = new OyenteDeMovimiento();
		addMouseListener( oyente1 );
		addMouseMotionListener( oyente2 );
	}
	public Color getColorActual(){
		return coloractual;
	}

	public void setColorActual(Color color){
		coloractual = color;
	}
	public void guardar() {
		try {
			JFileChooser jfc = createJFileChooser();
			jfc.showSaveDialog(this);
			File file = jfc.getSelectedFile();
			if ( file == null ){
				return;
			}
			javax.swing.filechooser.FileFilter ff = jfc.getFileFilter();
			String fileName = file.getName();
			String extension = "jpg";
			if ( ff instanceof MyFileFilter){
				extension = ((MyFileFilter)ff).getExtension();
			}

			fileName = fileName + "." + extension;
			file = new File(file.getParent(),fileName);
			javax.imageio.ImageIO.write(myImage, extension, file);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	public void abrir() {
		try {
			JFileChooser jfc = createJFileChooser();
			jfc.showOpenDialog(this);

			File file = jfc.getSelectedFile();
			if ( file == null ){
				return;
			}

			myImage = javax.imageio.ImageIO.read(file);
			int w = myImage.getWidth(null);
			int h = myImage.getHeight(null);
			if (myImage.getType() != BufferedImage.TYPE_INT_RGB) {
				BufferedImage bi2 =
						new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
				Graphics big = bi2.getGraphics();
				big.drawImage(myImage, 0, 0, null);
			}
			g2D = (Graphics2D)myImage.getGraphics();
			repaint();
		} catch (IOException e) {

			System.exit(1);
		}
	}
	public JFileChooser createJFileChooser(){
		JFileChooser jfc = new JFileChooser();
		jfc.setAcceptAllFileFilterUsed(false);
		String [] fileTypes = getFormats();
		for(int i=0; i<fileTypes.length; i++ ){
			jfc.addChoosableFileFilter(new MyFileFilter(fileTypes[i],fileTypes[i] + " file"));
		}
		return jfc;
	}

	public String[] getFormats() {
		String[] formats = javax.imageio.ImageIO.getWriterFormatNames();
		java.util.TreeSet<String> formatSet= new java.util.TreeSet<String>();
		for (String s : formats) {
			formatSet.add(s.toLowerCase());
		}
		return formatSet.toArray(new String[0]);
	}

	public Graphics2D crearGraphics2D() {
		Graphics2D g2 = null;

		if (myImage == null || myImage.getWidth() != getSize().width
				|| myImage.getHeight() != getSize().height) {
			myImage = (BufferedImage) createImage(getSize().width, getSize().height);
		}

		if (myImage != null) {
			g2 = myImage.createGraphics();
			g2.setColor(coloractual);
			g2.setBackground(getBackground());
		}

		g2.clearRect(0, 0, getSize().width, getSize().height);
		return g2;
	}

	public void paintComponent( Graphics g ){
		super.paintComponent(g);
		if ( myImage == null ){
			g2D  = crearGraphics2D();
		}
		if(figura!=null){
			if(relleno){
				
				g2D.setColor(coloractual);
				g2D.draw(figura);	
				g2D.fill(figura);
			}else{
				g2D.setColor(coloractual);
				g2D.draw(figura);	
			}
			if (myImage != null && isShowing()) {
				g.drawImage(myImage, 0, 0, this);
			}
			figura=null;
		}
	}

	public Shape crearFigura(Point p1, Point p2 ){
		double xInicio = Math.min( p1.getX(), p2.getX() );
		double yInicio = Math.min( p1.getY(), p2.getY() );
		double ancho = Math.abs( p2.getX() - p1.getX() );
		double altura = Math.abs( p2.getY() - p1.getY() );
		Shape nuevaFigura = new Rectangle2D.Double( xInicio, yInicio, ancho, altura );
		return nuevaFigura;
	}

	public Shape crearLinea(Point p1, Point p2){
		Shape nuevaFigura = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		return nuevaFigura;
	}

	public Shape crearElipse(Point p1, Point p2){
		double xInicio = Math.min( p1.getX(), p2.getX() );
		double yInicio = Math.min( p1.getY(), p2.getY() );
		double ancho = Math.abs( p2.getX() - p1.getX() );
		double altura = Math.abs( p2.getY() - p1.getY() );
		Shape nuevaFigura = new Ellipse2D.Double( xInicio, yInicio, ancho, altura );
		return nuevaFigura;
	}

	public void resetAll(){
		myImage = null;
		repaint();
	}
	class OyenteDeRaton extends MouseAdapter{
		public void mousePressed( MouseEvent evento ){
			Panel.this.setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
			punto1 = evento.getPoint();
		}

		public void mouseReleased( MouseEvent evento ){
			Panel.this.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
			if(rectangulo){
				punto2 = evento.getPoint();
				figura = crearFigura(punto1, punto2 );				
				repaint();
			} else 
				if(linea){
					punto2 = evento.getPoint();
					figura = crearLinea(punto1, punto2 );
					repaint();
				} else{
					punto2 = evento.getPoint();
					figura = crearElipse(punto1, punto2 );
					repaint();
				}
			repaint();
		}
	}

	class OyenteDeMovimiento extends MouseMotionAdapter{
		public void mouseDragged( MouseEvent evento ){
			Graphics2D g2D;
			if(rectangulo){
				if( figura != null ){
					g2D = (Graphics2D) Panel.this.getGraphics();
					g2D.setXORMode(Panel.this.getBackground() );
					g2D.setColor(coloractual);
					g2D.draw(figura);
				}
				punto2 = evento.getPoint();
				figura = crearFigura(punto1, punto2 );
				g2D = (Graphics2D) Panel.this.getGraphics();
				g2D.setXORMode(Panel.this.getBackground() );
				g2D.setColor(coloractual);
				g2D.draw(figura);
			}else if(linea){
				if( figura != null ){
					g2D = (Graphics2D) Panel.this.getGraphics();
					g2D.setXORMode(Panel.this.getBackground() );
					g2D.setColor(coloractual);
					g2D.draw(figura);
				}
				punto2 = evento.getPoint();
				figura = crearLinea(punto1, punto2 );
				g2D = (Graphics2D) Panel.this.getGraphics();
				g2D.setXORMode(Panel.this.getBackground() );
				g2D.setColor(coloractual);
				g2D.draw(figura);
			} else{
				if( figura != null ){
					g2D = (Graphics2D) Panel.this.getGraphics();
					g2D.setXORMode(Panel.this.getBackground());
					g2D.setColor(coloractual);
					g2D.draw(figura);
				}
				punto2 = evento.getPoint();
				figura = crearElipse(punto1, punto2 );
				g2D = (Graphics2D) Panel.this.getGraphics();
				g2D.setXORMode(Panel.this.getBackground() );
				g2D.setColor(coloractual);
				g2D.draw(figura);
			}
		}
	}
	class MyFileFilter extends javax.swing.filechooser.FileFilter
	{

		private String extension;
		private String description;

		public MyFileFilter(String extension,String description){
			this.extension = extension;
			this.description = description;
		}

		public boolean accept (File f) {
			return f.getName ().toLowerCase ().endsWith ("."+extension)
					|| f.isDirectory ();
		}

		public String getDescription () {
			return description;
		}

		public String getExtension(){
			return extension;
		}
	}
}