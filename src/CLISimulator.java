import java.util.Scanner;

/**
* Manages command line interface simulation to test the map CRDT.
**/
public class CLISimulator
{
   /**
   * Scanner variable used to get input from the user.
   **/
   protected Scanner keyboard;

   /**
   * Number of replicas used for the current simulation.
   **/
   protected int nReplicas;

   /**
   * Default constructor.
   **/
   public CLISimulator()
   {
      this.keyboard = new Scanner(System.in);
      this.nReplicas = 0;
   }

   /**
   * Main function.
   **/
   public static void main(String args[])
   {
      CLISimulator simulator = new CLISimulator();
      simulator.start();
   }

   /**
   * Prints the help menu containing the different valid commands.
   **/
   protected void printHelp()
   {
      String helpMenu = "LIST OF COMMANDS:\n";
      helpMenu += "  * put <replica_id> <key> <value>\n";
      helpMenu += "  * get <replica_id> <key>\n";
      helpMenu += "  * del <replica_id> <key>\n";
      helpMenu += "  * merge <from_replica_id> <to_replica_id>\n";
      helpMenu += "  * dump <replica_id>\n";
      helpMenu += "  * help\n";
      helpMenu += "  * quit";

      System.out.println(helpMenu);
   }

   /**
   * Prints the welcome menu and introduction to the simulation.
   **/
   protected void printWelcomeMenu()
   {
      String welcomeMenu = "\nWelcome into the map CRDT command line interface. This simulator";
      welcomeMenu += " allows you to test different scenarios while manipulating replicas of a map";
      welcomeMenu += " CRDT object.\n\n";

      System.out.println(welcomeMenu);
   }

   /**
   * Launches and manages the simulation.
   **/
   public void start()
   {
      this.printWelcomeMenu();

      // We get here the number of replicas.
      boolean retry;
      do
      {
         retry = false;

         System.out.println("How many replicas do you want to use for this simulation?");
         System.out.print(">>> ");
         String nReplicasStr = this.keyboard.nextLine();
         try
         {
            this.nReplicas = Integer.parseInt(nReplicasStr);
         }
         catch (NumberFormatException e)
         {
            retry = true;
         }
      }
      while (retry);

      // Replicas are created.
      MapCrdt replicas[] = new MapCrdt[this.nReplicas];
      for (int i = 0; i < this.nReplicas; i++)
      {
         replicas[i] = new MapCrdt();
      }

      System.out.println("\nSimulation begins with " + this.nReplicas + " replicas.");
      this.printHelp();

      // Commands are treated until the 'QUIT' command is used.
      boolean continueSimulation = true;
      do
      {
         System.out.print(">>> ");
         String command = this.keyboard.nextLine();

         String[] params = command.split(" ");
         String action = params[0].toUpperCase();

         boolean isCommandValid = true;
         int replicaId;

         try
         {
            switch(action)
            {
               case "PUT":
                  if (params.length != 4)
                  {
                     isCommandValid = false;
                     break;
                  }
   
                  replicaId = Integer.parseInt(params[1]);
   
                  replicas[replicaId - 1].put(params[2], params[3]);
   
                  break;
   
               case "DEL":
               case "DELETE":
                  if (params.length != 3)
                  {
                     isCommandValid = false;
                     break;
                  }
   
                  replicaId = Integer.parseInt(params[1]);
   
                  replicas[replicaId - 1].delete(params[2]);
   
                  break;
   
               case "GET":
                  if (params.length != 3)
                  {
                     isCommandValid = false;
                     break;
                  }
   
                  replicaId = Integer.parseInt(params[1]);
   
                  System.out.println(replicas[replicaId - 1].get(params[2]));
   
                  break;
   
               case "MERGE":
                  if (params.length != 3)
                  {
                     isCommandValid = false;
                     break;
                  }
   
                  int fromId = Integer.parseInt(params[1]);
                  replicaId = Integer.parseInt(params[2]);
   
                  replicas[replicaId - 1].merge(replicas[fromId - 1]);
   
                  break;
   
               case "DUMP":
                  if (params.length != 2)
                  {
                     isCommandValid = false;
                     break;
                  }
   
                  replicaId = Integer.parseInt(params[1]);
   
                  System.out.println(replicas[replicaId - 1]);
   
                  break;
   
               case "QUIT":
               case "Q":
                  if (params.length != 1)
                  {
                     isCommandValid = false;
                     break;
                  }
   
                  continueSimulation = false;
   
                  break;
   
               case "HELP":
               case "H":
                  if (params.length != 1)
                  {
                     isCommandValid = false;
                     break;
                  }
   
                  this.printHelp();
   
                  break;
   
               default:
                  isCommandValid = false;
            }
         }
         catch (NumberFormatException e)
         {
            isCommandValid = false;
         }
         catch (ArrayIndexOutOfBoundsException e)
         {
            System.out.println("Replicas' id should be between 1 and " + this.nReplicas + ".");
         }

         if (isCommandValid == false)
         {
            System.out.println("Wrong command...");
            this.printHelp();
         }
      }
      while (continueSimulation);
   }
}
