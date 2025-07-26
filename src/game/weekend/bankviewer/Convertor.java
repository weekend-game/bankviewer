package game.weekend.bankviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Преобразование из формата банковской выписки в HTML.
 *
 */
public class Convertor {

	public static final String LS = System.lineSeparator();

	/**
	 * Создание объектов этого класса запрещено. Класс содержит только статические
	 * методы.
	 */
	private Convertor() {
	}

	/**
	 * Преобразовать из формата банковской выписки в HTML.
	 * 
	 * @param src имя исходного файла выписки.
	 * @param dst имя HTML-файла выписки.
	 */
	public static void convert(String src, File dstFile) {
		ArrayList<Substance> al = loadSource(src);
		ArrayList<String> title = createTitle(al);

		lineCounter = 0;
		createHTML(dstFile, al, title);

		BankViewer.status.showText1("Строк: " + lineCounter);
		BankViewer.status.showText2("Колонок: " + (title.size() - 1)); // Колонка "No" (номер строки) не считается
	}

	/**
	 * Загрузить исходный текст в список ArrayList&lt;Substance&gt;.
	 * 
	 * Исходный текст это пары ключ/значение. Такие пары между строкой с ключём
	 * "СекцияДокумент" и ключем "КонецДокумента" образуют документ, который можно
	 * предстваить как строку таблицы отображаемой на экране. Назову такую
	 * последовательность пар документом. Каждый документ может содержать поля
	 * отсутствующие в других документах и наоборот. Но, в основном поля в
	 * документах одинаковы.
	 * 
	 * Substance это класс содержащий логическое поле isDocument и спискок полей в
	 * виде пар ключ/значение. Если это документ, список полей содержит много
	 * записей, если нет, то только одну. Ту самую строку которая оказалась НЕ между
	 * "СекцияДокумент" и "КонецДокумента".
	 * 
	 * Из объектов класса Substance и составляется список
	 * ArrayList&lt;Substance&gt;.
	 * 
	 * @param src имя исходного файла выписки.
	 * 
	 * @return разобранная выписка.
	 */
	private static ArrayList<Substance> loadSource(String src) {
		String s, s1, s2;
		int pos;
		boolean inDoc = false;
		Substance sub = null;
		ArrayList<Substance> vid = new ArrayList<Substance>();
		int nn = 0;

		try {
			// Открываю файл
			BufferedReader inp = new BufferedReader(new FileReader(src));

			// и последовательно читаю строки
			while ((s = inp.readLine()) != null) {
				if (s.trim().length() == 0)
					continue;

				// Т.к. в каждой строке пара ключ=значение, ищу символ '='
				pos = s.indexOf('=');
				if (pos > 0) {
					// Если найден, то s1 - ключ, а s2 - значение
					s1 = s.substring(0, pos);
					s2 = s.substring(pos + 1);
				} else {
					// Иначе, s1 вся строка, а s2 - пусто
					s1 = s.trim();
					s2 = "";
				}

				if (s1.equalsIgnoreCase("СекцияДокумент")) {
					// Создаю объект нового документа
					sub = new Substance(Substance.A_DOC);
					// Первым идет его номер
					sub.addPair("No", "" + ++nn);
					// Затем, эта надпись СекцияДокумент
					sub.addPair(s1, s2);
					// Добавляю новый документ в результирующий список
					vid.add(sub);

					// Признак "Я в документе"
					inDoc = true;
				} else {
					if (inDoc) {
						// Если "Я в документе" и встречаю "КонецДокумента",
						if (s1.equalsIgnoreCase("КонецДокумента")) {
							// то сбрасываю признак.
							inDoc = false;
						} else {
							// Иначе, добавляю в текущий документ очередную пару ключ/значение
							sub.addPair(s1, s2);
						}
					} else {
						// Если не в документе,
						// то создаю объект с признаком "недокумент"
						sub = new Substance(Substance.NOT_A_DOC);
						// добавляю в него пару ключ/значение
						sub.addPair(s1, s2);
						// и добавляю это в результирующий список.
						vid.add(sub);
					}
				}
			}
			inp.close();
		} catch (IOException e) {
		}
		return vid;
	}

	/**
	 * Сформировать заголовок таблицы отображаемой на экране на основании документов
	 * выписки. Заголовок таблицы это ArrayList&lt;String&gt; содержащий ключи всех
	 * полей которые встречаются в документах в исходном файле. Но, т.к. исходный
	 * файл уже разобран и помещен в список, работаю именно с этим списком.
	 * 
	 * @param vid разобранная выписка.
	 * 
	 * @return заголовок на основании документов выписки.
	 */
	private static ArrayList<String> createTitle(ArrayList<Substance> vid) {
		// Возвращаемое значение. Заголовок. Будущий список всех полей которые
		// встречаются в документах.
		ArrayList<String> title = new ArrayList<String>();

		// Документ содержащий самое большое количество полей становится основой для
		// формирования списка полей (загловка).

		// Ищу такой
		int curr = 0;
		Substance etal = null;
		for (Substance sub : vid) {
			if (sub.isDoc && sub.pairs.size() > curr) {
				curr = sub.pairs.size();
				etal = sub;
			}
		}

		// Копирую его ключи в заголовок
		if (etal != null) {
			for (Pair p : etal.pairs) {
				title.add(p.key);
			}
		}

		// Расширяю заголовок, добавля в него ключи из прочих документов
		for (Substance sub : vid) {
			if (sub.isDoc) {
				for (Pair p : sub.pairs) {
					if (hasName(title, p.key) < 0) {
						title.add(p.key);
					}
				}
			}
		}
		return title;
	}

	/**
	 * Создать HTML-файл выписки.
	 * 
	 * @param dst   имя HTML-файла выписки.
	 * @param vid   разобранная выписка.
	 * @param title заголовок на основании документов выписки.
	 */
	private static void createHTML(File dstFile, ArrayList<Substance> vid, ArrayList<String> title) {

		try {
			out = new PrintWriter(new FileWriter(dstFile));

			// Заголовок файла
			write("<html>");
			write(" <head>");
			write("   <title>");
			write("   </title>");
			write(" </head>");
			write("<body>");

			boolean inTable = false;
			for (Substance sb : vid) {
				if (sb.isDoc) {
					// Печатем заголовок
					if (!inTable) {
						inTable = true;
						write("<table border='1'>");
						write("<tr>");
						for (String t : title) {
							// Некоторые эмпирические правила для определения ширины колонок. Так удобнее
							// становится. Впрочем, можно было бы и определить реально необходимую ширину
							// колонки...
							if (t.equalsIgnoreCase("Плательщик1") || t.equalsIgnoreCase("Получатель")
									|| t.equalsIgnoreCase("Получатель1") || t.equalsIgnoreCase("НазначениеПлатежа")
									|| t.equalsIgnoreCase("НазначениеПлатежа1")) {
								write("<th>" + spacing(t) + "&nbsp;".repeat(100) + "</th>");
							} else if (t.equalsIgnoreCase("Плательщик") || t.equalsIgnoreCase("ПлательщикБанк1")
									|| t.equalsIgnoreCase("ПолучательБанк") || t.equalsIgnoreCase("ПолучательБанк1")) {
								write("<th>" + spacing(t) + "&nbsp;".repeat(50) + "</th>");
							} else {
								write("<th>" + spacing(t) + "</th>");
							}
						}
						write("</tr>");
					}

					// Формирование строки документа
					String[] d = new String[title.size()];
					for (Pair p : sb.pairs) {
						int ps = hasName(title, p.key);
						d[ps] = p.value;
					}

					// Вывод строки документа
					write("<tr>");
					for (int i = 0; i < d.length; ++i) {
						write("<td>" + spacing(d[i]) + "</td>");
					}
					write("</tr>");

					++lineCounter;

				} else {
					if (inTable) {
						write("</table>");
						inTable = false;
					}
					// Вывод прочего
					for (Pair p : sb.pairs) {
						String ss = "<b>" + spacing(p.key) + "</b> = " + spacing(p.value) + "<br>";
						write(ss);
					}
				}
			}
			if (inTable) {
				write("</table>");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		write("");
		write("</body>");
		write("</html>");

		out.close();
	}

	/**
	 * Заменить пустую строку на &nbsp;.
	 * 
	 * @param s строка
	 * @return "&nbsp;" если строка была пустой и она же если нет.
	 */
	private static String spacing(String s) {
		if (s == null) {
			s = "&nbsp;";
		}
		if (s.trim().length() == 0) {
			s = "&nbsp;";
		}
		return s;
	}

	/**
	 * Вывести в файл (this.out) строку завершив её переводом строки.
	 * 
	 * @param s выводимая в файл строка.
	 */
	private static void write(String s) {
		Convertor.out.write(s + LS);
	}

	/**
	 * Определить позицию ключа в заголовке.
	 * 
	 * Используется при создании заголовка документа. Заголовок формируется из
	 * ключей документа содержащего самое большое количество полей. А затем в него
	 * добавляются поля прочих документов которых возможно не было в документе
	 * принятом за основу. Для ответа на вопрос, имеется ли такое поле в списке, и
	 * служит этот метод.
	 * 
	 * Эффективность такого поиска низкая, но для данной задачи приемлемая.
	 * 
	 * @param title заголовок на основании документов выписки.
	 * @param key   ключ.
	 * 
	 * @return - позиция ключа в заголовке или -1.
	 */
	private static int hasName(ArrayList<String> title, String key) {
		int i = -1;
		for (String str : title) {
			++i;
			if (str.equalsIgnoreCase(key))
				return i;
		}
		return -1;
	}

	/**
	 * Содержит логическое поле isDocument и спискок полей в виде пар ключ/значение.
	 * Если это документ, список полей содержит много записей, если нет, то только
	 * одну. Ту самую строку которая оказалась НЕ между "СекцияДокумент" и
	 * "КонецДокумента".
	 * 
	 * Класс простейший, поэтому вместо getters использую public final поля.
	 */
	static class Substance {

		// Использую константы вместо enum. Какая дикость...
		public static final boolean A_DOC = true;
		public static final boolean NOT_A_DOC = false;

		public Substance(boolean isDoc) {
			this.isDoc = isDoc;
		}

		public void addPair(String key, String value) {
			pairs.add(new Pair(key, value));
		}

		public final boolean isDoc;
		public final ArrayList<Pair> pairs = new ArrayList<Pair>();
	}

	static class Pair {
		public Pair(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public final String key;
		public final String value;
	}

	private static PrintWriter out;
	private static int lineCounter = 0;
}
