import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
* Represents a state-based map CRDT replica.
**/
public class MapCrdt
{
   /**
   * A hashmap storing logs information relative to the last operation of each key.
   */
   protected HashMap<String, MapCrdtLogEntry> logs;

   /**
   * A unique id for the current replica.
   */
   protected int replicaId;

   /**
   * A global counter to create unique identifiers for replicas.
   **/  
   protected static Integer replicaIdCpt = 1;

   /**
   * Default constructor
   */
   public MapCrdt()
   {
      this.logs = new HashMap<String, MapCrdtLogEntry>();
      
      synchronized(replicaIdCpt)
      {
         this.replicaId = this.replicaIdCpt++;
      }
   }

   /**
   * Gives information about the presence of a given key in the map.
   * @param key the key that should be looked for.
   * @return true if the key is in the logs and last operation is a put, false otherwise.
   **/
   protected synchronized boolean containsKey(String key)
   {
      return this.logs.containsKey(key) == true &&
             this.logs.get(key).type.equals("DELETE") == false;
   }

   /**
   * Deletes a given key if it is present in the logs and has not been yet deleted.
   * @param key the key that should be deleted.
   **/
   public synchronized void delete(String key)
   {
      if (this.containsKey(key) == false)
      {
         return;
      }

      MapCrdtLogEntry info = this.logs.get(key);
      info.type = "DELETE";
      info.value = null;
      this.logs.put(key, info);
   }

   /**
   * Gets the value corresponding to a given key.
   * @param key the key that should be looked for.
   * @return the value associated to the key, or null if the key is not presente in the logs or last
   * operation is a delete.
   **/
   public synchronized String get(String key)
   {
      if (this.containsKey(key) == false)
      {
         return null;
      }

      return this.logs.get(key).value;
   }

   /**
   * Gets the unique identifier of the current replica.
   * @return the unique identifier.
   **/
   public synchronized int getReplicaId()
   {
      return this.replicaId;
   }

   /**
   * Merges informations contained in a given replica into the local replica, the merge is
   * unilateral and only local replica is modified.
   * @param other the other replica of the map that should be merge with the local replica.
   **/
   public synchronized void merge(MapCrdt other)
   {
      for (String key : other.logs.keySet())
      {
         boolean makeUpdate = false;
         MapCrdtLogEntry otherInfo = other.logs.get(key);
         MapCrdtLogEntry localInfo = null;
         
         if (this.logs.containsKey(key) == false)
         {
            makeUpdate = true;
         }
         else
         {
            localInfo = this.logs.get(key);
            
            if (localInfo.compareTo(otherInfo) < 0)
            {
               makeUpdate = true;
            }
         }

         if (makeUpdate == true)
         {
            localInfo = new MapCrdtLogEntry(otherInfo);
            this.logs.put(key, localInfo);
         }
      }
   }

   /**
   * Puts a key value pair into the map.
   * @param key the key that is targeted
   * @param value the value that should be assigned to the key
   **/
   public synchronized void put(String key, String value)
   {
      Timestamp time = new Timestamp(System.currentTimeMillis());
      MapCrdtLogEntry info = new MapCrdtLogEntry(this.replicaId, time, "PUT", value);
      this.logs.put(key, info);
   }

   /**
   * Creates a string containing the logs for the different keys present in this replica.
   * @return a string containing the state of the local replica.
   **/
   public synchronized String toString()
   {
      String string = "LOGS OF REPLICA " + this.replicaId + "\n";

      for (String key : this.logs.keySet())
      {
         string += key + ": " + this.logs.get(key) + "\n";
      }

      return string;
   }
}
