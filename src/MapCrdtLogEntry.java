import java.sql.Timestamp;

/**
* Represents a log entry used into map CRDT replicas' logs.
**/
public class MapCrdtLogEntry
{
   /**
   * The unique id of the replica responsible for the operation.
   **/
   public int replicaId;
   
   /**
   * The timestamp at which the operation has been initiated.
   **/
   public Timestamp time;

   /**
   * The type of the operation, can be 'PUT' or 'DELETE'.
   **/
   public String type;

   /**
   * The value associated to the operation, is null if operation is a 'DELETE'.
   **/
   public String value;

   /**
   * Default constructor.
   * @param replicaId the id of the replica.
   * @param time the timestamp corrsponding to the current operation.
   * @param type the type of the opeartion: 'PUT' or 'DELETE'.
   * @param value the value associated to the operation.
   **/
   public MapCrdtLogEntry(int replicaId, Timestamp time, String type, String value)
   {
      this.replicaId = replicaId;
      this.time = time;
      this.type = type;
      this.value = value;
   }

   /**
   * Copy constructor.
   * @param other the MapCrdtLogEntry instance that should be copied.
   **/
   public MapCrdtLogEntry(MapCrdtLogEntry other)
   {
      this.replicaId = other.replicaId;
      this.time = other.time;
      this.type = other.type;
      this.value = other.value;
   }

   /**
   * Compares two operations in order to know which one should be kept.
   * @param other the other operation compared to 'this' operation.
   * @return a positive integer if 'this' should be kept, a negative integer if the other operation
   * should be kept, and 0 if both operation are equals.
   **/
   public int compareTo(MapCrdtLogEntry other)
   {
      // First we compare timestamp
      int compTime = this.time.compareTo(other.time);

      if (compTime != 0)
      {
         return compTime;
      }
      
      // If both timestamp are equals we compare replicas' id
      if (this.replicaId != other.replicaId)
      {
         return - (this.replicaId - other.replicaId);
      }

      // If both replicas' id are equals 'DELETE' wins because it deleles the 'PUT'.
      return - this.type.compareTo(other.type);
   }

   /**
   * Creates a string containing information related to this log entry.
   * @return a string containing log entry's information.
   **/
   public String toString()
   {
      return "(" + this.replicaId + ", " + this.time + ", " + this.type + ", " + this.value + ")";
   }
}
