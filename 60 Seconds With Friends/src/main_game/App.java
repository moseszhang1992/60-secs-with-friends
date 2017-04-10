package main_game;

import java.applet.Applet;
import java.awt.BorderLayout;

import javax.swing.JFrame;

public class App  extends Applet {
	
	private static Game game = new Game();
	
	public void init() {
		setLayout(new BorderLayout());
        add(game, BorderLayout.CENTER);
        setMaximumSize(Game.DIMENSIONS);
        setMinimumSize(Game.DIMENSIONS);
        setPreferredSize(Game.DIMENSIONS);
        game.debug = DEBUG;
        game.isApplet = true;
	}

	public void start() {
		game.start();
	}
	
	public void stop() {
		game.stop;
	}
	public static void main(String[] args) {
		game.setMinimumSize(Game.DIMENSIONS);
	    game.setMaximumSize(Game.DIMENSIONS);
	    game.setPreferredSize(Game.DIMENSIONS);

	    game.frame = new JFrame(Game.NAME);

	    game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    game.frame.setLayout(new BorderLayout());

	    game.frame.add(game, BorderLayout.CENTER);
	    game.frame.pack();

	    game.frame.setResizable(false);
	    game.frame.setLocationRelativeTo(null);
	    game.frame.setVisible(true);

	    game.windowHandler = new WindowHandler(game);
	    game.debug = DEBUG;

	    game.start(); 
	        
		Item[] list = new Item[6];
		Resources player_stats = new Resources(100, 100, 100, 100, 0, list);
		WorldData data = new WorldData();
		data.date = 1;
		data.end_date = 10;
		data.timer = 60;
		
		new Game(player_stats, data).setVisible(true);
	}

}
