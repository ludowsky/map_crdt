import java.sql.Timestamp;
import java.util.HashSet;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
* Represents a suite test for map CRDT.
**/
public class MapCrdtTest
{
   /**
   * Default constructor. 
   **/
   public MapCrdtTest()
   {
   }

   /**
   * Delays the test to ensure total order.
   * @param millisec number of milliseconds test will sleep.
   **/
   protected void delay(int millisec)
   {
      try
      {
         Thread.sleep(millisec);
      }
      catch (InterruptedException e)
      {
      }
   }

   /**
   * This test initializes two MapCrdtLogEntry with different timestamps and compares them.
   **/
   @Test
   public void testLogEntryComparatorTime()
   {  
      Timestamp time1 = new Timestamp(System.currentTimeMillis());
      MapCrdtLogEntry log1 = new MapCrdtLogEntry(1, time1, "PUT", "VAL");
      this.delay(1);
      Timestamp time2 = new Timestamp(System.currentTimeMillis());
      MapCrdtLogEntry log2 = new MapCrdtLogEntry(1, time2, "PUT", "VAL");

      assertTrue(log1.compareTo(log2) < 0);
      assertTrue(log2.compareTo(log1) > 0);
   }

   /**
   * This test initializes two MapCrdtLogEntry with the same timestamp but different replicas'id
   * and compares them.
   **/
   @Test
   public void testLogEntryComparatorId()
   {  
      Timestamp time = new Timestamp(System.currentTimeMillis());
      MapCrdtLogEntry log1 = new MapCrdtLogEntry(1, time, "PUT", "VAL");
      MapCrdtLogEntry log2 = new MapCrdtLogEntry(2, time, "PUT", "VAL");

      assertTrue(log1.compareTo(log2) > 0);
      assertTrue(log2.compareTo(log1) < 0);
   }

   /**
   * This test initializes two MapCrdtLogEntry with the same timestamp and same replica's id but
   * different operation types (one 'PUT' and one 'DELETE') and compares them.
   **/
   @Test
   public void testLogEntryComparatorType()
   {  
      Timestamp time = new Timestamp(System.currentTimeMillis());
      MapCrdtLogEntry log1 = new MapCrdtLogEntry(1, time, "DELETE", "VAL");
      MapCrdtLogEntry log2 = new MapCrdtLogEntry(1, time, "PUT", "VAL");

      assertTrue(log1.compareTo(log2) > 0);
      assertTrue(log2.compareTo(log1) < 0);
   }

   /**
   * This test creates one hundred replicas' and checks that they have distinct ids.
   **/
   @Test
   public void testReplicaIdUniqueness()
   {
      int nReplicas = 100;
      MapCrdt[] replicas = new MapCrdt[nReplicas];
      HashSet<Integer> setReplicaIds = new HashSet<Integer>();
      
      for (int i = 0; i < nReplicas; i++)
      {
         replicas[i] = new MapCrdt();
         setReplicaIds.add(replicas[i].getReplicaId());
      }

      assertEquals(nReplicas, setReplicaIds.size());
   }

   /**
   * This test evaluates the scenario: Get.
   * <pre>
   *         Get
   * ---------x------------&#62;
   * </pre>
   * Call to Get should return null.
   **/
   @Test
   public void testEmptyGet()
   {
      MapCrdt replica = new MapCrdt();
      String key = "KEY";

      assertNull(replica.get(key));
   }
   
   /**
   * This test evaluates the scenario: Put Get.
   * <pre>
   *     Put      Get
   * -----x--------x----------&#62;
   * </pre>
   * Call to Get should return the value set by the 'PUT'.
   **/
   @Test
   public void testPutGet()
   {
      MapCrdt replica = new MapCrdt();
      String key = "KEY";
      String value = "VALUE";

      replica.put(key, value);

      assertEquals(value, replica.get(key));
   }

   /**
   * This test evaluates the scenario: Put Del Get.
   * <pre>
   *     Put    Del      Get
   * -----x------x--------x-------&#62;
   * </pre>
   * Call to Get should return null.
   **/
   @Test
   public void testPutDelGet()
   {
      MapCrdt replica = new MapCrdt();
      String key = "KEY";
      String value = "AAAA";

      replica.put(key, value);
      replica.delete(key);

      assertNull(replica.get(key));
   }

   /**
   * This test evaluates the scenario: Del Get.
   * <pre>
   *           Del      Get
   * -----------x--------x-------&#62;
   * </pre>
   * Call to Get should return null.
   **/
   @Test
   public void testDelGet()
   {
      MapCrdt replica = new MapCrdt();
      String key = "KEY";

      replica.delete(key);

      assertNull(replica.get(key));
   }
   
   /**
   * This test evaluates the scenario: Put Put Get.
   * <pre>
   *     Put    Put      Get
   * -----x------x--------x-------&#62;
   * </pre>
   * Call to Get should return the value set by the second 'PUT'.
   **/
   @Test
   public void testPutPutGet()
   {
      MapCrdt replica = new MapCrdt();
      String key = "KEY";
      String value1 = "AAAA";
      String value2 = "VAL2";

      replica.put(key, value1);
      replica.put(key, value2);

      assertEquals(value2, replica.get(key));
   }
   
   /**
   * This test evaluates the scenario: Put Del Del Get.
   * <pre>
   *     Put    Del    Del   Get
   * -----x------x------x-----x-------&#62;
   * </pre>
   * Call to Get should return null.
   **/
   @Test
   public void testPutDelDelGet()
   {
      MapCrdt replica = new MapCrdt();
      String key = "KEY";
      String value = "AAAA";

      replica.put(key, value);
      replica.delete(key);
      replica.delete(key);

      assertNull(replica.get(key));
   }
   
   /**
   * This test evaluates the scenario: Put Put Del Get.
   * <pre>
   *     Put    Put    Del   Get
   * -----x------x------x-----x-------&#62;
   * </pre>
   * Call to Get should return null.
   **/
   @Test
   public void testPutPutDelGet()
   {
      MapCrdt replica = new MapCrdt();
      String key = "KEY";
      String value1 = "KDODSi";
      String value2 = "DJODJEOD";

      replica.put(key, value1);
      replica.put(key, value2);
      replica.delete(key);

      assertNull(replica.get(key));
   }

   /**
   * This test evaluates the scenario: Put || Merge Get.
   * <pre>
   *        Put
   * r1 -----x--------------------&#62;
   *              | Merge
   *              v
   * r2 -----------------x--------&#62;
   *                    Get
   * </pre>
   * Call to Get should return the value set by the 'PUT' registered in replica r1.
   **/
   @Test
   public void testPut_MergeGet()
   {
      MapCrdt replica1 = new MapCrdt();
      MapCrdt replica2 = new MapCrdt();
      String key = "KEY";
      String value = "VALUE";

      replica1.put(key, value);
      replica2.merge(replica1);

      assertEquals(value, replica2.get(key));
   }
   
   /**
   * This test evaluates the scenario: Put || Merge PutLWW Get.
   * <pre>
   *        Put
   * r1 -----x---------------------------&#62;
   *            | Merge
   *            v
   * r2 -------------x---------x---------&#62;
   *                Put       Get
   * </pre>
   * Call to Get should return the value set by the 'PUT' registered in replica r2.
   **/
   @Test
   public void testPut_MergePutLWWGet()
   {
      MapCrdt replica1 = new MapCrdt();
      MapCrdt replica2 = new MapCrdt();
      String key = "KEY";
      String value1 = "VALUE1";
      String value2 = "VALUE2!!";

      replica1.put(key, value1);
      replica2.merge(replica1);
      this.delay(1);
      replica2.put(key, value2);
      
      assertEquals(value2, replica2.get(key));
   }

   /**
   * This test evaluates the scenario: Put || PutLWW Merge Get.
   * <pre>
   *        Put
   * r1 -----x----------------------&#62;
   *                | Merge
   *                v
   * r2 --------x---------x---------&#62;
   *            Put       Get
   * </pre>
   * Call to Get should return the value set by the 'PUT' registered in replica r2.
   **/
   @Test
   public void testPut_PutLWWMergeGet()
   {
      MapCrdt replica1 = new MapCrdt();
      MapCrdt replica2 = new MapCrdt();
      String key = "KEY";
      String value1 = "VALUE1";
      String value2 = "VALUE2!!";

      replica1.put(key, value1);
      this.delay(1);
      replica2.put(key, value2);
      replica2.merge(replica1);
      
      assertEquals(value2, replica2.get(key));
   }

   /**
   * This test evaluates the scenario: PutLWW || Put Merge Get.
   * <pre>
   *            Put
   * r1 ---------x-----------------&#62;
   *                | Merge
   *                v
   * r2 ---- x-------------x-------&#62;
   *        Put           Get
   * </pre>
   * Call to Get should return the value set by the 'PUT' registered in replica r1.
   **/
   @Test
   public void testPutLWW_PutMergeGet()
   {
      MapCrdt replica1 = new MapCrdt();
      MapCrdt replica2 = new MapCrdt();
      String key = "KEY";
      String value1 = "VALUE1";
      String value2 = "VALUE2!!";

      replica2.put(key, value2);
      this.delay(1);
      replica1.put(key, value1);
      replica2.merge(replica1);
      
      assertEquals(value1, replica2.get(key));
   }
   
   /**
   * This test evaluates the scenario: PutLWW Del || Put Merge(before Del) Get.
   * <pre>
   *           Put        Del
   * r1 --------x----------x--------------&#62;
   *                | Merge
   *                v
   * r2 ----x-------------------x---------&#62;
   *       Put                 Get
   * </pre>
   * Call to Get should return the value set by the 'PUT' registered in replica r1.
   **/
   @Test
   public void testPutLWWDel_PutMergeBeforeDelGet()
   {
      MapCrdt replica1 = new MapCrdt();
      MapCrdt replica2 = new MapCrdt();
      String key = "KEY";
      String value1 = "VALUE1";
      String value2 = "VALUE2!!";

      replica2.put(key, value2);
      this.delay(1);
      replica1.put(key, value1);
      replica2.merge(replica1);
      replica1.delete(key);
      
      assertEquals(value1, replica2.get(key));
   }
   
   /**
   * This test evaluates the scenario: PutLWW Del || Put Merge(after Del) Get.
   * <pre>
   *           Put     Del
   * r1 --------x-------x----------------&#62;
   *                        | Merge
   *                        v
   * r2 ----x-------------------x--------&#62;
   *       Put                 Get
   * </pre>
   * Call to Get should return null.
   **/
   @Test
   public void testPutLWWDel_PutMergeAfterDelGet()
   {
      MapCrdt replica1 = new MapCrdt();
      MapCrdt replica2 = new MapCrdt();
      String key = "KEY";
      String value1 = "VALUE1";
      String value2 = "VALUE2!!";

      replica2.put(key, value2);
      this.delay(1);
      replica1.put(key, value1);
      replica1.delete(key);
      replica2.merge(replica1);
      
      assertNull(replica2.get(key));
   }

   /**
   * This test evaluates the scenario: Put Del || PutLWW Merge(before Del) Get.
   * <pre>
   *       Put            Del
   * r1 ----x--------------x---------&#62;
   *                | Merge
   *                v
   * r2 -------x------------x--------&#62;
   *          Put          Get
   * </pre>
   * Call to Get should return the value set by the 'PUT' registered in replica r2.
   **/
   @Test
   public void testPutDel_PutLWWMergeBeforeDelGet()
   {
      MapCrdt replica1 = new MapCrdt();
      MapCrdt replica2 = new MapCrdt();
      String key = "KEY";
      String value1 = "VALUE1";
      String value2 = "VALUE2!!";

      replica1.put(key, value1);
      this.delay(1);
      replica2.put(key, value2);
      replica2.merge(replica1);
      replica1.delete(key);
      
      assertEquals(value2, replica2.get(key));
   }
   
   /**
   * This test evaluates the scenario: Put Del || PutLWW Merge(after Del) Get.
   * <pre>
   *       Put    Del
   * r1 ----x------x-----------------&#62;
   *                    | Merge
   *                    v
   * r2 -------x------------x--------&#62;
   *          Put          Get
   * </pre>
   * Call to Get should return the value set by the 'PUT' registered in replica r2.
   **/
   @Test
   public void testPutDel_PutLWWMergeAfterDelGet()
   {
      MapCrdt replica1 = new MapCrdt();
      MapCrdt replica2 = new MapCrdt();
      String key = "KEY";
      String value1 = "VALUE1";
      String value2 = "VALUE2!!";

      replica1.put(key, value1);
      this.delay(1);
      replica2.put(key, value2);
      replica1.delete(key);
      replica2.merge(replica1);
      
      assertEquals(value2, replica2.get(key));
   }

   /**
   * This test evaluates the scenario: Put || PutLWW || Merge(from r1) Del Merge(from r2) Get.
   * <pre>
   *       Put
   * r1 ----x--------------------------------&#62;
   *            | Merge
   *            |
   * r2 --------+-----x----------------------&#62;
   *            |    Put   | Merge
   *            v          v
   * r3 ---------------x------------x--------&#62;
   *                  Del          Get
   * </pre>
   * Call to Get should return the value set by the 'PUT' registered in replica r2.
   **/
   @Test
   public void testPut_PutLWW_Merge1DelMerge2Get()
   {
      MapCrdt replica1 = new MapCrdt();
      MapCrdt replica2 = new MapCrdt();
      MapCrdt replica3 = new MapCrdt();
      String key = "KEY";
      String value1 = "VALUE1";
      String value2 = "VALUE2!!";

      replica1.put(key, value1);
      this.delay(1);
      replica2.put(key, value2);
      replica3.merge(replica1);
      replica3.delete(key);
      replica3.merge(replica2);

      assertEquals(value2, replica3.get(key));
   }

   /**
   * This test evaluates the scenario: PutLWW || Put || Merge(from r1) Del Merge(from r2) Get.
   * <pre>
   *          Put
   * r1 -------x-----------------------------&#62;
   *               | Merge
   *               |
   * r2 ---x-------+-------------------------&#62;
   *      Put      |          | Merge
   *               v          v
   * r3 ---------------x------------x--------&#62;
   *                  Del          Get
   * </pre>
   * Call to Get should return null.
   **/
   @Test
   public void testPutLWW_Put_Merge1DelMerge2Get()
   {
      MapCrdt replica1 = new MapCrdt();
      MapCrdt replica2 = new MapCrdt();
      MapCrdt replica3 = new MapCrdt();
      String key = "KEY";
      String value1 = "VALUE1";
      String value2 = "VALUE2!!";

      replica2.put(key, value2);
      this.delay(1);
      replica1.put(key, value1);
      replica3.merge(replica1);
      replica3.delete(key);
      replica3.merge(replica2);

      assertNull(replica3.get(key));
   }
}
