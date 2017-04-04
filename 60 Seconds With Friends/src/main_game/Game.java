package main_game;

import java.util.*;
import java.util.Timer;
import java.awt.*; 			//For adding fonts, color, and graphics to frames
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*; 		//For frames and buttons and such

/*Class that will define every item in the game
 *Inventory size will be capped at 6 for the time being
 *List of items: Food, Water, Medicine, Knife, Gun, Ammunition
 *Future Note to self: Make items global objects to call and set them into an array. Also give items ID values for faster searches
 */
class Item
{
	String name; 		//Name of item
	int count;			//Number of items the player owns
	
	//Initializing constructor for the item
	public Item(String item_name, int item_count)
	{
		name = item_name;
		count = item_count;
	}
}

/*Class that contains all the resources that a player will see
 *Player can directly affect every value in this class
 *Current inventory system is set in a way that every players can only have items
 *From a set list. Will change for future play through but this works for current version
 */
class Resources
{
	int hunger; 		//Hunger rating for player
	int hydration; 		//Hydration rating for player
	int sanity; 		//Sanity rating for player
	int health;
	int specialty; 		//Makes player a survival expert[0], soldier[1], or medic[2]. Implement more later
	Item[] item_list = new Item[6];	//List of items that the player has
	
	//Initializes the resources that the player has
	public Resources(int hung, int hydr, int san, int hp, int spc, Item[] list)
	{
		hunger = hung;
		hydration = hydr;
		sanity = san;
		health = hp;
		specialty = spc;
		
		//Initializing the player inventory
		item_list[0] = new Item("Food", 5);
		item_list[1] = new Item("Water", 5);
		item_list[2] = new Item("Medicine", 0);
		item_list[3] = new Item("Knife", 0);
		item_list[4] = new Item("Gun", 0);
		item_list[5] = new Item("Ammunition", 0);
	}
}

/*Class that contains the game's global data that is visible to all players
 *Player cannot directly affect these values
 *Will also determine when the game ends and when scripted events happen
 */
class WorldData
{
	int date;
	int end_date;
	int timer;
}


public class Game extends JFrame
{
	final String[] SPECIALTY = {"Medic", "Soldier", "Survival Expert", "Artist", "Engineer"};
	final int GAME_WIDTH = 400; 		//Width of game frame
	final int GAME_LENGTH = 500;		//Length of game frame
	Resources player_data; 			//Object that holds all the data the player can manipulate or has direct access to
	WorldData global_data;			//Object that holds all the global variable data. Date, end date, etc.
	Font header_font;				//Holds the font style for any headings
	Font menu_font;					//Holds the font style for all the menu buttons
	Font text_font;					//Holds the font for any text that appears on screen
	JPanel text_panel = new JPanel(); 				//Holds the text boxes the game has. Here because I need to add and remove this whenever I want
	JTextArea text = new JTextArea(); 			//Holds the text to put into the text. Here because I need to add and remove this whenever I want
	JPanel timer_panel = new JPanel();
	JTextArea timer_text = new JTextArea();
	Timer turn_timer = new Timer();
	GridBagConstraints text_layout = new GridBagConstraints(); 		//GridBagConstraint to place the text box on the main frame
	
	/*Class constructor that displays the GUI for the player
	 *Shows menu options and make it interactable to show inventory or menu options
	 *This will be the main game frame that will be used
	 */
	public Game(Resources stats, WorldData data) 
	{
		player_data = stats; 			//Constructor variable for player data
		global_data = data;			//Constructor variable for global data
		header_font = new Font("Helvetica", Font.BOLD, 18); 		//Style to use for headings of menus or titles
		menu_font = new Font("Georgia", Font.PLAIN, 16);			//Style to use for menu options
		text_font = new Font("Helvetica", Font.PLAIN, 16);			//Style to use for res tof game
		
		//Initializing the Frame for the game
		setSize(GAME_WIDTH, GAME_LENGTH); 		//Set size of frame
		setTitle("60 Seconds With Friends: WIP"); 		//Header of frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 		//Exits program when frame is closed
		setResizable(false); 		//so player can't resize the frame
		setLayout(new GridBagLayout());
		
		basicMenuDisplay(stats, data);
		statDisplay(stats, data);
		this.turn_timer = callTimerDisplay(stats, data);
	}

	
	/*Method that displays all the player's information and current date
	 *Is called every time a day is incremented
	 */
	public void statDisplay(Resources stats, WorldData data)
	{
		//Deleting the old display
		this.remove(this.text_panel);
		this.remove(this.text);
		this.validate();
		this.repaint();
		
		this.text_panel = new JPanel();
		this.text = new JTextArea("Day: " + data.date + "/" + data.end_date + "\n" +
								  "Health: " + stats.health + "\n" + 
								  "Hunger: " + stats.hunger + "\n" + 
								  "Hydration: " + stats.hydration + "\n" + 
								  "Sanity: " + stats.sanity + "\n" +
								  "Specialty: " + SPECIALTY[stats.specialty]);
		this.text.setFont(text_font);
		this.text_panel.add(this.text);
		this.text_layout.gridx = 0;
		this.text_layout.gridy = 0;
		add(this.text_panel, this.text_layout);
		this.setVisible(true);
		this.validate();
		this.repaint();
	}
	
	/*Method that is called to display the basic menu options
	 *Suppressed warning for my own sanity
	 *Each button has its own Action to make things more manageable
	 */
	@SuppressWarnings("serial")
	public void basicMenuDisplay(Resources stats, WorldData data)
	{
		Timer timer = this.turn_timer; 		//Setting class variable to a local variable to call when using buttons
		
		//Button to make player eat food. Does not go over 100 and player cannot eat when not hungry or has no food
		JButton eat_button = new JButton(new AbstractAction("Eat")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if(stats.item_list[0].count > 0) 		//Check if player has food in inventory
					{
						if(stats.hunger > 90) 			//Check if player is full or not
						{
							JOptionPane.showMessageDialog(null, "You are too full to eat");
						}
						else 		//Player eats food
						{
							JOptionPane.showMessageDialog(null, "You ate some food");
							stats.hunger += 10;
							stats.item_list[0].count--;
							statDisplay(stats, data);
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "You have no food left");
					}
				}
			});
		eat_button.setFont(header_font);
		
		//Button to make player drink water. Does not go over 100 and player cannot drink when not thirsty or has no water
		JButton drink_button = new JButton(new AbstractAction("Drink")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if(stats.item_list[1].count > 0) 		//Check if player has water
					{ 
						if(stats.hydration > 90) 			//Check if player is thirsty or not
						{
							JOptionPane.showMessageDialog(null, "You are not thirsty");
						}
						else 		//Player drinks water
						{
							JOptionPane.showMessageDialog(null, "You drink some water");
							stats.hydration += 10;
							stats.item_list[1].count--;
							statDisplay(stats, data);
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "You have no water left");
					}
				}
			});
		drink_button.setFont(header_font);
		
		//Button to make player meditate. Takes some food and water but restores a bit of sanity. Also takes a whole day
		JButton meditate_button = new JButton(new AbstractAction("Meditate")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					timer.cancel(); 		//Stops the timer from running
					timer.purge(); 		//Deletes the old timer cache
						
					JOptionPane.showMessageDialog(null, "You meditated");
					stats.sanity += 5;
					stats.hunger -= 5;
					stats.hydration -= 5;
					stats.health += 10;
					data.date ++;
						
					global_data.timer = 60;
					gameStateCheck(player_data, global_data);
					statDisplay(stats,data);
					callTimerDisplay(stats, data);
				}
			});
		meditate_button.setFont(header_font);
		
		//Button to make the player forage. They go around and add to the inventory. Can take damage and stuff during this
		JButton forage_button = new JButton(new AbstractAction("Forage")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					timer.purge();
					timer.cancel(); 		//Stops the timer from running
					
					stats.hunger -= 10;
					stats.hydration -= 10;
					data.date ++;
					forageMethod();
					
					global_data.timer = 60;
					gameStateCheck(player_data, global_data);
					statDisplay(stats, data);
					callTimerDisplay(stats, data);
				}
			
			});
		forage_button.setFont(header_font);
		
		//Button that displays the player's inventory
		JButton inventory_button = new JButton(new AbstractAction("Inventory")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					timer.cancel(); 		//Stops the timer from running
					timer.purge(); 		//Deletes the older timer cache
					
					String inventory_data; 		//Holds item name
					String inv_count; 			//Holds item count
					String inventory = "";
					
					//Loops through all available items in the player inventory
					for(int i = 0; i < 6; i++)
					{
						inventory_data = stats.item_list[i].name; 		//Sets the name of item
						inv_count = Integer.toString(stats.item_list[i].count); 		//Convert the count of item into a string
						inventory_data = inventory_data + " - " + inv_count + "\n"; 		//Add strings together, format, then end with return character
						inventory = inventory + inventory_data;
					}
					JOptionPane.showMessageDialog(null, inventory);
				}
				
			});
		inventory_button.setFont(header_font);

		//Adding each button to the main game frame with setting the text button layout
		this.text_layout.gridx = 4;
		this.text_layout.gridy = 5;
		this.text_layout.fill = GridBagConstraints.HORIZONTAL; 		//Make all buttons fill the same space
		add(eat_button, this.text_layout);
		this.text_layout.gridy++;
		add(drink_button, this.text_layout);
		this.text_layout.gridy++;
		add(meditate_button, this.text_layout);
		this.text_layout.gridy++;
		add(forage_button, this.text_layout);
		this.text_layout.gridy++;
		add(inventory_button, this.text_layout);
		
		statDisplay(stats, data);
	}
	
	/*Method that brings up the timer used in the game screen
	 *Returns a Timer in order to be able to terminate the timer whenever an action takes place
	 */
	public Timer callTimerDisplay(Resources stats, WorldData data)
	{	
		this.turn_timer = new Timer(); 		//Initialize a new Timer to make a new one
		
		//Block that runs the timer for the turn
		this.turn_timer.scheduleAtFixedRate(new TimerTask()
			{
				public void run() 		//Pre-made call method built by TimerTask. Runs during timer tick
				{
					//Deletes old panel so we don't end with unnecessary amounts of panels
					remove(timer_panel);
					remove(timer_text);
					validate();
					repaint();
					
					//Initializes and creates a new timer panel
					timer_panel = new JPanel();
					timer_text = new JTextArea("Timer: " + Integer.toString(data.timer));
					data.timer--;
					text_layout.gridx = 4;
					text_layout.gridy = 0;
					text_layout.fill = GridBagConstraints.HORIZONTAL;
					text_layout.fill = GridBagConstraints.VERTICAL;
					timer_text.setFont(text_font);
					timer_panel.add(timer_text);
					add(timer_panel, text_layout);
					setVisible(true);
					revalidate();
					repaint();
					
					if(data.timer == 0)
					{
						turn_timer.cancel();
						turn_timer.purge();
						stats.hunger -= 10;
						stats.hydration -= 10;
						data.date++;
						data.timer = 60;
						JOptionPane.showMessageDialog(null, "You did nothing all day");
						statDisplay(player_data, global_data);
						callTimerDisplay(player_data, global_data);
					}
				}
			}, 0, 1000);
		return turn_timer;
	}
	
	/*Method that will handle the foraging call
	 *Has set list of scripted events that will occur randomly
	 *Called directly from the main game frame
	 */
	public void forageMethod()
	{
		long seed = System.currentTimeMillis();
		Random random_gen = new Random(seed); 		//Setting random seed
		int event = random_gen.nextInt(100);
		
		if(event < 40) 		//Most standard event that gives 1 food and 1 water 
		{
			JOptionPane.showMessageDialog(null,
					"You were moderately successful in your forage session\n"
					+ "You gained 1 Food and 1 Water!");
			this.player_data.item_list[0].count++;
			this.player_data.item_list[1].count++;
		}
		else if(event < 50) 		//Gives 3 water
		{
			JOptionPane.showMessageDialog(null,
					"You found a fresh supply of water\n"
					+ "You gained 3 Water!");
			this.player_data.item_list[1].count += 3;
		}
		else if(event < 60) 		//Gives 3 food
		{
			JOptionPane.showMessageDialog(null,
					"You managed to forage around for a lot of food\n"
					+ "You gained 2 Food!");
			this.player_data.item_list[0].count += 2;
		}
		else if(event < 70) 		//Give 2 Food and 2 Water for the cost of 20 life
		{
			JOptionPane.showMessageDialog(null,
					"You found a huge supply of food and water left on the side of the road\n"
					+ "It was trapped! But you still manage to make off with the goods\n"
					+ "You gained 2 Food and 2 Water!\n" 
					+ "You lost 20 life!");
			this.player_data.item_list[0].count += 2;
			this.player_data.item_list[1].count += 2;
			this.player_data.health = this.player_data.health - 20;
		}
		else if(event < 75) 		//Give player option to search through an abandoned camp. Super high risk/reward option. Add in ways to change odds?
		{	
			//Quick object creation to build options for the choice dialogue
			Object[] options = {"Yes","No"};
			int choice = 0;
			choice = JOptionPane.showOptionDialog(null,
							 "You find an abandoned camp with most of its supplies still there. Whoever was here left in a hurry\n "
					         + "Something tells you whatever scared them off may still be around. Do you take the time to scavanege the camp?",
					         "EVENT", JOptionPane.YES_OPTION, JOptionPane.NO_OPTION, null, options, options[1]);
			
			//Create a new pop up text box for this event
			//Will implement later
			
			if(choice == 0) 		//If player decides to raid the camp
			{
				event = random_gen.nextInt(100);
				
				if(event < 75) 		//Case of failure. Lose 50 life. May add in losing weapons or other supplies.
				{
					JOptionPane.showMessageDialog(null,
							"You were attacked! Limping, you leave in a panic and make off with your life \n"
							+ "You lose 50 life!");
					this.player_data.health = this.player_data.health - 50;
					
					event = random_gen.nextInt(100);
					
					if(event < 50) 		//Player makes it back home
					{
						JOptionPane.showMessageDialog(null, "You barely made it back to camp");
					}
					else if(event < 75) 		//Player gets a little food
					{
						JOptionPane.showMessageDialog(null,
								"On the way back you find a freshy killed animal with most the meat intact\n"
								+ "You got 1 Food!");
						this.player_data.item_list[0].count++;
					}
					else 				//Player finds a little water
					{
						JOptionPane.showMessageDialog(null,
								"On the way back you stop by a small stream of fresh water to get some rest\n"
								+ "You got 1 Water!");
						this.player_data.item_list[1].count++;
						
					}
				}
				else 		//Case of success
				{
					event = random_gen.nextInt(100);
					
					if(event > 50) 		//More medicine
					{
						JOptionPane.showMessageDialog(null,
								"What great fortune! You managed to scour the whole camp for lots of supplies!\n"
								+ "This camp was well stocked with food, water, and medicine! You also come across some weapons\n"
								+ "You found 4 Food, 3 Water, 3 Medicine, 1 Knife, and 5 Ammunition!");
						this.player_data.item_list[0].count += 4;
						this.player_data.item_list[1].count += 3;
						this.player_data.item_list[2].count += 3;
						this.player_data.item_list[3].count += 1;
						this.player_data.item_list[5].count += 5;
					}
					else 		//More weapons
					{
						JOptionPane.showMessageDialog(null,
								"What great fortune! You managed to scour the whole camp for lots of supplies!\n"
								+ "This camp was well stocked with food, water, and plenty of weapons! You also find some basic first aid\n"
								+ "You found 3 Food, 4 Water, 1 Medicine, 2 Knives, 1 Gun, and 15 Ammuntion!");
						this.player_data.item_list[0].count += 4;
						this.player_data.item_list[1].count += 3;
						this.player_data.item_list[2].count += 3;
						this.player_data.item_list[3].count += 2;
						this.player_data.item_list[4].count += 1;
						this.player_data.item_list[5].count += 15;
					}
				}
			}
		}
		else if(event < 80) 		//Nothing happened event
		{
			JOptionPane.showMessageDialog(null, "You found nothing on this foraging session");
		}
		else if(event < 85)
		{
			JOptionPane.showMessageDialog(null, "Placeholder for Bandit event");
			this.player_data.item_list[0].count++;
			this.player_data.item_list[1].count++;
		}
		else if(event < 90)
		{
			JOptionPane.showMessageDialog(null, "Placeholder for Merchant event");
			this.player_data.item_list[0].count++;
			this.player_data.item_list[1].count++;
		}
		else if(event < 95) 		//Finding 1-2 medicine
		{
			JOptionPane.showMessageDialog(null,
					"You found some valuable medical supplies!\n" +
					"You gained 2 medicine!");
			this.player_data.item_list[2].count += 2;
		}
		else 		//Good event that gives a surplus of supplies
		{
			event = random_gen.nextInt(100);
			
			if(event < 50)
			{
				JOptionPane.showMessageDialog(null,
						"You had a very succesful foraging session\n"
						+ "You found 3 Food and 2 Water!");
				this.player_data.item_list[0].count += 3;
				this.player_data.item_list[1].count += 2;
			}
			else
			{
				JOptionPane.showMessageDialog(null,
						"You had a very succesful foraging session\n"
						+ "You found 2 Food and 3 Water!");;
				this.player_data.item_list[0].count += 2;
				this.player_data.item_list[1].count += 3;
			}
		}
		return;
	}

	public boolean gameStateCheck(Resources stats, WorldData data)
	{
		if(data.date == data.end_date) 			//Check if the end game ended
		{
			JOptionPane.showMessageDialog(null, "You have survived the apocalypse!");
			return true;
		}
		
		if(stats.health < 5) 		//Player has run out of health
		{
			JOptionPane.showMessageDialog(null, "You have died");
			
			return false;
		}
		else				//Other checks for the player
		{ 
			if(stats.hunger < 20 || stats.hydration < 20 || stats.sanity < 20 || stats.health < 60)
			{
				JOptionPane.showMessageDialog(null, "You are feeling unwell");
			}
			return true;
		}
	}
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		
		//Initializing all data for game to begin
		Item[] list = new Item[6];
		Resources player_stats = new Resources(100, 100, 100, 100, 0, list);
		WorldData data = new WorldData();
		data.date = 1;
		data.end_date = 10;
		data.timer = 60;
		
		new Game(player_stats, data).setVisible(true);
	}
}
