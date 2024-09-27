package vip.xiaonuo.utils;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: 志源大魔王
 * @Date: 2022/2/18 14:32
 * @description: List集合工具类
 */
@Slf4j
public class ListUtils {

	public static void main(String[] args) {
		System.out.println(splitString("1231231", 2));
	}

	/**
	 * 按照字符串数量切割
	 *
	 * @param input
	 * @param chunkSize
	 * @return
	 */
	public static List<String> splitString(String input, int chunkSize) {
		List<String> chunks = new ArrayList<>();
		int length = input.length();

		for (int i = 0; i < length; i += chunkSize) {
			int end = Math.min(length, i + chunkSize);
			chunks.add(input.substring(i, end));
		}
		return chunks;
	}

	/**
	 * 按照段落将字符串切割成 list
	 *
	 * @param input
	 * @return
	 */
	public static List<String> splitStringByParagraph(String input) {
		List<String> paragraphs = new ArrayList<>();

		String[] paragraphsArray = input.split("\\n");

		for (String paragraph : paragraphsArray) {
			paragraphs.add(paragraph);
		}

		return paragraphs;
	}

	/**
	 * 无重复合并 list
	 *
	 * @param list
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> mergeAllList(List<T>... list) {
		int i = 0;
		List<T> findList = list[0];
		for (List<T> ts : list) {
			// 第一个 list 直接跳过
			if (i == 0) {
				i++;
				continue;
			}
			// 从第二个 list 中开始循环遍历
			for (T t : ts) {
				// 如果列表中不包含要返回的元素就直接添加到最终返回的 list 中
				if (!findList.contains(t)) {
					findList.add(t);
				}
			}
		}
		return removeDuplicationByHashSet(findList);
	}

	public static <T> List<List<T>> splitList(List<T> list, int size) {
		return list.stream()
				.collect(Collectors.groupingBy(element -> (list.indexOf(element) / size)))
				.values()
				.stream()
				.map(elements -> new ArrayList<>(elements))
				.collect(Collectors.toList());
	}

	/**
	 * 将字符串按照指定标识分割成 list
	 * @param list
	 * @param interval
	 * @return
	 */
	public static List<String> splitStr(String list, String  interval) {
		if (StrUtil.isBlank(list)) {
			return new ArrayList<>();
		}
		return Arrays.asList(list.split(interval));
	}

	/**
	 * list 逗号间隔
	 *
	 * @param list
	 * @param interval
	 * @param <T>
	 * @return
	 */
	public static <T> String join(List<T> list, String interval
	) {
		if (isBlank(list)) {
			return "";
		}
		StringBuilder resultStr = new StringBuilder();
		for (T t : list) {
			resultStr.append(t.toString()).append(interval
			);
		}
		return resultStr.substring(0, resultStr.length() - 1);
	}

	/**
	 * 根据元素特征分隔 list，最后得到一个更长的 list
	 * 例如：在一个 list 中有以下元素
	 * 元素1： 士大夫，，地方，放到，发，阿萨,fd
	 * 元素2： 发，的，的，啊，，，，的，
	 * 调用方法后结果：
	 * 发, 地方, 的, 士大夫, 啊, 阿萨, 放到, fd
	 *
	 * @param list
	 * @param logo
	 * @return
	 */
	public static List<String> listLogoSplit(List<String> list, String logo) {
		List<String> findList = new ArrayList<>();
		for (String s : list) {
			if (CharSequenceUtil.isBlank(s)) {
				continue;
			}
			if (s.contains("，")) {
				String[] split = s.split("，");
				for (String h : split) {
					if (CharSequenceUtil.isNotBlank(h)) {
						findList.add(h);
					}
				}
			} else {
				findList.add(s);
			}
		}
		// 最后进行查重
		return ListUtils.removeDuplicationByHashSet(findList);
	}

	/**
	 * 使用 HashSet 实现 List 去重（无序查重）
	 * 这种效率更高，更稳定
	 *
	 * @param list
	 * @param <T>
	 * @return
	 */
	private static <T> List<T> removeDuplicationByHashSet(List<T> list) {
		HashSet<T> set = new HashSet<>(list);
		//把 list 集合中所有元素清空
		list.clear();
		// 把 HashSet 对象添加至 List 集合
		list.addAll(set);
		return list;
	}

	/**
	 * 使用 TreeSet 实现 List 去重（有序）
	 *
	 * @param list
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> removeDuplicationByTreeSet(List<T> list) {
		TreeSet<T> set = new TreeSet<>(list);
		//把List集合所有元素清空
		list.clear();
		//把HashSet对象添加至List集合
		list.addAll(set);
		return list;
	}

	public static <T> List<T> filterEmpty(List<T> list) {
		if (list == null) {
			return new ArrayList<>();
		}
		return list;
	}

	/**
	 * 根据对象属性删除重复元素
	 *
	 * @param sourceList
	 * @param keyExtractor
	 * @param <T>
	 * @param <U>
	 * @return
	 */
	public static <T, U extends Comparable<? super U>>
	List<T> listObjPropertyRmRedo(List<T> sourceList, Function<? super T, ? extends U> keyExtractor) {
		Set<T> treeSet = new TreeSet<>(Comparator.comparing(keyExtractor));
		treeSet.addAll(sourceList);
		return new ArrayList<>(treeSet);
	}

	/**
	 * 判断是否为空白
	 *
	 * @param targetList
	 * @return
	 */
	public static <T> boolean isBlank(Collection<T> targetList) {
		return targetList == null || targetList.isEmpty();
	}

	public static <T> boolean isNotBlank(Collection<T> targetList) {
		return targetList != null && !targetList.isEmpty();
	}

	/**
	 * 将对应List转换为不可修改的List
	 */
	public static <T> List<T> unmodifiable(List<T> list) {
		if (null == list) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(list);
	}

	/**
	 * 获取一个空List，这个空List不可变
	 *
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> empty() {
		return Collections.emptyList();
	}

	/**
	 * 多元素拼接返回list
	 *
	 * @param elements
	 * @param <E>
	 * @return
	 */
	@SafeVarargs
	public static <E> List<E> of(E... elements) {
		Objects.requireNonNull(elements);
		return (elements.length == 0) ? Collections.emptyList() :
				(elements.length == 1) ? Collections.singletonList(elements[0]) :
						new ArrayList<>(Arrays.asList(elements));
	}


}
