package de.topobyte.mapocado.swing.rendering.poi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.slimjars.dist.gnu.trove.set.TIntSet;
import com.slimjars.dist.gnu.trove.set.hash.TIntHashSet;

import de.topobyte.mapocado.swing.rendering.poi.category.Category;
import de.topobyte.mapocado.swing.rendering.poi.category.DatabaseCategory;
import de.topobyte.mapocado.swing.rendering.poi.category.MapfileCategory;
import de.topobyte.mapocado.swing.rendering.poi.category.SpecialCategory;

public class Categories
{

	public static final String PREF_PREFIX_LABELS = "d:";
	public static final String PREF_PREFIX_SEARCH = "s:";

	public static final String PREF_KEY_STREETS = "c:streets";
	public static final String PREF_KEY_OTHERS = "c:other";
	public static final String TYPE_NAME_HOUSENUMBERS = "housenumbers";
	public static final String TYPE_NAME_HOUSENUMBERS_BUILDINGS = "housenumbers-buildings";

	private static Categories labelInstance = null;
	private static Categories searchInstance = null;

	private enum Type {
		Labels,
		Search,
		All
	}

	public static Categories getLabelInstance()
	{
		if (labelInstance == null) {
			labelInstance = new Categories(PREF_PREFIX_LABELS, Type.Labels);
		}
		return labelInstance;
	}

	public static Categories getSearchInstance()
	{
		if (searchInstance == null) {
			searchInstance = new Categories(PREF_PREFIX_SEARCH, Type.Search);
		}
		return searchInstance;
	}

	public static Categories getAllInstance()
	{
		if (labelInstance == null) {
			labelInstance = new Categories("a:", Type.All);
		}
		return labelInstance;
	}

	private List<Group> groups;
	private Set<Group> special;
	private TIntSet specialIndices;
	private Map<Category, Group> backlinks;

	private final String prefix;

	private Categories(String prefix, Type type)
	{
		this.prefix = prefix;

		prepareListData(type);

		initSpecial();

		initBacklinks();
	}

	private void initBacklinks()
	{
		backlinks = new HashMap<>();
		for (Group group : groups) {
			for (Category category : group.getChildren()) {
				backlinks.put(category, group);
			}
		}
	}

	private void initSpecial()
	{
		specialIndices = new TIntHashSet();
		for (int i = 0; i < groups.size(); i++) {
			if (special.contains(groups.get(i))) {
				specialIndices.add(i);
			}
		}
	}

	private Category streets;
	private Group foodDrink;

	private void prepareListData(Type type)
	{
		groups = new ArrayList<>();
		special = new HashSet<>();

		if (type == Type.Search || type == Type.All) {
			Group orientation = new Group("Orientation");
			groups.add(orientation);
			streets = new SpecialCategory("Streets", PREF_KEY_STREETS);
			orientation.add(streets);
			orientation.add(new DatabaseCategory("Places", "c:places", "city",
					"town", "village", "hamlet", "island", "islet", "borough",
					"suburb", "quarter", "neighborhood"));
		}

		Group transportation = new Group("Transportation");
		groups.add(transportation);
		transportation.add(new DatabaseCategory("Railway stops",
				"c:railwaystop", "railwaystation", "railwayhalt"));
		transportation.add(
				new DatabaseCategory("Tram stops", "c:tramstop", "tramstop"));
		transportation.add(new DatabaseCategory("Bus stops", "c:busstop",
				"busstop", "busstation"));

		Group nature = new Group("Nature");
		groups.add(nature);
		nature.add(new DatabaseCategory("Parks", "c:park", "park"));
		nature.add(new DatabaseCategory("Water", "c:water", "water"));
		nature.add(new DatabaseCategory("Peaks", "c:peak", "peak", "volcano"));
		nature.add(new DatabaseCategory("Playgrounds", "c:playground",
				"playground"));
		nature.add(
				new DatabaseCategory("Cemeteries", "c:cemetery", "cemetery"));

		foodDrink = new Group("Food & Drink");
		groups.add(foodDrink);
		foodDrink.add(new DatabaseCategory("Restaurants", "c:restaurants",
				"restaurant"));
		foodDrink.add(
				new DatabaseCategory("Fast food", "c:fastfood", "fastfood"));
		foodDrink.add(new DatabaseCategory("Cafes", "c:cafes", "cafe"));
		foodDrink.add(new DatabaseCategory("Bakeries", "c:bakeries", "bakery"));
		foodDrink.add(
				new DatabaseCategory("Pubs", "c:pubs", "pub", "biergarten"));
		foodDrink.add(new DatabaseCategory("Bars", "c:bars", "bar"));
		foodDrink.add(new DatabaseCategory("Nightclubs", "c:nightclubs",
				"nightclub"));

		Group culture = new Group("Culture");
		groups.add(culture);
		culture.add(new DatabaseCategory("Museums", "c:museums", "museum",
				"gallery"));
		culture.add(new DatabaseCategory("Attractions", "c:attractions",
				"attraction"));

		Group others = new Group("None");
		groups.add(others);
		if (type == Type.Labels) {
			others.add(new MapfileCategory("Housenumbers", "c:housenumbers",
					TYPE_NAME_HOUSENUMBERS));
		}
		others.add(new DatabaseCategory("Other", PREF_KEY_OTHERS, "other"));
		special.add(others);
	}

	public List<Group> getGroups()
	{
		return groups;
	}

	public Set<Group> getSpecial()
	{
		return special;
	}

	public TIntSet getSpecialIndices()
	{
		return specialIndices;
	}

	public Group getGroup(Category category)
	{
		return backlinks.get(category);
	}

}
