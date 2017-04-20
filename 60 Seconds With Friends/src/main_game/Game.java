package main_game;

import java.awt.Color;
import java.awt.Dimension;
//For adding fonts, color, and graphics to frames
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
//For frames and buttons and such
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

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
	int player_count;
	HashMap<Integer, Integer> player_status = new HashMap<Integer,Integer>();
}


@SuppressWarnings("serial")
public class Game extends JFrame
{
	final String[] SPECIALTY = {"Medic", "Soldier", "Survival Expert", "Musician"};
	final int GAME_WIDTH = 800; // Width of game frame
	final int GAME_LENGTH = 500; // Length of game frame
	Resources[] player_data; 			//Object that holds all the data the player can manipulate or has direct access to
	WorldData global_data;			//Object that holds all the global variable data. Date, end date, etc.
	int p_index = 0; 
	Font button_font;				//Holds the font style for any headings
	Font menu_font;					//Holds the font style for all the menu buttons
	Font text_font;					//Holds the font for any text that appears on screen
	JPanel text_panel = new JPanel(); 				//Holds the text boxes the game has. Here because I need to add and remove this whenever I want
	JTextArea text = new JTextArea(); 			//Holds the text to put into the text. Here because I need to add and remove this whenever I want
	JPanel timer_panel = new JPanel();
	JTextArea timer_text = new JTextArea();
	Timer turn_timer = new Timer();
	GridBagConstraints text_layout = new GridBagConstraints(); 		//GridBagConstraint to place the text box on the main frame
	BufferedImage img;
	ImageIcon imgIcon;
	JLabel background;
	
	/*Class constructor that displays the GUI for the player
	 *Shows menu options and the current player's resources and world stats
	 *This will be the main game frame that will be used
	 */
	public Game(Resources[] stats, WorldData data) 
	{
		player_data = stats; 			//Constructor variable for player data
		global_data = data;			//Constructor variable for global data
		button_font = new Font("Helvetica", Font.BOLD, 18); 		//Style to use for buttons
		menu_font = new Font("Georgia", Font.PLAIN, 16);			//Style to use for menu options
		text_font = new Font("Helvetica", Font.PLAIN, 16);			//Style to use for rest of game

		setBackgroundApocaImg();

		// Initializing the Frame for the game
		setSize(GAME_WIDTH, GAME_LENGTH); // Set size of frame
		setTitle("60 Seconds With Friends: WIP"); // Header of frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exits program when
														// frame is closed

		setResizable(true); // so player can't resize the frame
		setLayout(new GridBagLayout());
		basicMenuDisplay();
		statDisplay();
		this.turn_timer = callTimerDisplay();


	}

	
	/*Method that displays all the player's information and current date
	 *Is called every time a day is incremented
	 */
	public void statDisplay()
	{
		//Deleting the old display
		this.remove(this.text_panel);
		this.remove(this.text);
		this.validate();
		this.repaint();
		
		JPanel blank_text_panel = new JPanel();
		this.text_panel = new JPanel();
		this.text_panel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.BLACK));
		this.text = new JTextArea("Day: " + this.global_data.date + "/" + this.global_data.end_date + "\n" +
								  "Health: " + this.player_data[p_index].health + "\n" + 
								  "Hunger: " + this.player_data[p_index].hunger + "\n" + 
								  "Hydration: " + this.player_data[p_index].hydration + "\n" + 
								  "Sanity: " + this.player_data[p_index].sanity + "\n" +
								  "Specialty: " + SPECIALTY[this.player_data[p_index].specialty]  + "\n" +
								  "Player: " + (p_index + 1));
		this.text.setFont(text_font);
		this.text_panel.add(this.text);
		this.text_layout.gridx = 1;
		this.text_layout.gridy = 0;
		add(this.text_panel, this.text_layout);

		this.text_layout.gridx = 2;
		this.text_layout.gridy = 0;
		add(blank_text_panel, this.text_layout);
		blank_text_panel.setPreferredSize(new Dimension(25, 25));
		blank_text_panel.setBackground(new Color(0, 0, 0, 0));

		this.setVisible(true);
		this.validate();
		this.repaint();
	}
	
	/*Method that is called to display the basic menu options
	 *Suppressed warning for my own sanity
	 *Each button has its own Action to make things more manageable
	 */
	public void basicMenuDisplay()
	{	
		//Button to make player eat food. Does not go over 100 and player cannot eat when not hungry or has no food
		JButton eat_button = new JButton(new AbstractAction("Eat")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if(player_data[p_index].item_list[0].count > 0) 		//Check if player has food in inventory
					{
						if(player_data[p_index].hunger > 90) 			//Check if player is full or not
						{
							JOptionPane.showMessageDialog(null, "You are too full to eat");
						}
						else 		//Player eats food
						{
							JOptionPane.showMessageDialog(null, "You ate some food");
							player_data[p_index].hunger += 10;
							player_data[p_index].item_list[0].count--;
							statDisplay();
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "You have no food left");
					}
				}
			});
		eat_button.setFont(button_font);
		eat_button.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLUE));

		//Button to make player drink water. Does not go over 100 and player cannot drink when not thirsty or has no water
		JButton drink_button = new JButton(new AbstractAction("Drink")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if(player_data[p_index].item_list[1].count > 0) 		//Check if player has water
					{ 
						if(player_data[p_index].hydration > 90) 			//Check if player is thirsty or not
						{
							JOptionPane.showMessageDialog(null, "You are not thirsty");
						}
						else 		//Player drinks water
						{
							JOptionPane.showMessageDialog(null, "You drink some water");
							player_data[p_index].hydration += 10;
							player_data[p_index].item_list[1].count--;
							statDisplay();
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "You have no water left");
					}
				}
			});
		drink_button.setFont(button_font);
		drink_button.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLUE));

		//Button to make player meditate. Takes some food and water but restores a bit of sanity. Also takes a whole day
		JButton meditate_button = new JButton(new AbstractAction("Meditate")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					turn_timer.cancel(); 		//Stops the timer from running
					turn_timer.purge(); 		//Deletes the old timer cache
					
					//Meditate to gain sanity and lose a bit of hunger and health
					JOptionPane.showMessageDialog(null, "You meditated");
					player_data[p_index].sanity += 10;
					player_data[p_index].hunger -= 5;
					player_data[p_index].hydration -= 5;
					player_data[p_index].health += 10;
					
					nextTurnMethod();
				}
			});
		meditate_button.setFont(button_font);
		meditate_button.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLUE));

		//Button to make the player forage. They go around and add to the inventory. Can take damage and stuff during this
		JButton forage_button = new JButton(new AbstractAction("Forage")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					turn_timer.purge();
					turn_timer.cancel(); 		//Stops the timer from running
					
					//Calling forage method. Reduce hunger hydration and sanity
					player_data[p_index].hunger -= 10;
					player_data[p_index].hydration -= 10;
					player_data[p_index].sanity -= 5;
					
					forageMethod();
					nextTurnMethod();
				}
			});
		forage_button.setFont(button_font);
		forage_button.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLUE));

		//Button that lets a player steal from another one
		JButton take_button = new JButton(new AbstractAction("Take")
		{		
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int choice_index = 0, 		//Index of player to steal from
					item_index = 0; 		//Index of the item to steal
				String item_choice = null; 				//String that holds the item to be stolen
				Object[] choices = {"1. Food x2", "2. Water x2", "3. Medicine x1","4. Knife x1", "5. Gun x1", "6. Ammunition x5"}; 	//Steal list
				
				choice_index = chooseOtherPlayerMethod("Choose a player to take from", "TAKE ITEM");
				
				if(choice_index >= 0)
				{			
					//Building a dialog box to prompt user to pick an item to steal
					item_choice = (String) JOptionPane.showInputDialog(null, "Choose an item to steal",
							"TAKE ITEM",
							JOptionPane.PLAIN_MESSAGE,
							null,
							choices,
							choices[0]);
					
					if(item_choice != null) 		//If the player didn't exit out of the dialog box
					{
						turn_timer.cancel();
						turn_timer.purge();
						item_index = Integer.parseInt(item_choice.substring(0,1));
						item_index--; 		//Decrement to get index of the item
						
						player_data[p_index].hunger -= 5;
						player_data[p_index].hydration -= 5;
						
						if(item_index == 1 || item_index == 0)		//Stealing food or water
						{
							if(player_data[choice_index].item_list[item_index].count > 1)
							{
								player_data[choice_index].item_list[item_index].count -= 2;
								player_data[p_index].item_list[item_index].count += 2;
							}
							else
							{
								JOptionPane.showMessageDialog(null, "The player did not have any of that item");
							}
						
						}
						else if(item_index == 5) 		//Stealing ammo
						{
							if(player_data[choice_index].item_list[item_index].count > 4)
							{
								player_data[choice_index].item_list[item_index].count -= 5;
								player_data[p_index].item_list[item_index].count += 5;
							}
							else
							{
								JOptionPane.showMessageDialog(null, "The player did not have any of that item");
							}
							
						}
						else 			//Stealing medicine, knife, or gun
						{
							if(player_data[choice_index].item_list[item_index].count > 0)
							{
								player_data[choice_index].item_list[item_index].count--;
								player_data[p_index].item_list[item_index].count++;
							}
							else
							{
								JOptionPane.showMessageDialog(null, "The player did not have any of that item");
							}
						}
						nextTurnMethod();
					}
				}
			}
		});
		take_button.setFont(button_font);
		take_button.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLUE));

		//Button that lets player give items to another player
		JButton give_button = new JButton(new AbstractAction("Give")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int choice_index = 0,
					item_index = 0; 		//Index of the item to steal
				String item_choice = null; 				//String that holds the item to give
				Object[] choices = {"1. Food x1", "2. Water x1", "3. Medicine x1","4. Knife x1", "5. Gun x1", "6. Ammunition x5"}; 	//Give list
				
				choice_index = chooseOtherPlayerMethod("Choose a player to give to", "GIVE ITEM");

				if(choice_index >= 0)
				{
					//Building a dialog box to prompt user to choose what items to give
					item_choice = (String) JOptionPane.showInputDialog(null,
							"Choose what item to give to the player",
							"GIVE ITEM",
							JOptionPane.PLAIN_MESSAGE,
							null,
							choices,
							choices[0]);
					
					//Checking if the dialog box was closed or not
					if(item_choice != null)
					{
						item_index = Integer.parseInt(item_choice.substring(0,1));
						item_index--; 		//Decrement to the item index
						
						if(player_data[p_index].item_list[item_index].count > 0)
						{
							player_data[p_index].item_list[item_index].count--;
							player_data[choice_index].item_list[item_index].count++;
						}
						else
						{
							JOptionPane.showMessageDialog(null, "You do not have that item to give");
						}
					}
				}
			}
		});
		give_button.setFont(button_font);
		give_button.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLUE));

		//Button that lets players heal other players
		JButton heal_button = new JButton(new AbstractAction("Heal")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int choice_index; 		//Holds index of target player
				
				choice_index = chooseOtherPlayerMethod("Choose a player to heal", "HEAL PLAYER"); 		//Get index of target player
				
				if(choice_index >= 0) 			//Selection was not canceled
				{
					if(player_data[p_index].item_list[2].count > 0) 		//Check if player has medicine
					{
						if(player_data[choice_index].health < 90) 			//Check if target player has low enough health
						{
							player_data[p_index].item_list[2].count--;
	
							if(player_data[p_index].specialty == 0) 		//Check if player is medic
							{
								player_data[choice_index].health += 40;
							}
							else
							{
								player_data[choice_index].health += 10;
							}
							nextTurnMethod();
						}
						else
						{
							JOptionPane.showMessageDialog(null, "That player is not injured");
						}
					}
					else 			//No medicine
					{
						JOptionPane.showMessageDialog(null, "You do not have any medicine");
					}
				}
			}
			
		});
		heal_button.setFont(button_font);
		heal_button.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLUE));

		//Button that displays the player's inventory
		JButton inventory_button = new JButton(new AbstractAction("Inventory")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{				
					String inventory_data; 		//Holds item name
					String inv_count; 			//Holds item count
					String inventory = "";
					
					//Loops through all available items in the player inventory
					for(int i = 0; i < 6; i++)
					{
						inventory_data = player_data[p_index].item_list[i].name; 		//Sets the name of item
						inv_count = Integer.toString(player_data[p_index].item_list[i].count); 		//Convert the count of item into a string
						inventory_data = inventory_data + " - " + inv_count + "\n"; 		//Add strings together, format, then end with return character
						inventory = inventory + inventory_data;
					}
					JOptionPane.showMessageDialog(null, inventory);
				}
			});
		inventory_button.setFont(button_font);
		inventory_button.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLUE));

		//Adding each button to the main game frame with setting the text button layout
		//Incrementing the gridy to make buttons stack on top of each other
		JPanel blank_panel = new JPanel();
		blank_panel.setPreferredSize(new Dimension(25, 25));
		blank_panel.setBackground(new Color(0, 0, 0, 0));
		this.text_layout.gridx = 4;
		this.text_layout.gridy = 5;
		add(blank_panel, this.text_layout);

		this.text_layout.gridx = 4;
		this.text_layout.gridy = 6;
		this.text_layout.fill = GridBagConstraints.HORIZONTAL; // Make all
																// buttons fill
																// the same
																// space
		add(eat_button, this.text_layout);
		this.text_layout.gridy++;
		add(drink_button, this.text_layout);
		this.text_layout.gridy++;
		add(meditate_button, this.text_layout);
		this.text_layout.gridy++;
		add(forage_button, this.text_layout);
		this.text_layout.gridy++;
		add(take_button, this.text_layout);
		this.text_layout.gridy++;
		add(give_button, this.text_layout);
		this.text_layout.gridy++;
		add(heal_button, this.text_layout);
		this.text_layout.gridy++;
		add(inventory_button, this.text_layout);
		
		statDisplay();
	}
	
	public int chooseOtherPlayerMethod(String prompt, String title)
	{
			int index = 0;
			String player_choice = null;		//String that holds the player
			
			do 		//Loops until close dialog box or choice is not the player's number or out of the player number range
			{
				//Creation dialog for player to choose a player
				player_choice = (String) JOptionPane.showInputDialog(null,
						prompt,
						title,
						JOptionPane.PLAIN_MESSAGE);
				
				if(player_choice == null)
				{
					index = -1;
					return index;
				}
				
				index = Integer.parseInt(player_choice); 		//Set the choice_index to the player number
				index--; 		//Decrement to get player index 	
			}while((index > (global_data.player_count - 1) || index < 0) || (index == (p_index)));
			
			return index;
	}
	
	/*Method that brings up the timer used in the game screen
	 *Returns a Timer in order to be able to terminate the timer whenever an action takes place
	 */
	public Timer callTimerDisplay()
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
				timer_panel.setBorder(BorderFactory.createMatteBorder(7, 7, 7, 7, Color.BLACK));


				timer_text = new JTextArea("Timer: " + Integer.toString(global_data.timer));
					global_data.timer--;
					text_layout.gridx = 4;
					text_layout.gridy = 0;
					text_layout.fill = GridBagConstraints.HORIZONTAL;
					text_layout.fill = GridBagConstraints.VERTICAL;
					timer_text.setFont(text_font);

				if (global_data.timer > 30) {
					timer_text.setBackground(Color.GREEN);
					timer_panel.setBackground(Color.GREEN);
				} else if (global_data.timer > 15) {
					timer_text.setBackground(Color.YELLOW);
					timer_panel.setBackground(Color.YELLOW);
				} else {
					timer_text.setBackground(Color.RED);
					timer_panel.setBackground(Color.RED);
				}

					timer_panel.add(timer_text);
					add(timer_panel, text_layout);
					setVisible(true);
					revalidate();
					repaint();
					
					if(global_data.timer == 0) 		//Turn timer hits 0
					{
						turn_timer.cancel();
						turn_timer.purge();
						player_data[p_index].hunger -= 10;
						player_data[p_index].hydration -= 10;
						
						//Close out of all other JDialog menus on the screen
						Window[] windows = Window.getWindows();
						for(Window window:windows)
						{
							if(window instanceof JDialog)
							{
								 JDialog dialog = (JDialog) window;
								 if(dialog.getContentPane().getComponentCount() == 1 &&
										 dialog.getContentPane().getComponent(0) instanceof JOptionPane)
								 {
									 dialog.dispose();
								 }
							}
						}
						
						JOptionPane.showMessageDialog(null, "You did nothing all day");
						nextTurnMethod();
					}
				}
			}, 0, 1000);
		return turn_timer;
	}
	
	/*Basic method that comes between player turns
	 * Simply there to stop players being able to look at the next player's stats and stuff
	 */
	public void nextTurnMethod()
	{

		// TODO: IMPLEMENT RANDOM NATURAL EVENT OCCURRENCES AND CONTINUE TURN
		if(p_index == (global_data.player_count - 1)) 			//Check if last player is playing or not
		{
			global_data.date++;		//Increment day for other players
			p_index = 0; 			//Set player back to the first player
		}
		else
		{ 
			p_index++; 				//Increment to the next player
		}
		
		// Random natural event with 8% chance of something happening each turn
		Random rand = new Random();
		int randVal = rand.nextInt(100);
		if (randVal >= 0 && randVal < 8) {
			randNaturalEventGenerator();
		}
		// change back to apocalapyse background if not changed
		else {
			setBackgroundApocaImg();
		}

		global_data.timer = 60;
		
		JOptionPane.showMessageDialog(null, "It is Player " + (p_index + 1) + "'s turn to play \n");
		statDisplay();
		if (gameStateCheck() > 0) {
			
			if (global_data.player_status.size() < 4) {
				global_data.player_status.put(p_index + 1, 1);
			}
			else {
				JOptionPane.showMessageDialog(null, "Everyone has died. You fail.");
				end_game(1);
				return;
			}
			
			nextTurnMethod();
		}
		callTimerDisplay();
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
			this.player_data[p_index].item_list[0].count++;
			this.player_data[p_index].item_list[1].count++;
		}
		
		else if(event < 50) 		//Gives 3 water
		{
			JOptionPane.showMessageDialog(null,
					"You found a fresh supply of water\n"
					+ "You gained 3 Water!");
			this.player_data[p_index].item_list[1].count += 3;
		}
		
		else if(event < 60) 		//Gives 3 food
		{
			JOptionPane.showMessageDialog(null,
					"You managed to forage around for a lot of food\n"
					+ "You gained 2 Food!");
			this.player_data[p_index].item_list[0].count += 2;
		}
		
		else if(event < 70) 		//Give 2 Food and 2 Water for the cost of 20 life
		{
			JOptionPane.showMessageDialog(null,
					"You found a huge supply of food and water left on the side of the road\n"
					+ "It was trapped! But you still manage to make off with the goods\n"
					+ "You gained 2 Food and 2 Water!\n" 
					+ "You lost 20 life!");
			this.player_data[p_index].item_list[0].count += 2;
			this.player_data[p_index].item_list[1].count += 2;
			this.player_data[p_index].health -= 20;
		}
		
		else if(event < 75) 		//Give player option to search through an abandoned camp. Super high risk/reward option. Add in ways to change odds?
		{	
			campEventMethod();
		}
		
		else if(event < 80) 		//Nothing happened event
		{
			if(this.player_data[p_index].specialty == 2)
			{
				JOptionPane.showMessageDialog(null, "You managed to scrounge up some supplies\n"
						+ "You gained 1 food and 1 water!");
				this.player_data[p_index].item_list[0].count++;
				this.player_data[p_index].item_list[1].count++;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "You found nothing on this foraging session");
			}	
		}
		
		else if(event < 85) 		//Bandit event
		{
			banditEventMethod(); 
		}
		
		else if(event < 90) 		//Merchant event
		{
			merchantEventMethod();
		}
		
		else if(event < 95) 		//Finding 1-2 medicine
		{
			JOptionPane.showMessageDialog(null,
					"You found some valuable medical supplies!\n" +
					"You gained 2 medicine!");
			this.player_data[p_index].item_list[2].count += 2;
		}
		
		else 		//Good event that gives a surplus of supplies
		{
			event = random_gen.nextInt(100);
			
			if(event < 50)
			{
				JOptionPane.showMessageDialog(null,
						"You had a very succesful foraging session\n"
						+ "You found 3 Food and 2 Water!");
				this.player_data[p_index].item_list[0].count += 3;
				this.player_data[p_index].item_list[1].count += 2;
			}
			else
			{
				JOptionPane.showMessageDialog(null,
						"You had a very succesful foraging session\n"
						+ "You found 2 Food and 3 Water!");;
				this.player_data[p_index].item_list[0].count += 2;
				this.player_data[p_index].item_list[1].count += 3;
			}
		}
		return;
	}
	
	/*Method that is called when the abandoned camp event triggers during a foraging session
	 * A very high-risk high-reward event
	 * Can be adjusted by being a survival expert however
	 */
	public void campEventMethod()
	{
		int choice = 0, 		//Selection for dealing with merchants
			event = 0;			//Holds the random integer to determine random events
		Random random_gen; 
		long seed = System.currentTimeMillis(); 		//Set seed as time of computer
	
		random_gen = new Random(seed);
		
		Object[] options = {"Yes","No"};		//Quick object creation to build options for the choice dialogue
		choice = JOptionPane.showOptionDialog(null,
						 "You find an abandoned camp with most of its supplies still there. Whoever was here left in a hurry\n "
				         + "Something tells you whatever scared them off may still be around. Do you take the time to scavanege the camp?",
				         "EVENT", JOptionPane.YES_OPTION, JOptionPane.NO_OPTION, null, options, options[1]);
		
		if(choice == 0) 		//If player decides to raid the camp
		{
			event = random_gen.nextInt(100);
			
			if(this.player_data[p_index].specialty == 2) 		//Check if player is survival expert
			{
				event += 40;
			}
			
			if(event < 75) 		//Case of failure. Lose 50 life. May add in losing weapons or other supplies.
			{
				
				JOptionPane.showMessageDialog(null,
						"You were attacked! Limping, you leave in a panic and make off with your life \n"
						+ "You lose 50 life!");
				this.player_data[p_index].health -= 50;
				
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
					this.player_data[p_index].item_list[0].count++;
				}
				else 				//Player finds a little water
				{
					JOptionPane.showMessageDialog(null,
							"On the way back you stop by a small stream of fresh water to get some rest\n"
							+ "You got 1 Water!");
					this.player_data[p_index].item_list[1].count++;
					
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
					this.player_data[p_index].item_list[0].count += 4;
					this.player_data[p_index].item_list[1].count += 3;
					this.player_data[p_index].item_list[2].count += 3;
					this.player_data[p_index].item_list[3].count += 1;
					this.player_data[p_index].item_list[5].count += 5;
				}
				else 		//More weapons
				{
					JOptionPane.showMessageDialog(null,
							"What great fortune! You managed to scour the whole camp for lots of supplies!\n"
							+ "This camp was well stocked with food, water, and plenty of weapons! You also find some basic first aid\n"
							+ "You found 3 Food, 4 Water, 1 Medicine, 2 Knives, 1 Gun, and 15 Ammuntion!");
					this.player_data[p_index].item_list[0].count += 4;
					this.player_data[p_index].item_list[1].count += 3;
					this.player_data[p_index].item_list[2].count += 3;
					this.player_data[p_index].item_list[3].count += 2;
					this.player_data[p_index].item_list[4].count += 1;
					this.player_data[p_index].item_list[5].count += 15;
				}
			}
		}
	}
	
	/*Method that is called when the bandit event triggers during a foraging session
	 * Gives players options in dealing with the bandits
	 * Soldiers and musician get a special bonus depending on the option taken
	 */
	public void banditEventMethod()
	{
		int choice = 0;
		Random random_gen;
		long seed = System.currentTimeMillis();
		int event = 0;
		
		random_gen = new Random(seed);
		
		
		Object[] options = {"Fight", "Bargain", "Give up supplies"};
		choice = JOptionPane.showOptionDialog(null, "You are surrounded by bandits! What do you do?\n",
				"EVENT",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null,
				options,
				options[2]);
		
		if(choice == 0)
		{
			event = random_gen.nextInt();
			
			if(this.player_data[p_index].specialty == 1)
			{
				event += 25; 		//Soldier buff
			}
			
			if(this.player_data[p_index].item_list[3].count > 0) 			//Check for knife
			{
				event += 5;
				if(this.player_data[p_index].specialty == 1)
				{
					event += 10; 		//Soldier buff
				}
			}
			if(this.player_data[p_index].item_list[4].count > 0 && this.player_data[p_index].item_list[5].count > 1) //Check for gun and ammo
			{
				event += 10;
				this.player_data[p_index].item_list[5].count -= 5;
				if(this.player_data[p_index].specialty == 1)
				{
					event += 20; 		//Soldier buff
				}
			}
			
			if(event < 75) 	//75% failure chance without being a soldier and no weapons
			{
				JOptionPane.showMessageDialog(null, "The bandits beat you thoroughly\n"
						+ "You lost 40 health and any supplies on you");
				this.player_data[p_index].health -= 40;
				this.player_data[p_index].item_list[0].count /= 2;
				this.player_data[p_index].item_list[1].count /= 2;
				this.player_data[p_index].item_list[2].count = 2;
				this.player_data[p_index].item_list[3].count = 0;
				this.player_data[p_index].item_list[4].count = 0;
				this.player_data[p_index].item_list[5].count = 0;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "You were slightly injured but you soundly defeat them and get their supplies\n"
						+ "You gained 6 food, 6 water, 3 medicine, 2 knives, 2 guns, 15 ammo");
				this.player_data[p_index].health -= 10;
				this.player_data[p_index].item_list[0].count += 6;
				this.player_data[p_index].item_list[1].count += 6;
				this.player_data[p_index].item_list[2].count += 3;
				this.player_data[p_index].item_list[3].count += 2;
				this.player_data[p_index].item_list[4].count += 2;
				this.player_data[p_index].item_list[5].count += 15;
			}
		}
		else if(choice == 1)
		{
			event = random_gen.nextInt();
			
			if(this.player_data[p_index].sanity == 3) 		//musician check
			{
				event += 65;
			}
			
			if(event < 75)
			{
				JOptionPane.showMessageDialog(null, "The bandits could not care less about your words.\n"
						+ "They beat you soundly and take your supplies\n");
				this.player_data[p_index].health -= 40;
				this.player_data[p_index].item_list[0].count /= 2;
				this.player_data[p_index].item_list[1].count /= 2;
				this.player_data[p_index].item_list[2].count = 2;
				this.player_data[p_index].item_list[3].count = 0;
				this.player_data[p_index].item_list[4].count = 0;
				this.player_data[p_index].item_list[5].count = 0;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "You manage to bargain with them to give half your food and water\n");
				this.player_data[p_index].item_list[0].count /= 2;
				this.player_data[p_index].item_list[1].count /= 2;
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "You give up your food, water, and medicine. \n"
					+ "The bandits take your supplies and leave you be");
			this.player_data[p_index].item_list[0].count /= 2;
			this.player_data[p_index].item_list[1].count /= 2;
			this.player_data[p_index].item_list[2].count = 0;
		}
	}

	/*Method that is called when the merchant event triggers during a foraging session
	 * Gives medic and soldier bonuses with selecting certain options
	 */
	public void merchantEventMethod()
	{
		int choice = 0, 		//Selection for dealing with merchants
			bargain_index, 		//Index that holds the value of the trading options
			event = 0;			//Holds the random integer to determine random events
		Random random_gen; 
		long seed = System.currentTimeMillis(); 		//Set seed as time of computer
		
		random_gen = new Random(seed);
		
		Object[] options = {"Steal", "Trade", "Aid"};
		event = random_gen.nextInt();
		
		choice = JOptionPane.showOptionDialog(null, "You meet some traveling merchants along the road", 
				"EVENT",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null,
				options,
				options[0]);
		
		if(choice == 0) 			//Steal from the merchants
		{
			event = random_gen.nextInt();
			
			if(this.player_data[p_index].specialty == 1)
			{
				event += 25; 		//Soldier buff
			}
			
			if(this.player_data[p_index].item_list[3].count > 0) 			//Check for knife
			{
				event += 5;
				if(this.player_data[p_index].specialty == 1)
				{
					event += 10; 		//Soldier buff
				}
			}
			if(this.player_data[p_index].item_list[4].count > 0 && this.player_data[p_index].item_list[5].count > 5) //Check for gun and ammo
			{
				event += 10;
				this.player_data[p_index].item_list[5].count -= 5;
				if(this.player_data[p_index].specialty == 1)
				{
					event += 20; 		//Soldier buff
				}
			}
			
			if(event < 65) 		//Fail to kill merchants
			{
				JOptionPane.showMessageDialog(null, "Your assault fails and the merchants run away, leaving you wounded on the road \n"
						+ "You lost 40 health!");
				this.player_data[p_index].health -= 40;
			}
			else 			//Kill the merchants
			{
				JOptionPane.showMessageDialog(null, "You catch the merchants off guard in your assault.\n"
						+ "You kill them and take their goods\n"
						+ "You gained 8 food, 8 water, and 4 medicine!");
				this.player_data[p_index].item_list[0].count += 8;
				this.player_data[p_index].item_list[1].count += 8;
				this.player_data[p_index].item_list[2].count += 4;	
			}
		}
		
		else if(choice == 1) 		//Bargain with the merchants
		{
			Object[] bargain_list = {"1. Food x1 -> Water x1", "2. Water x1 -> Food x1", "3. Food x5 -> Water x6", "4. Water x5 -> Food x6",
					"5. Food x5 -> 1 Knife", "6. Food x6 -> Medicine x2", "7. Water x6 -> Medicine x2",
					"8. Water x8 ->  Gun x1", "9. Medicine x1 -> Ammunition x15"};
			String trade_choice;
			boolean item_check = false; 	//Flag that triggers if the player has those items to trade
			
			do
			{
				trade_choice = (String) JOptionPane.showInputDialog(null,
						"What will you trade with the merchants?",
						"TRADE OPTIONS",
						JOptionPane.PLAIN_MESSAGE,
						null,
						bargain_list,
						bargain_list[0]);
				
				bargain_index = Integer.parseInt(trade_choice.substring(0,1));
				
				switch(bargain_index)
				{
					case 1:			//Food x1 -> Water x1
						if(this.player_data[p_index].item_list[0].count > 0)
						{
							this.player_data[p_index].item_list[0].count--;
							this.player_data[p_index].item_list[1].count++;
							item_check = true;
						}
						break;
					case 2: 		//Water x1 -> Food x1
						if(this.player_data[p_index].item_list[1].count > 0)
						{
							this.player_data[p_index].item_list[1].count--;
							this.player_data[p_index].item_list[0].count++;
							item_check = true;
						}
						break;
					case 3: 		//Food x5 -> Water x6
						if(this.player_data[p_index].item_list[0].count > 4)
						{
							this.player_data[p_index].item_list[0].count -= 5;
							this.player_data[p_index].item_list[1].count += 6;
							item_check = true;
						}
						break;
					case 4: 		//Water x5 -> Food x6
						if(this.player_data[p_index].item_list[1].count > 4)
						{
							this.player_data[p_index].item_list[1].count -= 5;
							this.player_data[p_index].item_list[0].count += 6;
							item_check = true;
						}
						break;
					case 5: 		//Food x5 -> Knife x1
						this.player_data[p_index].item_list[0].count -= 5;
						this.player_data[p_index].item_list[3].count++;
						break;
					case 6:			//Food x6 -> Medicine x2
						this.player_data[p_index].item_list[0].count -= 5;
						this.player_data[p_index].item_list[2].count += 2;
						break;
					case 7:			//Water x6 -> Medicine x2
						this.player_data[p_index].item_list[1].count -= 6;
						this.player_data[p_index].item_list[2].count += 2;
						break;
					case 8: 		//Water x8 -> Gun x1
						this.player_data[p_index].item_list[1].count -= 8;
						this.player_data[p_index].item_list[4].count++;
						break;
					case 9: 		//Medicine x1 -> Ammunition x15
						this.player_data[p_index].item_list[2].count--;
						this.player_data[p_index].item_list[5].count += 15;
						break;
					default:	
				}
			}while(!item_check);
		}
		
		else 		//Help the merchants
		{
			if(this.player_data[p_index].specialty == 0)
			{
				JOptionPane.showMessageDialog(null, "You decide to offer your services to the merchants for some payment\n"
						+ "You tell them that you are a medical professional and they\n"
						+ "immediately take you to an injured man on the back of their cart\n"
						+ "They thank you for your help and you are rewarded well for your work\n"
						+ "You gained 3 food, 3 water, and 1 medicine!");
				this.player_data[p_index].item_list[0].count += 3;
				this.player_data[p_index].item_list[1].count += 3;
				this.player_data[p_index].item_list[2].count ++;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "You didn't have anything special to offer to the merchants\n"
						+ "They still very much appreciated having an extra pair of hand arounds for travel and protection\n"
						+ "You gained 2 food and 1 water!");
				this.player_data[p_index].item_list[0].count += 2;
				this.player_data[p_index].item_list[1].count ++;
			}
		}
	}
	
	/*Method that is called at the beginning or end of every turn
	 * Checks for the current day to see if the game is over or not
	 * Checks for the player_stats to see if they have any low stats or have health = 0
	 */
	public int gameStateCheck()
	{
		if(global_data.date == global_data.end_date) 			//Check if the end game ended
		{
			JOptionPane.showMessageDialog(null, "You have survived the apocalypse!");
			if (p_index == 3) {
				end_game(0);
			}

			return 2;
		}
		
		if(player_data[p_index].health < 5) 		//Player has run out of health
		{
			JOptionPane.showMessageDialog(null, "You have died");
			return 1;
		}
		else				//Other checks for the player
		{
			if(player_data[p_index].health < 50) 		//Below half health
			{
				JOptionPane.showMessageDialog(null, "You are low on health\n");
			}
			if(player_data[p_index].hunger < 50) 		//Below half hunger
			{
				JOptionPane.showMessageDialog(null, "You are feeling very hungry\n");
			}
			if(player_data[p_index].hydration < 50) 	//Below half thirst
			{
				JOptionPane.showMessageDialog(null, "You are feeling very thirsty\n");
			}
			if(player_data[p_index].sanity < 50) 		//Below half sanity
			{
				JOptionPane.showMessageDialog(null, "You are feeling unwell\n");
			}
			if(player_data[p_index].sanity > 100) 		//Sanity went over 100
			{
				player_data[p_index].sanity = 100;
			}
			if(player_data[p_index].health > 100) 			//Health over 100
			{
				player_data[p_index].health = 100;
			}
			return 0;
		}
	}
	
	/*****************
	 * Natural Event Methods: Random Occurrences that affect all players
	 ********************************/
	// Simple method to check to choose which natural event should occur
	void randNaturalEventGenerator() {

		Random rand = new Random();
		int event = rand.nextInt(3);

		switch (event) {
		case 0:
			earthquake();
			break;
		case 1:
			drought();
			break;
		case 2:
			asteroidStrike();
			break;
		default:
			earthquake();
			break;

		}

	}

	private void setBackgroundApocaImg() {

		try {

			// Be aware of image path -- will not work on other computers
			File myImg = new File("apocalypse_by_pierremassine.png");
			img = ImageIO.read(myImg);
		} catch (Exception e) {
			e.printStackTrace();
		}

		imgIcon = new ImageIcon(img);
		if (background == null) {
			background = new JLabel(imgIcon);
			this.setContentPane(background);
		} else {
			background.setIcon(imgIcon);
		}

		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);

	}
	// TODO: IMPLEMENT THESE RANDOM METHODS
	public void earthquake() {

		// adjust fields for each player here
		for (int i = 0; i < this.global_data.player_count; i++) {
			this.player_data[i].health -= 12;
			this.player_data[i].hunger -= 5;
			this.player_data[i].sanity -= 10;

		}

		try {

			// Be aware of image path -- will not work on other computers
			File myImg = new File("earthquake.png");
			img = ImageIO.read(myImg);
		} catch (Exception e) {
			e.printStackTrace();
		}

		imgIcon = new ImageIcon(img);
		background.setIcon(imgIcon);
		this.validate();
		this.repaint();
		this.pack();
        this.setLocationRelativeTo(null);
		this.setVisible(true);

		JOptionPane.showMessageDialog(null, "The surrounding area experienced an earthquake!\n");

	}

	public void drought() {

		// adjust fields for each player here
		for (int i = 0; i < this.global_data.player_count; i++) {
			this.player_data[i].health -= 2;
			this.player_data[i].hunger -= 5;
			this.player_data[i].sanity -= 2;
			this.player_data[i].hydration -= 12;
		}

		try {

			// Be aware of image path -- will not work on other computers
			File myImg = new File("drought.png");
			img = ImageIO.read(myImg);
		} catch (Exception e) {
			e.printStackTrace();
		}

		imgIcon = new ImageIcon(img);
		background.setIcon(imgIcon);
		this.validate();
		this.repaint();
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		JOptionPane.showMessageDialog(null, "There was a drought that recently occurred!\n");

	}

	public void asteroidStrike() {

		// adjust fields for each player here
		for (int i = 0; i < this.global_data.player_count; i++) {
			this.player_data[i].health -= 20;
			this.player_data[i].hunger -= 5;
		}

		try {

			// Be aware of image path -- will not work on other computers
			File myImg = new File("asteroidStrike.png");
			img = ImageIO.read(myImg);
		} catch (Exception e) {
			e.printStackTrace();
		}

		imgIcon = new ImageIcon(img);
		background.setIcon(imgIcon);
		this.validate();
		this.repaint();
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		JOptionPane.showMessageDialog(null, "Asteroids have broke the atmosphere and impacted your location!\n");

	}
	
	public void end_game(int status) { //return 0 for successful game and 1 for failed game
		dispose();
		System.exit(status);
	}

	public static void main(String[] args)
	{
		//Initializing all data for game to begin
		Item[] list = new Item[6];
		Resources[] player_stats = new Resources[4];
		long seed = System.currentTimeMillis();
		Random random_gen = new Random(seed);
		int specialty_gen = 0;

		for (int i = 0; i < 4; i++)
		{
			specialty_gen = random_gen.nextInt(4);
			player_stats[i] = new Resources(100, 100, 100, 100, specialty_gen, list);
		}

		WorldData data = new WorldData();
		data.date = 1;
		data.end_date = 10;
		data.timer = 60;
		data.player_count = 4;
		
		new Game(player_stats, data).setVisible(true);
	}
}