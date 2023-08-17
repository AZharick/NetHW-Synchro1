import java.util.*;

public class Main {
   public static final HashMap<Integer, Integer> sizeToFreq = new HashMap<>();
   public static int stringNumber = 1;
   public static final String ALT = "\u001b[34;1m";
   public static final String RESET = "\u001B[0m";
   public static final String ALT2 = "\u001b[34m";

   public static void main(String[] args) throws InterruptedException {

      Runnable logic = () -> {
         String route = generateRoute("RLRFR", 100);
         System.out.println(ALT + stringNumber + ": " + RESET + route);
         int r = countR(route);
         System.out.println(ALT2 + "Кол-во R: " + RESET + r);

         synchronized (sizeToFreq) {
            if (sizeToFreq.containsKey(r)) {
               sizeToFreq.put(r, sizeToFreq.get(r) + 1);
            } else {
               sizeToFreq.put(r, 1);
            }
         }
         stringNumber++;
      };

      for (int i = 0; i < 1000; i++) {
         Thread thread = new Thread(logic);
         thread.start();
         thread.join();
      }

      displayMapResult(sizeToFreq);
   }

   public static String generateRoute(String letters, int length) {
      Random random = new Random();
      StringBuilder route = new StringBuilder();
      for (int i = 0; i < length; i++) {
         route.append(letters.charAt(random.nextInt(letters.length())));
      }
      return route.toString();
   }

   public static int countR(String string) {
      int count = 0;
      for (int i = 0; i < string.length(); i++) {
         if ('R' == string.charAt(i)) {
            count++;
         }
      }
      return count;
   }

   public static void displayMapResult(HashMap map) {
      //computing max value
      int maxValue = 0;
      int keyOfMax = 0;
      Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
      while (iterator.hasNext()) {
         Map.Entry<Integer, Integer> entry = iterator.next();
         if (entry.getValue() > maxValue) {
            maxValue = entry.getValue();
            keyOfMax = entry.getKey();
         }
      }
      System.out.printf("\nСамое частое количество повторений - %d (встретилось %d раз)\n", keyOfMax, maxValue);
      System.out.println("Другие размеры:");

      Iterator<Map.Entry<Integer, Integer>> iterator2 = map.entrySet().iterator();
      while (iterator2.hasNext()) {
         Map.Entry<Integer, Integer> entry = iterator2.next();
         if (entry.getKey() == keyOfMax) {
            iterator2.next();
         } else {
            System.out.printf("- %d (%d раз)\n", entry.getKey(), entry.getValue());
         }
      }
   }
}