//доработка v.1
import java.util.*;

public class Main {
   public static final HashMap<Integer, Integer> sizeToFreq = new HashMap<>();
   public static final String ALT = "\u001b[34;1m";
   public static final String RESET = "\u001B[0m";
   private static int keyOfMax = 0;

   public static void main(String[] args) throws InterruptedException {

      Runnable pathGenerator_RCounter = () -> {
         String route = generateRoute("RLRFR", 100);
         System.out.println(route);
         int r = countR(route);
         System.out.println(ALT + "Кол-во R: " + RESET + r);

         synchronized (sizeToFreq) {
            if (sizeToFreq.containsKey(r)) {
               sizeToFreq.put(r, sizeToFreq.get(r) + 1);
            } else {
               sizeToFreq.put(r, 1);
            }
            sizeToFreq.notify();
         }
      };

      Runnable mapMaxSeeker = () -> {
         while (!Thread.interrupted()) {
            int maxValue = 0;
            synchronized (sizeToFreq) {
               try {
                  sizeToFreq.wait();     // InterruptedException here sometimes
               } catch (InterruptedException e) {
                  throw new RuntimeException(e);
               }
               Iterator<Map.Entry<Integer, Integer>> iterator = sizeToFreq.entrySet().iterator();
               while (iterator.hasNext()) {
                  Map.Entry<Integer, Integer> entry = iterator.next();
                  if (entry.getValue() > maxValue) {
                     maxValue = entry.getValue();
                     keyOfMax = entry.getKey();
                  }
               }

            }
            System.out.printf("\n" + ALT + "Самое частое количество повторений: "
                    + RESET + "%d (встретилось %d раз)\n", keyOfMax, maxValue);
         }
      };

      Thread mapMax = new Thread(mapMaxSeeker);
      mapMax.start();

      List<Thread> pathThreads = new ArrayList<>();
      for (int i = 0; i < 1000; i++) {
         Thread path = new Thread(pathGenerator_RCounter);
         pathThreads.add(path);
      }
      for (Thread path : pathThreads) {
         path.start();
      }
      for (Thread path : pathThreads) {
         path.join();
      }

      mapMax.interrupt();
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
      System.out.println("Другие размеры:");
      Iterator<Map.Entry<Integer, Integer>> iterator2 = map.entrySet().iterator();
      while (iterator2.hasNext()) {
         Map.Entry<Integer, Integer> entry = iterator2.next();
         if (entry.getKey() == keyOfMax) {
            iterator2.next();   //NoSuchElementException here seldom
         } else {
            System.out.printf("- %d (%d раз)\n", entry.getKey(), entry.getValue());
         }
      }
   }
}