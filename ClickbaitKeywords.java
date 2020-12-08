import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ClickbaitKeywords {
	public static ArrayList<Integer> uniqueItems = new ArrayList<Integer>();
	public static ArrayList<ArrayList<Integer>> itemList = new ArrayList<ArrayList<Integer>>();
	public static HashMap<Integer, Integer> itemSets = new HashMap<Integer, Integer>();
	public static HashMap<HashSet<Integer>, Integer> frequentItemSets = new HashMap<HashSet<Integer>, Integer>();
	public static HashMap<HashSet<Integer>, Integer> candidateItemSets = new HashMap<HashSet<Integer>, Integer>();
	public static HashMap<HashSet<Integer>, Integer> L = new HashMap<HashSet<Integer>, Integer>();
	public static ArrayList<HashSet<Integer>> C = new ArrayList<HashSet<Integer>>();
	public static int transactions = 0;
	public static int supportCount;
	public static long startTime, endTime;

	public static boolean isItemPresent(int val) {
		int i, flag = 0;
		for (i = 0; i < uniqueItems.size(); i++) {
			if (uniqueItems.get(i) == val) {
				flag++;
				break;
			} else {
				flag = 0;
			}
		}
		if (flag > 0)
			return true;
		else
			return false;
	}

	public static void apriori() {
		/*
		 * Now the next step is to calculate count for each ItemSet that is generation
		 * of L1 For storing ItemsSets and key,I have used HashMap with key as HashSet
		 * and value as support 2 HashMaps are used here:candidateItemSets and
		 * frequentItemSets
		 */
		int i = 0, j = 0, k = 0, cnt, setCnt = 1;
		String outputFile = "";
		Scanner sc = new Scanner(System.in);

		/*
		 * Uncomment below lines to take user input
		 * System.out.print("\nEnter output file name:"); outputFile = sc.nextLine();
		 */
		for (i = 0; i < uniqueItems.size(); i++) {
			cnt = 0;
			for (j = 0; j < itemList.size(); j++) {
				cnt += Collections.frequency(itemList.get(j), uniqueItems.get(i));
			}
			HashSet<Integer> candidates = new HashSet<Integer>();
			candidates.add(uniqueItems.get(i));
			candidateItemSets.put(candidates, cnt);
		}

		/*
		 * Now here Set and Iterator are used to extract key and values from HashMap.
		 * Because HashMap has no method to return key.
		 */
		Set set = candidateItemSets.entrySet();
		Iterator iterator = set.iterator();
		/*
		 * Uncomment below lines to see intermediate result of apriori
		 * System.out.println("\n\n==>C1:");
		 * System.out.println("\nItem-Set\t\tSupport-Count");
		 * System.out.println("---------------------------------------");
		 */
		while (iterator.hasNext()) {
			Map.Entry<HashSet<Integer>, Integer> mentry = (Map.Entry<HashSet<Integer>, Integer>) iterator.next();
			// System.out.println(mentry.getKey() + "\t\t\t\t" + mentry.getValue());
			if (mentry.getValue() >= supportCount) {
				frequentItemSets.put(mentry.getKey(), mentry.getValue());
				L.put(mentry.getKey(), mentry.getValue());
			}
		}

		// PRINTING L1
		/*
		 * Uncomment below lines to see intermediate result of apriori
		 * System.out.println("\n\n==>L1:");
		 * System.out.println("\nItem-Set\t\tSupport-Count");
		 * System.out.println("---------------------------------------");
		 */
		set = L.entrySet();
		iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry<HashSet<Integer>, Integer> mentry = (Map.Entry<HashSet<Integer>, Integer>) iterator.next();
			// System.out.println(mentry.getKey() + "\t\t\t\t" + mentry.getValue());
		}
		// PRINTING L1 COMPLETED

		// GENERATION OF FURTHER SETS
		ArrayList<HashSet<Integer>> tempC = new ArrayList<HashSet<Integer>>();
		String format = "%-30s%s%n";
		while (L.size() != 1) {
			++setCnt;
			tempC.clear();
			set = L.entrySet();
			iterator = set.iterator();
			while (iterator.hasNext()) {
				Map.Entry<HashSet<Integer>, Integer> mentry = (Map.Entry<HashSet<Integer>, Integer>) iterator.next();
				tempC.add(mentry.getKey());
			}
			// System.out.println("TEMPC"+tempC);
			int size = L.size();
			L.clear();
			C.clear();
			for (i = 0; i < size; i++) {
				for (j = i + 1; j < size; j++) {
					HashSet<Integer> mergedSet = new HashSet<Integer>();
					mergedSet.addAll(tempC.get(i));
					mergedSet.addAll(tempC.get(j));
					// System.out.println(mergedSet+"\n");
					if (mergedSet.size() == setCnt) {
						C.add(mergedSet);
					}
				}
			}

			candidateItemSets.clear();
			for (i = 0; i < C.size(); i++) {
				cnt = 0;
				for (j = 0; j < itemList.size(); j++) {
					ArrayList<Integer> tempList = new ArrayList<>(C.get(i));
					if (itemList.get(j).containsAll(tempList)) {
						cnt++;
					}
				}
				candidateItemSets.put(C.get(i), cnt);
			}
			/*
			 * Uncomment below lines to see intermediate result of apriori
			 * System.out.println("\n==>C" + setCnt + ":");
			 * System.out.println("\nItem-Set\t\tSupport-Count");
			 * System.out.println("---------------------------------------");
			 */
			L.clear();
			set = candidateItemSets.entrySet();
			iterator = set.iterator();
			while (iterator.hasNext()) {
				Map.Entry<HashSet<Integer>, Integer> mentry = (Map.Entry<HashSet<Integer>, Integer>) iterator.next();
				// System.out.println("\t" +mentry.getKey() + "\t\t\t|\t\t\t" +
				// mentry.getValue());
				// System.out.printf(format, mentry.getKey(), mentry.getValue());
				if (mentry.getValue() >= supportCount) {
					frequentItemSets.put(mentry.getKey(), mentry.getValue());
					L.put(mentry.getKey(), mentry.getValue());
				}
			}
			if (L.size() == 0) {
				break;
			}
			/*
			 * Uncomment below lines to see intermediate result of apriori
			 * System.out.println("\n==>L" + setCnt + ":");
			 * System.out.println("\nItem-Set\t\tSupport-Count");
			 * System.out.println("---------------------------------------");
			 */
			set = L.entrySet();
			iterator = set.iterator();
			while (iterator.hasNext()) {
				Map.Entry<HashSet<Integer>, Integer> mentry = (Map.Entry<HashSet<Integer>, Integer>) iterator.next();
				// System.out.println("\t" +mentry.getKey() + "\t\t\t|\t\t\t" +
				// mentry.getValue());
				// System.out.printf(format, mentry.getKey(), mentry.getValue());
			}
		}
		try {
			FileWriter output = new FileWriter("aprioriOutput.txt");
			/*
			 * Uncomment below lines to see the output of apriori
			 * System.out.println("\n\n*****Frequent Item Sets are as follows*****");
			 * System.out.println("\nItem-Set\t\tSupport-Count");
			 * System.out.println("---------------------------------------");
			 */
			set = frequentItemSets.entrySet();
			iterator = set.iterator();
			while (iterator.hasNext()) {
				Map.Entry<HashSet<Integer>, Integer> mentry = (Map.Entry<HashSet<Integer>, Integer>) iterator.next();
				// System.out.printf(format, mentry.getKey(), mentry.getValue());
				HashSet<Integer> toPrint = mentry.getKey();
				output.write(toPrint + ":" + mentry.getValue());
				output.write("\n");
			}
			output.close();
			endTime = System.nanoTime();
			double sec = (endTime - startTime) / 1000000000;
			// System.out.println("\nExecution Time is: " + sec + "s");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		startTime = System.nanoTime();
		Scanner sc = new Scanner(System.in);
		String inputFile = "", itemFile = "";
		int i = 0, j = 0, k = 0;
		/*
		 * Uncomment below lines to take user input
		 * System.out.print("\nEnter input file name:"); inputFile = sc.nextLine();
		 */
		supportCount = Integer.parseInt(args[0]);
		/*
		 * Step-1]-->Take file name and minimum support as input 
		 * Step-2}-->Read file and add unique items in uniqueItems ArrayList
		 */
		try {
			File input = new File("apriori.txt");
			Scanner reader = new Scanner(input);
			while (reader.hasNextLine()) {
				itemFile = reader.nextLine();
				if (itemFile.length() > 0) {
					String[] fields = itemFile.split(" ");
					for (i = 0; i < fields.length; i++) {
						if (isItemPresent(Integer.parseInt(fields[i])) == false) {
							uniqueItems.add(Integer.parseInt(fields[i]));
						}
					}
					transactions++;
				}
			}
			/*
			 * System.out.println("\nUniqueItems:"); for(i=0;i<uniqueItems.size();i++) {
			 * System.out.println("\n" +uniqueItems.get(i)); }
			 */
			/*
			 * Step-3]-->Store all transactions as arraylist of arraylist
			 */
			for (i = 0; i < transactions; i++) {
				ArrayList<Integer> tempList = new ArrayList<Integer>();
				itemList.add(tempList);
			}
			// System.out.println("\nItems are as follows:");
			// System.out.println(uniqueItems);

			reader = new Scanner(input);
			while (reader.hasNextLine()) {
				itemFile = reader.nextLine();
				if (itemFile.length() > 0) {
					String[] fields = itemFile.split(" ");
					for (i = 0; i < fields.length; i++) {
						itemList.get(k).add(Integer.parseInt(fields[i]));
					}
					k++;
				}
			}
			/*
			 * itemList contains all transactions as they occur in file. basically it
			 * contains the file data as it is.
			 */

			/* Printing Itemsets
			 * for (i = 0; i < itemList.size(); i++) { for (j = 0; j <
			 * itemList.get(i).size(); j++) { System.out.print(itemList.get(i).get(j) +
			 * " "); } System.out.println(); }
			 */
			apriori();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
