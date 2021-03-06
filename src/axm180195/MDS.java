/**
 * Starter code for MDS
 *
 * @author rbk
 */

// Change to your net id
package axm180195;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

// If you want to create additional classes, place them in this file as subclasses of MDS

public class MDS {
	// Add fields of MDS here
	private TreeMap<Long, Product> tree;
	private Map<Long, TreeSet<Product>> map;

	// Constructors
	public MDS() {
		tree = new TreeMap<>();
		map = new HashMap<>();
	}

	private class Product {
		private Long id;
		private List<Long> desc;
		private Money price;

		Product() { // never used.
			id = (long) 0;
			desc = new LinkedList<>();
			price = new Money();
		}

		Product(long id, Money price, List<Long> desc) {
			this.id = id;
			this.price = price;
			this.desc = new LinkedList<>();
			for (Long num : desc) {
				this.desc.add(num);
			}
		}

		/**
		 * @return id of the product
		 */
		private Long getId() {
			return this.id;
		}

		private Money getPrice() {
			return this.price;
		}
	}

	private class priceComp implements Comparator<Product> {
		@Override
		public int compare(Product o1, Product o2) {
			// TODO Auto-generated method stub
			int priceValue = o1.getPrice().compareTo(o2.getPrice());
			if (priceValue < 0) {
				return -1;
			} else if (priceValue > 0) {
				return 1;
			} else { // if prices are same then comparison is done on ID because TreeSet doesn't
						// automatically handles the equal case.
				if (o1.getId() < o2.getId()) {
					return -1;
				} else if (o1.getId() > o2.getId()) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}

	/*
	 * Public methods of MDS. Do not change their signatures.
	 * __________________________________________________________________ a.
	 * Insert(id,price,list): insert a new item whose description is given in the
	 * list. If an entry with the same id already exists, then its description and
	 * price are replaced by the new values, unless list is null or empty, in which
	 * case, just the price is updated. Returns 1 if the item is new, and 0
	 * otherwise.
	 */

	/**
	 * @param id    The product id
	 * @param price The product price
	 * @param list  Description List
	 * @return
	 */
	public int insert(long id, Money price, java.util.List<Long> list) {
		if (!tree.containsKey(id)) { // Tree doesn't contains the ID.
			Product p = new Product(id, price, list);
			tree.put(id, p);
			// inserting values into Map.
			for (Long num : list) {
				if (!map.containsKey(num)) { // Map doesn't have desc as key.
					TreeSet<Product> productSet = new TreeSet<>(new priceComp());
					productSet.add(p);
					map.put(num, productSet);
				} else { // Map have the desc key.
					map.get(num).add(p); // getting the treeSet and adding product to it.
				}
			}
			return 1;
		} else { // Tree contains the ID.
			Product p = tree.get(id);
			if (list == null || list.isEmpty()) { // list is null || list is empty
				p.price = price;
				for (Long num : p.desc) { // adding and removing p so that tree set sorts data automatically.
					map.get(num).remove(p);
					map.get(num).add(p);
				}
			} else { // list contains data.
				p.price = price;
				// remove entries from map and clear desc list.
				updateMapEntry(p, "remove");
				p.desc.clear();
				// update desc list and add entries to map.
				for (Long num : list) {
					p.desc.add(num);
				}
				updateMapEntry(p, "add");
			}
			return 0;
		}
	}

	private void updateMapEntry(Product p, String s) {
		if (s.equals("remove")) {
			for (Long num : p.desc) {
				map.get(num).remove(p); // getting treeSet and removing given product from that.
			}
		} else if (s.equals("add")) {
			for (Long num : p.desc) {
				if (!map.containsKey(num)) {// desc key not in map.
					// creating TreeSet to add to HashMap with corresponding desc key.
					TreeSet<Product> productSet = new TreeSet<>(new priceComp());
					productSet.add(p);
					map.put(num, productSet);
				} else { // desc key is present in map.
					map.get(num).add(p); // getting treeSet for num key and adding product to it.
				}
			}
		} else {
			System.out.println(
					"Something went WRONG!\nCheck the string passed to updateMapEntry() - It should be 'remove' or 'add'");
		}
	}

	// b. Find(id): return price of item with given id (or 0, if not found).
	public Money find(long id) {
		if (tree.containsKey(id)) { // ID found.
			return tree.get(id).price;
		}
		return new Money(); // has price=0
	}

	/*
	 * c. Delete(id): delete item from storage. Returns the sum of the long ints
	 * that are in the description of the item deleted, or 0, if such an id did not
	 * exist.
	 */
	public long delete(long id) {
		if (tree.containsKey(id)) {// ID found -> return sum of desc.
			Product p = tree.get(id);
			// Delete product from all entries in map.
			updateMapEntry(p, "remove");

			Long sum = (long) 0;
			for (Long num : p.desc) {
				sum += num;
			}
			tree.remove(id);
			return sum; // returning sum of all the desc items of deleted id.
		}
		return 0; // ID not found -> return 0.
	}

	/*
	 * d. FindMinPrice(n): given a long int, find items whose description contains
	 * that number (exact match with one of the long ints in the item's
	 * description), and return lowest price of those items. Return 0 if there is no
	 * such item.
	 */
	public Money findMinPrice(long n) {
		if (map.containsKey(n)) { // map contains desc key.
			if (map.get(n).size() == 0) {
				return new Money();
			}
			return map.get(n).first().price;
		}
		return new Money();
	}

	/*
	 * e. FindMaxPrice(n): given a long int, find items whose description contains
	 * that number, and return highest price of those items. Return 0 if there is no
	 * such item.
	 */
	public Money findMaxPrice(long n) {
		if (map.containsKey(n)) { // map contains desc key.
			if (map.get(n).size() == 0) {
				return new Money();
			}
			return map.get(n).last().price;
		}
		return new Money();
	}

	/*
	 * f. FindPriceRange(n,low,high): given a long int n, find the number of items
	 * whose description contains n, and in addition, their prices fall within the
	 * given range, [low, high].
	 */
	public int findPriceRange(long n, Money low, Money high) {
		int count = 0;

		if (low.compareTo(high) > 0)
			return count;

		if (map.containsKey(n)) {
			if (map.get(n).size() == 0) {
				return count;
			}
			// Dummy products to set low and high ends for NavigableSet.
			Product pLow = new Product(Long.MIN_VALUE, new Money(low), new LinkedList<>());
			Product pHigh = new Product(Long.MAX_VALUE, new Money(high), new LinkedList<>());

			// Filtered Set between given low and high.
			NavigableSet<Product> productSet = map.get(n).subSet(pLow, true, pHigh, true);
			if (productSet.size() == 0) {
				return count;
			}
			for (Product p : productSet) {
				count++;
			}
		}
		return count;
	}

	/*
	 * g. PriceHike(l,h,r): increase the price of every product, whose id is in the
	 * range [l,h] by r%. Discard any fractional pennies in the new prices of items.
	 * Returns the sum of the net increases of the prices.
	 */
	public Money priceHike(long l, long h, double rate) {
		if (l > h)
			return new Money();

		BigDecimal rate_bd = new BigDecimal(rate);

		rate_bd = rate_bd.divide(new BigDecimal(100)).setScale(2, RoundingMode.DOWN);

		SortedMap<Long, Product> searchTree = tree.subMap(l, h + 1);

		BigDecimal sum_bd = new BigDecimal(0);
		for (Long id : searchTree.keySet()) {
			if (id >= l && id <= h) {
				Product p = searchTree.get(id);

				BigDecimal oldPrice_bd = new BigDecimal(p.price.toString()).setScale(2, RoundingMode.DOWN);

				BigDecimal newPrice_bd = oldPrice_bd.add(oldPrice_bd.multiply(rate_bd)).setScale(2, RoundingMode.DOWN);

				BigDecimal diff_bd = newPrice_bd.subtract(oldPrice_bd).setScale(2, RoundingMode.DOWN);

				sum_bd = sum_bd.add(diff_bd).setScale(2, RoundingMode.DOWN);

				p.price = new Money(newPrice_bd.toString());

				for (Long num : p.desc) { // removing and adding in TreeSet so it automatically sorts itself.
					map.get(num).remove(p);
					map.get(num).add(p);
				}

			}
		}

		return new Money(sum_bd.toString());
	}

	/*
	 * h. RemoveNames(id, list): Remove elements of list from the description of id.
	 * It is possible that some of the items in the list are not in the id's
	 * description. Return the sum of the numbers that are actually deleted from the
	 * description of id. Return 0 if there is no such id.
	 */
	public long removeNames(long id, java.util.List<Long> list) {
		Product p = tree.get(id);

		// List of common Id's to be deleted.
		List<Long> delList = new LinkedList<>();
		for (Long num : p.desc) {
			if (list.contains(num)) {
				delList.add(num);
			}
		}

		long sum = 0;
		for (Long num : delList) {
			sum += num;
			map.get(num).remove(p);
		}

		p.desc.removeAll(delList);

		return sum;
	}

	// Do not modify the Money class in a way that breaks LP4Driver.java
	public static class Money implements Comparable<Money> {
		long d;
		int c;

		public Money() {
			d = 0;
			c = 0;
		}

		public Money(long d, int c) {
			this.d = d;
			this.c = c;
		}

		public Money(String s) {
			String[] part = s.split("\\.");
			int len = part.length;
			if (len < 1) {
				d = 0;
				c = 0;
			} else if (len == 1) {
				d = Long.parseLong(s);
				c = 0;
			} else {
				d = Long.parseLong(part[0]);
				c = Integer.parseInt(part[1]);
				if (part[1].length() == 1) {
					c = c * 10;
				}
			}
		}

		/**
		 * Copy constructor for Money class, creates a deep copy of the object.
		 * 
		 * @param o object to be copied
		 */
		public Money(Money o) {
			this.d = o.d;
			this.c = o.c;
		}

		public long dollars() {
			return d;
		}

		public int cents() {
			return c;
		}

		public int compareTo(Money other) { // Complete this, if needed
			if (this.d < other.d) {
				return -1;
			} else if (this.d > other.d) {
				return 1;
			} else {
				if (this.c < other.c) {
					return -1;
				} else if (this.c > other.c) {
					return 1;
				} else {
					return 0;
				}
			}
		}

		public String toString() {
			if (c < 10)
				return d + ".0" + c; // driver code mistake
			return d + "." + c;
		}
	}

}
